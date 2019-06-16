/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.wg.jakartaee.deps;

import org.eclipse.wg.jakartaee.Resources;
import org.eclipse.wg.jakartaee.repos.Path;
import org.eclipse.wg.jakartaee.repos.Paths;
import org.junit.Assert;
import org.junit.Test;
import org.tomitribe.util.Join;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class GroupingTest extends Assert {

    @Test
    public void projects() throws Exception {
        final String[] split = Resources.load("javax-files.txt").split("\n");
        final List<String> paths = Arrays.asList(split);

        final List<String> names = Paths.projects(paths);

        assertEquals(Resources.load("javax-projects.txt").trim(), Join.join("\n", names).trim());

    }

    @Test
    public void projectJavaxFiles() throws Exception {
        final String[] split = Resources.load("source-files.txt").split("\n");
        final List<String> paths = Arrays.asList(split);

        final Map<String, List<Path>> projects = paths.stream()
                .filter(Paths::isJavax)
                .filter(Paths::isMain)
                .map(Paths::basedir)
                .collect(Collectors.groupingBy(Path::getLabel));

        projects.keySet().stream()
                .sorted()
                .forEach(s -> {print(s, projects.get(s)); });
    }

    private void print(final String s, final List<Path> paths) {
        System.out.println(s);
        paths.stream()
                .map(Path::getPath)
                .map(Paths::toPackageName)
                .distinct()
                .forEach(packageName -> System.out.printf(" - %s%n", packageName));
        System.out.println();
    }

    @Test
    public void projectSummary() throws Exception {
        final String[] split = Resources.load("source-files.txt").split("\n");
        final List<String> paths = Arrays.asList(split);

        final Map<String, List<Path>> projects = paths.stream()
                .map(Paths::basedir)
                .collect(Collectors.groupingBy(Path::getLabel));

        final Set<Map.Entry<String, List<Path>>> entries = projects.entrySet();
        for (final Map.Entry<String, List<Path>> project : entries) {
            System.out.printf("## %s", project.getKey());

            final List<String> sources = project.getValue().stream().map(Path::getPath).collect(Collectors.toList());

            final List<String> javaxSourcs = sources.stream()
                    .filter(Paths::isJavax)
                    .collect(Collectors.toList());

            final List<String> otherSourcs = sources.stream()
                    .filter(Paths::isNonJavax)
                    .collect(Collectors.toList());

            final List<String> javaxMain = Paths.main(javaxSourcs);
            final List<String> javaxTest = Paths.main(javaxSourcs);
            final List<String> otherMain = Paths.main(otherSourcs);
            final List<String> otherTest = Paths.main(otherSourcs);

            System.out.printf("- javax { %s main, %s test} ", javaxMain.size(), javaxTest.size());
            System.out.printf("- other { %s main, %s test}%n", otherMain.size(), otherTest.size());
            System.out.println();
        }

    }

    @Test
    public void filesByProject() throws Exception {
        final String[] split = Resources.load("source-files.txt").split("\n");
        final List<String> paths = Arrays.asList(split);

        final Map<String, List<Path>> projects = paths.stream()
                .map(Paths::basedir)
                .collect(Collectors.groupingBy(Path::getLabel));

        final Set<Map.Entry<String, List<Path>>> entries = projects.entrySet();
        for (final Map.Entry<String, List<Path>> project : entries) {
            System.out.printf("## %s%n", project.getKey());

            final List<String> sources = project.getValue().stream().map(Path::getPath).collect(Collectors.toList());

            final List<String> javaxSourcs = sources.stream()
                    .filter(Paths::isJavax)
                    .collect(Collectors.toList());

            final List<String> otherSourcs = sources.stream()
                    .filter(Paths::isNonJavax)
                    .collect(Collectors.toList());

            final List<String> javaxMain = Paths.main(javaxSourcs);
            final List<String> javaxTest = Paths.main(javaxSourcs);
            final List<String> otherMain = Paths.main(otherSourcs);
            final List<String> otherTest = Paths.main(otherSourcs);

            System.out.printf("- javax { %s main, %s test}%n", javaxMain.size(), javaxTest.size());
            System.out.printf("- other { %s main, %s test}%n", otherMain.size(), otherTest.size());
            System.out.println();
        }

    }

}
