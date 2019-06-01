/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.wg.jakartaee;

import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class JavamailTest extends Assert {

    @Test
    public void reduce() throws Exception {
        final List<String> list = new ArrayList<>(Arrays.asList(
                "javax.mail",
                "javax.mail.util",
                "javax.activation",
                "javax.mail.event",
                "javax.mail.search",
                "javax.activation.foo"
        ));

        final List<String> strings = new ArrayList<>(list);
        for (final String string : strings) {
            final Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                final String value = iterator.next();
                if (value.equals(string)) continue;
                if (value.startsWith(string)) iterator.remove();
            }
        }
        list.stream().forEach(System.out::println);

    }

    @Test
    public void test() throws Exception {
        final File dir = new File("/Users/dblevins/work/eclipse-ee4j/javamail");

        final Project project = Project.parse(dir);
        for (final Source source : project.getSources()) {
            System.out.println(source);
        }

        System.out.println(project);

        final List<String> collect = project.getPackages().stream()
                .sorted()
                .sorted((o1, o2) -> Integer.compare(o1.length(), o2.length()))
                .collect(Collectors.toList());

        final List<String> summary = reduce(collect);
    }

    private List<String> reduce(final List<String> list) {

        for (final String s : list) {

        }

        return null;
    }

}
