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
package org.tomitribe.jkta.usage.tsv;

import org.junit.Test;
import org.tomitribe.jkta.Resources;
import org.tomitribe.jkta.usage.Jar;
import org.tomitribe.jkta.usage.Package;
import org.tomitribe.jkta.usage.PackageUsage;
import org.tomitribe.jkta.usage.Scan;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.tomitribe.jkta.usage.tsv.ScanTsvTest.normalize;

public class JarTsv5Test {

    @Test
    public void heading() throws Exception {
        final JarTsv5 tsv = new JarTsv5();
        assertEquals(Resources.load("headers/v7.tsv"), tsv.heading());
    }

    @Test
    public void write() {
        final Jar jar = new Jar(new File("/foo/bar"), "12345678901234567890123456789", 12345L, 34567L, 67, 5555, new int[]{34, 56});
        final PackageUsage<Jar> usage = new PackageUsage<>(jar);

        Package.names().forEach(usage::test);
        Package.names().filter(s -> s.contains("e")).forEach(usage::test);

        final JarTsv5 tsv7 = new JarTsv5();
        final String actual = tsv7.write(usage);
        assertEquals("12345678901234567890123456789\t12345\t34567\t5555\t67\t34,56\t/foo/bar" +
                "\t55\t48\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t" +
                "2\t1\t1\t2\t1\t1\t2\t1\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2" +
                "\t2\t2\t2\t2\t2\t1\t1\t2\t1\t1\t1\t1", actual);
    }

    @Test
    public void read() {

        final PackageUsage<Jar> actual = new JarTsv5().read("8b89f22aa16858cc54bdc6a2f525b4b6510bec63\t1590779360000\t" +
                "com/fortitudetec/elucidation-bundle/2.1.0/elucidation-bundle-2.1.0.jar\t91\t0\t0\t1\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t" +
                "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t3\t0\t87\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0" +
                "\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0");

        assertEquals("8b89f22aa16858cc54bdc6a2f525b4b6510bec63", actual.getContext().getSha1());
        assertEquals(new File("com/fortitudetec/elucidation-bundle/2.1.0/elucidation-bundle-2.1.0.jar"), actual.getContext().getJar());
        assertEquals(1590779360000L, actual.getContext().getLastModified());
        assertEquals(0, actual.getContext().getInternalDate());
        assertEquals(-1, actual.getContext().getClasses());
        assertEquals(-1, actual.getContext().getSize());
        assertArrayEquals(new int[]{}, actual.getContext().getJavaVersions());

        assertEquals(91, actual.getJavax());
        assertEquals(0, actual.getJakarta());

        final List<Package> expected = new ArrayList<>();
        expected.add(Package.JAVAX_ANNOTATION);
        for (int i = 0; i < 3; i++) expected.add(Package.JAVAX_VALIDATION);
        for (int i = 0; i < 87; i++) expected.add(Package.JAVAX_WS_RS);
        Scan.assertUsage(actual, expected.toArray(new Package[]{}));
    }

    @Test
    public void summary() {

        final PackageUsage<Jar> usage = new JarTsv5().read("8b89f22aa16858cc54bdc6a2f525b4b6510bec63\t1590779360000\t" +
                "com/fortitudetec/elucidation-bundle/2.1.0/elucidation-bundle-2.1.0.jar\t91\t0\t0\t1\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t" +
                "0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t3\t0\t87\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0" +
                "\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0");

        final JarTsv5 tsv = new JarTsv5();

        tsv.write(usage);

        assertEquals("0000000000000000000000000000000000000000\t0000000000000\t0000000000000\t0\t0\t0\t" +
                "total affected 100% (1 of 1 scanned)\t91\t0\t0\t1\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0" +
                "\t0\t3\t0\t87\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0", normalize(tsv.summary()));

        tsv.write(usage);

        assertEquals("0000000000000000000000000000000000000000\t0000000000000\t0000000000000\t0\t0\t0\t" +
                "total affected 100% (2 of 2 scanned)\t182\t0\t0\t2\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0" +
                "\t0\t6\t0\t174\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0\t0", normalize(tsv.summary()));
    }

}
