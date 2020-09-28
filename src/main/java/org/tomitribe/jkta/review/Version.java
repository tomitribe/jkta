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
package org.tomitribe.jkta.review;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.tomitribe.swizzle.stream.StreamBuilder;
import org.tomitribe.util.IO;
import org.tomitribe.util.dir.Dir;
import org.tomitribe.util.dir.Name;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public interface Version extends org.tomitribe.util.dir.Dir {

    @Name("_index.md")
    public File indexMd();

    default Index getIndex() {
        return Index.from(indexMd());
    }

    default String name() {
        return get().getName();
    }

    default Specification specification() {
        return Specification.from(parent());
    }

    default String version() {
        return get().getParentFile().getParentFile().getName();
    }

//    default File specHtml() {
//        return Stream.of(get().listFiles())
//                .filter(file -> file.getName().endsWith(".html"));
//    }

    Apidocs apidocs();

    static Version from(final String name) {
        return from(new File(name));
    }

    static Version from(final File file) {
        return Dir.of(Version.class, file);
    }

    @Data
    @AllArgsConstructor
    @Builder(builderClassName = "Parser")
    class Index {

        private final String title;
        private final String summary;
        private final Date date;
        private final List<Link> links;
        private final List<Link> compatibleImplementations;

        public static Index from(final File file) {
            return builder().from(file).build();
        }

        public static class Parser {

            public Parser() {
                links = new ArrayList<>();
                compatibleImplementations = new ArrayList<>();
            }

            public Index.Parser from(final File file) {
                final AtomicReference<String> section = new AtomicReference<>();
                try {
                    try (final InputStream in = IO.read(file)) {
                        StreamBuilder.create(in)
                                .substream("---", "---", inputStream -> StreamBuilder.create(inputStream)
                                        .replace(": \"", ":\"")
                                        .watch("\ntitle:\"", "\"", this::title)
                                        .watch("\nsummary:\"", "\"", this::summary)
                                        .watch("\ndate:", "\"", this::parseDate)
                                        .get())
                                .watch("\n#", "\n", section::set)
                                .watch("[", ")", s -> addLink(s, section.get()))
                                .run();
                        return this;
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("Cannot parse specification _index.md file: " + file.getAbsolutePath(), e);
                }
            }

            private void parseDate(final String value) {
                try {
                    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                    date(format.parse(value.trim()));
                } catch (ParseException e) {
                    throw new IllegalArgumentException("Invalid date header", e);
                }
            }

            private void addLink(final String text, final String section) {
                final Link link = Link.parse("[" + text + ")");
                if (link == null) return;

                links.add(link);

                if (section != null && section.toLowerCase().contains("compatible implementation")) {
                    compatibleImplementations.add(link);
                }
            }
        }
    }

}
