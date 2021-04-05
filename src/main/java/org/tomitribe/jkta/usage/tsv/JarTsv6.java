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
import org.tomitribe.jkta.usage.PackageUsage;

import java.io.File;
import java.util.function.Consumer;

public class JarTsv6 implements Format<Jar> {

    private final JarTsv7 tsv7;
    private final Consumer<String> failed;

    public JarTsv6(final File repository, final Consumer<String> failed) {
        this.tsv7 = new JarTsv7(repository);
        this.failed = failed;
    }

    public JarTsv6(final Consumer<String> failed) {
        this(new File(""), failed);
    }

    public JarTsv6() {
        this(s -> {
        });
    }

    @Override
    public String heading() {
        return tsv7.heading();
    }

    @Override
    public String write(final PackageUsage<Jar> usage) {
        return tsv7.write(usage);
    }

    @Override
    public PackageUsage<Jar> read(final String line) {
        try {
            final Columns columns = new Columns(line, 7);

            // These are order sensitive
            final String hash = columns.nextString();
            final long lastModified = columns.nextLong();
            final long internalDate = columns.nextLong();
            final long size = columns.nextLong();
            final long classes = columns.nextLong();
            final File file = new File(columns.nextString());

            final Jar jar = new Jar(file, hash, lastModified, internalDate, classes, size, new int[0]);

            return PackageUsage.fromTsv(jar, columns.nextString());
        } catch (Exception e) {
            failed.accept(line);
            return null;
        }
    }

    @Override
    public String summary() {
        return tsv7.summary();
    }
}
