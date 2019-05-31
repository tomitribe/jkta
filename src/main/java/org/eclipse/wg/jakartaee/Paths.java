/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.eclipse.wg.jakartaee;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Paths {
    static String toClassName(final String s) {
        return s.replaceAll(".*/(javax.*).java", "$1")
                .replace('/', '.');
    }

    static List<String> classes(final List<String> paths) {
        return paths.stream().map(Paths::toClassName).collect(Collectors.toList());
    }

    public static boolean isMain(final String path) {
        return isTest().negate().test(path);
    }

    public static boolean isTest(final String path) {
        return isTest().test(path);
    }

    public static boolean isJavax(final String path) {
        return isJavax().test(path);
    }

    public static boolean isNonJavax(final String path) {
        return isNonJavax().test(path);
    }

    public static Predicate<? super String> isJavax() {
        return s -> s.contains("/javax/");
    }

    public static Predicate<? super String> isNonJavax() {
        return isJavax().negate();
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
