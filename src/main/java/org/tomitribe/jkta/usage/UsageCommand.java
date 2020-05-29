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
package org.tomitribe.jkta.usage;

import org.tomitribe.crest.api.Command;
import org.tomitribe.crest.api.Default;
import org.tomitribe.crest.api.In;
import org.tomitribe.crest.api.Option;
import org.tomitribe.crest.api.PrintOutput;
import org.tomitribe.crest.val.Exists;
import org.tomitribe.crest.val.Readable;
import org.tomitribe.jkta.util.Predicates;
import org.tomitribe.util.Join;
import org.tomitribe.util.PrintString;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@Command("usage")
public class UsageCommand {

    public String tsvColumns() {
        final ArrayList<String> columns = new ArrayList<>();
        columns.addAll(JarUsage.tsvColumns());
        Stream.of(Package.values())
                .map(Package::getName)
                .forEach(columns::add);

        return Join.join("\t", columns);
    }

    @Command
    public PrintOutput jar(@Exists @Readable final File jar) throws IOException, NoSuchAlgorithmException {

        return out -> {
            { // print the TSV header
                final ArrayList<String> columns = new ArrayList<>();
                columns.add("class name");
                columns.add("javax uses total");
                columns.add("jakarta uses total");
                Stream.of(Package.values())
                        .map(Package::getName)
                        .forEach(columns::add);

                out.println(Join.join("\t", columns));
            }

            final AtomicInteger scanned = new AtomicInteger();
            final AtomicInteger affected = new AtomicInteger();

            final AtomicReference<Usage<String>> total = new AtomicReference<>(new Usage<>("total"));
            ClassUsage.forEachClass(jar, usage -> {
                total.accumulateAndGet(usage, Usage::add);
                scanned.incrementAndGet();
                if (usage.getJavax() > 0) affected.incrementAndGet();
                out.printf("%s\t%s\n", usage.getContext(), usage.toTsv());
            });

            out.printf("%s\t%s\n", JarUsage.summary(scanned.get(), affected.get()), total.get().toTsv());
        };
    }

    @Command
    public PrintOutput dir(@Option("format") @Default("tsv") final Format format,
                           @Option("include") final Pattern include,
                           @Option("exclude") final Pattern exclude,
                           @Option("repository") @Default("${user.dir}") Dir repository,
                           final Dir dir) {
        final Stream<File> fileStream = dir.searcJars();

        return scanFiles(format, include, exclude, repository, fileStream);
    }

