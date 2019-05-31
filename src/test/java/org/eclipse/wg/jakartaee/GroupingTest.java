/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.eclipse.wg.jakartaee;

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
    public void filesByProjects() throws Exception {
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
