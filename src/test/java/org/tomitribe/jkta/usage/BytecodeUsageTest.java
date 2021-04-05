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

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import javax.ejb.EJBException;
import javax.ejb.EnterpriseBean;
import javax.ejb.LockType;
import javax.ejb.SessionBean;
import javax.ejb.Singleton;
import javax.persistence.Id;
import javax.transaction.Transaction;
import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.util.concurrent.atomic.AtomicReference;

import static org.tomitribe.jkta.usage.Scan.usage;

@Ignore
public class BytecodeUsageTest extends Assert {
    
    @Test
    public void testReturn() throws Exception {

        final PackageUsage<Jar> usage = usage(new Object() {
            public Transaction get() {
                return null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_TRANSACTION));
    }

    @Test
    public void testParameter() throws Exception {

        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(SessionBean s) {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testField() throws Exception {

        final PackageUsage<Jar> usage = usage(new Object() {
            private SessionBean sb = null;
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testFieldGenericDeclaration() throws Exception {

        final PackageUsage<Jar> usage = usage(new Object() {
            private AtomicReference<SessionBean> sb = null;
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    /**
     * These are compiled away
     */
    @Test
    public void testVariableGenericAssignment() throws Exception {

        final PackageUsage<Jar> usage = usage(new Object() {
            public void foo() {
                AtomicReference<?> sb = new AtomicReference<SessionBean>();
            }
        });

        assertEquals(0, usage.getJavax());
        assertEquals(0, usage.get(Package.JAVAX_EJB));
    }

    @Ignore
    @Test
    public void testVariableDeclaration() throws Exception {

        final PackageUsage<Jar> usage = usage(new Object() {
            public void foo() {
                SessionBean sb = null;
                if (sb == null) {
                    System.out.println(sb);
                }
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testVariableGenericDeclaration() throws Exception {

        final PackageUsage<Jar> usage = usage(new Object() {
            public void foo() {
                AtomicReference<?> sb = new AtomicReference<SessionBean>();
            }
        });

        assertEquals(0, usage.getJavax());
        assertEquals(0, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testThrows() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get() throws EJBException {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testIf() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Class c) {
                if (c == SessionBean.class) {
                    c.getSimpleName();
                }
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testSwitch() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final LockType c) {
                switch (c) {
                    case READ:
                        System.out.println("READ");
                        break;
                    case WRITE:
                        System.out.println("WRITE");
                        break;
                    default: /* */
                }
            }
        });

        assertEquals(2, usage.getJavax());
        assertEquals(2, usage.get(Package.JAVAX_EJB));
    }

    @Ignore
    @Test
    public void testFor() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final LockType... c) {
                for (final LockType lockType : c) {
                    System.out.println(lockType);
                }
            }
        });

        assertEquals(2, usage.getJavax());
        assertEquals(2, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testInstanceof() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                boolean b = o instanceof SessionBean;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testIfInstanceof() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                if (o instanceof SessionBean) {
                    System.out.println();
                }
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testTernaryInstanceof() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                final Object o2 = o instanceof SessionBean ? "" : null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testWhileInstanceof() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(Object o) {
                while (o instanceof SessionBean) {
                    o = System.currentTimeMillis();
                }
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testHardEquals() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                boolean b = o == SessionBean.class;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testIfHardEquals() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                if (o == SessionBean.class) {
                    System.out.println();
                }
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testTernaryHardEquals() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                final Object o2 = o == SessionBean.class ? "" : null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testWhileHardEquals() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(Object o) {
                while (o == SessionBean.class) {
                    o = System.currentTimeMillis();
                }
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Ignore
    @Test
    public void testGenericMethodParam() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public void get(final Reference<SessionBean> c) {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }


    @Test
    public void testGenericField() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            final Reference<SessionBean> c = new WeakReference<>(null);

            public void get() {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testGenericMethodReturn() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public <T extends SessionBean> T get() {
                return null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }


    @Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
    @Retention(RetentionPolicy.RUNTIME)
    public static @interface Clazz {
        Class<?> value();
    }

    @Test
    public void testAnnotationParameter() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            @Clazz(SessionBean.class)
            public void get() {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testFieldAnnotation() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            @Id
            private long foo;
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_PERSISTENCE));
    }

    @Test
    public void testMethodAnnotation() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            @GET
            public String get() {
                return null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_WS_RS));
    }

    @Ignore
    @Test
    public void testParameterAnnotation() throws Exception {
        final PackageUsage<Jar> usage = usage(new Object() {
            public String get(@PathParam("foo") String foo) {
                return null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_WS_RS));
    }

    @Ignore
    @Test
    public void testClassAnnotation() throws Exception {
        final PackageUsage<Jar> usage = usage(AnnotatedClass.class);

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Singleton
    public static class AnnotatedClass {

    }

    public static class MyBean implements EnterpriseBean {
    }
}
