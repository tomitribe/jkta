/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.eclipse.wg.jakartaee;

import org.apache.cxf.jaxrs.client.WebClient;
import org.apache.openejb.loader.Zips;
import org.junit.Assert;
import org.junit.Test;
import org.tomitribe.util.Files;
import org.tomitribe.util.IO;
import org.tomitribe.util.Join;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.ws.rs.core.Response;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BinaryAnalysisTest extends Assert {

    private final File downloads = new File("/Users/dblevins/work/jakartaee/downloads");
    private final File tmp = new File("/tmp");

    public BinaryAnalysisTest() {
        downloads.mkdirs();
    }

    @Test
    public void test() throws Exception {
        final List<File> list = getUniqueJarFiles();

        final List<Jar> jars = parseJars(list).stream()
                .map(Classes::javaxUses)
                .map(Classes::externalUses)
                .map(Classes::distinctUses)
                .map(Classes::trimEmptyReferences)
                .filter(Jar::hasReferences)
                .collect(Collectors.toList());

        for (final Jar jar : jars) {
            System.out.printf("%n## %s%n%n", jar.getName());

            for (final String ref : jar.getDistinctReferences()) {
                System.out.printf(" - %s%n", ref);
            }
        }

//        Jsonb jsonb = JsonbBuilder.create();
//        IO.copy(IO.read(jsonb.toJson(jars)), new File("/tmp/dependencies.json"));


        jars.size();
    }

    public List<Jar> parseJars(final List<File> list) {
        final List<Jar> jars = new ArrayList<Jar>();

        for (final File file : list) {
            final File dir = extract(file);

            final Jar jar = new Jar(dir.getName());
            jars.add(jar);
            final DependencyVisitor analysis = new DependencyVisitor(jar);
            Dependencies.dir(dir, analysis);

            System.out.println();
            System.out.println(dir.getName());
            analysis.getPackages().stream().sorted()
                    .map(s -> String.format(" - %s", s))
                    .forEach(System.out::println);
        }
        return jars;
    }

    private File extract(final File file) {
        final String name = simpleName(file);
        final File dir = new File(tmp, name);
        dir.mkdirs();
        try {
            Zips.unzip(file, dir);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        return dir;
    }

    public List<File> getUniqueJarFiles() {
        final Map<String, File> jars = new HashMap<>();

        getCachedJakartaJarsList()
                .stream()
                .map(this::getDownloaded)
                .filter(file -> !file.getName().contains("api-test"))
                .forEach(file -> {
                    final String simpleName = simpleName(file);
                    jars.put(simpleName, file);
                });

        return (List<File>) jars.keySet().stream()
                .sorted()
                .map(jars::get)
                .collect(Collectors.toList());
    }

    public static String simpleName(final File file) {
        return file.getName().replaceAll("-[0-9.]+.jar", ".jar");
    }


    private List<URI> getCachedJakartaJarsList() {
        final File index = new File(downloads, "index.txt");
        if (index.exists()) {
            return Stream.of(slurp(index).split("\n"))
                    .map(URI::create)
                    .collect(Collectors.toList());
        }

        final List<URI> uris = getJarsRecursively(URI.create("http://repo.maven.apache.org/maven2/jakarta/"));

        try {
            IO.copy(IO.read(Join.join("\n", uris)), index);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        return uris;
    }

    private String slurp(final File index) {
        try {
            return IO.slurp(index);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public List<URI> getJarsRecursively(final URI uri) {
        final List<URI> jars = new ArrayList<>();
        walk(jars, uri);
        return jars;
    }

    public void walk(final List<URI> jars, final URI uri) {
        final WebClient client = WebClient.create(uri.toASCIIString());
        final String html = client.get(String.class);

        jars.addAll(parseJars(uri, html));
        for (final URI child : parseSubDirectories(uri, html)) {
            walk(jars, child);
        }
    }

    public List<URI> getDirectories(final URI uri) {
        final WebClient client = WebClient.create(uri.toASCIIString());
        final String html = client.get(String.class);
        return parseSubDirectories(uri, html);
    }

    public List<URI> parseSubDirectories(final URI uri, final String html) {
        return (List<URI>) Stream.of(html.split("\n"))
                .filter(s -> s.contains("/\""))
                .filter(s -> !s.contains("../\""))
                .map(s -> s.replaceAll(".*href=\"([^\"]+)\".*", "$1"))
                .map(uri::resolve)
                .collect(Collectors.toList());
    }

    public List<URI> getJars(final URI uri) {
        final WebClient client = WebClient.create(uri.toASCIIString());
        final String html = client.get(String.class);
        return parseJars(uri, html);
    }

    public List<URI> parseJars(final URI uri, final String html) {
        return (List<URI>) Stream.of(html.split("\n"))
                .filter(s -> s.contains("jar\""))
                .filter(s -> !s.contains("-javadoc.jar"))
                .filter(s -> !s.contains("-sources.jar"))
                .map(s -> s.replaceAll(".*href=\"([^\"]+)\".*", "$1"))
                .map(uri::resolve)
                .collect(Collectors.toList());
    }

    private File getDownloaded(final URI uri) {
        final File file = getFile(uri);

        if (file.exists()) return file;

        return download(uri);
    }

    private File download(final URI uri) {
        final WebClient client = WebClient.create(uri.toASCIIString());
        final Response response = client.get();
        assertEquals(200, response.getStatus());

        final InputStream entity = (InputStream) response.getEntity();
        final BufferedInputStream buffer = new BufferedInputStream(entity);

        Files.mkdir(downloads);

        final File file = getFile(uri);

        System.out.printf("Downloading: %s%n", file.getAbsolutePath());

        try {
            IO.copy(buffer, file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return file;
    }


    private File getFile(final URI uri) {
        return new File(downloads, new File(uri.getPath()).getName());
    }

}
