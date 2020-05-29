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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Supplier;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUsage {

    private JarUsage() {
    }

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        final Usage<Jar> of = of(new File("/tmp/apache-tomcat-10.0.0-M5/lib/tomcat-i18n-ja.jar"));
    }

    public static Usage<Jar> of(final File jar) throws NoSuchAlgorithmException, IOException {
        final Usage usage = new Usage();
        final SynchronizedDescriptiveStatistics entryDates = new SynchronizedDescriptiveStatistics();
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
                scan(zipInputStream, usage);
            } else {
                IO.copy(zipInputStream, ignore);
            }
        }

        // make sure all bytes are read just in case
        IO.copy(digestIn, ignore);

        final byte[] messageDigest = md.digest();
        final String hash = Hex.toString(messageDigest);
        final long internalDate = (long) entryDates.getPercentile(0.9);
        return new Usage<>(new Jar(jar, hash, jar.lastModified(), internalDate, classes.get(), jar.length())).add(usage);
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

    public static List<String> tsvColumns() {
        final ArrayList<String> columns = new ArrayList<>();
        columns.add("SHA-1");
        columns.add("Last Modified");
        columns.add("Internal Date");
        columns.add("Size");
        columns.add("Classes");
        columns.add("Path");
        columns.add("javax uses total");
        columns.add("jakarta uses total");
        return columns;
    }

    public static String toTsv(final Usage<Jar> jarUsage, final File parent) {
        final StringBuilder sb = new StringBuilder();

        final Jar jar = jarUsage.getContext();
        final String t = "\t";
        sb.append(jar.getSha1()).append(t);
        sb.append(jar.getLastModified()).append(t);
        sb.append(jar.getInternalDate()).append(t);
        sb.append(jar.getSize()).append(t);
        sb.append(jar.getClasses()).append(t);
        sb.append(childPath(parent, jar.getJar())).append(t);
        sb.append(jarUsage.toTsv());
        return sb.toString();
    }

    public static Usage<Jar> fromTsv(final String line) {
        final String[] strings = line.split("\t", 7);

        if (strings.length != 7) {
            throw new IllegalStateException("Incomplete TSV format: " + line);
        }

        final Iterator<String> parts = new ArrayList<>(Arrays.asList(strings)).iterator();

        final String hash = parts.next();
        final long lastModified = Long.parseLong(parts.next());
        final long internalDate = Long.parseLong(parts.next());
        final long size = Long.parseLong(parts.next());
        final long classes = Long.parseLong(parts.next());
        final File file = new File(parts.next());
        final Jar jar = new Jar(file, hash, lastModified, internalDate, classes, size);

        return Usage.fromTsv(jar, parts.next());
    }

    public static String childPath(final File parent, final File file) {
        final String parentPath = parent.getAbsolutePath();
        final String childPath = file.getAbsolutePath();

        if (childPath.startsWith(parentPath)) {
            final int base = parentPath.length();
            return childPath.substring(base + 1);
        } else {
            return childPath;
        }
    }


    private static final OutputStream ignore = new OutputStream() {
        @Override
        public void write(final int b) {
        }
    };

    private static void scan(final InputStream in, final Usage usage) throws IOException {
        final ClassScanner classScanner = new ClassScanner(usage);
        final ClassReader classReader = new ClassReader(in);
        classReader.accept(classScanner, 0);
    }

    public static String toTotalTsv(final double scanned, final double affected, final Usage total) {
        final String t = "\t";
        return "0000000000000000000000000000000000000000" + t +
                System.currentTimeMillis() + t +
                System.currentTimeMillis() + t +
                0 + t +
                0 + t +
                summary((int) scanned, (int) affected) + t +
                total.toTsv();
    }

    static String summary(final int scanned, final int affected) {
        final int percent = (int) ((affected / scanned) * 100);
        return String.format("total affected %s%% (%s of %s scanned)", percent, affected, scanned);
    }
}
