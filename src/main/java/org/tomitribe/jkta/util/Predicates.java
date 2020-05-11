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
package org.tomitribe.jkta.util;

import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Predicates {

    private Predicates() {
    }

    public static <T> Predicate<T> when(final Predicate<T> predicate) {
        return predicate;
    }

    public static <T> Predicate<T> unless(final Predicate<T> predicate) {
        return predicate.negate();
    }

    public static <T> Predicate<T> not(final Predicate<T> predicate) {
        return predicate.negate();
    }

    public static <T> Predicate<T> equals(final Function<T, String> field, final String value) {
        return t -> field.apply(t).equals(value);
    }

    public static Predicate<File> endsWith(final String suffix) {
        return file -> file.getName().endsWith(suffix);
    }

    public static Predicate<File> exts(final String... suffixes) {
        return Stream.of(suffixes)
                .map(Predicates::endsWith)
                .reduce(Predicate::or)
                .orElse(file -> false);
    }

    public static Predicate<String> filter(final Pattern include, final Pattern exclude) {
        final Predicate<String> includes = include != null ? include.asPredicate() : s -> true;
        final Predicate<String> excludes = exclude != null ? exclude.asPredicate() : s -> false;

        return includes.and(not(excludes));
    }

    public static Predicate<File> fileFilter(final Pattern include, final Pattern exclude) {
        final Predicate<String> filter = filter(include, exclude);
        return file -> filter.test(file.getName());
    }
}
