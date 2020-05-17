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
package org.tomitribe.jkta.deps;

import org.apache.xbean.asm7.ClassReader;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Dependencies {

    private Dependencies() {
    }

    public static void dir(final File dir, final DependencyVisitor dependencyVisitor) {
        final File[] files = dir.listFiles();
        if (files != null) {
            for (final File file : files) {
                if (file.isDirectory()) {
                    dir(file, dependencyVisitor);
                } else if (file.getName().endsWith(".class")) {
                    file(file, dependencyVisitor);
                }
            }
        }
    }

    public static void file(final File classFile, final DependencyVisitor dependencyVisitor) {
        try {
            final InputStream in = IO.read(classFile);
            try {
                classStream(dependencyVisitor, in);
            } finally {
                IO.close(in);
            }
        } catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static void classStream(final DependencyVisitor dependencyVisitor, final InputStream in) throws IOException {
        final ClassReader classReader = new ClassReader(in);
        classReader.accept(dependencyVisitor, 0);
    }
}
