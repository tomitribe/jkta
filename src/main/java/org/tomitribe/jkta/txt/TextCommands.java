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
import org.tomitribe.swizzle.stream.StreamBuilder;
import org.tomitribe.tio.ColoredMatches;
import org.tomitribe.tio.Grep;
import org.tomitribe.tio.GrepCommand;
import org.tomitribe.tio.Match;
import org.tomitribe.tio.PatternMatcher;
import org.tomitribe.util.IO;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Command("text")
public class TextCommands {

    @Command(interceptedBy = ColoredMatches.class)
    public Stream<Match> grep(@Option("include") final Pattern include,
                              @Option("exclude") final Pattern exclude,
                              final org.tomitribe.tio.Dir dir) {

        final String invalidPackages = "" +
                "javax\\.activation" +
                "|javax\\.annotation" +
                "|javax\\.batch" +
                "|javax\\.decorator" +
                "|javax\\.ejb" +
                "|javax\\.el" +
                "|javax\\.enterprise" +
                "|javax\\.faces" +
                "|javax\\.inject" +
                "|javax\\.interceptor" +
                "|javax\\.jms" +
                "|javax\\.json" +
                "|javax\\.json\\.bind" +
                "|javax\\.jws" +
                "|javax\\.mail" +
                "|javax\\.persistence" +
                "|javax\\.resource" +
                "|javax\\.security\\.auth\\.message" +
                "|javax\\.security\\.enterprise" +
                "|javax\\.security\\.jacc" +
                "|javax\\.servlet" +
                "|javax\\.transaction" +
                "|javax\\.validation" +
                "|javax\\.websocket" +
                "|javax\\.ws\\.rs" +
                "|javax\\.xml\\.bind" +
                "|javax\\.xml\\.soap" +
                "|javax\\.xml\\.ws" +
                "|jakarta\\.annotation\\.process" +
                "|jakarta\\.enterprise\\.deploy" +
                "|jakarta\\.transaction\\.xa" +
                "|jakarta\\.accessibility" +
                "|jakarta\\.annotation\\.processing" +
                "|jakarta\\.cache" +
                "|jakarta\\.crypto" +
                "|jakarta\\.imageio" +
                "|jakarta\\.jdo" +
                "|jakarta\\.jmdns" +
                "|jakarta\\.lang" +
                "|jakarta\\.lang\\.model" +
                "|jakarta\\.management" +
                "|jakarta\\.naming" +
                "|jakarta\\.net" +
                "|jakarta\\.portlet" +
                "|jakarta\\.print" +
                "|jakarta\\.rmi" +
                "|jakarta\\.script" +
                "|jakarta\\.security\\.Principal" +
                "|jakarta\\.security\\.auth\\.AuthPermission" +
                "|jakarta\\.security\\.auth\\.Deprecated" +
                "|jakarta\\.security\\.auth\\.DestroyFailedException" +
                "|jakarta\\.security\\.auth\\.Destroyable" +
                "|jakarta\\.security\\.auth\\.LdapPrincipal" +
                "|jakarta\\.security\\.auth\\.NTDomainPrincipal" +
                "|jakarta\\.security\\.auth\\.NTNumericCredential" +
                "|jakarta\\.security\\.auth\\.NTSid" +
                "|jakarta\\.security\\.auth\\.NTSidDomainPrincipal" +
                "|jakarta\\.security\\.auth\\.NTSidGroupPrincipal" +
                "|jakarta\\.security\\.auth\\.NTSidPrimaryGroupPrincipal" +
                "|jakarta\\.security\\.auth\\.NTSidUserPrincipal" +
                "|jakarta\\.security\\.auth\\.NTUserPrincipal" +
                "|jakarta\\.security\\.auth\\.PolicyFile" +
                "|jakarta\\.security\\.auth\\.PrincipalComparator" +
                "|jakarta\\.security\\.auth\\.PrivateCredentialPermission" +
                "|jakarta\\.security\\.auth\\.RefreshFailedException" +
                "|jakarta\\.security\\.auth\\.Refreshable" +
                "|jakarta\\.security\\.auth\\.SolarisNumericGroupPrincipal" +
                "|jakarta\\.security\\.auth\\.SolarisNumericUserPrincipal" +
                "|jakarta\\.security\\.auth\\.SolarisPrincipal" +
                "|jakarta\\.security\\.auth\\.Subject" +
                "|jakarta\\.security\\.auth\\.SubjectDomainCombiner" +
                "|jakarta\\.security\\.auth\\.UnixNumericGroupPrincipal" +
                "|jakarta\\.security\\.auth\\.UnixNumericUserPrincipal" +
                "|jakarta\\.security\\.auth\\.UnixPrincipal" +
                "|jakarta\\.security\\.auth\\.UserPrincipal" +
                "|jakarta\\.security\\.auth\\.X500Principal" +
                "|jakarta\\.security\\.auth\\.callback" +
                "|jakarta\\.security\\.auth\\.kerberos" +
                "|jakarta\\.security\\.auth\\.login" +
                "|jakarta\\.security\\.auth\\.spi" +
                "|jakarta\\.security\\.auth\\.subject" +
                "|jakarta\\.security\\.auth\\.x500" +
                "|jakarta\\.security\\.cert" +
                "|jakarta\\.security\\.sasl" +
                "|jakarta\\.smartcardio" +
                "|jakarta\\.sound" +
                "|jakarta\\.sql" +
                "|jakarta\\.swing" +
                "|jakarta\\.tools" +
                "|jakarta\\.transaction\\.xa" +
                "|jakarta\\.wsdl" +
                "|jakarta\\.xml\\.XML" +
                "|jakarta\\.xml\\.access" +
                "|jakarta\\.xml\\.catalog" +
                "|jakarta\\.xml\\.crypto" +
                "|jakarta\\.xml\\.datatype" +
                "|jakarta\\.xml\\.messaging" +
                "|jakarta\\.xml\\.namespace" +
                "|jakarta\\.xml\\.parser" +
                "|jakarta\\.xml\\.parsers" +
                "|jakarta\\.xml\\.registry" +
                "|jakarta\\.xml\\.rpc" +
                "|jakarta\\.xml\\.stream" +
                "|jakarta\\.xml\\.transform" +
                "|jakarta\\.xml\\.validation" +
                "|jakarta\\.xml\\.xpath";

        /*
         * The following packages are excluded from the above matches
         */
        final String excludedPackages = "javax\\.annotation\\.process|javax\\.enterprise\\.deploy|javax\\.transaction\\.xa";
        final Pattern exclusions = Pattern.compile(excludedPackages);
        final Predicate<File> fileFilter = org.tomitribe.tio.Predicates.fileFilter(include, exclude)
                .and(file -> GrepCommand.binaries.asPredicate().negate().test(file.getName()));

        final Grep firstGrep = Grep.builder()
                .dir(dir)
                .matcher(PatternMatcher.from(invalidPackages))
                .build();

        return dir.searchFiles()
                .filter(Grep::excludeGitFiles)
                .filter(fileFilter)
                .flatMap(firstGrep::file)
                .filter(match -> !exclusions.matcher(match.getLine().getText()).find());
    }

