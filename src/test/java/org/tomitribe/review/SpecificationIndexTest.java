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
package org.tomitribe.review;

import org.junit.Before;
import org.junit.Test;
import org.tomitribe.jkta.Resources;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

public class SpecificationIndexTest {

    private Specification.Index index;

    @Before
    public void before() throws IOException {
        final File file = Resources.loadAsFile("review/enterprise-beans/_index.md");
        index = Specification.Index.from(file);
    }

    @Test
    public void title() throws IOException {
        assertEquals("Jakarta Enterprise Beans", index.getTitle());
    }

    @Test
    public void summary() throws IOException {
        assertEquals("Jakarta Enterprise Beans defines an architecture for the development and " +
                "deployment of component-based business applications.", index.getSummary());
    }

    @Test
    public void projectId() throws IOException {
        assertEquals("ee4j.ejb", index.getProjectId());
    }
}
