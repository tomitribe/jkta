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
package org.tomitribe.jkta.transform;

import org.junit.Ignore;
import org.junit.Test;
import org.tomitribe.jkta.usage.ArrayData;
import org.tomitribe.jkta.usage.ClassScannerTest;
import org.tomitribe.jkta.usage.Data;
import org.tomitribe.jkta.usage.Jar;
import org.tomitribe.jkta.usage.Package;
import org.tomitribe.jkta.usage.PackageUsage;

import javax.ejb.EJBException;
import javax.ejb.EnterpriseBean;
import javax.ejb.SessionBean;
import javax.enterprise.context.MockScoped;
import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.spi.Bean;
import javax.persistence.Persistence;
import javax.servlet.http.HttpServlet;
import javax.ws.rs.Path;
import java.io.IOException;
import java.io.Serializable;
import java.lang.ref.Reference;
import java.util.Set;

import static org.tomitribe.jkta.transform.Transform.usage;
import static org.tomitribe.jkta.usage.Scan.assertUsage;

public class ClassTransformerTest {
    // ------------------------------------------------------

    @Test
    public void visit_Negative() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.VisitNegative.class);

        assertUsage(usage);
    }

    public static class Parent {
    }

    public static interface Contract {
    }

    public static class VisitNegative extends ClassScannerTest.Parent implements Serializable, ClassScannerTest.Contract {
    }

    // ------------------------------------------------------

    @Test
    public void visit_SuperClass() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasSuper.class);

        assertUsage(usage, Package.JAVAX_SERVLET, Package.JAVAX_SERVLET);

    }

    public static class HasSuper extends HttpServlet implements Serializable, ClassScannerTest.Contract {
    }

    // ------------------------------------------------------

    @Test
    public void visit_Interfaces() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasInterface.class);

        assertUsage(usage, Package.JAVAX_EJB);

    }

    public static class HasInterface implements Serializable, EnterpriseBean, ClassScannerTest.Contract {
    }

    // ------------------------------------------------------

    @Test
    public void visit_Signature() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasSignature.class);

        assertUsage(usage, Package.JAVAX_EJB);

    }

    public static class HasSignature<T extends SessionBean> {
    }

    // ------------------------------------------------------

    @Test
    public void visit_Signature2() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasSignature2.class);

        assertUsage(usage, Package.JAVAX_EJB);

    }

    public static interface HasSignature2 extends Set<SessionBean> {
    }

    // ------------------------------------------------------

    @Test
    public void visit_All() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasAll.class);

        assertUsage(usage,
                Package.JAVAX_EJB,
                Package.JAVAX_SERVLET,
                Package.JAVAX_SERVLET,
                Package.JAVAX_PERSISTENCE
        );

    }

    public static interface Generic<S> {
    }

    public static class HasAll extends HttpServlet implements Serializable, EnterpriseBean, ClassScannerTest.Generic<Persistence> {
    }

    // ------------------------------------------------------

    @Test
    public void visitAnnotation() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasAnnotation.class);

        assertUsage(usage, Package.JAVAX_ENTERPRISE);
    }

    @RequestScoped
    public static class HasAnnotation {
    }

    // ------------------------------------------------------

    @Test
    public void visitAnnotation_Deep() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasAnnotationData.class);

        assertUsage(usage, Package.JAVAX_WS_RS, Package.JAVAX_SERVLET);
    }

    @ArrayData(data = {@Data(path = @Path("/foo")), @Data(type = HttpServlet.class)})
    public static class HasAnnotationData {
    }

    // ------------------------------------------------------

    @Test
    public void visitTypeAnnotation() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasTypeAnnotation.class);

        assertUsage(usage, Package.JAVAX_ENTERPRISE);
    }

    public static class HasTypeAnnotation implements ClassScannerTest.Generic<@MockScoped Reference> {
    }

    // ------------------------------------------------------

    @Ignore
    @Test
    public void visitTypeAnnotation_Deep_PossibleAsmBug() {
        final PackageUsage<Jar> usage = usage(ClassScannerTest.HasTypeAnnotationDeep.class);

        assertUsage(usage, Package.JAVAX_WS_RS, Package.JAVAX_SERVLET);
    }

    public static class HasTypeAnnotationDeep implements ClassScannerTest.Generic<@ArrayData(data = {@Data(path = @Path("/foo")), @Data(type = HttpServlet.class)}) Reference> {
    }

    // ------------------------------------------------------

    @Test
    public void visitField() {
        final PackageUsage<Jar> usage = usage(new Object() {
            SessionBean sb;
        });

        assertUsage(usage, Package.JAVAX_EJB);
    }

    // ------------------------------------------------------

    @Test
    public void visitField_Signature1() {
        final PackageUsage<Jar> usage = usage(new Object() {
            final Reference<SessionBean> sb = null;
        });

        assertUsage(usage, Package.JAVAX_EJB);
    }

    // ------------------------------------------------------

    /**
     * The "Bean" type will show up both in ASM's field descriptor
     * and in the generic signature string.  We should only count
     * one of them.
     */
    @Test
    public void visitField_SignatureNoDuplicate() {
        final PackageUsage<Jar> usage = usage(new Object() {
            Bean<SessionBean> bean;
        });

        assertUsage(usage, Package.JAVAX_EJB, Package.JAVAX_ENTERPRISE);
    }

    // ------------------------------------------------------

    @Test
    public void visitMethod_Return() {
        final PackageUsage<Jar> usage = usage(new Object() {
            public SessionBean get() {
                return null;
            }
        });

        assertUsage(usage, Package.JAVAX_EJB);
    }

    // ------------------------------------------------------

    @Test
    public void visitMethod_ReturnGeneric() {
        final PackageUsage<Jar> usage = usage(new Object() {
            public ClassScannerTest.Generic<SessionBean> get() {
                return null;
            }
        });

        assertUsage(usage, Package.JAVAX_EJB);
    }

    // ------------------------------------------------------

    @Test
    public void visitMethod_Parameter() {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(SessionBean sb) {
            }
        });

        assertUsage(usage, Package.JAVAX_EJB);
    }

    // ------------------------------------------------------

    @Test
    public void visitMethod_ParameterGeneric() {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(Object o, ClassScannerTest.Generic<SessionBean> sb, int i) {
            }
        });

        assertUsage(usage, Package.JAVAX_EJB);
    }

    // ------------------------------------------------------

    @Test
    public void visitMethod_Throws() {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(Serializable s) throws EJBException, IOException {
            }
        });

        assertUsage(usage, Package.JAVAX_EJB);
    }

}
