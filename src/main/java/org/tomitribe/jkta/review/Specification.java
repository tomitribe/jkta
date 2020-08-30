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
import java.util.stream.Stream;

public interface Specification extends Dir {

    @Name("_index.md")
    File indexMd();

    default String shortName() {
        return get().getName();
    }

    default String title() {
        return Stream.of(readIndexMd().split("\n"))
                .filter(s -> s.startsWith("title:"))
                .map(s -> s.substring("title:".length()))
                .map(s -> s.replace("\"", " "))
                .map(String::trim)
                .findFirst().orElseThrow(() -> new MissingIndexMdFieldException("title"));
    }

    default String readIndexMd() {
        try {
            return IO.slurp(indexMd());
        } catch (IOException e) {
            throw new UncheckedIOException("Cannot read " + indexMd().getAbsolutePath(), e);
        }
    }

    Version version(final String name);

    static Specification from(final String name) {
        return from(new File(name));
    }

    static Specification from(final File file) {
        return Dir.of(Specification.class, file);
    }


    @AllArgsConstructor
    @Builder(builderClassName = "Parser")
    @Data
    class Index {

        private final String title;
        private final String summary;
        private final String projectId;

        public static Index from(final File file) {
            return builder().from(file).build();
        }

        public static class Parser {
            public Parser from(final File file) {
                try {
                    try (final InputStream in = IO.read(file)) {
                        StreamBuilder.create(in)
                                .substream("---", "---", inputStream -> StreamBuilder.create(inputStream)
                                        .replace(": \"", ":\"")
                                        .watch("\ntitle:\"", "\"", this::title)
                                        .watch("\nsummary:\"", "\"", this::summary)
                                        .watch("\nproject_id:\"", "\"", this::projectId)
                                        .get())
                                .run();
                        return this;
                    }
                } catch (IOException e) {
                    throw new UncheckedIOException("Cannot parse specification _index.md file: " + file.getAbsolutePath(), e);
                }
            }
        }
    }
}
