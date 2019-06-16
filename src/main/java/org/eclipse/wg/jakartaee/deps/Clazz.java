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
package org.eclipse.wg.jakartaee.deps;

import java.util.ArrayList;
import java.util.List;

public class Clazz {
    /**
     * Intentionally accepts duplicates to facilitate counting
     */
    private final String name;
    private final List<String> references = new ArrayList<>();

    public Clazz(final String name) {
        this.name = name;
    }

    public Clazz(final String name, final List<String> references) {
        this.name = name;
        this.references.addAll(references);
    }

    public boolean hasReferences() {
        return references.size() > 0;
    }

    public List<String> getReferences() {
        return references;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "Clazz{" +
                "name='" + name + '\'' +
                ", references=" + references.size() +
                '}';
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Clazz clazz = (Clazz) o;

        if (!name.equals(clazz.name)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }
}