    @Command
    public void replace(@Option("include") final Pattern include,
                        @Option("exclude") final Pattern exclude,
                        final Dir dir) {

        final Predicate<File> fileFilter = Predicates.fileFilter(include, exclude);
        dir.files()
                .filter(File::isFile)
                .filter(file -> !file.getAbsolutePath().contains("/.git/"))
                .filter(fileFilter)
                .forEach(this::jakartize);
    }

    @Command("replace-invalid")
    public void fixBadRenames(@Option("include") final Pattern include,
                              @Option("exclude") final Pattern exclude,
                              final Dir dir) {

        final Predicate<File> fileFilter = Predicates.fileFilter(include, exclude);
        dir.files()
                .filter(File::isFile)
                .filter(file -> !file.getAbsolutePath().contains("/.git/"))
                .filter(fileFilter)
                .forEach(this::fixBadRenames);
    }

    @Command("replace-branding")
    public void fixBranding(@Option("include") final Pattern include,
                            @Option("exclude") final Pattern exclude,
                            final Dir dir) {

        final Predicate<File> fileFilter = Predicates.fileFilter(include, exclude);
        dir.files()
                .filter(File::isFile)
                .filter(file -> !file.getAbsolutePath().contains("/.git/"))
                .filter(fileFilter)
                .forEach(this::fixBranding);
    }

    @Command("replace-ejb")
    public void fixEjb(@Option("include") final Pattern include,
                       @Option("exclude") final Pattern exclude,
                       final Dir dir) {

        final Predicate<File> fileFilter = Predicates.fileFilter(include, exclude);
        dir.files()
                .filter(File::isFile)
                .filter(file -> !file.getAbsolutePath().contains("/.git/"))
                .filter(fileFilter)
                .forEach(this::fixEjb);
    }

