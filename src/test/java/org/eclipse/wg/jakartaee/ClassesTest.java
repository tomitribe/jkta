package org.eclipse.wg.jakartaee;

import junit.framework.TestCase;
import org.apache.openejb.util.Join;
import org.junit.Assert;
import org.junit.Test;

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
}