/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.eclipse.wg.jakartaee.repos;

import org.eclipse.wg.jakartaee.Resources;
import org.junit.Assert;
import org.junit.Test;
import org.tomitribe.util.Join;

import java.util.Arrays;
import java.util.List;

public class PathsTest extends Assert {

    @Test
    public void test() throws Exception {

        final String[] split = Resources.load("javax-files.txt").split("\n");
        final List<String> paths = Arrays.asList(split);

        assertEquals(3271, paths.size());

        assertEquals(1339, Paths.tests(paths).size());
        assertEquals(1932, Paths.main(paths).size());
        assertEquals(paths.size(), Paths.main(paths).size() + Paths.tests(paths).size());

        assertEquals(1932, Paths.classes(Paths.main(paths)).size());

        assertEquals(Resources.load("javax-main-classes.txt").trim(), Join.join("\n", Paths.classes(Paths.main(paths))).trim());
        assertEquals(Resources.load("javax-test-classes.txt").trim(), Join.join("\n", Paths.classes(Paths.tests(paths))).trim());

        assertEquals(Resources.load("javax-main-packages.txt").trim(), Join.join("\n", Paths.packages(Paths.main(paths))).trim());

    }

}