    private void jakartize(final File file) {
        try {
            final InputStream inputStream = StreamBuilder.create(IO.read(file))
                    .replace("javax.activation", "jakarta.activation")
                    .replace("javax.annotation", "jakarta.annotation")
                    .replace("javax.batch", "jakarta.batch")
                    .replace("javax.decorator", "jakarta.decorator")
                    .replace("javax.ejb", "jakarta.ejb")
                    .replace("javax.el", "jakarta.el")
                    .replace("javax.enterprise", "jakarta.enterprise")
                    .replace("javax.faces", "jakarta.faces")
                    .replace("javax.inject", "jakarta.inject")
                    .replace("javax.interceptor", "jakarta.interceptor")
                    .replace("javax.jms", "jakarta.jms")
                    .replace("javax.json", "jakarta.json")
                    .replace("javax.json.bind", "jakarta.json.bind")
                    .replace("javax.jws", "jakarta.jws")
                    .replace("javax.mail", "jakarta.mail")
                    .replace("javax.persistence", "jakarta.persistence")
                    .replace("javax.resource", "jakarta.resource")
                    .replace("javax.security.auth.message", "jakarta.security.auth.message")
                    .replace("javax.security.enterprise", "jakarta.security.enterprise")
                    .replace("javax.security.jacc", "jakarta.security.jacc")
                    .replace("javax.servlet", "jakarta.servlet")
                    .replace("javax.transaction", "jakarta.transaction")
                    .replace("javax.validation", "jakarta.validation")
                    .replace("javax.websocket", "jakarta.websocket")
                    .replace("javax.ws.rs", "jakarta.ws.rs")
                    .replace("javax.xml.bind", "jakarta.xml.bind")
                    .replace("javax.xml.soap", "jakarta.xml.soap")
                    .replace("javax.xml.ws", "jakarta.xml.ws")

                    // These sub packages to the above must be renamed back
                    .replace("jakarta.annotation.process", "javax.annotation.process")
                    .replace("jakarta.enterprise.deploy", "javax.enterprise.deploy")
                    .replace("jakarta.transaction.xa", "javax.transaction.xa")

//                    // Packages that are often falsely renamed
//                    .replace("jakarta.cache", "javax.cache")
//                    .replace("jakarta.crypto", "javax.crypto")
//                    .replace("jakarta.jdo", "javax.jdo")
//                    .replace("jakarta.jmdns", "javax.jmdns")
//                    .replace("jakarta.lang", "javax.lang")
//                    .replace("jakarta.management", "javax.management")
//                    .replace("jakarta.naming", "javax.naming")
//                    .replace("jakarta.net", "javax.net")
//                    .replace("jakarta.portlet", "javax.portlet")
//                    .replace("jakarta.rmi", "javax.rmi")
//                    .replace("jakarta.script", "javax.script")
//                    .replace("jakarta.security.Principal", "javax.security.Principal")
//                    .replace("jakarta.security.auth.Deprecated", "javax.security.auth.Deprecated")
//                    .replace("jakarta.security.auth.Deprecated", "javax.security.auth.Deprecated")
//                    .replace("jakarta.security.auth.Deprecated", "javax.security.auth.Deprecated")
//                    .replace("jakarta.security.auth.Deprecated", "javax.security.auth.Deprecated")
//                    .replace("jakarta.security.auth.LdapPrincipal", "javax.security.auth.LdapPrincipal")
//                    .replace("jakarta.security.auth.NTDomainPrincipal", "javax.security.auth.NTDomainPrincipal")
//                    .replace("jakarta.security.auth.NTNumericCredential", "javax.security.auth.NTNumericCredential")
//                    .replace("jakarta.security.auth.NTSid", "javax.security.auth.NTSid")
//                    .replace("jakarta.security.auth.NTSidDomainPrincipal", "javax.security.auth.NTSidDomainPrincipal")
//                    .replace("jakarta.security.auth.NTSidGroupPrincipal", "javax.security.auth.NTSidGroupPrincipal")
//                    .replace("jakarta.security.auth.NTSidPrimaryGroupPrincipal", "javax.security.auth.NTSidPrimaryGroupPrincipal")
//                    .replace("jakarta.security.auth.NTSidUserPrincipal", "javax.security.auth.NTSidUserPrincipal")
//                    .replace("jakarta.security.auth.NTUserPrincipal", "javax.security.auth.NTUserPrincipal")
//                    .replace("jakarta.security.auth.PolicyFile", "javax.security.auth.PolicyFile")
//                    .replace("jakarta.security.auth.PrincipalComparator", "javax.security.auth.PrincipalComparator")
//                    .replace("jakarta.security.auth.SolarisNumericGroupPrincipal", "javax.security.auth.SolarisNumericGroupPrincipal")
//                    .replace("jakarta.security.auth.SolarisNumericUserPrincipal", "javax.security.auth.SolarisNumericUserPrincipal")
//                    .replace("jakarta.security.auth.SolarisPrincipal", "javax.security.auth.SolarisPrincipal")
//                    .replace("jakarta.security.auth.UnixNumericGroupPrincipal", "javax.security.auth.UnixNumericGroupPrincipal")
//                    .replace("jakarta.security.auth.UnixNumericUserPrincipal", "javax.security.auth.UnixNumericUserPrincipal")
//                    .replace("jakarta.security.auth.UnixPrincipal", "javax.security.auth.UnixPrincipal")
//                    .replace("jakarta.security.auth.UserPrincipal", "javax.security.auth.UserPrincipal")
//                    .replace("jakarta.security.auth.X500Principal", "javax.security.auth.X500Principal")
//                    .replace("jakarta.security.auth.callback", "javax.security.auth.callback")
//                    .replace("jakarta.security.auth.kerberos", "javax.security.auth.kerberos")
//                    .replace("jakarta.security.auth.login", "javax.security.auth.login")
//                    .replace("jakarta.security.auth.spi", "javax.security.auth.spi")
//                    .replace("jakarta.security.auth.subject", "javax.security.auth.subject")
//                    .replace("jakarta.security.auth.x500", "javax.security.auth.x500")
//                    .replace("jakarta.security.cert", "javax.security.cert")
//                    .replace("jakarta.security.sasl", "javax.security.sasl")
//                    .replace("jakarta.sql", "javax.sql")
//                    .replace("jakarta.swing", "javax.swing")
//                    .replace("jakarta.tools", "javax.tools")
//                    .replace("jakarta.wsdl", "javax.wsdl")
//                    .replace("jakarta.xml.XML", "javax.xml.XML")
//                    .replace("jakarta.xml.access", "javax.xml.access")
//                    .replace("jakarta.xml.crypto", "javax.xml.crypto")
//                    .replace("jakarta.xml.datatype", "javax.xml.datatype")
//                    .replace("jakarta.xml.messaging", "javax.xml.messaging")
//                    .replace("jakarta.xml.namespace", "javax.xml.namespace")
//                    .replace("jakarta.xml.parser", "javax.xml.parser")
//                    .replace("jakarta.xml.registry", "javax.xml.registry")
//                    .replace("jakarta.xml.rpc", "javax.xml.rpc")
//                    .replace("jakarta.xml.stream", "javax.xml.stream")
//                    .replace("jakarta.xml.stream", "javax.xml.stream")
//                    .replace("jakarta.xml.transform", "javax.xml.transform")
//                    .replace("jakarta.xml.validation", "javax.xml.validation")
//                    .replace("jakarta.xml.xpath", "javax.xml.xpath")
//                    // Exceptions to the exceptions
//                    .replace("javax.enterprise.deploy-api", "jakarta.enterprise.deploy-api")
                    .get();

            final String content = IO.slurp(inputStream);
            IO.copy(IO.read(content), file);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process file: " + file.getAbsolutePath(), e);
        }

    }

