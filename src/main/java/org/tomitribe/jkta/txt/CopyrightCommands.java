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
package org.tomitribe.jkta.txt;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Option;
import org.tomitribe.jkta.usage.Dir;
import org.tomitribe.jkta.util.Predicates;
import org.tomitribe.util.IO;
import org.tomitribe.util.Join;
import org.tomitribe.util.hash.XxHash64;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Command("copyright")
public class CopyrightCommands {

    @Command("update")
    public void update(@Option("include") final Pattern include,
                       @Option("exclude") final Pattern exclude,
                       final Dir dir) {

        final Predicate<File> fileFilter = Predicates.fileFilter(include, exclude);
        dir.sources()
                .filter(fileFilter)
                .forEach(this::updateCopyright);
    }

    private void updateCopyright(final File file) {
        try {
            final String content = IO.slurp(file);

            final Optional<String> copyrightLine = Stream.of(content.split("\n"))
                    .filter(s -> s.contains("Copyright"))
                    .filter(s -> !s.contains("2020"))
                    .findFirst();

            if (!copyrightLine.isPresent()) return;

            final String updatedCopyright = updateCopyright(copyrightLine.get());

            final String updated = content.replace(copyrightLine.get(), updatedCopyright);

            IO.copy(IO.read(updated), file);
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot update copyright: " + file.getAbsolutePath(), e);
        }
    }

    final Pattern format = Pattern.compile("Copyright \\(c\\) ([0-9][0-9][0-9][0-9])(, *[0-9][0-9][0-9][0-9])? Oracle");

    private String updateCopyright(final String line) {
        if (!line.contains("Copyright")) return line;
        if (line.contains("2020")) return line;

        final Matcher matcher = format.matcher(line);
        if (!matcher.find()) return line;

        final String copyright = matcher.group(0);
        final String year = matcher.group(1);

        return line.replace(copyright, String.format("Copyright (c) %s, %tY Oracle", year, ZonedDateTime.now()));

    }

    /**
     * Revert any falsely updated copyright statements for files that have not
     * actually been updated in the current copyright year.
     *
     * @param previous Directory containing previous year's copywritten files
     * @param current Directory containing current year's modified files, possibly
     *                with incorrect copyright updates that we should correct
     */
    @Command("compare-and-revert")
    public void revert(final Dir previous,
                       final Dir current) {

        System.out.println("Reading " + previous.get().getAbsolutePath());
        System.out.println("Updating " + current.get().getAbsolutePath());

        final int i = current.get().getAbsolutePath().length() + 1;

        current.files()
                .filter(file -> file.getName().endsWith(".java"))
                .map(file -> new Comparison(previous.file(file.getAbsolutePath().substring(i)), file))
                .filter(comparison -> comparison.getPrevious().exists())
                .forEach(Comparison::compareAndRevert);
    }

    public static class Comparison {
        private final File previous;
        private final File current;

        public Comparison(final File previous, final File current) {
            this.previous = previous;
            this.current = current;
        }

        public File getPrevious() {
            return previous;
        }

        public File getCurrent() {
            return current;
        }

        public void compareAndRevert() {
            final Content a = read(previous);
            final Content b = read(current);

            if (a.equalsIgnoreCopyright(b) && !a.equals(b)) {
                System.out.println("Updating " + current.getAbsolutePath());
                try {
                    IO.copy(previous, current);
                } catch (IOException e) {
                    throw new UncheckedIOException("Unable to copy file " + previous.getAbsolutePath() + " to " + current.getAbsolutePath(), e);
                }
            }
        }

        private Content read(final File file) {
            try {
                final String slurp = IO.slurp(file);
                final long hash = XxHash64.hash(slurp);

                return new Content(hash, slurp);
            } catch (IOException e) {
                throw new UncheckedIOException("Cannot read " + file.getAbsolutePath(), e);
            }
        }

        public static class Content {
            private final long hash;
            private final String content;

            public Content(final long hash, final String content) {
                this.hash = hash;
                this.content = content;
            }

            public long getHash() {
                return hash;
            }

            public String getContent() {
                return content;
            }

            public boolean equalsIgnoreCopyright(final Content that) {
                if (this == that) return true;
                return this.noCopyright().equals(that.noCopyright());
            }

            private String noCopyright() {
                final List<String> lines = Stream.of(content.split("\n"))
                        .filter(s -> !s.contains("Copyright"))
                        .collect(Collectors.toList());
                return Join.join("\n", lines);
            }

            @Override
            public boolean equals(final Object that) {
                if (this == that) return true;
                if (that == null || getClass() != that.getClass()) return false;

                final Content content = (Content) that;

                if (hash != content.hash) return false;

                return true;
            }

            @Override
            public int hashCode() {
                return (int) (hash ^ (hash >>> 32));
            }

        }
    }

}
