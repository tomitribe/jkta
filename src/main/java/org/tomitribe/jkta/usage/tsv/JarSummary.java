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

public class JarSummary {
    private final long size;
    private final long classes;

    public JarSummary(final Jar jar) {
        this(jar.getSize(), jar.getClasses());
    }

    public JarSummary(final long size, final long classes) {
        this.size = size;
        this.classes = classes;
    }

    public long getSize() {
        return size;
    }

    public long getClasses() {
        return classes;
    }

    public JarSummary add(final JarSummary that) {
        return new JarSummary(
                this.size + that.size,
                this.classes + that.classes
        );
    }
}
