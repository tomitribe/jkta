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
package org.tomitribe.jakartaee.analysis.deps;

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

public class Transitive {

    private final List<Jar> apis;
    private final Map<String, Jar> classes = new HashMap<>();
    private final PrintStream out;

    public Transitive(final File file) throws FileNotFoundException {
        apis = Stream.of(Src.main().resources().deps().split().dir().listFiles())
                .map(Jars::fromJson)
                .collect(Collectors.toList());

        for (final Jar api : apis) {
            for (final Clazz clazz : api.getClasses()) {
                classes.put(clazz.getName(), api);
            }
        }

        out = new PrintStream(IO.write(file));
//        out = System.out;
    }

    public static void main(String[] args) throws Exception {
        new Transitive(new File("/Users/dblevins/work/eclipse-ee4j/jakartaee-platform/namespace/transitive.adoc")).main();
    }

    private void main() throws Exception {

        final List<Analysis> results = apis.stream()
                .map(this::analyze)
                .collect(Collectors.toList());

        final List<Analysis> simple = results.stream()
                .filter(analysis -> analysis.affected.size() == 1)
                .collect(Collectors.toList());

        final List<Analysis> complex = results.stream()
                .filter(analysis -> analysis.affected.size() > 1)
                .sorted((o1, o2) -> Integer.compare(o1.getClasses().size(), o2.getClasses().size()))
                .sorted((o1, o2) -> Integer.compare(o1.affected.size(), o2.affected.size()))
                .collect(Collectors.toList());

        out.printf("= Simple%n%n");
        out.printf("The following %s apis can be renamed without " +
                "consequence to other javax packages%n%n", simple.size());

        for (final Analysis analysis : simple) {
            out.printf(" - %s%n", analysis.api.getName());
        }
        out.println();

        out.printf("= Complex%n%n");
        out.printf("The following %s apis if renamed will transitively " +
                "force other javax packages to be renamed%n", complex.size());
        out.println();

        for (final Analysis analysis : complex) {

            out.printf("== %s%n", analysis.api.getName());
            out.printf("%nForces the following %s package renames, %s classes total:%n%n", analysis.affected.size(), analysis.getClasses().size());

            for (final Jar jar : analysis.affected) {
                out.printf(" - %s%n", jar.getName());
            }

            out.printf("%nDue to the following %s transitive references:%n%n", analysis.reasons.size());

            for (final String reason : analysis.reasons) {
                out.printf(" - %s%n", reason);
            }
            out.println();
        }


//        final Jar current = apis.stream().filter(jar -> jar.getName().equals("javax.servlet")).findFirst().get();
//
//        analyze(current);

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
