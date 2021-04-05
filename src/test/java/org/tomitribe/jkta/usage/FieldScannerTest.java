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

import org.junit.Ignore;
import org.junit.Test;

import javax.enterprise.context.MockScoped;
import javax.persistence.Id;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.Path;

import java.util.Set;

import static org.tomitribe.jkta.usage.Scan.assertUsage;
import static org.tomitribe.jkta.usage.Scan.usage;

public class FieldScannerTest {

    // ------------------------------------------------------

    @Test
    public void visitAnnotation() {
        final PackageUsage<Jar> usage = usage(new Object() {
            @Id
            private Object o;
        });

        assertUsage(usage, Package.JAVAX_PERSISTENCE);
    }

    // ------------------------------------------------------

    @Ignore
    @Test
    public void visitAnnotation_Deep_PossibleAsmBug() {
        final PackageUsage<Jar> usage = usage(new Object() {
            @ArrayData(data = {@Data(path = @Path("/foo")), @Data(type = HttpServlet.class)})
            private Object o;
        });

        assertUsage(usage, Package.JAVAX_SERVLET, Package.JAVAX_WS_RS);
    }

    // ------------------------------------------------------

    @Test
    public void visitAnnotation_Deep() {
        final PackageUsage<Jar> usage = usage(new Object() {
            @Data(type = HttpServlet.class)
            private Object o;
        });

        assertUsage(usage, Package.JAVAX_SERVLET);
    }

    // ------------------------------------------------------

    @Test
    public void visitTypeAnnotation() {
        final PackageUsage<Jar> usage = usage(new Object() {
            Set<@MockScoped Long> set;
        });

        assertUsage(usage, Package.JAVAX_ENTERPRISE);
    }

    // ------------------------------------------------------

    @Test
    public void visitTypeAnnotation_Deep() {
        final PackageUsage<Jar> usage = usage(new Object() {
            Set<@ArrayData(data = {@Data(path = @Path("/foo")), @Data(type = HttpServlet.class)}) Long> set;
        });

        assertUsage(usage, Package.JAVAX_SERVLET, Package.JAVAX_WS_RS);
    }

}