    private void fixBadRenames(final File file) {
        try {
            final InputStream inputStream = StreamBuilder.create(IO.read(file))

                    // These sub packages to the above must be renamed back
                    .replace("jakarta.annotation.process", "javax.annotation.process")
                    .replace("jakarta.enterprise.deploy", "javax.enterprise.deploy")
                    .replace("jakarta.transaction.xa", "javax.transaction.xa")

                    // Packages that are often falsely renamed
                    // Exceptions to the exceptions

                    .replace("jakarta.accessibility", "javax.accessibility")
                    .replace("jakarta.annotation.processing", "javax.annotation.processing")
                    .replace("jakarta.cache", "javax.cache")
                    .replace("jakarta.crypto", "javax.crypto")
                    .replace("jakarta.imageio", "javax.imageio")
                    .replace("jakarta.jdo", "javax.jdo")
                    .replace("jakarta.jmdns", "javax.jmdns")
                    .replace("jakarta.lang", "javax.lang")
                    .replace("jakarta.lang.model", "javax.lang.model")
                    .replace("jakarta.management", "javax.management")
                    .replace("jakarta.naming", "javax.naming")
                    .replace("jakarta.net", "javax.net")
                    .replace("jakarta.portlet", "javax.portlet")
                    .replace("jakarta.print", "javax.print")
                    .replace("jakarta.rmi", "javax.rmi")
                    .replace("jakarta.script", "javax.script")
                    .replace("jakarta.security.Principal", "javax.security.Principal")
                    .replace("jakarta.security.auth.AuthPermission", "javax.security.auth.AuthPermission")
                    .replace("jakarta.security.auth.Deprecated", "javax.security.auth.Deprecated")
                    .replace("jakarta.security.auth.DestroyFailedException", "javax.security.auth.DestroyFailedException")
                    .replace("jakarta.security.auth.Destroyable", "javax.security.auth.Destroyable")
                    .replace("jakarta.security.auth.LdapPrincipal", "javax.security.auth.LdapPrincipal")
                    .replace("jakarta.security.auth.NTDomainPrincipal", "javax.security.auth.NTDomainPrincipal")
                    .replace("jakarta.security.auth.NTNumericCredential", "javax.security.auth.NTNumericCredential")
                    .replace("jakarta.security.auth.NTSid", "javax.security.auth.NTSid")
                    .replace("jakarta.security.auth.NTSidDomainPrincipal", "javax.security.auth.NTSidDomainPrincipal")
                    .replace("jakarta.security.auth.NTSidGroupPrincipal", "javax.security.auth.NTSidGroupPrincipal")
                    .replace("jakarta.security.auth.NTSidPrimaryGroupPrincipal", "javax.security.auth.NTSidPrimaryGroupPrincipal")
                    .replace("jakarta.security.auth.NTSidUserPrincipal", "javax.security.auth.NTSidUserPrincipal")
                    .replace("jakarta.security.auth.NTUserPrincipal", "javax.security.auth.NTUserPrincipal")
                    .replace("jakarta.security.auth.PolicyFile", "javax.security.auth.PolicyFile")
                    .replace("jakarta.security.auth.PrincipalComparator", "javax.security.auth.PrincipalComparator")
                    .replace("jakarta.security.auth.PrivateCredentialPermission", "javax.security.auth.PrivateCredentialPermission")
                    .replace("jakarta.security.auth.RefreshFailedException", "javax.security.auth.RefreshFailedException")
                    .replace("jakarta.security.auth.Refreshable", "javax.security.auth.Refreshable")
                    .replace("jakarta.security.auth.SolarisNumericGroupPrincipal", "javax.security.auth.SolarisNumericGroupPrincipal")
                    .replace("jakarta.security.auth.SolarisNumericUserPrincipal", "javax.security.auth.SolarisNumericUserPrincipal")
                    .replace("jakarta.security.auth.SolarisPrincipal", "javax.security.auth.SolarisPrincipal")
                    .replace("jakarta.security.auth.Subject", "javax.security.auth.Subject")
                    .replace("jakarta.security.auth.SubjectDomainCombiner", "javax.security.auth.SubjectDomainCombiner")
                    .replace("jakarta.security.auth.UnixNumericGroupPrincipal", "javax.security.auth.UnixNumericGroupPrincipal")
                    .replace("jakarta.security.auth.UnixNumericUserPrincipal", "javax.security.auth.UnixNumericUserPrincipal")
                    .replace("jakarta.security.auth.UnixPrincipal", "javax.security.auth.UnixPrincipal")
                    .replace("jakarta.security.auth.UserPrincipal", "javax.security.auth.UserPrincipal")
                    .replace("jakarta.security.auth.X500Principal", "javax.security.auth.X500Principal")
                    .replace("jakarta.security.auth.callback", "javax.security.auth.callback")
                    .replace("jakarta.security.auth.kerberos", "javax.security.auth.kerberos")
                    .replace("jakarta.security.auth.login", "javax.security.auth.login")
                    .replace("jakarta.security.auth.spi", "javax.security.auth.spi")
                    .replace("jakarta.security.auth.subject", "javax.security.auth.subject")
                    .replace("jakarta.security.auth.x500", "javax.security.auth.x500")
                    .replace("jakarta.security.cert", "javax.security.cert")
                    .replace("jakarta.security.sasl", "javax.security.sasl")
                    .replace("jakarta.smartcardio", "javax.smartcardio")
                    .replace("jakarta.sound", "javax.sound")
                    .replace("jakarta.sql", "javax.sql")
                    .replace("jakarta.swing", "javax.swing")
                    .replace("jakarta.tools", "javax.tools")
                    .replace("jakarta.transaction.xa", "javax.transaction.xa")
                    .replace("jakarta.wsdl", "javax.wsdl")
                    .replace("jakarta.xml.XML", "javax.xml.XML")
                    .replace("jakarta.xml.access", "javax.xml.access")
                    .replace("jakarta.xml.catalog", "javax.xml.catalog")
                    .replace("jakarta.xml.crypto", "javax.xml.crypto")
                    .replace("jakarta.xml.datatype", "javax.xml.datatype")
                    .replace("jakarta.xml.messaging", "javax.xml.messaging")
                    .replace("jakarta.xml.namespace", "javax.xml.namespace")
                    .replace("jakarta.xml.parser", "javax.xml.parser")
                    .replace("jakarta.xml.parsers", "javax.xml.parsers")
                    .replace("jakarta.xml.registry", "javax.xml.registry")
                    .replace("jakarta.xml.rpc", "javax.xml.rpc")
                    .replace("jakarta.xml.stream", "javax.xml.stream")
                    .replace("jakarta.xml.transform", "javax.xml.transform")
                    .replace("jakarta.xml.validation", "javax.xml.validation")
                    .replace("jakarta.xml.xpath", "javax.xml.xpath")
                    .replace("javax.enterprise.deploy-api", "jakarta.enterprise.deploy-api")
                    .get();

            final String content = IO.slurp(inputStream);
            IO.copy(IO.read(content), file);
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to process file: " + file.getAbsolutePath(), e);
        }

    }

