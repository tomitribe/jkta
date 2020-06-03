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

import org.junit.Test;
import org.tomitribe.jkta.Results;
import org.tomitribe.jkta.usage.tsv.ScanTsvTest;
import org.tomitribe.util.Files;
import org.tomitribe.util.IO;
import org.tomitribe.util.Mvn;
import org.tomitribe.util.Zips;

import java.io.File;
import java.util.TimeZone;

import static org.tomitribe.jkta.CommandAssertion.command;
import static org.tomitribe.jkta.Resources.load;

public class JarsTest {

    @Test
    public void test() throws Exception {
        final File zip = Mvn.mvn("org.apache.tomcat:tomcat:zip:10.0.0-M5");
        final File tmpdir = Files.tmpdir();
        Zips.unzip(zip, tmpdir);
        final String list = list(tmpdir);
        command(UsageCommand.class)
                .input(list)
                .output(load("tomcat-10.0.0-M5.tsv"))
                .results(this::normalize)
                .exec("usage", "jars", "--repository=" + tmpdir.getAbsolutePath());
    }

    @Test
    public void testZip() throws Exception {
        final File zip = Mvn.mvn("org.apache.tomcat:tomcat:zip:10.0.0-M5");
        final File tmpdir = Files.tmpdir();
        final File target = new File(tmpdir, zip.getName());
        IO.copy(zip, target);

        final String list = list(tmpdir);
        target.setLastModified(1588732796000L);

        command(UsageCommand.class)
                .input(list)
                .output(load("tomcat-10.0.0-M5-zip.tsv"))
                .results(this::normalize)
                .exec("usage", "jars", "--repository=" + tmpdir.getAbsolutePath());
    }

    private String list(final File tmpdir) {
        final int length = tmpdir.getAbsolutePath().length() + 1;
        final Dir from = Dir.from(tmpdir);
        return from.files()
                .peek(this::adjustTimeZone)
                .map(File::getAbsolutePath)
                .map(s -> s.substring(length))
                .reduce((s, s2) -> s + System.lineSeparator() + s2)
                .get();
    }

    public void adjustTimeZone(final File file) {
        final long localTime = file.lastModified();
        final int offset = TimeZone.getDefault().getOffset(localTime);
        final long utcTime = localTime + offset;
        file.setLastModified(utcTime);
    }
    private Results normalize(final Results results) {
        return new Results(results.getExpected(), ScanTsvTest.normalize(results.getActual()));
    }

}
