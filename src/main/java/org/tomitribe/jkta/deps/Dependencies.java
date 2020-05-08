package org.tomitribe.jkta.deps;

import org.apache.xbean.asm7.ClassReader;
import org.apache.xbean.asm7.ClassWriter;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class Dependencies {

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
        classReader.accept(dependencyVisitor, ClassWriter.COMPUTE_MAXS);
    }
}
