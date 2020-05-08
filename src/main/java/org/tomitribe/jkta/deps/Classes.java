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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Classes {

    public static boolean isJavax(final Clazz clazz) {
        return clazz.getName().startsWith("javax.");
    }

    public static Clazz javaxUses(final Clazz clazz) {
        final Clazz filtered = new Clazz(clazz.getName());
        clazz.getReferences().stream()
                .filter(s -> s.startsWith("javax."))
                .forEach(filtered.getReferences()::add);

        return filtered;
    }

    /**
     * Remove any references to Classes in this jar
     */
    public static Jar externalUses(final Jar jar) {
        final Jar filtered = new Jar(jar.getName());

        final Set<String> classes = jar.getClasses().stream()
                .map(Clazz::getName)
                .collect(Collectors.toSet());

        for (final Clazz clazz : jar.getClasses()) {
            final List<String> references = clazz.getReferences().stream()
                    .filter(s -> !classes.contains(s))
                    .collect(Collectors.toList());
            filtered.getClasses().add(new Clazz(clazz.getName(), references));
        }


        return filtered;
    }

    /**
     * Remove any references to Classes outside this jar
     */
    public static Jar internalUses(final Jar jar) {
        final Jar filtered = new Jar(jar.getName());

        final Set<String> classes = jar.getClasses().stream()
                .map(Clazz::getName)
                .collect(Collectors.toSet());

        for (final Clazz clazz : jar.getClasses()) {
            final List<String> references = clazz.getReferences().stream()
                    .filter(classes::contains)
                    .collect(Collectors.toList());
            filtered.getClasses().add(new Clazz(clazz.getName(), references));
        }

        return filtered;
    }

    /**
     * Remove any Clazz instances with no references
     */
    public static Jar trimEmptyReferences(final Jar jar) {
        final Jar filtered = new Jar(jar.getName());

        for (final Clazz clazz : jar.getClasses()) {
            if (!clazz.hasReferences()) continue;
            filtered.getClasses().add(clazz);
        }

        return filtered;
    }

    /**
     * Remove all duplicate references from each Clazz
     */
    public static Clazz distinctUses(final Clazz clazz) {
        final Clazz filtered = new Clazz(clazz.getName());
        clazz.getReferences().stream()
                .sorted()
                .distinct()
                .forEach(filtered.getReferences()::add);

        return filtered;
    }

    /**
     * Remove all duplicate references from each Clazz
     * There will still be duplicates at the Jar level
     */
    public static Jar distinctUses(final Jar jar) {
        final Jar filtered = new Jar(jar.getName());
        jar.getClasses().stream()
                .map(Classes::distinctUses)
                .forEach(filtered.getClasses()::add);
        return filtered;
    }

    /**
     * Remove all non javax Clazz instances
     * Remove all non javax references from each Clazz
     */
    public static Jar javaxUses(final Jar jar) {
        final Jar filtered = new Jar(jar.getName());
        jar.getClasses().stream()
                .filter(Classes::isJavax)
                .map(Classes::javaxUses)
                .forEach(filtered.getClasses()::add);

        return filtered;
    }
}
