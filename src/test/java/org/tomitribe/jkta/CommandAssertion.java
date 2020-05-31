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
package org.tomitribe.jkta;

import org.tomitribe.crest.Main;
import org.tomitribe.jkta.usage.UsageCommand;
import org.tomitribe.util.IO;

import java.io.IOException;
import java.util.function.Function;

import static org.junit.Assert.assertEquals;

public class CommandAssertion {


    private final Class<UsageCommand> commandClass;
    private String input = "";
    private String output = "";
    private Function<Results, Results> filter = results -> results;

    public CommandAssertion(final Class<UsageCommand> commandClass) {
        this.commandClass = commandClass;
    }

    public static CommandAssertion command(final Class<UsageCommand> commandClass) {
        return new CommandAssertion(commandClass);
    }

    public CommandAssertion input(final String s) throws IOException {
        input = Resources.load(s);
        return this;
    }

    public CommandAssertion output(final String s) throws IOException {
        this.output = Resources.load(s);
        return this;
    }

    public CommandAssertion results(final Function<Results, Results> observer) {
        this.filter = filter.andThen(observer);
        return this;
    }

    public void exec(final String... args) throws Exception {
        final TestEnvironment env = new TestEnvironment(IO.read(input));

        new Main(commandClass).main(env, args);

        final String actual = env.getOut().toString();

        final Results results = filter.apply(new Results(output, actual));
        
        assertEquals(results.getExpected(), results.getActual());
    }

}
