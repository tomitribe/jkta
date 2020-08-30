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

import org.junit.Test;
import org.tomitribe.util.Archive;
import org.tomitribe.util.Join;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

public class SpecificationsTest {

    @Test
    public void specifications() throws IOException {
        final File dir = new Archive()
                .add("annotations/2.0/_index.md", "foo")
                .add("dependency-injection/2.0/_index.md", "foo")
                .add("persistence/3.0/_index.md", "foo")
                .add("enterprise-beans/4.0/_index.md", "foo")
                .toDir();

        final Specifications specifications = Specifications.from(dir);

        final List<String> names = specifications.specifications()
                .map(Specification::shortName)
                .sorted()
                .collect(Collectors.toList());

        assertEquals("" +
                "annotations\n" +
                "dependency-injection\n" +
                "enterprise-beans\n" +
                "persistence", Join.join("\n", names));
    }

    @Test
    public void specification() throws IOException {
        final File dir = new Archive()
                .add("annotations/2.0/_index.md", "foo")
                .add("dependency-injection/2.0/_index.md", "foo")
                .add("persistence/3.0/_index.md", "foo")
                .add("enterprise-beans/4.0/_index.md", "foo")
                .toDir();

        final Specifications specifications = Specifications.from(dir);

        final Specification specification = specifications.specification("enterprise-beans");

        assertEquals("enterprise-beans", specification.shortName());
    }
}
