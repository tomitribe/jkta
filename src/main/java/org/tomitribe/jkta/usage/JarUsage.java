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
package org.tomitribe.jkta.usage;

import org.apache.commons.math3.stat.descriptive.SynchronizedDescriptiveStatistics;
import org.objectweb.asm.ClassReader;
import org.tomitribe.util.Hex;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUsage {

    private JarUsage() {
    }

    public static Usage<Jar> of(final File jar) throws NoSuchAlgorithmException, IOException {
        final Usage usage = new Usage();
        final SynchronizedDescriptiveStatistics entryDates = new SynchronizedDescriptiveStatistics();
        final Set<Integer> versions = new HashSet<>();
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        final DigestInputStream digestIn = new DigestInputStream(IO.read(jar), md);
        final ZipInputStream zipInputStream = new ZipInputStream(digestIn);

        final AtomicLong classes = new AtomicLong();
        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            final String path = entry.getName();

            final long time = getTime(entry);
            if (time != -1) {
                entryDates.addValue(time);
            }

            if (path.endsWith(".class")) {
                classes.incrementAndGet();
                final int version = scan(zipInputStream, usage);
                versions.add(version);
            } else {
                IO.copy(zipInputStream, ignore);
            }
        }

        // make sure all bytes are read just in case
        IO.copy(digestIn, ignore);

        final byte[] messageDigest = md.digest();
        final String hash = Hex.toString(messageDigest);
        final long internalDate = (long) entryDates.getPercentile(0.9);
        return new Usage<>(new Jar(jar, hash, jar.lastModified(), internalDate, classes.get(), jar.length(), versions(versions))).add(usage);
    }

    private static int[] versions(final Set<Integer> set) {
        final int[] ints = new int[set.size()];
        final Iterator<Integer> iterator = set.iterator();
        for (int i = 0; i < ints.length; i++) {
            ints[i] = iterator.next();
        }
        return ints;
    }

    private static long getTime(final ZipEntry entry) {
        final Supplier<Long>[] times = new Supplier[]{
                entry::getTime,
                () -> entry.getLastModifiedTime() != null ? entry.getLastModifiedTime().toMillis() : 0,
                () -> entry.getCreationTime() != null ? entry.getCreationTime().toMillis() : 0
        };
        for (final Supplier<Long> time : times) {
            final Long aLong = time.get();
            if (aLong > 0) return aLong;
        }
        return 0;
    }


    private static final OutputStream ignore = new OutputStream() {
        @Override
        public void write(final int b) {
        }
    };

    private static int scan(final InputStream in, final Usage usage) throws IOException {
        final ClassScanner classScanner = new ClassScanner(usage);
        final ClassReader classReader = new ClassReader(in);
        classReader.accept(classScanner, 0);
        return classScanner.getVersion();
    }

    static String summary(final int scanned, final int affected) {
        final int percent = (int) ((affected / scanned) * 100);
        return String.format("total affected %s%% (%s of %s scanned)", percent, affected, scanned);
    }
}
