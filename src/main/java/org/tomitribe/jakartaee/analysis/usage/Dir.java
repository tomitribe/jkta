package org.tomitribe.jakartaee.analysis.usage;

import org.tomitribe.util.dir.Filter;
import org.tomitribe.util.dir.Walk;

import java.io.File;
import java.util.stream.Stream;

public interface Dir extends org.tomitribe.util.dir.Dir {

    /**
     * Recursively find all *.java files contained anywhere in this Git repository
     *
     * @return the equivalent of `find <repo> -name '*.java'` as a Java Stream
     */
    @Walk
    @Filter(Is.Jar.class)
    Stream<File> searcJars();


    /**
     * JAX-RS and CREST compatible constructor.  Do not delete.
     */
    static Dir from(final String path) {
        return from(new File(path));
    }

    /**
     *
     * @param file instance representing the root directory of an actual Git clone
     * @return a strongly-typed perspective of the Git clone
     */
    static Dir from(final File file) {
        return org.tomitribe.util.dir.Dir.of(Dir.class, file);
    }


}
