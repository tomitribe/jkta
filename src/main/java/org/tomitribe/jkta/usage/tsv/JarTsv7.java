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
package org.tomitribe.jkta.usage.tsv;

import org.tomitribe.jkta.usage.Jar;
import org.tomitribe.jkta.usage.Package;
import org.tomitribe.jkta.usage.PackageUsage;

import java.io.File;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class JarTsv7 implements Format<Jar> {
    private final Summary summary = new Summary();
    private final File repository;
    private final AtomicReference<JarSummary> jars = new AtomicReference<>(new JarSummary(0, 0));
    private final Consumer<String> failed;

    public JarTsv7(final File repository) {
        this(repository, s -> {
        });
    }

    public JarTsv7() {
        this(s -> {
        });
    }

    public JarTsv7(final Consumer<String> failed) {
        this(new File(""), failed);
    }

    public JarTsv7(final File repository, final Consumer<String> failed) {
        this.failed = failed;
        this.repository = repository;
    }

    @Override
    public String heading() {
        return ScanTsv.tabbed(Stream.of(
                "SHA-1",
                "Last Modified",
                "Internal Date",
                "Size",
                "Classes",
                "Java Version",
                "Path",
                "javax uses total",
                "jakarta uses total"
        ), Package.names());
    }

    @Override
    public String write(final PackageUsage<Jar> usage) {
        final Jar jar = usage.getContext();

        summary.add(usage);
        jars.accumulateAndGet(new JarSummary(jar), JarSummary::add);

        return ScanTsv.tabbed(Stream.of(
                jar.getSha1(),
                jar.getLastModified(),
                jar.getInternalDate(),
                jar.getSize(),
                jar.getClasses(),
                ScanTsv.versions(jar),
                ScanTsv.childPath(repository, jar.getJar()),
                usage.toTsv()
        ));
    }

    @Override
    public PackageUsage<Jar> read(final String line) {
        try {
            final Columns columns = new Columns(line, 8);

            // These are order sensitive
            final String hash = columns.nextString();
            final long lastModified = columns.nextLong();
            final long internalDate = columns.nextLong();
            final long size = columns.nextLong();
            final long classes = columns.nextLong();
            final int[] javaVersions = columns.nextInts();
            final File file = new File(columns.nextString());

            final Jar jar = new Jar(file, hash, lastModified, internalDate, classes, size, javaVersions);

            return PackageUsage.fromTsv(jar, columns.nextString());
        } catch (Exception e) {
            failed.accept(line);
            return null;
        }
    }

    @Override
    public String summary() {
        return ScanTsv.tabbed(Stream.of(
                "0000000000000000000000000000000000000000",
                System.currentTimeMillis(),
                System.currentTimeMillis(),
                jars.get().getSize(),
                jars.get().getClasses(),
                0,
                summary.summary(),
                summary.getTotal().toTsv()
        ));
    }
}
