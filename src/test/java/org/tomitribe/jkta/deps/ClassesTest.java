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
package org.tomitribe.jkta.deps;

import org.junit.Assert;
import org.junit.Test;
import org.tomitribe.util.Join;

public class ClassesTest extends Assert {

    @Test
    public void testIsJavax() throws Exception {

    }

    @Test
    public void testJavaxUses() throws Exception {
        final Jar jar = new Jar("foo-api");
        jar.getClasses().add(new Clazz("javax.activation.ActivationDataFlavor") {
            {
                getReferences().add("java.awt.datatransfer.DataFlavor");
                getReferences().add("java.lang.String");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.MimeType");
                getReferences().add("java.lang.String");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.MimeType");
            }
        });
        jar.getClasses().add(new Clazz("javax.activation.ActivationDataFlavour") {
            {
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.PantomimeType");
                getReferences().add("java.lang.String");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.PantomimeType");
            }
        });

        final Jar filtered = Classes.javaxUses(jar);

        Assert.assertEquals("javax.activation.MimeType\n" +
                "javax.activation.MimeType", Join.join("\n", filtered.getClasses().get(0).getReferences()));

        Assert.assertEquals("javax.activation.PantomimeType\n" +
                "javax.activation.PantomimeType", Join.join("\n", filtered.getClasses().get(1).getReferences()));

    }

    @Test
    public void testJavaxUses1() throws Exception {
        final Clazz clazz = new Clazz("javax.activation.ActivationDataFlavor");
        clazz.getReferences().add("java.awt.datatransfer.DataFlavor");
        clazz.getReferences().add("java.lang.String");
        clazz.getReferences().add("java.lang.Object");
        clazz.getReferences().add("javax.activation.MimeType");
        clazz.getReferences().add("java.lang.String");
        clazz.getReferences().add("java.lang.Object");
        clazz.getReferences().add("javax.activation.MimeType");
        clazz.getReferences().add("java.lang.String");
        clazz.getReferences().add("java.lang.String");
        clazz.getReferences().add("java.lang.String");
        clazz.getReferences().add("java.lang.Object");

        final Clazz filtered = Classes.javaxUses(clazz);

        Assert.assertEquals("javax.activation.MimeType\n" +
                "javax.activation.MimeType", Join.join("\n", filtered.getReferences()));
    }


    @Test
    public void testRemoveInnerReferences() throws Exception {
        final Jar jar = new Jar("foo-api");
        jar.getClasses().add(new Clazz("javax.activation.ActivationDataFlavor") {
            {
                getReferences().add("java.awt.datatransfer.DataFlavor");
                getReferences().add("java.lang.String");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.MimeType");
                getReferences().add("javax.ejb.EJBObject");
                getReferences().add("java.lang.String");
                getReferences().add("javax.ejb.EJBObject");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.MimeType");
            }
        });
        jar.getClasses().add(new Clazz("javax.activation.Color") {
            {
                getReferences().add("java.lang.Object");
                getReferences().add("javax.mail.PantomimeType");
                getReferences().add("java.lang.String");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.PantomimeType");
            }
        });
        jar.getClasses().add(new Clazz("javax.activation.MimeType"));
        jar.getClasses().add(new Clazz("javax.activation.PantomimeType"));

        final Jar filtered = Classes.externalUses(jar);

        Assert.assertEquals("java.awt.datatransfer.DataFlavor\n" +
                "java.lang.String\n" +
                "java.lang.Object\n" +
                "javax.ejb.EJBObject\n" +
                "java.lang.String\n" +
                "javax.ejb.EJBObject\n" +
                "java.lang.Object", Join.join("\n", filtered.getClasses().get(0).getReferences()));

        Assert.assertEquals("java.lang.Object\n" +
                "javax.mail.PantomimeType\n" +
                "java.lang.String\n" +
                "java.lang.Object", Join.join("\n", filtered.getClasses().get(1).getReferences()));

    }

    @Test
    public void testDistinctUses() throws Exception {
        final Jar jar = new Jar("foo-api");
        jar.getClasses().add(new Clazz("javax.activation.ActivationDataFlavor") {
            {
                getReferences().add("java.awt.datatransfer.DataFlavor");
                getReferences().add("java.lang.String");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.MimeType");
                getReferences().add("javax.ejb.EJBObject");
                getReferences().add("java.lang.String");
                getReferences().add("javax.ejb.EJBObject");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.MimeType");
            }
        });
        jar.getClasses().add(new Clazz("javax.activation.Color") {
            {
                getReferences().add("java.lang.Object");
                getReferences().add("javax.mail.PantomimeType");
                getReferences().add("java.lang.String");
                getReferences().add("java.lang.Object");
                getReferences().add("javax.activation.PantomimeType");
            }
        });
        jar.getClasses().add(new Clazz("javax.activation.MimeType"));
        jar.getClasses().add(new Clazz("javax.activation.PantomimeType"));

        final Jar filtered = Classes.distinctUses(jar);

        Assert.assertEquals("java.awt.datatransfer.DataFlavor\n" +
                "java.lang.Object\n" +
                "java.lang.String\n" +
                "javax.activation.MimeType\n" +
                "javax.ejb.EJBObject", Join.join("\n", filtered.getClasses().get(0).getReferences()));

        Assert.assertEquals("java.lang.Object\n" +
                "java.lang.String\n" +
                "javax.activation.PantomimeType\n" +
                "javax.mail.PantomimeType", Join.join("\n", filtered.getClasses().get(1).getReferences()));
    }

}