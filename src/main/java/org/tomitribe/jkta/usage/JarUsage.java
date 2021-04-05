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
import java.util.TimeZone;
import java.util.concurrent.atomic.AtomicLong;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUsage {

    private JarUsage() {
    }

    public static PackageUsage<Jar> of(final File jar) throws NoSuchAlgorithmException, IOException {
        if (jar.getName().endsWith(".class")) {
            return ofClass(jar);
        }
        final InputStream inputStream = IO.read(jar);
        final PackageUsage usage = new PackageUsage();

        final Set<Integer> versions = new HashSet<>();
        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        final DigestInputStream digestIn = new DigestInputStream(inputStream, md);
        final AtomicLong classes = new AtomicLong();

        final long internalDate = scanJar(usage, versions, digestIn, classes);

        final byte[] messageDigest = md.digest();
        final String hash = Hex.toString(messageDigest);
        return new PackageUsage<>(new Jar(jar, hash, jar.lastModified(), internalDate, classes.get(), jar.length(), versions(versions))).add(usage);
    }

    private static PackageUsage<Jar> ofClass(final File clazz) throws IOException, NoSuchAlgorithmException {
        final InputStream inputStream = IO.read(clazz);
        final PackageUsage usage = new PackageUsage();

        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        final DigestInputStream digestIn = new DigestInputStream(inputStream, md);

        final int version = scanClass(digestIn, usage);

        final byte[] messageDigest = md.digest();
        final String hash = Hex.toString(messageDigest);
        return new PackageUsage<>(new Jar(clazz, hash, clazz.lastModified(), clazz.lastModified(), 1, clazz.length(), new int[]{version})).add(usage);
    }

    private static long scanJar(final Usage usage, final Set<Integer> versions, final InputStream inputStream, final AtomicLong classes) throws IOException {
        final SynchronizedDescriptiveStatistics entryDates = new SynchronizedDescriptiveStatistics();
        final ZipInputStream zipInputStream = new ZipInputStream(inputStream);

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            final String path = entry.getName();

            final long time = getTime(entry);
            if (time != -1) {
                entryDates.addValue(time);
            }

            if (path.endsWith(".class")) {
                classes.incrementAndGet();
                final int version = scanClass(zipInputStream, usage);
                versions.add(version);
            } else if (isZip(path)) {
                scanJar(usage, versions, zipInputStream, classes);
            } else {
                IO.copy(zipInputStream, ignore);
            }
        }

        // make sure all bytes are read just in case
        IO.copy(inputStream, ignore);
        return (long) entryDates.getPercentile(0.9);
    }

    private static boolean isZip(final String path) {
        return Is.Zip.accept(path);
    }

    private static int[] versions(final Set<Integer> set) {
        final int[] ints = new int[set.size()];
        final Iterator<Integer> iterator = set.iterator();
        for (int i = 0; i < ints.length; i++) {
            ints[i] = iterator.next();
        }
        return ints;
    }

    /**
     * Zip files use MS-DOS time format which does not have
     * the time zone in it.  The JVM made the decision to
     * use the timezone of the system viewing the zip as the
     * implied timezone.
     *
     * The impact of this is that the times inside a zip file
     * can have more than 24 interpretations depending on who
     * is "looking" at the zip and what time zone they are
     * currently in.
     *
     * As Maven Central is a very global repository, we know
     * that the timezone of the person looking at the jar has
     * no relationship to the timezone of the person who made
     * the jar.  Thus, we want to interpret all times inside
     * jars found in Maven Central as being in UTC so the
     * default is the same for everyone and everyone sees the
     * same time.
     *
     * We'll never know the actual timezone of the creator
     * of the jar, but we will at least all agree on the
     * same "wrong" answer.
     */
    public static long getTime(final ZipEntry entry) {
        long time = entry.getTime();

        if (time < 1 && entry.getLastModifiedTime() != null) {
            time = entry.getLastModifiedTime().toMillis();
        }

        if (time < 0) return 0;

        final int offset = TimeZone.getDefault().getOffset(time);
        return time + offset;
    }


    private static final OutputStream ignore = new OutputStream() {
        @Override
        public void write(final int b) {
        }
    };

    private static int scanClass(final InputStream in, final Usage usage) throws IOException {
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
