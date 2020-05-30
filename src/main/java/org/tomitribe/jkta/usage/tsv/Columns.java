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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class Columns {

    private final Iterator<String> parts;

    public Columns(final String line, final int size) {
        final String[] strings = line.split("\t", size);

        if (strings.length != size) {
            throw new IllegalStateException("Incomplete TSV format: " + line);
        }

        this.parts = new ArrayList<>(Arrays.asList(strings)).iterator();
    }

    public long nextLong() {
        return Long.parseLong(parts.next());
    }

    public String nextString() {
        return parts.next();
    }

    public int nextInt() {
        return Integer.parseInt(parts.next());
    }

    public int[] nextInts() {
        final String[] strings = parts.next().split(",");

        final int[] ints = new int[strings.length];

        for (int i = 0; i < strings.length; i++) {
            ints[i] = Integer.parseInt(strings[i]);
        }
        
        return ints;
    }
}
