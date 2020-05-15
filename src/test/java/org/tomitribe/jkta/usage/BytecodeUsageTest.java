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
import org.tomitribe.jkta.Asmifier;
import org.tomitribe.jkta.Bytecode;
import org.tomitribe.util.Archive;

import javax.ejb.EJBException;
import javax.ejb.LockType;
import javax.ejb.SessionBean;
import javax.ejb.Singleton;
import javax.persistence.Id;
import javax.transaction.Transaction;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.atomic.AtomicReference;

public class BytecodeUsageTest extends Assert {

    @Test
    public void testReturn() throws Exception {

        final Usage<Jar> usage = usage(new Object() {
            public Transaction get() {
                return null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_TRANSACTION));
    }

    @Test
    public void testParameter() throws Exception {

        final Usage<Jar> usage = usage(new Object() {
            public void get(SessionBean s) {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Ignore
    @Test
    public void testField() throws Exception {

        final Usage<Jar> usage = usage(new Object() {
            private SessionBean sb = null;
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testFieldGenericDeclaration() throws Exception {

        final Usage<Jar> usage = usage(new Object() {
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

        final Usage<Jar> usage = usage(new Object() {
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

        final Usage<Jar> usage = usage(new Object() {
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

        final Usage<Jar> usage = usage(new Object() {
            public void foo() {
                AtomicReference<?> sb = new AtomicReference<SessionBean>();
            }
        });

        assertEquals(0, usage.getJavax());
        assertEquals(0, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testThrows() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
            public void get() throws EJBException {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testIf() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                boolean b = o instanceof SessionBean;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testIfInstanceof() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                final Object o2 = o instanceof SessionBean ? "" : null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testWhileInstanceof() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                boolean b = o == SessionBean.class;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testIfHardEquals() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
            public void get(final Object o) {
                final Object o2 = o == SessionBean.class ? "" : null;
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testWhileHardEquals() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
            public void get(final Reference<SessionBean> c) {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }


    @Test
    public void testGenericField() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
            final Reference<SessionBean> c = new WeakReference<>(null);

            public void get() {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testGenericMethodReturn() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
            @Clazz(SessionBean.class)
            public void get() {
            }
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Test
    public void testFieldAnnotation() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
            @Id
            private long foo;
        });

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_PERSISTENCE));
    }

    @Test
    public void testMethodAnnotation() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(new Object() {
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
        final Usage<Jar> usage = usage(AnnotatedClass.class);

        assertEquals(1, usage.getJavax());
        assertEquals(1, usage.get(Package.JAVAX_EJB));
    }

    @Singleton
    public static class AnnotatedClass {
        
    }

    private static Usage<Jar> usage(final Object o) throws IOException, NoSuchAlgorithmException {
        return usage(o.getClass());
    }

    private static Usage<Jar> usage(final Class<?> aClass) throws IOException, NoSuchAlgorithmException {
        final ClassLoader loader = aClass.getClassLoader();
        System.out.println(Asmifier.asmify(Bytecode.readClassFile(loader, aClass)));
        System.out.println();
        final File jar = Archive.archive().add(aClass).toJar();
        return JarUsage.of(jar);
    }
}
