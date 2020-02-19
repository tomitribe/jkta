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
package org.tomitribe.jakartaee.analysis.usage;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.PrintOutput;
import org.tomitribe.crest.val.Exists;
import org.tomitribe.crest.val.Readable;
import org.tomitribe.util.PrintString;

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Objects;
import java.util.stream.Stream;

@Command("usage")
public class UsageCommand {

    @Command
    public String jar(@Option("format") @Default("tsv") final Format format,
                      @Exists @Readable final File jar) throws IOException, NoSuchAlgorithmException {

        final Usage<Jar> usage = JarUsage.of(jar);

        switch (format) {
            case tsv:
                return JarUsage.toTsv(usage, new File(""));
            case plain:
                return toPlain(usage);
        }

        return "Unsupported format: " + format;
    }

    @Command
    public PrintOutput dir(@Option("format") @Default("tsv") final Format format,
                           final Dir dir) {
        final Stream<Usage<Jar>> usageStream = dir.searcJars()
                .map(this::jarUsage)
                .filter(Objects::nonNull);


        switch (format) {
            case tsv:
                return out -> {
                    final Usage<Jar> total = usageStream.peek(jarUsage -> out.println(JarUsage.toTsv(jarUsage, dir.dir())))
                            .reduce(Usage::add)
                            .orElse(null);
                    if (total == null) {
                        out.println("No jars found");
                    } else {
                        out.print("Total ");
                        out.println(total.toTsv());
                        out.println();
                        out.println(toPlain(total));

                    }
                };
            case plain:
                return out -> {
                    final Usage<Jar> total = usageStream
                            .reduce(Usage::add)
                            .orElse(null);
                    if (total == null) {
                        out.println("No jars found");
                    } else {
                        out.println(toPlain(total));
                    }
                };
        }

        return printStream -> printStream.println("Unsupported format: " + format);
    }

    private Usage<Jar> jarUsage(final File file) {
        try {
            return JarUsage.of(file);
        } catch (Exception e) {
            System.err.println("Skipping jar: " + JarUsage.childPath(new File(""), file) + " : " + e.getMessage());
            return null;
        }
    }

    private String toPlain(final Usage<Jar> usage) {
        final Jar jar = usage.getContext();
        final PrintString out = new PrintString();

        out.printf("sha1: %s%n", jar.getSha1());
        out.printf("last-modified: %tc%n", new Date(jar.getLastModified()));
        out.printf("name: %s%n", JarUsage.childPath(new File(""), jar.getJar()));
        for (int i = 0; i < usage.getPackages().length; i++) {
            final int count = usage.getPackages()[i];
            final Package aPackage = Package.values()[i];
            out.printf("%s: %s%n", aPackage.getName(), count);
        }
        return out.toString();
    }


    enum Format {
        tsv,
        json,
        plain
    }
}
