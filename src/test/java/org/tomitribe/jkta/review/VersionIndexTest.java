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

import org.junit.Before;
import org.junit.Test;
import org.tomitribe.jkta.Resources;
import org.tomitribe.util.Join;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class VersionIndexTest {

    private Version.Index index;

    @Before
    public void before() throws IOException {
        final File file = Resources.loadAsFile("review/activation/1.2/_index.md");
        index = Version.Index.from(file);
    }

    @Test
    public void title() throws Exception {
        assertEquals("Jakarta Activation 1.2", index.getTitle());
    }

    @Test
    public void summary() throws Exception {
        assertEquals("Initial release of Jakarta Activation", index.getSummary());
    }

    @Test
    public void date() throws Exception {
        final Date date = index.getDate();
        assertNotNull(date);
        final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        assertEquals("2019-10-01", format.format(date));
    }

    @Test
    public void links() throws Exception {
        assertEquals("" +
                "Link{title='Jakarta Activation 1.2 Specification Document', href='./activation-spec-1.2.pdf'}\n" +
                "Link{title='Jakarta Activation 1.2 Specification Document', href='./activation-spec-1.2.html'}\n" +
                "Link{title='Jakarta Activation 1.2 Javadoc', href='./apidocs'}\n" +
                "Link{title='Jakarta Activation 1.2 TCK', href='http://downloads.eclipse.org/jakarta/activation/1.2/jakarta-activation-tck-1.2.0.zip'}\n" +
                "Link{title='sig', href='http://downloads.eclipse.org/jakarta/activation/1.2/jakarta-activation-tck-1.2.0.zip.sig'}\n" +
                "Link{title='sha', href='http://downloads.eclipse.org/jakarta/activation/1.2/jakarta-activation-tck-1.2.0.zip.sha256'}\n" +
                "Link{title='pub', href='https://raw.githubusercontent.com/jakartaee/specification-committee/master/jakartaee-spec-committee.pub'}\n" +
                "Link{title='jakarta.activation:jakarta.activation-api:jar:1.2.2', href='https://search.maven.org/artifact/jakarta.activation/jakarta.activation-api/1.2.2/jar'}\n" +
                "Link{title='Eclipse implementation of Jakarta Activation 1.2.2', href='https://github.com/eclipse-ee4j/activation'}", Join.join("\n", index.getLinks()));
    }

    @Test
    public void compatibleImplementations() {
        assertEquals("Link{title='Eclipse implementation of Jakarta Activation 1.2.2', href='https://github.com/eclipse-ee4j/activation'}"
                , Join.join("\n", index.getCompatibleImplementations()));
    }

}
