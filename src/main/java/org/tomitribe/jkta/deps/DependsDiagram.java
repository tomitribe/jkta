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

public class DependsDiagram {

    private final List<Jar> apis;
    private final Map<String, Jar> classes = new HashMap<>();
    private final PrintStream out;

    public DependsDiagram(final File file) throws FileNotFoundException {
        apis = Stream.of(Src.main().resources().deps().split().dir().listFiles())
                .map(Jars::fromJson)
                .collect(Collectors.toList());

        for (final Jar api : apis) {
            for (final Clazz clazz : api.getClasses()) {
                classes.put(clazz.getName(), api);
            }
        }

        out = new PrintStream(IO.write(file));
    }

    public static void main(String[] args) throws Exception {
        new DependsDiagram(new File("/tmp/depends.adoc")).main();
    }

    private void main() throws Exception {

        final List<Analysis> results = apis.stream()
                .map(this::analyze)
                .collect(Collectors.toList());

        final List<Analysis> simple = results.stream()
                .filter(analysis -> analysis.affected.size() == 1)
                .collect(Collectors.toList());

        out.printf("@startuml\n");
        for (Jar api : apis) {
            long trouve = simple.stream()
                    .map(analysis -> analysis.api)
                    .filter(a -> a.getName().equals(api.getName()))
                    .count();
            if (trouve == 1) {
                out.printf("[%s] #SeaGreen\n", api.getName());
            } else {
                out.printf("[%s]\n", api.getName());
            }
            out.printf("note top : %d classes. \n", api.getClasses().size(), api.getName());
        }

        for (Jar api : apis) {
            // Construct List for remove duplicate entry
            Set<String> dependsComponentList = new HashSet<>();
            Set<String> dependsClassList = new HashSet<>();
            List<String> references = api.getReferences();
            for (String reference : references) {
                Jar dependJar = classes.get(reference);
                if (dependJar != null) {
                    if (!dependJar.getName().equals(api.getName())) { // Exclude self
                        dependsComponentList.add(dependJar.getName());
                    }
                } else {
                    dependsClassList.add(reference);
                }
            }
            for (String depend : dependsComponentList) {
                out.printf("[%s] --> [%s] : use\n", api.getName(), depend);
            }
//            for (String depend : dependsClassList) {
//                out.printf("[%s] --> %s : use\n", api.getName(), depend);
//            }
        }
        out.println("legend top left");
        out.println("   Jakarta EE");
        out.println("   Dependency relation between package");
        out.println("   green package - Rename package without consequence");
        out.println("   yellow package - Rename package with consequence");
        out.println("endlegend");

        out.println("@end\n");

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
