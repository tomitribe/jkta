package org.tomitribe.jkta.usage;

import java.io.File;
import java.io.FileFilter;

public interface Is {
    class Jar implements FileFilter {
        @Override
        public boolean accept(final File pathname) {
            return pathname.isFile() && pathname.getName().endsWith(".jar");
        }
    }
}