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
package org.tomitribe.jkta.deps;

import org.junit.Test;
import org.tomitribe.util.Join;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.Assert.*;

public class DependenciesTest {

    @Test
    public void scan() throws IOException {
        final ClassLoader loader = this.getClass().getClassLoader();
        final InputStream inputStream = loader.getResourceAsStream("scan/activemq-pool/org/apache/activemq/pool/JcaPooledConnectionFactory$1$1.class");

        final DependencyVisitor visitor = new DependencyVisitor();
        Dependencies.classStream(visitor, inputStream);

        final List<Clazz> classes = visitor.getJar().getClasses();
        assertEquals(1, classes.size());
        final Clazz clazz = classes.get(0);
        assertEquals("org.apache.activemq.pool.JcaPooledConnectionFactory$1$1", clazz.getName());
        assertEquals("java.lang.Object\n" +
                "org.apache.activemq.transport.TransportListener\n" +
                "javax.jms.Connection\n" +
                "org.apache.activemq.pool.JcaPooledConnectionFactory$1\n" +
                "org.apache.activemq.pool.JcaPooledConnectionFactory$1\n" +
                "javax.jms.Connection\n" +
                "java.lang.Object\n" +
                "java.io.IOException", Join.join("\n", clazz.getReferences()));
    }
}
