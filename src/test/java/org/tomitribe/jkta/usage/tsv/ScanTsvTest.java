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

import org.junit.Ignore;
import org.junit.Test;
import org.tomitribe.jkta.usage.Jar;
import org.tomitribe.jkta.usage.Usage;
import org.tomitribe.util.IO;
import org.tomitribe.util.PrintString;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.tomitribe.jkta.Resources.load;

public class ScanTsvTest {

    @Test
    public void version8() throws Exception {
        final List<Usage<Jar>> usages = ScanTsv.fromJarTsv(IO.read(load("scan-v0.8.tsv")))
                .collect(Collectors.toList());
        assertEquals(32, usages.size());

        final Usage<Jar> usage = usages.get(12);
        assertEquals("83997d867ea54f5e555a0865957d975875c5aa2d", usage.getContext().getSha1());
        assertEquals(new File("lib/catalina.jar"), usage.getContext().getJar());
        assertEquals(1588703994000L, usage.getContext().getLastModified());
        assertEquals(1588729158000L, usage.getContext().getInternalDate());
        assertEquals(689, usage.getContext().getClasses());
        assertEquals(1666821, usage.getContext().getSize());
        assertArrayEquals(new int[]{52}, usage.getContext().getJavaVersions());

        assertEquals(125, usage.getJavax());
        assertEquals(5465, usage.getJakarta());

        final String jars = usages.stream()
                .map(jarUsage -> jarUsage.getContext().getJar().getName())
                .reduce((s, s2) -> s + "\n" + s2).get();

        assertEquals("" +
                "tomcat-i18n-ko.jar\n" +
                "el-api.jar\n" +
                "tomcat-i18n-es.jar\n" +
                "tomcat-websocket.jar\n" +
                "jasper.jar\n" +
                "jasper-el.jar\n" +
                "tomcat-util.jar\n" +
                "tomcat-i18n-de.jar\n" +
                "catalina-storeconfig.jar\n" +
                "jsp-api.jar\n" +
                "catalina-tribes.jar\n" +
                "tomcat-i18n-cs.jar\n" +
                "catalina.jar\n" +
                "tomcat-jni.jar\n" +
                "tomcat-i18n-pt-BR.jar\n" +
                "catalina-ssi.jar\n" +
                "websocket-api.jar\n" +
                "tomcat-coyote.jar\n" +
                "catalina-ha.jar\n" +
                "tomcat-api.jar\n" +
                "ecj-4.15.jar\n" +
                "annotations-api.jar\n" +
                "jaspic-api.jar\n" +
                "tomcat-i18n-zh-CN.jar\n" +
                "catalina-ant.jar\n" +
                "servlet-api.jar\n" +
                "tomcat-util-scan.jar\n" +
                "tomcat-i18n-ja.jar\n" +
                "tomcat-i18n-ru.jar\n" +
                "tomcat-jdbc.jar\n" +
                "tomcat-i18n-fr.jar\n" +
                "tomcat-dbcp.jar", jars
        );

    }

    @Test
    public void version7() throws Exception {
        final String content = load("scan-v0.7.tsv");
        final List<Usage<Jar>> usages = ScanTsv.fromJarTsv(IO.read(content))
                .collect(Collectors.toList());
        assertEquals(9, usages.size());

        final Usage<Jar> usage = usages.get(0);
        assertEquals("49a8e05010566bd513e162238ca4ca2d2b0a7fea", usage.getContext().getSha1());
        assertEquals(new File("org/tomitribe/jkta/jkta/0.7/jkta-0.7.jar"), usage.getContext().getJar());
        assertEquals(1590789046000L, usage.getContext().getLastModified());
        assertEquals(1590763808000L, usage.getContext().getInternalDate());
        assertEquals(29038, usage.getContext().getClasses());
        assertEquals(50668175, usage.getContext().getSize());
        assertArrayEquals(new int[]{47}, usage.getContext().getJavaVersions());

        assertEquals(96657, usage.getJavax());
        assertEquals(0, usage.getJakarta());

        final String jars = usages.stream()
                .map(jarUsage -> jarUsage.getContext().getJar().getName())
                .reduce((s, s2) -> s + "\n" + s2).get();

        assertEquals("" +
                "jkta-0.7.jar\n" +
                "jkta-0.7-javadoc.jar\n" +
                "jkta-0.7-sources.jar\n" +
                "google-api-services-file-v1-rev20200520-1.30.9.jar\n" +
                "google-api-services-dns-v2beta1-rev20200515-1.30.9.jar\n" +
                "google-api-services-file-v1-rev20200520-1.30.9-javadoc.jar\n" +
                "google-api-services-file-v1-rev20200520-1.30.9-sources.jar\n" +
                "google-api-services-dns-v2beta1-rev20200515-1.30.9-javadoc.jar\n" +
                "google-api-services-dns-v2beta1-rev20200515-1.30.9-sources.jar", jars
        );

        assertConvertedTsv(usages, "scan-v0.7-converted.tsv");
    }