    private PrintOutput scanFiles(final Format format, final Pattern include, final Pattern exclude, final Dir repository, final Stream<File> fileStream) {
        final Predicate<File> fileFilter = Predicates.fileFilter(include, exclude);
        final Stream<Usage<Jar>> usageStream = fileStream
                .filter(fileFilter)
                .map(this::jarUsage)
                .filter(Objects::nonNull);


        switch (format) {
            case tsv:
                return out -> {
                    out.println(tsvColumns());
                    final AtomicInteger scanned = new AtomicInteger();
                    final AtomicInteger affected = new AtomicInteger();
                    final Usage<Jar> total = usageStream
                            .peek(jarUsage -> scanned.incrementAndGet())
                            .peek(jarUsage -> {
                                if (jarUsage.getJavax() > 0) affected.incrementAndGet();
                            })
                            .peek(jarUsage -> out.println(JarUsage.toTsv(jarUsage, repository.dir())))
                            .reduce(Usage::add)
                            .orElse(null);

                    if (total == null) {
                        out.println("No jars found");
                        return;
                    }

                    out.println(JarUsage.toTotalTsv(scanned.get(), affected.get(), total));
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
            default: { /* ignored */}
        }

        return printStream -> printStream.println("Unsupported format: " + format);
    }

    /**
     * Read a list of jars from STDIN and scan each one for usages of the affected
     * javax and jakarta namespaces.
     *
     * @param format
     * @param include
     * @param exclude
     * @param repository
     * @param stdin
     * @return
     */
    @Command
    public PrintOutput jars(@Option("format") @Default("tsv") final Format format,
                            @Option("include") final Pattern include,
                            @Option("exclude") final Pattern exclude,
                            @Option("repository") @Default("${user.dir}") Dir repository,
                            @In InputStream stdin
    ) {
        final Stream<File> fileStream = lines(stdin)
                .map(repository::file)
                .filter(File::isFile)
                .filter(new Is.Jar()::accept);

        return scanFiles(format, include, exclude, repository, fileStream);
    }

    //    public static void main(String[] args) {
//        for (final Package p : Package.values()) {
//            final String dashed = p.getName().replace(".", "-");
//            final String var = Strings.lcfirst(Strings.camelCase(dashed));
////            System.out.printf("@Option(\"%s\") final Pattern %s,%n", dashed, var);
//            System.out.printf(".with(%s, Package.%s)%n", var, p.name());
//        }
//    }
//
    private static Stream<String> lines(@In final InputStream stdin) {
        final InputStreamReader reader = new InputStreamReader(stdin);
        final BufferedReader bufferedReader = new BufferedReader(reader);
        return bufferedReader.lines();
    }

    private Usage<Jar> jarUsage(final File file) {
        try {
            return JarUsage.of(file);
        } catch (Exception e) {
            e.printStackTrace();
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

    //CHECKSTYLE:OFF

    /**
     * Greps the specified jar usage tsv report for records containing specific count ranges.
     *
     * Searching for records that have 1 or more uses of the EJB api could be done as follows:
     *
     *     cat uses.tsv | jkta usage grep --javax-ejb="[1-9].*"
     *
     * The following would search for records that have 1 or more uses of both the EJB and JAX-RS apis:
     *
     *     cat uses.tsv | jkta usage grep --javax-ejb="[1-9].*"  --javax-ws-rs="[1-9].*"
     *
     * The following would search for records that have 1 or more uses of either the EJB or JAX-RS apis:
     *
     *     cat uses.tsv | jkta usage grep --javax-ejb="[1-9].*"  --javax-ws-rs="[1-9].*" --mode=OR
     *
     * To search for a range above a hundred, expand the regular expression:
     *
     *     cat uses.tsv | jkta usage grep --javax-ejb="[1-9][0-9][0-9].*"
     *
     * @param in
     * @param javax
     * @param jakarta
     * @param javaxActivation
     * @param javaxAnnotation
     * @param javaxBatch
     * @param javaxDecorator
     * @param javaxEjb
     * @param javaxEl
     * @param javaxEnterprise
     * @param javaxEnterpriseConcurrent
     * @param javaxEnterpriseDeploy
     * @param javaxFaces
     * @param javaxInject
     * @param javaxInterceptor
     * @param javaxJms
     * @param javaxJson
     * @param javaxJsonBind
     * @param javaxJws
     * @param javaxMail
     * @param javaxManagementJ2ee
     * @param javaxPersistence
     * @param javaxResource
     * @param javaxSecurityAuthMessage
     * @param javaxSecurityEnterprise
     * @param javaxSecurityJacc
     * @param javaxServlet
     * @param javaxServletJsp
     * @param javaxServletJspJstl
     * @param javaxTransaction
     * @param javaxValidation
     * @param javaxWebsocket
     * @param javaxWsRs
     * @param javaxXmlBind
     * @param javaxXmlRegistry
     * @param javaxXmlRpc
     * @param javaxXmlSoap
     * @param javaxXmlWs
     * @param jakartaActivation
     * @param jakartaAnnotation
     * @param jakartaBatch
     * @param jakartaDecorator
     * @param jakartaEjb
     * @param jakartaEl
     * @param jakartaEnterprise
     * @param jakartaEnterpriseConcurrent
     * @param jakartaFaces
     * @param jakartaInject
     * @param jakartaInterceptor
     * @param jakartaJms
     * @param jakartaJson
     * @param jakartaJsonBind
     * @param jakartaJws
     * @param jakartaMail
     * @param jakartaPersistence
     * @param jakartaResource
     * @param jakartaSecurityAuthMessage
     * @param jakartaSecurityEnterprise
     * @param jakartaSecurityJacc
     * @param jakartaServlet
     * @param jakartaServletJsp
     * @param jakartaServletJspJstl
     * @param jakartaTransaction
     * @param jakartaValidation
     * @param jakartaWebsocket
     * @param jakartaWsRs
     * @param jakartaXmlBind
     * @param jakartaXmlSoap
     * @param jakartaXmlWs
     * @param mode
     * @return
     */
    @Command
    @SuppressWarnings("checkstyle:ParameterNumber")
    public Stream<String> grep(@In final InputStream in,
                               @Option("javax") final Pattern javax,
                               @Option("jakarta") final Pattern jakarta,
                               @Option("javax-activation") final Pattern javaxActivation,
                               @Option("javax-annotation") final Pattern javaxAnnotation,
                               @Option("javax-batch") final Pattern javaxBatch,
                               @Option("javax-decorator") final Pattern javaxDecorator,
                               @Option("javax-ejb") final Pattern javaxEjb,
                               @Option("javax-el") final Pattern javaxEl,
                               @Option("javax-enterprise") final Pattern javaxEnterprise,
                               @Option("javax-enterprise-concurrent") final Pattern javaxEnterpriseConcurrent,
                               @Option("javax-enterprise-deploy") final Pattern javaxEnterpriseDeploy,
                               @Option("javax-faces") final Pattern javaxFaces,
                               @Option("javax-inject") final Pattern javaxInject,
                               @Option("javax-interceptor") final Pattern javaxInterceptor,
                               @Option("javax-jms") final Pattern javaxJms,
                               @Option("javax-json") final Pattern javaxJson,
                               @Option("javax-json-bind") final Pattern javaxJsonBind,
                               @Option("javax-jws") final Pattern javaxJws,
                               @Option("javax-mail") final Pattern javaxMail,
                               @Option("javax-management-j2ee") final Pattern javaxManagementJ2ee,
                               @Option("javax-persistence") final Pattern javaxPersistence,
                               @Option("javax-resource") final Pattern javaxResource,
                               @Option("javax-security-auth-message") final Pattern javaxSecurityAuthMessage,
                               @Option("javax-security-enterprise") final Pattern javaxSecurityEnterprise,
                               @Option("javax-security-jacc") final Pattern javaxSecurityJacc,
                               @Option("javax-servlet") final Pattern javaxServlet,
                               @Option("javax-servlet-jsp") final Pattern javaxServletJsp,
                               @Option("javax-servlet-jsp-jstl") final Pattern javaxServletJspJstl,
                               @Option("javax-transaction") final Pattern javaxTransaction,
                               @Option("javax-validation") final Pattern javaxValidation,
                               @Option("javax-websocket") final Pattern javaxWebsocket,
                               @Option("javax-ws-rs") final Pattern javaxWsRs,
                               @Option("javax-xml-bind") final Pattern javaxXmlBind,
                               @Option("javax-xml-registry") final Pattern javaxXmlRegistry,
                               @Option("javax-xml-rpc") final Pattern javaxXmlRpc,
                               @Option("javax-xml-soap") final Pattern javaxXmlSoap,
                               @Option("javax-xml-ws") final Pattern javaxXmlWs,
                               @Option("jakarta-activation") final Pattern jakartaActivation,
                               @Option("jakarta-annotation") final Pattern jakartaAnnotation,
                               @Option("jakarta-batch") final Pattern jakartaBatch,
                               @Option("jakarta-decorator") final Pattern jakartaDecorator,
                               @Option("jakarta-ejb") final Pattern jakartaEjb,
                               @Option("jakarta-el") final Pattern jakartaEl,
                               @Option("jakarta-enterprise") final Pattern jakartaEnterprise,
                               @Option("jakarta-enterprise-concurrent") final Pattern jakartaEnterpriseConcurrent,
                               @Option("jakarta-faces") final Pattern jakartaFaces,
                               @Option("jakarta-inject") final Pattern jakartaInject,
                               @Option("jakarta-interceptor") final Pattern jakartaInterceptor,
                               @Option("jakarta-jms") final Pattern jakartaJms,
                               @Option("jakarta-json") final Pattern jakartaJson,
                               @Option("jakarta-json-bind") final Pattern jakartaJsonBind,
                               @Option("jakarta-jws") final Pattern jakartaJws,
                               @Option("jakarta-mail") final Pattern jakartaMail,
                               @Option("jakarta-persistence") final Pattern jakartaPersistence,
                               @Option("jakarta-resource") final Pattern jakartaResource,
                               @Option("jakarta-security-auth-message") final Pattern jakartaSecurityAuthMessage,
                               @Option("jakarta-security-enterprise") final Pattern jakartaSecurityEnterprise,
                               @Option("jakarta-security-jacc") final Pattern jakartaSecurityJacc,
                               @Option("jakarta-servlet") final Pattern jakartaServlet,
                               @Option("jakarta-servlet-jsp") final Pattern jakartaServletJsp,
                               @Option("jakarta-servlet-jsp-jstl") final Pattern jakartaServletJspJstl,
                               @Option("jakarta-transaction") final Pattern jakartaTransaction,
                               @Option("jakarta-validation") final Pattern jakartaValidation,
                               @Option("jakarta-websocket") final Pattern jakartaWebsocket,
                               @Option("jakarta-ws-rs") final Pattern jakartaWsRs,
                               @Option("jakarta-xml-bind") final Pattern jakartaXmlBind,
                               @Option("jakarta-xml-soap") final Pattern jakartaXmlSoap,
                               @Option("jakarta-xml-ws") final Pattern jakartaXmlWs,
                               @Option("mode") @Default("AND") final Mode mode
    ) {

        final Predicate<Usage> usagePredicate = new GrepBuilder(mode)
                .with(javaxActivation, Package.JAVAX_ACTIVATION)
                .with(javaxAnnotation, Package.JAVAX_ANNOTATION)
                .with(javaxBatch, Package.JAVAX_BATCH)
                .with(javaxDecorator, Package.JAVAX_DECORATOR)
                .with(javaxEjb, Package.JAVAX_EJB)
                .with(javaxEl, Package.JAVAX_EL)
                .with(javaxEnterprise, Package.JAVAX_ENTERPRISE)
                .with(javaxEnterpriseConcurrent, Package.JAVAX_ENTERPRISE_CONCURRENT)
                .with(javaxEnterpriseDeploy, Package.JAVAX_ENTERPRISE_DEPLOY)
                .with(javaxFaces, Package.JAVAX_FACES)
                .with(javaxInject, Package.JAVAX_INJECT)
                .with(javaxInterceptor, Package.JAVAX_INTERCEPTOR)
                .with(javaxJms, Package.JAVAX_JMS)
                .with(javaxJson, Package.JAVAX_JSON)
                .with(javaxJsonBind, Package.JAVAX_JSON_BIND)
                .with(javaxJws, Package.JAVAX_JWS)
                .with(javaxMail, Package.JAVAX_MAIL)
                .with(javaxManagementJ2ee, Package.JAVAX_MANAGEMENT_J2EE)
                .with(javaxPersistence, Package.JAVAX_PERSISTENCE)
                .with(javaxResource, Package.JAVAX_RESOURCE)
                .with(javaxSecurityAuthMessage, Package.JAVAX_SECURITY_AUTH_MESSAGE)
                .with(javaxSecurityEnterprise, Package.JAVAX_SECURITY_ENTERPRISE)
                .with(javaxSecurityJacc, Package.JAVAX_SECURITY_JACC)
                .with(javaxServlet, Package.JAVAX_SERVLET)
                .with(javaxServletJsp, Package.JAVAX_SERVLET_JSP)
                .with(javaxServletJspJstl, Package.JAVAX_SERVLET_JSP_JSTL)
                .with(javaxTransaction, Package.JAVAX_TRANSACTION)
                .with(javaxValidation, Package.JAVAX_VALIDATION)
                .with(javaxWebsocket, Package.JAVAX_WEBSOCKET)
                .with(javaxWsRs, Package.JAVAX_WS_RS)
                .with(javaxXmlBind, Package.JAVAX_XML_BIND)
                .with(javaxXmlRegistry, Package.JAVAX_XML_REGISTRY)
                .with(javaxXmlRpc, Package.JAVAX_XML_RPC)
                .with(javaxXmlSoap, Package.JAVAX_XML_SOAP)
                .with(javaxXmlWs, Package.JAVAX_XML_WS)
                .with(jakartaActivation, Package.JAKARTA_ACTIVATION)
                .with(jakartaAnnotation, Package.JAKARTA_ANNOTATION)
                .with(jakartaBatch, Package.JAKARTA_BATCH)
                .with(jakartaDecorator, Package.JAKARTA_DECORATOR)
                .with(jakartaEjb, Package.JAKARTA_EJB)
                .with(jakartaEl, Package.JAKARTA_EL)
                .with(jakartaEnterprise, Package.JAKARTA_ENTERPRISE)
                .with(jakartaEnterpriseConcurrent, Package.JAKARTA_ENTERPRISE_CONCURRENT)
                .with(jakartaFaces, Package.JAKARTA_FACES)
                .with(jakartaInject, Package.JAKARTA_INJECT)
                .with(jakartaInterceptor, Package.JAKARTA_INTERCEPTOR)
                .with(jakartaJms, Package.JAKARTA_JMS)
                .with(jakartaJson, Package.JAKARTA_JSON)
                .with(jakartaJsonBind, Package.JAKARTA_JSON_BIND)
                .with(jakartaJws, Package.JAKARTA_JWS)
                .with(jakartaMail, Package.JAKARTA_MAIL)
                .with(jakartaPersistence, Package.JAKARTA_PERSISTENCE)
                .with(jakartaResource, Package.JAKARTA_RESOURCE)
                .with(jakartaSecurityAuthMessage, Package.JAKARTA_SECURITY_AUTH_MESSAGE)
                .with(jakartaSecurityEnterprise, Package.JAKARTA_SECURITY_ENTERPRISE)
                .with(jakartaSecurityJacc, Package.JAKARTA_SECURITY_JACC)
                .with(jakartaServlet, Package.JAKARTA_SERVLET)
                .with(jakartaServletJsp, Package.JAKARTA_SERVLET_JSP)
                .with(jakartaServletJspJstl, Package.JAKARTA_SERVLET_JSP_JSTL)
                .with(jakartaTransaction, Package.JAKARTA_TRANSACTION)
                .with(jakartaValidation, Package.JAKARTA_VALIDATION)
                .with(jakartaWebsocket, Package.JAKARTA_WEBSOCKET)
                .with(jakartaWsRs, Package.JAKARTA_WS_RS)
                .with(jakartaXmlBind, Package.JAKARTA_XML_BIND)
                .with(jakartaXmlSoap, Package.JAKARTA_XML_SOAP)
                .with(jakartaXmlWs, Package.JAKARTA_XML_WS)
                .with(javax, Usage::getJavax)
                .with(jakarta, Usage::getJavax)
                .build();

        final AtomicInteger lines = new AtomicInteger();
        final Stream<String> matching = lines(in)
                .filter(s -> lines.incrementAndGet() != 1) // Skip the headers
                .filter(s -> !s.startsWith("0000000000000000000000000000000000000000")) // Skip the summary
                .map(JarUsage::fromTsv)
                .filter(usagePredicate)
                .map(jarUsage -> JarUsage.toTsv(jarUsage, new File("")));

        return Stream.concat(
                Stream.of(tsvColumns()),
                matching
        );
    }
    //CHECKSTYLE:ON

    public enum Mode {
        AND(o -> true, Predicate::and),
        OR(o -> false, Predicate::or);

        private final Predicate initial;
        private final BiFunction<Predicate, Predicate<?>, Predicate<?>> accumulate;

        Mode(final Predicate initial, final BiFunction<Predicate, Predicate<?>, Predicate<?>> accumulate) {
            this.initial = initial;
            this.accumulate = accumulate;
        }

        public Predicate getInitial() {
            return initial;
        }

        public <T> Predicate<T> apply(final Predicate<T> one, Predicate<T> two) {
            return (Predicate<T>) accumulate.apply(one, two);
        }
    }

    public static class GrepBuilder {
        private final Mode mode;
        private Predicate<Usage> compoundPredicate;

        public GrepBuilder(final Mode mode) {
            this.mode = mode;
            this.compoundPredicate = mode.getInitial();
        }

        public GrepBuilder with(final Predicate<Usage> predicate) {
            if (predicate == null) return this;
            this.compoundPredicate = mode.apply(this.compoundPredicate, predicate);
            return this;
        }

        public GrepBuilder with(final Pattern pattern, final Package aPackage) {
            if (pattern == null) return this;
            return with(usage -> pattern.matcher(usage.get(aPackage) + "").matches());
        }

        public GrepBuilder with(final Pattern pattern, final Function<Usage, Integer> getter) {
            if (pattern == null) return this;
            return with(usage -> pattern.matcher(getter.apply(usage) + "").matches());
        }

        public Predicate<Usage> build() {
            return compoundPredicate;
        }
    }
}
