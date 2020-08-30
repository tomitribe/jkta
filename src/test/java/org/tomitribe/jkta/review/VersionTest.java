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

import org.junit.Before;
import org.junit.Test;
import org.tomitribe.util.Archive;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class VersionTest {

    private Version version;

    @Before
    public void before() throws IOException {
        final File dir = new Archive()
                .add("annotations/2.0/_index.md", "foo")
                .add("annotations/2.0/apidocs/index.html", "")
                .add("annotations/2.0/apidocs/doc-files/EFSL.html", "")
                .add("annotations/2.0/annotations-spec-2.0-RC1.pdf", "")
                .add("annotations/2.0/annotations-spec-2.0-RC1.html", "")
                .toDir();

        version = Specifications.from(dir).specification("annotations").version("2.0");
    }
    
    @Test
    public void indexMd() throws IOException {
        final File file = version.indexMd();
        assertEquals("_index.md", file.getName());
        assertTrue(file.exists());
        assertEquals("foo", IO.slurp(file));
    }

    @Test
    public void name() {
        assertEquals("2.0", version.name());
    }

    @Test
    public void apidocs() {
        assertNotNull(version.apidocs());
    }

    @Test
    public void specification() {
        assertNotNull(version.specification());
        assertEquals("annotations", version.specification().shortName());
    }
}
