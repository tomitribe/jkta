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
package org.tomitribe.jkta.deps;

import org.apache.openejb.loader.IO;
import org.apache.openejb.util.Join;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransitiveJson {

    private final List<Jar> apis;
    private final Map<String, Jar> classes = new HashMap<>();

    public TransitiveJson() throws FileNotFoundException {
        apis = Stream.of(Src.main().resources().deps().split().dir().listFiles())
                .filter(file1 -> file1.getName().endsWith(".json"))
                .map(Jars::fromJson)
                .collect(Collectors.toList());

        for (final Jar api : apis) {
            for (final Clazz clazz : api.getClasses()) {
                classes.put(clazz.getName(), api);
            }
        }

//        out = System.out;
    }

    public static void main(String[] args) throws Exception {
        new TransitiveJson().main();
    }

    private void main() throws Exception {

        final List<Analysis> results = apis.stream()
                .map(this::analyze)
                .sorted((o1, o2) -> o1.api.getName().compareTo(o2.api.getName()))
                .collect(Collectors.toList());

//        dependenciesJson(results);
//        dependenciesEnumJson(results);
        hoverJson(results);
//        enumJava(results);
    }

    private void enumJava(final List<Analysis> results) {
        for (final Analysis result : results) {
            final String name = result.api.getName();
            final String enumName = enumName(result.api);
            System.out.printf("%s(\"%s\"),%n", enumName, name);
        }
    }

    private static String enumName(final Jar api) {
        return api.getName().toUpperCase().replace('.', '_');
    }

    private void hoverJson(final List<Analysis> results) throws FileNotFoundException {
        final JsonObjectBuilder json = Json.createObjectBuilder();

        for (final Analysis analysis : results) {
            final List<String> list = analysis.affected.stream()
                    .map(Jar::getName)
                    .collect(Collectors.toList());

            final List<String> grey = new ArrayList<String>();
            final List<String> orange = new ArrayList<String>();

            grey.addAll(list);
            grey.remove(analysis.api.getName());

            for (final Analysis a : results) {
                if (a.affected.contains(analysis.api)) {
                    orange.add(a.api.getName());
                }
            }

//            final JsonObjectBuilder hover = Json.createObjectBuilder();
//            hover.add("grey", Json.createArrayBuilder(grey));
//            hover.add("orange", Json.createArrayBuilder(orange));
//            json.add(analysis.api.getName(), hover);
//            final JsonObjectBuilder hover = Json.createObjectBuilder();
//            hover.add("grey", Json.createArrayBuilder(grey));
//            hover.add("orange", Json.createArrayBuilder(orange));
            if (grey.size() > 0) {
                json.add(analysis.api.getName(), String.format("X causes %s packages to be renamed", grey.size()));
            } else {
                json.add(analysis.api.getName(), "X");
            }
        }

        final PrintStream out = new PrintStream(IO.write(new File("/tmp/hover.json")));
        final JsonObject jsonObject = json.build();
        final JsonWriter writer = Json.createWriter(out);
        writer.writeObject(jsonObject);
        out.close();
    }

    private void dependenciesJson(final List<Analysis> results) throws FileNotFoundException {
        final JsonObjectBuilder json = Json.createObjectBuilder();

        for (final Analysis analysis : results) {
            final List<String> list = analysis.affected.stream()
                    .map(Jar::getName)
                    .collect(Collectors.toList());
            json.add(analysis.api.getName(), Json.createArrayBuilder(list));
        }

        final PrintStream out = new PrintStream(IO.write(new File("/tmp/dependencies.json")));
        final JsonObject jsonObject = json.build();
        final JsonWriter writer = Json.createWriter(out);
        writer.writeObject(jsonObject);
        out.close();
    }

    private void dependenciesEnumJson(final List<Analysis> results) throws FileNotFoundException {
        final JsonObjectBuilder json = Json.createObjectBuilder();

        for (final Analysis analysis : results) {
            final List<Jar> affected = new ArrayList<>(analysis.affected);
            affected.remove(analysis.api);

            if (affected.size() == 0) continue;

            affected.remove(analysis.api);
            System.out.printf("%s.references.addAll(Arrays.asList(%n%s%n));%n",
                    enumName(analysis.api),
                    Join.join(",\n", TransitiveJson::enumName, affected)
            );
        }

        final PrintStream out = new PrintStream(IO.write(new File("/tmp/dependencies.json")));
        final JsonObject jsonObject = json.build();
        final JsonWriter writer = Json.createWriter(out);
        writer.writeObject(jsonObject);
        out.close();
    }

    private Analysis analyze(final Jar current) {
        final Analysis analysis = new Analysis(current);
        final List<Jar> affected = analysis.affected;
        final ArrayList<String> reasons = analysis.reasons;

        follow(current, affected, reasons);

        return analysis;
    }

    private void follow(final Jar current, final List<Jar> affected, final List<String> reasons) {
        if (current == null) return;
        if (affected.contains(current)) return;
        affected.add(current);

        for (final Jar api : apis) {
            // skip ourselves
            if (api.getName().equals(current.getName())) continue;

            for (final Clazz clazz : api.getClasses()) {

                final Set<String> references = new HashSet<>(clazz.getReferences());

                for (final String ref : references) {
                    if (!current.getClasses().contains(new Clazz(ref))) continue;

                    reasons.add(String.format("%s -> %s", clazz.getName(), ref));

                    if (affected.contains(api)) continue;

                    follow(api, affected, reasons);
                }
            }
        }
    }

    public static class Analysis {
        private final Jar api;
        private final List<Jar> affected = new ArrayList<>();
        private final ArrayList<String> reasons = new ArrayList<>();

        public Analysis(final Jar api) {
            this.api = api;
        }

        public List<Clazz> getClasses() {
            return Stream.concat(Stream.of(api), affected.stream())
                    .map(Jar::getClasses)
                    .flatMap(Collection::stream)
                    .distinct()
                    .collect(Collectors.toList());
        }
    }
}
