package org.eclipse.wg.jakartaee;

import junit.framework.TestCase;
import org.apache.openejb.loader.IO;
import org.apache.xbean.asm7.ClassReader;
import org.apache.xbean.asm7.ClassWriter;

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

    private static void file(final File file, final DependencyVisitor dependencyVisitor) {
        try {
            final InputStream in = IO.read(file);
            try {
                final ClassReader classReader = new ClassReader(in);
                classReader.accept(dependencyVisitor, ClassWriter.COMPUTE_MAXS);
            } finally {
                IO.close(in);
            }
        } catch (final IOException e) {
            e.printStackTrace();
            TestCase.fail(e.getMessage());
        }
    }
}
