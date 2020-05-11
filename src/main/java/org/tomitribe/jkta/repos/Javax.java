/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.tomitribe.jkta.repos;

import org.tomitribe.jkta.util.Formatter;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.val.Directory;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command
public class Javax {

    private static final String FOMRAT = "{status}\t{name}\t[{packageList}]\tjavax({classesCount} " +
            "classes, {packagesCount} packages)\t\t{nonstandardImportsCount} nonstandardImports";

    //    Project{name=javamail, javax=[javax.activation, javax.mail], classes=120, packages=6, nonportable=48}
    @Command
    public StreamingOutput repos(@Option("format") @Default(FOMRAT) Formatter format, @Directory final File dir) {
        return os -> {
            final PrintStream out = new PrintStream(os);

            getDirectories(dir).stream()
                    .map(Project::parse)
                    .sorted()
                    .sorted((o1, o2) -> o1.getStatus().compareTo(o2.getStatus()))
                    .map(format)
                    .forEach(out::println);
        };
    }

    @Command
    public StreamingOutput files(@Option("format") @Default("{absolutePath}") Formatter format,
                                 @Directory final File dir) {
        return os -> {
            final PrintStream out = new PrintStream(os);

            getDirectories(dir).stream()
                    .map(Javax::getJavaxFiles)
                    .flatMap(Collection::stream)
                    .map(format)
                    .forEach(out::println);
        };
    }

    @Command
    public StreamingOutput classes(@Option("format") @Default("{className}") Formatter format, @Directory final File dir) {
        return os -> {
            final PrintStream out = new PrintStream(os);

            getDirectories(dir).stream()
                    .map(Javax::getJavaxFiles)
                    .flatMap(Collection::stream)
                    .filter(Paths::isMain)
                    .map(Source::parse)
//                    .peek(System.out::println)
//                    .filter(Source::isJavax)
//                    .sorted()
                    .map(Source::getClassName)
                    .forEach(out::println);
        };
    }

    public static List<File> getJavaxFiles(final File clone) {
        try {
            return (List<File>) Files.walk(clone.toPath())
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(Javax::isJava)
                    .filter(Javax::isJavax)
                    .filter(Javax::isNotFake)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    public static List<File> getJavaFiles(final File clone) {
        try {
            return (List<File>) Files.walk(clone.toPath())
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .filter(Javax::isJava)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private static boolean isNotFake(final File file) {
        return !file.getName().equals("Hello.java");
    }

    private static boolean isJavax(final File file) {
        return file.toURI().getPath().contains("/javax/");
    }

    private static boolean isJava(final File file) {
        return file.getName().endsWith(".java");
    }

    private List<File> getClones(final File dir) {
        return (List<File>) Stream.of(dir.listFiles())
                .filter(File::isDirectory)
                .filter(file -> new File(file, ".git").exists())
                .map(this::getCanonicalFile)
                .collect(Collectors.toList());
    }

    private List<File> getDirectories(final File dir) {
        return (List<File>) Stream.of(dir.listFiles())
                .filter(File::isDirectory)
                .map(this::getCanonicalFile)
                .collect(Collectors.toList());
    }

    private File getCanonicalFile(final File file) {
        try {
            return file.getCanonicalFile();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

}
