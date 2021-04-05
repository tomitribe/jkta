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

import org.objectweb.asm.ClassReader;
import org.tomitribe.jkta.usage.scan.ClassScanner;
import org.tomitribe.jkta.usage.scan.Usage;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.function.Consumer;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ClassUsage {

    private ClassUsage() {
    }

    public static void forEachClass(final File jar, final Consumer<PackageUsage<Clazz>> consumer) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(IO.read(jar));

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            final String path = entry.getName();

            if (path.endsWith(".class")) {
                final PackageUsage<Clazz> usage = new PackageUsage<>();
                final int version = scan(zipInputStream, usage);

                consumer.accept(new PackageUsage<>(new Clazz(path, version)).add(usage));
            } else {
                IO.copy(zipInputStream, ignore);
            }
        }
    }

    public static class Clazz {
        private final String name;
        private final int version;

        public Clazz(final String name, final int version) {
            this.name = name;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public int getVersion() {
            return version;
        }
    }

    public static String toTsv(final PackageUsage<String> classUsage) {
        final StringBuilder sb = new StringBuilder();

        final String className = classUsage.getContext();
        final String t = "\t";
        sb.append(className).append(t);
        sb.append(classUsage.toTsv());
        return sb.toString();
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
}
