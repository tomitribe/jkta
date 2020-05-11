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
package org.tomitribe.jkta.repos;

import org.tomitribe.jkta.Resources;
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
