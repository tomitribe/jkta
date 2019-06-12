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
package org.eclipse.wg.jakartaee;

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

    public static Jar javaxUses(final Jar jar) {
        final Jar filtered = new Jar(jar.getName());
        jar.getClasses().stream()
                .filter(Classes::isJavax)
                .map(Classes::javaxUses)
                .forEach(filtered.getClasses()::add);

        return filtered;
    }
}
