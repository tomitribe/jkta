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
package org.eclipse.wg.jakartaee.repos;

import org.eclipse.wg.jakartaee.util.Strings;
import org.eclipse.wg.jakartaee.util.Formatter;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.StreamingOutput;
import org.tomitribe.crest.val.Directory;
import org.tomitribe.crest.val.Readable;
import org.tomitribe.crest.val.Writable;
import org.tomitribe.util.PrintString;
import org.tomitribe.util.collect.ObjectMap;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

@Command("repos")
public class Repos {

    private final GitHub github = Env.github();

    @Command
    public String list(@Option("format") @Default("{sshUrl}") Formatter format) throws IOException {
        final PrintStream out = new PrintString();

        github.getOrganization("eclipse-ee4j")
                .listRepositories().asList().stream()
                .map(format)
                .sorted()
                .forEach(out::println);

        return out.toString();
    }

    @Command
    public String print(String repo) throws IOException {
        final PrintStream out = new PrintString();

        final GHRepository repository = (GHRepository) github.getOrganization("eclipse-ee4j").getRepository(repo);
        if (repository == null) throw new IllegalStateException("No such repository: " + repo);

        new ObjectMap(repository).entrySet().stream()
                .map(Strings::toString)
                .sorted()
                .forEach(out::println);

        return out.toString();
    }


    @Command
    public StreamingOutput clone(@Directory @Readable @Writable final File dir) {
        return os -> {
            final PrintStream out = new PrintStream(os);

            for (final GHRepository repo : github.getOrganization("eclipse-ee4j").listRepositories()) {
                out.printf("Cloning repo %s%n", repo.getName());
                try {
                    jgit("clone", repo.getSshUrl(), new File(dir, repo.getName()).getAbsolutePath());
                } catch (Exception e) {
                    e.printStackTrace(out);
                }
            }
        };
    }

    public static void jgit(final String... args) throws Exception {
        org.eclipse.jgit.pgm.Main.main(args);
    }

    @Command
    public String hello(@Option("name") @Default("World") String name,
                        @Option("language") @Default("EN") Language language) {
        return String.format("%s, %s!", language.getSalutation(), name);
    }

    public static enum Language {
        EN("Hello"),
        ES("Hola"),
        FR("Bonjour");

        private final String salutation;

        private Language(String salutation) {
            this.salutation = salutation;
        }

        public String getSalutation() {
            return salutation;
        }
    }

}