    private void fixBranding(final File file) {
        try {
            final InputStream inputStream = StreamBuilder
                    .create(IO.read(file))

                    // common typos
                    .replace("Jarkarta", "Jakarta")
                    .replace("Jakara", "Jakarta")
                    .replace("Jakata", "Jakarta")

                    // replace all Java EE with Jakarta EE
                    .replace("Java EE", "Jakarta EE")

                    // put back Java when it's allowed
                    // Correct                                    Incorrect
                    //
                    // - J2EE 1.2                              Java EE 1.2, Jakarta EE 1.2
                    // - J2EE 1.3                              Java EE 1.3, Jakarta EE 1.3
                    // - J2EE 1.4                              Java EE 1.4, Jakarta EE 1.4
                    // - Java EE 5                             J2EE 1.5, Jakarta EE 5
                    // - Java EE 6                             J2EE 1.6, Jakarta EE 6
                    // - Java EE 7                             J2EE 1.7, Jakarta EE 7
                    // - Java EE 8                             J2EE 1.8
                    // - Jakarta EE 8                          J2EE 1.8
                    // - Jakarta EE 9                          J2EE 1.9, Java EE 9

                    .replace("Java EE 1.2", "J2EE 1.2")
                    .replace("Jakarta EE 1.2", "J2EE 1.2")

                    .replace("Java EE 1.3", "J2EE 1.3")
                    .replace("Jakarta EE 1.3", "J2EE 1.3")

                    .replace("Java EE 1.4", "J2EE 1.4")
                    .replace("Jakarta EE 1.4", "J2EE 1.4")

                    .replace("J2EE 1.5", "Java EE 5")
                    .replace("Jakarta EE 5", "Java EE 5")

                    .replace("J2EE 1.6", "Java EE 6")
                    .replace("Jakarta EE 6", "Java EE 6")

                    .replace("J2EE 1.7", "Java EE 7")
                    .replace("Jakarta EE 7", "Java EE 7")

                    .replace("J2EE 1.8", "Java EE 8")
                    .replace("Jakarta EE 8", "Java EE 8")

                    .replace("J2EE 1.9", "Jakarta EE 9")
                    .replace("Java EE 9", "Jakarta EE 9")

                    .get();

            final String content = IO.slurp(inputStream);
            IO.copy(IO.read(content), file);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to process file: " + file.getAbsolutePath(), e);
        }
    }

    private void fixEjb(final File file) {
        try {
            final InputStream inputStream = StreamBuilder
                    .create(IO.read(file))


                    .get();

            final String content = IO.slurp(inputStream);
            IO.copy(IO.read(content), file);
        } catch (final IOException e) {
            throw new UncheckedIOException("Failed to process file: " + file.getAbsolutePath(), e);
        }
    }
}
