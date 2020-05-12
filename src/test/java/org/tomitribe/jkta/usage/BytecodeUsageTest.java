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
import org.tomitribe.util.Archive;

import javax.ejb.EJBException;
import javax.ejb.LockType;
import javax.ejb.SessionBean;
import javax.transaction.Transaction;
import java.io.File;
import java.io.IOException;
import java.lang.ref.Reference;
import java.security.NoSuchAlgorithmException;

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

    @Ignore
    @Test
    public void testGenericMethodParam() throws Exception {
        final Usage<Jar> usage = usage(new Object() {
            public void get(final Reference<LockType> c) {
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

    private static Usage<Jar> usage(final Object o) throws IOException, NoSuchAlgorithmException {
        final Class<? extends Object> aClass = o.getClass();
        final File jar = Archive.archive().add(aClass).toJar();
        return JarUsage.of(jar);
    }
}
