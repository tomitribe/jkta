/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.eclipse.wg.jakartaee;

import org.junit.Assert;
import org.junit.Test;
import org.tomitribe.util.IO;
import org.tomitribe.util.Join;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class FilesTest extends Assert {

    @Test
    public void test() throws Exception {

        final String[] split = load("javax-files.txt").split("\n");
        final List<String> paths = Arrays.asList(split);

        assertEquals(3271, paths.size());

        assertEquals(1339, tests(paths).size());
        assertEquals(1932, main(paths).size());
        assertEquals(paths.size(), main(paths).size() + tests(paths).size());

        assertEquals(1932, classes(main(paths)).size());

        assertEquals(load("javax-main-classes.txt").trim(), Join.join("\n", classes(main(paths))).trim());
        assertEquals(load("javax-test-classes.txt").trim(), Join.join("\n", classes(tests(paths))).trim());

        paths.stream()
                .filter(FilesTest::isMain)
                .map(FilesTest::toClassName)
                .forEach(System.out::println);
    }

    private String load(final String name) throws IOException {
        return IO.slurp(find(name));
    }

    private URL find(final String name) {
        final URL resource = this.getClass().getClassLoader().getResource(name);
        if (resource == null) throw new IllegalStateException("Not found: " + name);
        return resource;
    }

    private static String toClassName(final String s) {
        return s.replaceAll(".*/(javax.*).java", "$1")
                .replace('/', '.');
    }

    private Predicate<? super String> isMain() {
        return isTest().negate();
    }

    private static List<String> classes(final List<String> paths) {
        return paths.stream().map(FilesTest::toClassName).collect(Collectors.toList());
    }

    private List<String> tests(final List<String> paths) {
        return paths.stream().filter(FilesTest::isTest).collect(Collectors.toList());
    }

    private List<String> main(final List<String> paths) {
        return paths.stream().filter(FilesTest::isMain).collect(Collectors.toList());
    }

    public static boolean isMain(final String path) {
        return isTest().negate().test(path);
    }

    public static boolean isTest(final String path) {
        return isTest().test(path);
    }

    public static Predicate<? super String> isTest() {
        final Predicate<String> test = s -> false;
        return test
                .or(s -> s.contains("/src/test/"))
                .or(s -> s.contains("/src/oldtest/"))
                .or(s -> s.contains("/test/src/"))
                .or(s -> s.contains("/tests/"))
                ;
    }
}
