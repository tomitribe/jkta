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
package org.tomitribe.jakartaee.analysis.repos;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class Paths {

    public static String toClassName(final String s) {
        return s.replaceAll(".*/(javax.*).java", "$1")
                .replace('/', '.');
    }

    public static String toPackageName(final String s) {
        return s.replaceAll(".*/(javax.*)/[^/]+.java", "$1")
                .replace('/', '.');
    }

    public static List<String> packages(final List<String> paths) {
        return paths.stream().map(Paths::toPackageName).distinct().collect(Collectors.toList());
    }

    public static List<String> classes(final List<String> paths) {
        return paths.stream().map(Paths::toClassName).collect(Collectors.toList());
    }

    public static boolean isMain(final File path) {
        return isTest().negate().test(path.toURI().getPath());
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
                .or(s -> s.contains("src/test/"))
                .or(s -> s.contains("src/oldtest/"))
                .or(s -> s.contains("/test/src/"))
                .or(s -> s.contains("/tests/"))
                ;
    }

    public static Predicate<? super String> isMain() {
        return isTest().negate();
    }

    public static List<String> tests(final List<String> paths) {
        return paths.stream().filter(Paths::isTest).collect(Collectors.toList());
    }

    public static List<String> main(final List<String> paths) {
        return paths.stream().filter(Paths::isMain).collect(Collectors.toList());
    }

    public static List<String> projects(final List<String> paths) {
        final Map<String, List<Path>> groups = paths.stream()
                .map(Paths::basedir)
                .collect(Collectors.groupingBy(Path::getLabel));

        return (List<String>) groups.keySet().stream()
                .sorted()
                .collect(Collectors.toList());
    }

    public static Path basedir(final String path) {
        final int sep = path.indexOf('/');
        final String dir = path.substring(0, sep);
        final String remaining = path.substring(sep+1, path.length());
        return new Path(remaining, dir);
    }

    public static boolean isNonStandard(final String className) {
        return !isStandard(className);
    }

    public static boolean isStandard(final String className) {
        if (className.startsWith("java.")) return true;
        if (className.startsWith("javax.")) return true;
        if (className.startsWith("org.w3c.")) return true;
        if (className.startsWith("org.xml.")) return true;
        if (className.startsWith("org.omg.")) return true;
        return false;
    }
}
