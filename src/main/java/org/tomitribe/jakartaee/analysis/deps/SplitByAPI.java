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
package org.tomitribe.jakartaee.analysis.deps;

import java.io.File;
import java.util.Iterator;

/**
 * The jar files we produce and publish to Maven Central do contain split packages.
 * For example the ejb-api.jar contains some javax.xml.rpc.handler classes.
 *
 * We need a cleaner split to do actual analysis
 */
public class SplitByAPI {

    public static void main(String[] args) throws Exception {
        new SplitByAPI().main();
    }

    public void main() throws Exception {
        final File file = Src.main().resources().deps().raw().jakartaEEClassesJson();

        final Jar jakartaee = Jars.fromJson(file);

        System.out.println(file.getAbsolutePath());
        System.out.println(jakartaee);

        final Jar[] jars = {
                remove(jakartaee, "javax.xml.ws"),
                remove(jakartaee, "javax.xml.soap"),
                remove(jakartaee, "javax.xml.rpc"),
                remove(jakartaee, "javax.xml.registry"),
                remove(jakartaee, "javax.xml.bind"),
                remove(jakartaee, "javax.ws.rs"),
                remove(jakartaee, "javax.websocket"),
                remove(jakartaee, "javax.validation"),
                remove(jakartaee, "javax.transaction"),
                remove(jakartaee, "javax.servlet.jsp.jstl"),
                remove(jakartaee, "javax.servlet.jsp"),
                remove(jakartaee, "javax.servlet"),
                remove(jakartaee, "javax.security.jacc"),
                remove(jakartaee, "javax.security.enterprise"),
                remove(jakartaee, "javax.security.auth.message"),
                remove(jakartaee, "javax.resource"),
                remove(jakartaee, "javax.persistence"),
                remove(jakartaee, "javax.management.j2ee"),
                remove(jakartaee, "javax.mail"),
                remove(jakartaee, "javax.jws"),
                remove(jakartaee, "javax.json.bind"),
                remove(jakartaee, "javax.json"),
                remove(jakartaee, "javax.jms"),
                remove(jakartaee, "javax.interceptor"),
                remove(jakartaee, "javax.decorator"),
                remove(jakartaee, "javax.faces"),
                remove(jakartaee, "javax.enterprise.deploy"),
                remove(jakartaee, "javax.enterprise.concurrent"),
                remove(jakartaee, "javax.enterprise"),
                remove(jakartaee, "javax.el"),
                remove(jakartaee, "javax.ejb"),
                remove(jakartaee, "javax.batch"),
                remove(jakartaee, "javax.annotation"),
                remove(jakartaee, "javax.activation"),
        };

        if (jakartaee.getClasses().size()!=0) {
            throw new IllegalStateException("All classes should have been split");
        }

        for (final Jar jar : jars) {
            Jars.toJson(Src.main().resources().deps().split().mkdir(), jar);
        }
    }

    public static Jar remove(final Jar jar, final String prefix) {
        final Jar sub = new Jar(prefix);

        final Iterator<Clazz> classes = jar.getClasses().iterator();
        while (classes.hasNext()) {
            final Clazz clazz = classes.next();
            if (clazz.getName().startsWith(prefix)) {
                classes.remove();
                sub.getClasses().add(clazz);
            }
        }
        return sub;
    }
}
