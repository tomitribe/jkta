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
import org.tomitribe.jkta.usage.Usage;

import java.io.File;

public class JarTsv6 implements Format<Jar> {

    private JarTsv7 tsv7;

    public JarTsv6(final File repository) {
        this.tsv7 = new JarTsv7(repository);
    }

    public JarTsv6() {
        this(new File(""));
    }

    @Override
    public String heading() {
        return tsv7.heading();
    }

    @Override
    public String write(final Usage<Jar> usage) {
        return tsv7.write(usage);
    }

    @Override
    public Usage<Jar> read(final String line) {
        final Columns columns = new Columns(line, 7);

        // These are order sensitive
        final String hash = columns.nextString();
        final long lastModified = columns.nextLong();
        final long internalDate = columns.nextLong();
        final long size = columns.nextLong();
        final long classes = columns.nextLong();
        final File file = new File(columns.nextString());

        final Jar jar = new Jar(file, hash, lastModified, internalDate, classes, size, new int[0]);

        return Usage.fromTsv(jar, columns.nextString());
    }

    @Override
    public String summary() {
        return tsv7.summary();
    }
}
