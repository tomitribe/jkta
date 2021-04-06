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

import org.objectweb.asm.ClassReader;
import org.tomitribe.jkta.usage.scan.ClassScanner;
import org.tomitribe.jkta.usage.scan.Usage;
import org.tomitribe.util.IO;
import org.tomitribe.util.dir.Filter;
import org.tomitribe.util.dir.Walk;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.UncheckedIOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class EjbTestAnalysis {

    private EjbTestAnalysis() {
    }


    public static void main(String[] args) throws Exception {
        final List<EjbUsage> ejbUsages;
        {
            final List<Section> sections = Arrays.asList(
                    Section.from("/Users/dblevins/work/tcks/jakartaeetck-9.1.0-2021-03-24.1451/dist/com/sun/ts/tests/ejb32"),
                    Section.from("/Users/dblevins/work/tcks/jakartaeetck-9.1.0-2021-03-24.1451/dist/com/sun/ts/tests/ejb30"),
                    Section.from("/Users/dblevins/work/tcks/jakartaeetck-9.1.0-2021-03-24.1451/dist/com/sun/ts/tests/ejb")
            );

            ejbUsages = sections.stream()
                    .flatMap(Section::archives)
                    .map(EjbUsage::scan)
                    .collect(Collectors.toList());
        }

        final Properties properties = new Properties();
        try (final InputStream in = IO.read(new File("/Users/dblevins/work/eclipse-ee4j/jakartaee-tck/src/com/sun/ts/lib/harness/keyword.properties"))) {
            properties.load(in);
        }

        final List<String> alreadyExcluded = properties.entrySet().stream()
                .filter(entry -> entry.getValue().toString().contains("ejb_1x_optional") || entry.getValue().toString().contains("ejb_2x_optional"))
                .map(entry -> entry.getKey().toString())
                .collect(Collectors.toList());

        final Map<String, Set<String>> sections = new HashMap<>();

//        for (final String section : alreadyExcluded) {
//            final Set<String> flags = sections.computeIfAbsent(section, s -> new HashSet<>());
//            flags.add("optional");
//        }
//
        for (final EjbUsage ejbUsage : ejbUsages) {
            final String section = ejbUsage.getSection();
            final Set<String> flags = sections.computeIfAbsent(section, s -> new HashSet<>());

            /*
             * Has this package been excluded already?
             */
            for (final String s : alreadyExcluded) {
                if (section.startsWith(s)) {
                    flags.add("optional");
                    break;
                }
            }

            /*
             * Does this section use the EntityBeans optional group
             */
            if (ejbUsage.usesEntityBeans()) {
                flags.add("entitybeans");
            }

            /*
             * Does this section use the EJB 2.x optional group
             */
            if (ejbUsage.usesEjb2Group()) {
                flags.add("ejb2x");
            }
        }

        for (final Map.Entry<String, Set<String>> entry : sections.entrySet()) {
            final Set<String> flags = entry.getValue();

            /*
             * Should this be marked as optional and actually is not marked optional?
             */
            if (flags.contains("entitybeans") || flags.contains("ejb2x")) {
                if (flags.contains("optional")) {
                    flags.add("new_optional");
                }
            }

            /*
             * Should this be marked as optional and actually is not marked optional?
             */
            if (flags.contains("optional")) {
                if (flags.contains("entitybeans") || flags.contains("ejb2x")) {
                    flags.add("good_optional");
                } else {
                    flags.add("bad_optional");
                }
            }
        }

        final Map<String, PrintStream> reports = new HashMap<>();

        sections.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> {
                    for (final String flag : entry.getValue()) {
                        final PrintStream out = reports.computeIfAbsent(flag, EjbTestAnalysis::openReportStream);
                        out.println(entry.getKey());
                    }
                });

        reports.values().forEach(PrintStream::close);
    }

    private static PrintStream openReportStream(final String s) {
        try {
            return IO.print(new File("/tmp/sections/" + s + ".txt"));
        } catch (FileNotFoundException e) {
            throw new UncheckedIOException(e);
        }
    }

    private static void scanArchive(final InputStream read, final EjbUsage ejbUsage) throws IOException {
        final ZipInputStream zipInputStream = new ZipInputStream(read);

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            final String path = entry.getName();

            if (path.endsWith(".class")) {
                scanClass(zipInputStream, ejbUsage);
            } else if (path.endsWith(".ear")) {
                scanArchive(zipInputStream, ejbUsage);
            } else if (path.endsWith(".jar")) {
                scanArchive(zipInputStream, ejbUsage);
            } else if (path.endsWith(".war")) {
                scanArchive(zipInputStream, ejbUsage);
            } else if (path.endsWith(".rar")) {
                scanArchive(zipInputStream, ejbUsage);
            } else {
                IO.copy(zipInputStream, ignore);
            }
        }
    }

    public static class EjbUsage implements Usage<File> {
        private final Set<String> seen = new HashSet<>();
        private final File archive;

        public EjbUsage(final File archive) {
            this.archive = archive;
        }

        @Override
        public void accept(final String s) {
            if (!s.startsWith("jakarta.ejb")) return;
            seen.add(s);
        }

        @Override
        public File getContext() {
            return archive;
        }

        public String getSection() {
            return archive.getParentFile().getAbsolutePath().replaceAll(".*/dist/", "");
        }

        public boolean usesEntityBeans() {
            final List<String> classes = Arrays.asList(
                    "jakarta.ejb.DuplicateKeyException",
                    "jakarta.ejb.EntityBean",
                    "jakarta.ejb.EntityContext",
                    "jakarta.ejb.FinderException",
                    "jakarta.ejb.NoSuchEntityException"
            );

            for (final String clazz : classes) {
                if (seen.contains(clazz)) return true;
            }

            return false;
        }

        public boolean usesEjb2Group() {
            final List<String> classes = Arrays.asList(
                    "jakarta.ejb.EJBHome",
                    "jakarta.ejb.EJBLocalHome",
                    "jakarta.ejb.EJBLocalObject",
                    "jakarta.ejb.EJBMetaData",
                    "jakarta.ejb.EJBObject",
                    "jakarta.ejb.EnterpriseBean",
                    "jakarta.ejb.Handle",
                    "jakarta.ejb.HomeHandle",
                    "jakarta.ejb.LocalHome",
                    "jakarta.ejb.RemoteHome",
                    "jakarta.ejb.SessionSynchronization",
                    "jakarta.ejb.TimedObject"
            );

            for (final String clazz : classes) {
                if (seen.contains(clazz)) return true;
            }

            return false;
        }

        private static EjbUsage scan(final File jar) {

            try {
                final EjbUsage ejbUsage = new EjbUsage(jar);

                try (final InputStream read = IO.read(jar)) {
                    scanArchive(read, ejbUsage);
                }

                return ejbUsage;
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    public static class Clazz {
        private final String name;
        private final int version;

        public Clazz(final String name, final int version) {
            this.name = name;
            this.version = version;
        }

        public String getName() {
            return name;
        }

        public int getVersion() {
            return version;
        }
    }


    private static final OutputStream ignore = new OutputStream() {
        @Override
        public void write(final int b) {
        }
    };

    private static int scanClass(final InputStream in, final Usage<?> usage) throws IOException {
        final ClassScanner classScanner = new ClassScanner(usage);
        final ClassReader classReader = new ClassReader(in);
        classReader.accept(classScanner, 0);
        return classScanner.getVersion();
    }

    public interface Section extends Dir {

        @Walk
        @Filter(Is.Scannable.class)
        Stream<File> archives();

        static Section from(final String path) {
            return from(new File(path));
        }

        static Section from(final File file) {
            return org.tomitribe.util.dir.Dir.of(Section.class, file);
        }

    }
}
