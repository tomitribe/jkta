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
import org.tomitribe.jkta.usage.Usage;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class JarTsv7Test {

    @Test
    public void heading() throws Exception {
        final JarTsv7 tsv7 = new JarTsv7();
        assertEquals(Resources.load("headers/v7.tsv"), tsv7.heading());
    }

    @Test
    public void write() {
        final Jar jar = new Jar(new File("/foo/bar"), "12345678901234567890123456789", 12345L, 34567L, 67, 5555, new int[]{34, 56});
        final Usage<Jar> usage = new Usage<>(jar);

        Package.names().forEach(usage::test);
        Package.names().filter(s -> s.contains("e")).forEach(usage::test);

        final JarTsv7 tsv7 = new JarTsv7();
        final String actual = tsv7.write(usage);
        assertEquals("12345678901234567890123456789\t12345\t34567\t5555\t67\t34,56\t/foo/bar" +
                "\t55\t48\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t" +
                "2\t1\t1\t2\t1\t1\t2\t1\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2" +
                "\t2\t2\t2\t2\t2\t1\t1\t2\t1\t1\t1\t1", actual);
    }

    @Test
    public void read() {

        final Usage<Jar> actual = new JarTsv7().read("12345678901234567890123456789\t12345\t34567\t5555\t67\t34,56\t/foo/bar" +
                "\t55\t48\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t" +
                "2\t1\t1\t2\t1\t1\t2\t1\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2" +
                "\t2\t2\t2\t2\t2\t1\t1\t2\t1\t1\t1\t1");

        assertEquals("12345678901234567890123456789", actual.getContext().getSha1());
        assertEquals(new File("/foo/bar"), actual.getContext().getJar());
        assertEquals(12345, actual.getContext().getLastModified());
        assertEquals(34567, actual.getContext().getInternalDate());
        assertEquals(67, actual.getContext().getClasses());
        assertEquals(5555, actual.getContext().getSize());
        assertArrayEquals(new int[]{34, 56}, actual.getContext().getJavaVersions());

        assertEquals(55, actual.getJavax());
        assertEquals(48, actual.getJakarta());

        assertEquals(1, actual.get(Package.JAVAX_ACTIVATION));
        assertEquals(1, actual.get(Package.JAVAX_ANNOTATION));
        assertEquals(1, actual.get(Package.JAVAX_BATCH));
        assertEquals(2, actual.get(Package.JAVAX_DECORATOR));
        assertEquals(2, actual.get(Package.JAVAX_EJB));
        assertEquals(2, actual.get(Package.JAVAX_EL));
        assertEquals(2, actual.get(Package.JAVAX_ENTERPRISE));
        assertEquals(2, actual.get(Package.JAVAX_ENTERPRISE_CONCURRENT));
        assertEquals(2, actual.get(Package.JAVAX_ENTERPRISE_DEPLOY));
        assertEquals(2, actual.get(Package.JAVAX_FACES));
        assertEquals(2, actual.get(Package.JAVAX_INJECT));
        assertEquals(2, actual.get(Package.JAVAX_INTERCEPTOR));
        assertEquals(1, actual.get(Package.JAVAX_JMS));
        assertEquals(1, actual.get(Package.JAVAX_JSON));
        assertEquals(1, actual.get(Package.JAVAX_JSON_BIND));
        assertEquals(1, actual.get(Package.JAVAX_JWS));
        assertEquals(1, actual.get(Package.JAVAX_MAIL));
        assertEquals(2, actual.get(Package.JAVAX_MANAGEMENT_J2EE));
        assertEquals(2, actual.get(Package.JAVAX_PERSISTENCE));
        assertEquals(2, actual.get(Package.JAVAX_RESOURCE));
        assertEquals(2, actual.get(Package.JAVAX_SECURITY_AUTH_MESSAGE));
        assertEquals(2, actual.get(Package.JAVAX_SECURITY_ENTERPRISE));
        assertEquals(2, actual.get(Package.JAVAX_SECURITY_JACC));
        assertEquals(2, actual.get(Package.JAVAX_SERVLET));
        assertEquals(2, actual.get(Package.JAVAX_SERVLET_JSP));
        assertEquals(2, actual.get(Package.JAVAX_SERVLET_JSP_JSTL));
        assertEquals(1, actual.get(Package.JAVAX_TRANSACTION));
        assertEquals(1, actual.get(Package.JAVAX_VALIDATION));
        assertEquals(2, actual.get(Package.JAVAX_WEBSOCKET));
        assertEquals(1, actual.get(Package.JAVAX_WS_RS));
        assertEquals(1, actual.get(Package.JAVAX_XML_BIND));
        assertEquals(2, actual.get(Package.JAVAX_XML_REGISTRY));
        assertEquals(1, actual.get(Package.JAVAX_XML_RPC));
        assertEquals(1, actual.get(Package.JAVAX_XML_SOAP));
        assertEquals(1, actual.get(Package.JAVAX_XML_WS));
        assertEquals(1, actual.get(Package.JAKARTA_ACTIVATION));
        assertEquals(1, actual.get(Package.JAKARTA_ANNOTATION));
        assertEquals(1, actual.get(Package.JAKARTA_BATCH));
        assertEquals(2, actual.get(Package.JAKARTA_DECORATOR));
        assertEquals(2, actual.get(Package.JAKARTA_EJB));
        assertEquals(2, actual.get(Package.JAKARTA_EL));
        assertEquals(2, actual.get(Package.JAKARTA_ENTERPRISE));
        assertEquals(2, actual.get(Package.JAKARTA_ENTERPRISE_CONCURRENT));
        assertEquals(2, actual.get(Package.JAKARTA_FACES));
        assertEquals(2, actual.get(Package.JAKARTA_INJECT));
        assertEquals(2, actual.get(Package.JAKARTA_INTERCEPTOR));
        assertEquals(1, actual.get(Package.JAKARTA_JMS));
        assertEquals(1, actual.get(Package.JAKARTA_JSON));
        assertEquals(1, actual.get(Package.JAKARTA_JSON_BIND));
        assertEquals(1, actual.get(Package.JAKARTA_JWS));
        assertEquals(1, actual.get(Package.JAKARTA_MAIL));
        assertEquals(2, actual.get(Package.JAKARTA_PERSISTENCE));
        assertEquals(2, actual.get(Package.JAKARTA_RESOURCE));
        assertEquals(2, actual.get(Package.JAKARTA_SECURITY_AUTH_MESSAGE));
        assertEquals(2, actual.get(Package.JAKARTA_SECURITY_ENTERPRISE));
        assertEquals(2, actual.get(Package.JAKARTA_SECURITY_JACC));
        assertEquals(2, actual.get(Package.JAKARTA_SERVLET));
        assertEquals(2, actual.get(Package.JAKARTA_SERVLET_JSP));
        assertEquals(2, actual.get(Package.JAKARTA_SERVLET_JSP_JSTL));
        assertEquals(1, actual.get(Package.JAKARTA_TRANSACTION));
        assertEquals(1, actual.get(Package.JAKARTA_VALIDATION));
        assertEquals(2, actual.get(Package.JAKARTA_WEBSOCKET));
        assertEquals(1, actual.get(Package.JAKARTA_WS_RS));
        assertEquals(1, actual.get(Package.JAKARTA_XML_BIND));
        assertEquals(1, actual.get(Package.JAKARTA_XML_SOAP));
        assertEquals(1, actual.get(Package.JAKARTA_XML_WS));
    }

    @Test
    public void summary() {

        final Usage<Jar> usage = new JarTsv7().read("12345678901234567890123456789\t12345\t34567\t5555\t67\t34,56\t/foo/bar" +
                "\t55\t48\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t" +
                "2\t1\t1\t2\t1\t1\t2\t1\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1\t2\t2\t2" +
                "\t2\t2\t2\t2\t2\t1\t1\t2\t1\t1\t1\t1");

        final JarTsv7 tsv7 = new JarTsv7();
        tsv7.write(usage);

        assertEquals("0000000000000000000000000000000000000000\t0000000000000\t0000000000000\t5555\t67\t0\t" +
                "total affected 100% (1 of 1 scanned)\t55\t48\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1\t1" +
                "\t2\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t2\t1\t1\t2\t1\t1\t1\t1\t1\t1\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t1\t1" +
                "\t1\t2\t2\t2\t2\t2\t2\t2\t2\t1\t1\t2\t1\t1\t1\t1", ScanTsvTest.normalize(tsv7.summary()));

        tsv7.write(usage);
        assertEquals("0000000000000000000000000000000000000000\t0000000000000\t0000000000000\t11110\t134\t0\t" +
                "total affected 100% (2 of 2 scanned)\t110\t96\t2\t2\t2\t4\t4\t4\t4\t4\t4\t4\t4\t4\t2\t2\t2\t2\t2\t4" +
                "\t4\t4\t4\t4\t4\t4\t4\t4\t2\t2\t4\t2\t2\t4\t2\t2\t2\t2\t2\t2\t4\t4\t4\t4\t4\t4\t4\t4\t2\t2\t2\t2\t2" +
                "\t4\t4\t4\t4\t4\t4\t4\t4\t2\t2\t4\t2\t2\t2\t2", ScanTsvTest.normalize(tsv7.summary()));
    }

}
