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
package org.tomitribe.jkta.review;

import org.junit.Test;
import org.tomitribe.util.Archive;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class SpecificationTest {

    @Test
    public void indexMd() throws IOException {
        final File dir = new Archive()
                .add("annotations/2.0/_index.md", "foo")
                .add("dependency-injection/2.0/_index.md", "foo")
                .add("persistence/3.0/_index.md", "foo")
                .add("enterprise-beans/_index.md", "red")
                .add("enterprise-beans/3.2/_index.md", "green")
                .add("enterprise-beans/4.0/_index.md", "blue")
                .toDir();

        final Specifications specifications = Specifications.from(dir);
        final Specification specification = specifications.specification("enterprise-beans");

        assertNotNull(specification.indexMd());
        assertEquals("_index.md", specification.indexMd().getName());
        assertTrue(specification.indexMd().exists());
    }

    @Test
    public void shortName() throws IOException {
        final File dir = new Archive()
                .add("annotations/2.0/_index.md", "foo")
                .add("dependency-injection/2.0/_index.md", "foo")
                .add("persistence/3.0/_index.md", "foo")
                .add("enterprise-beans/_index.md", "red")
                .add("enterprise-beans/3.2/_index.md", "green")
                .add("enterprise-beans/4.0/_index.md", "blue")
                .toDir();

        final Specifications specifications = Specifications.from(dir);
        final Specification specification = specifications.specification("enterprise-beans");

        assertEquals("enterprise-beans", specification.shortName());
    }

    @Test
    public void version() throws IOException {
        final File dir = new Archive()
                .add("annotations/2.0/_index.md", "foo")
                .add("dependency-injection/2.0/_index.md", "foo")
                .add("persistence/3.0/_index.md", "foo")
                .add("enterprise-beans/_index.md", "red")
                .add("enterprise-beans/3.2/_index.md", "green")
                .add("enterprise-beans/4.0/_index.md", "blue")
                .toDir();

        final Specifications specifications = Specifications.from(dir);
        final Specification specification = specifications.specification("enterprise-beans");

        assertNotNull(specification.version("4.0"));
    }
}
