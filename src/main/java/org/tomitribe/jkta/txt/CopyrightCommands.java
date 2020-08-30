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

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
}
