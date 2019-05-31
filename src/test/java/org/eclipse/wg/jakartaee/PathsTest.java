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

public class PathsTest extends Assert {

    @Test
    public void test() throws Exception {

        final String[] split = load("javax-files.txt").split("\n");
        final List<String> paths = Arrays.asList(split);

        assertEquals(3271, paths.size());

        assertEquals(1339, tests(paths).size());
        assertEquals(1932, main(paths).size());
        assertEquals(paths.size(), main(paths).size() + tests(paths).size());

        assertEquals(1932, Paths.classes(main(paths)).size());

        assertEquals(load("javax-main-classes.txt").trim(), Join.join("\n", Paths.classes(main(paths))).trim());
        assertEquals(load("javax-test-classes.txt").trim(), Join.join("\n", Paths.classes(tests(paths))).trim());

        paths.stream()
                .filter(Paths::isMain)
                .map(Paths::toClassName)
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

    private Predicate<? super String> isMain() {
        return Paths.isTest().negate();
    }

    private List<String> tests(final List<String> paths) {
        return paths.stream().filter(Paths::isTest).collect(Collectors.toList());
    }

    private List<String> main(final List<String> paths) {
        return paths.stream().filter(Paths::isMain).collect(Collectors.toList());
    }

}
