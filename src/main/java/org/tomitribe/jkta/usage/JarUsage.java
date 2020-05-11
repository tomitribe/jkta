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

import org.tomitribe.jkta.deps.Clazz;
import org.tomitribe.jkta.deps.Dependencies;
import org.tomitribe.jkta.deps.DependencyVisitor;
import org.tomitribe.util.Hex;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUsage {

    private JarUsage() {
    }

    public static Usage<Jar> of(final File jar) throws NoSuchAlgorithmException, IOException {
        final DependencyVisitor analysis = new DependencyVisitor();

        final MessageDigest md = MessageDigest.getInstance("SHA-1");
        final DigestInputStream digestIn = new DigestInputStream(IO.read(jar), md);
        final ZipInputStream zipInputStream = new ZipInputStream(digestIn);

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            final String path = entry.getName();

            if (path.endsWith(".class")) {
                Dependencies.classStream(analysis, zipInputStream);
            } else {
                IO.copy(zipInputStream, ignore);
            }
        }

        // make sure all bytes are read just in case
        IO.copy(digestIn, ignore);

        final byte[] messageDigest = md.digest();
        final String hash = Hex.toString(messageDigest);

        final Usage<Jar> usage = new Usage(new Jar(jar, hash, jar.lastModified()));

        analysis.getJar().getClasses().stream()
                .map(Clazz::getReferences)
                .flatMap(Collection::stream)
                .forEach(usage::visit);

        return usage;
    }

    public static String toTsv(final Usage<Jar> jarUsage, final File parent) {
        final StringBuilder sb = new StringBuilder();

        final Jar jar = jarUsage.getContext();
        final String t = "\t";
        sb.append(jar.getSha1()).append(t);
        sb.append(jar.getLastModified()).append(t);
        sb.append(childPath(parent, jar.getJar())).append(t);
        sb.append(jarUsage.toTsv());
        return sb.toString();
    }

    public static Usage<Jar> fromTsv(final String line) {
        final String[] strings = line.split("\t", 4);

        if (strings.length != 4) {
            throw new IllegalStateException("Incomplete TSV format: " + line);
        }

        final String hash = strings[0];
        final long lastModified = new Long(strings[1]);
        final File file = new File(strings[2]);
        final Jar jar = new Jar(file, hash, lastModified);

        return Usage.fromTsv(jar, strings[3]);
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


}
