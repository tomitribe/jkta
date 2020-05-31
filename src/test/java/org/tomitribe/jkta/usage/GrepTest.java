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

import static org.tomitribe.jkta.CommandAssertion.command;

public class GrepTest {

    /**
     * By default grep should keep all results
     */
    @Test
    public void noArgs() throws Exception {
        command(UsageCommand.class)
                .input("scan-v0.5.tsv")
                .output("scan-v0.5-converted.tsv")
                .results(this::normalize)
                .exec("usage", "grep");
    }

    @Test
    public void javaxMatches() throws Exception {
        command(UsageCommand.class)
                .input("scan-v0.5.tsv")
                .output("grep-javax.tsv")
                .results(this::normalize)
                .exec("usage", "grep", "--javax=[1-9].*");
    }

    @Test
    public void javaxWsRsMatches() throws Exception {
        command(UsageCommand.class)
                .input("scan-v0.5.tsv")
                .output("grep-javax-ws-rs.tsv")
                .results(this::normalize)
                .exec("usage", "grep", "--javax=[1-9].*", "--javax-ws-rs=[1-9].*");
    }

    private Results normalize(final Results results) {
        return new Results(results.getExpected(), ScanTsvTest.normalize(results.getActual()));
    }

}
