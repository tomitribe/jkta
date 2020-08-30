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
package org.tomitribe.jkta.usage.tsv;

import org.tomitribe.jkta.usage.Jar;
import org.tomitribe.jkta.usage.Usage;
import org.tomitribe.util.IO;
import org.tomitribe.util.Join;
import org.tomitribe.util.hash.XxHash64;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ScanTsv {

    private ScanTsv() {
    }

    private static final long version5 = XxHash64.hash(load("headers/v5.tsv"));
    private static final long version6 = XxHash64.hash(load("headers/v6.tsv"));
    private static final long version7 = XxHash64.hash(load("headers/v7.tsv"));

    private static String load(final String resource) {
        final URL found = Thread.currentThread().getContextClassLoader().getResource(resource);
        if (found == null) throw new IllegalStateException(String.format("Missing resource: '%s'", resource));
        try {
            return IO.slurp(found);
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Unable to read resource: '%s'", resource), e);
        }
    }

    public static Format<Jar> jarFormatter(final File repository) {
        return new JarTsv7(repository);
    }

    public static void toJarTsv(final PrintStream out, final Stream<Usage<Jar>> usages, final File repository) {
        final org.tomitribe.jkta.usage.tsv.Format<Jar> formatter = jarFormatter(repository);
        out.println(formatter.heading());
        usages.map(formatter::write).forEach(out::println);
        out.println(formatter.summary());
    }

    public static Stream<Usage<Jar>> fromJarTsv(final InputStream content) {
        return fromJarTsv(content, s -> {
        });
    }

    public static Stream<Usage<Jar>> fromJarTsv(final InputStream content, final Consumer<String> failed) {
        final InputStreamReader reader = new InputStreamReader(content);
        final BufferedReader bufferedReader = new BufferedReader(reader);

        final String header = bufferedReader.lines()
                .limit(1)
                .collect(Collectors.toList()).get(0);

        final long version = XxHash64.hash(header);

        if (version == version7) {
            return bufferedReader.lines()
                    .filter(ScanTsv::skipFooter) // skip summary
                    .map(new JarTsv7(failed)::read)
                    .filter(Objects::nonNull);
        }

        if (version == version6) {
            return bufferedReader.lines()
                    .filter(ScanTsv::skipHeader) // skip header
                    .filter(ScanTsv::skipFooter) // skip summary
                    .map(new JarTsv6(failed)::read)
                    .filter(Objects::nonNull);
        }

        if (version == version5) {
            return bufferedReader.lines()
                    .filter(ScanTsv::skipHeader) // skip header
                    .filter(ScanTsv::skipFooter) // skip summary
                    .map(new JarTsv5(failed)::read)
                    .filter(Objects::nonNull);

        }

        throw new UnsupportedTsvFormatException();
    }

    private static boolean skipFooter(final String s) {
        return !s.startsWith("0000000000000000000000000000000000000000\t");
    }

    private static boolean skipHeader(final String s) {
        return !s.startsWith("SHA-1\t");
    }

    public static String toTsv(final Usage<Jar> jarUsage, final File parent) {
        final StringBuilder sb = new StringBuilder();

        final Jar jar = jarUsage.getContext();
        final String t = "\t";
        sb.append(jar.getSha1()).append(t);
        sb.append(jar.getLastModified()).append(t);
        sb.append(jar.getInternalDate()).append(t);
        sb.append(jar.getSize()).append(t);
        sb.append(jar.getClasses()).append(t);
        sb.append(versions(jar)).append(t);
        sb.append(childPath(parent, jar.getJar())).append(t);
        sb.append(jarUsage.toTsv());
        return sb.toString();
    }

    public static String versions(final Jar jar) {
        final int[] versions = jar.getJavaVersions();
        if (versions == null) return "0";
        if (versions.length == 0) return "0";
        if (versions.length == 1) return versions[0] + "";

        final List<String> strings = new ArrayList<>();
        for (final int version : versions) {
            strings.add(version + "");
        }

        return Join.join(",", strings);
    }

    public static String childPath(final File parent, final File file) {
        final String parentPath = parent.getAbsolutePath();
        final String childPath = file.getAbsolutePath();

        if (childPath.startsWith(parentPath)) {
            final int base = parentPath.length();
            return childPath.substring(base + 1);
        } else {
            return childPath;
        }
    }

    public static String tabbed(final Stream<?> stream) {
        return stream
                .map(s -> s + "")
                .reduce((s, s2) -> s + "\t" + s2)
                .get();
    }

    public static String tabbed(final Stream<?> stream, final Stream<?> stream2) {
        return tabbed(Stream.concat(stream, stream2));
    }

    public static int[] parseVersions(final String string) {
        final String[] strings = string.split(",");
        final int[] versions = new int[strings.length];
        for (int i = 0; i < strings.length; i++) {
            versions[i] = Integer.parseInt(strings[i]);
        }
        return versions;
    }
}
