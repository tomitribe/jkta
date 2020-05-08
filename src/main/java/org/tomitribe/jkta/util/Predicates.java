package org.tomitribe.jkta.util;

import java.io.File;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class Predicates {
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