    @Test
    public void version6() throws Exception {
        final String content = load("scan-v0.6.tsv");
        final List<Usage<Jar>> usages = ScanTsv.fromJarTsv(IO.read(content))
                .collect(Collectors.toList());
        assertEquals(27, usages.size());

        final Usage<Jar> usage = usages.get(10);
        assertEquals(new File("org/catools/tms.etl/0.1.32/tms.etl-0.1.32.jar"), usage.getContext().getJar());
        assertEquals("d393ef6a82b25cdf5d70980d97683aa0d8f6a036", usage.getContext().getSha1());
        assertEquals(1590789559000L, usage.getContext().getLastModified());
        assertEquals(1590701910000L, usage.getContext().getInternalDate());
        assertEquals(38, usage.getContext().getClasses());
        assertEquals(60156, usage.getContext().getSize());
        assertArrayEquals(new int[]{}, usage.getContext().getJavaVersions());

        assertEquals(275, usage.getJavax());
        assertEquals(0, usage.getJakarta());

        final String jars = usages.stream()
                .map(jarUsage -> jarUsage.getContext().getJar().getName())
                .reduce((s, s2) -> s + "\n" + s2).get();

        assertEquals("base-2.1.1.jar\n" +
                "ws-0.1.32.jar\n" +
                "sql-0.1.32.jar\n" +
                "media-0.1.32.jar\n" +
                "base-2.1.1-javadoc.jar\n" +
                "base-2.1.1-sources.jar\n" +
                "ws-0.1.32-javadoc.jar\n" +
                "ws-0.1.32-sources.jar\n" +
                "sql-0.1.32-javadoc.jar\n" +
                "sql-0.1.32-sources.jar\n" +
                "tms.etl-0.1.32.jar\n" +
                "web.axe-0.1.32.jar\n" +
                "ws.core-0.1.32.jar\n" +
                "java-path-0.2.2.jar\n" +
                "media-0.1.32-javadoc.jar\n" +
                "media-0.1.32-sources.jar\n" +
                "web.table-0.1.32.jar\n" +
                "web.driver-0.1.32.jar\n" +
                "tms.etl-0.1.32-javadoc.jar\n" +
                "tms.etl-0.1.32-sources.jar\n" +
                "web.axe-0.1.32-javadoc.jar\n" +
                "web.axe-0.1.32-sources.jar\n" +
                "web.element-0.1.32.jar\n" +
                "ws.core-0.1.32-javadoc.jar\n" +
                "ws.core-0.1.32-sources.jar\n" +
                "java-path-0.2.2-javadoc.jar\n" +
                "java-path-0.2.2-sources.jar", jars
        );

        assertConvertedTsv(usages, "scan-v0.6-converted.tsv");
    }

    @Test
    public void version5() throws Exception {
        final String content = load("scan-v0.5.tsv");
        final List<Usage<Jar>> usages = ScanTsv.fromJarTsv(IO.read(content))
                .collect(Collectors.toList());
        assertEquals(14, usages.size());

        final Usage<Jar> usage = usages.get(3);
        assertEquals(new File("com/fortitudetec/elucidation-bundle/2.1.0/elucidation-bundle-2.1.0.jar"), usage.getContext().getJar());
        assertEquals("8b89f22aa16858cc54bdc6a2f525b4b6510bec63", usage.getContext().getSha1());
        assertEquals(1590779360000L, usage.getContext().getLastModified());
        assertEquals(0, usage.getContext().getInternalDate());
        assertEquals(-1, usage.getContext().getClasses());
        assertEquals(-1, usage.getContext().getSize());
        assertArrayEquals(new int[]{}, usage.getContext().getJavaVersions());

        assertEquals(91, usage.getJavax());
        assertEquals(0, usage.getJakarta());

        final String jars = usages.stream()
                .map(jarUsage -> jarUsage.getContext().getJar().getName())
                .reduce((s, s2) -> s + "\n" + s2).get();

        assertEquals("curses4j-1.1.0.jar\n" +
                "curses4j-1.1.0-javadoc.jar\n" +
                "curses4j-1.1.0-sources.jar\n" +
                "elucidation-bundle-2.1.0.jar\n" +
                "elucidation-client-2.1.0.jar\n" +
                "elucidation-common-2.1.0.jar\n" +
                "elucidation-common-2.1.0-tests.jar\n" +
                "elucidation-bundle-2.1.0-javadoc.jar\n" +
                "elucidation-bundle-2.1.0-sources.jar\n" +
                "elucidation-client-2.1.0-javadoc.jar\n" +
                "elucidation-client-2.1.0-sources.jar\n" +
                "elucidation-common-2.1.0-javadoc.jar\n" +
                "elucidation-common-2.1.0-sources.jar\n" +
                "elucidation-common-2.1.0-test-sources.jar", jars
        );

        assertConvertedTsv(usages, "scan-v0.5-converted.tsv");
    }

    /**
     * Asserts the expected results when an older tsv is written back out into the newer format
     */
    private void assertConvertedTsv(final List<Usage<Jar>> usages, final String name) throws IOException {
        final String expected = load(name);
        final PrintString out = new PrintString();
        ScanTsv.toJarTsv(out, usages.stream(), new File(""));
        final String actual = out.toString();

        assertEquals(expected, normalize(actual));
    }


    /**
     * Our summary line at the end uses the current system time.  We must zero that out
     * or our test will not pass as this always changes.
     */
    public static String normalize(final String report) {
        return report.replaceAll(
                "0000000000000000000000000000000000000000\t[0-9]{13}\t[0-9]{13}\t",
                "0000000000000000000000000000000000000000\t0000000000000\t0000000000000\t");
    }
}
