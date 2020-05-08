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
package org.tomitribe.jkta.s3;

import org.tomitribe.util.Base32;
import org.tomitribe.util.Ints;
import org.tomitribe.util.hash.Slice;
import org.tomitribe.util.hash.Slices;
import org.tomitribe.util.hash.XxHash32;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Random;

/**
 * Short semi-unique ID to help disambiguate files that may clash due
 * to having the same date stamp.  Important to enable safe parallelization
 * as if there are several dozen or hundreds of processes running in parallel
 * there are good odds they may try to write to the same date stamp.
 */
public class Id {

    private final String id;

    private Id(String id) {
        this.id = id;
    }

    public static Id generate() {

        final byte[] bytes;

        try {
            // Generate some Random Data
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            System.getProperties().store(out, "" + System.currentTimeMillis() + new Random().nextDouble());
            out.flush();

            // XxHash32 hash it
            final byte[] array = out.toByteArray();
            final Slice data = Slices.wrappedBuffer(array);
            final int hash = XxHash32.hash(data);
            bytes = Ints.toBytes(hash);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }

        // Base32 encode it
        return new Id(Base32.encode(bytes).replaceAll("=", "").toLowerCase());
    }

    public String get() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Id that = (Id) o;

        if (!id.equals(that.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return id;
    }

    public static void main(String[] args) {
        final String s = Id.generate().get();
        System.out.println(s);
    }
}
