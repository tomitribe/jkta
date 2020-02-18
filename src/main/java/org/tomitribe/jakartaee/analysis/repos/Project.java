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
package org.tomitribe.jakartaee.analysis.repos;

import org.tomitribe.util.Join;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

public class Project implements Comparable<Project>{

    private final File dir;
    private final List<Source> sources;

    public Project(final File dir, final List<Source> sources) {
        this.dir = dir;
        this.sources = sources;
    }

    @Override
    public int compareTo(final Project o) {
        return this.dir.getName().compareTo(o.dir.getName());
    }

    public File getDir() {
        return dir;
    }

    public List<Source> getSources() {
        return sources;
    }

    public List<String> getClasses() {
        return sources.stream()
                .map(Source::getClassName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getPackages() {
        return sources.stream()
                .map(Source::getPackageName)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<String> getShortPackages() {
        final List<String> list = new ArrayList<String>(getPackages());

        final List<String> strings = new ArrayList<>(list);
        for (final String string : strings) {
            final Iterator<String> iterator = list.iterator();
            while (iterator.hasNext()) {
                final String value = iterator.next();
                if (value.equals(string)) continue;
                if (value.startsWith(string)) iterator.remove();
            }
        }
        Collections.sort(list);
        return list;
    }

    public List<String> getNonstandardImports() {
        return sources.stream()
                .map(Source::getImports)
                .flatMap(Collection::stream)
                .sorted()
                .collect(Collectors.toList());
    }

    public String getStatus() {
        if (getClassesCount() == 0) return "other";
        if (getNonstandardImportsCount() == 0) return "api";
        return "api-impl";
    }

    public static Project parse(final File dir) {
        final List<Source> sources = Javax.getJavaxFiles(dir)
                .stream()
                .filter(Paths::isMain)
                .filter(file -> !file.getName().equals("module-info.java"))
                .filter(file -> !file.getName().equals("package-info.java"))
                .map(Source::parse)
                .filter(Source::isJavax)
                .collect(Collectors.toList());

        return new Project(dir, sources);
    }

    @Override
    public String toString() {
        return "Project{" +
                "name=" + getName() +
                ", javax=[" + getPackageList() + "]" +
                ", classes=" + getClassesCount() +
                ", packages=" + getPackagesCount() +
                (getNonstandardImportsCount() > 0 ? ", nonportable=" + getNonstandardImportsCount() : "") +
                '}';
    }

    public String getName() {
        return dir.getName();
    }

    public String getPackageList() {
        return Join.join(", ", getShortPackages());
    }

    public int getNonstandardImportsCount() {
        return getNonstandardImports().size();
    }

    public int getPackagesCount() {
        return getPackages().size();
    }

    public int getClassesCount() {
        return getClasses().size();
    }
}
