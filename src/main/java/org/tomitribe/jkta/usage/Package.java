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

import java.util.stream.Stream;

public enum Package {
    JAVAX_ACTIVATION("javax.activation"),
    JAVAX_ANNOTATION("javax.annotation", "javax.annotation.processing"),
    JAVAX_BATCH("javax.batch"),
    JAVAX_DECORATOR("javax.decorator"),
    JAVAX_EJB("javax.ejb"),
    JAVAX_EL("javax.el"),
    JAVAX_ENTERPRISE("javax.enterprise", "javax.enterprise.concurrent", "javax.enterprise.deploy"),
    JAVAX_ENTERPRISE_CONCURRENT("javax.enterprise.concurrent"),
    JAVAX_ENTERPRISE_DEPLOY("javax.enterprise.deploy"),
    JAVAX_FACES("javax.faces"),
    JAVAX_INJECT("javax.inject"),
    JAVAX_INTERCEPTOR("javax.interceptor"),
    JAVAX_JMS("javax.jms"),
    JAVAX_JSON("javax.json", "javax.json.bind"),
    JAVAX_JSON_BIND("javax.json.bind"),
    JAVAX_JWS("javax.jws"),
    JAVAX_MAIL("javax.mail"),
    JAVAX_MANAGEMENT_J2EE("javax.management.j2ee"),
    JAVAX_PERSISTENCE("javax.persistence"),
    JAVAX_RESOURCE("javax.resource"),
    JAVAX_SECURITY_AUTH_MESSAGE("javax.security.auth.message"),
    JAVAX_SECURITY_ENTERPRISE("javax.security.enterprise"),
    JAVAX_SECURITY_JACC("javax.security.jacc"),
    JAVAX_SERVLET("javax.servlet", "javax.servlet.jsp"),
    JAVAX_SERVLET_JSP("javax.servlet.jsp", "javax.servlet.jsp.jstl"),
    JAVAX_SERVLET_JSP_JSTL("javax.servlet.jsp.jstl"),
    JAVAX_TRANSACTION("javax.transaction", "javax.transaction.xa"),
    JAVAX_VALIDATION("javax.validation"),
    JAVAX_WEBSOCKET("javax.websocket"),
    JAVAX_WS_RS("javax.ws.rs"),
    JAVAX_XML_BIND("javax.xml.bind"),
    JAVAX_XML_REGISTRY("javax.xml.registry"),
    JAVAX_XML_RPC("javax.xml.rpc"),
    JAVAX_XML_SOAP("javax.xml.soap"),
    JAVAX_XML_WS("javax.xml.ws"),
    JAKARTA_ACTIVATION("jakarta.activation"),
    JAKARTA_ANNOTATION("jakarta.annotation"),
    JAKARTA_BATCH("jakarta.batch"),
    JAKARTA_DECORATOR("jakarta.decorator"),
    JAKARTA_EJB("jakarta.ejb"),
    JAKARTA_EL("jakarta.el"),
    JAKARTA_ENTERPRISE("jakarta.enterprise", "jakarta.enterprise.concurrent"),
    JAKARTA_ENTERPRISE_CONCURRENT("jakarta.enterprise.concurrent"),
    JAKARTA_FACES("jakarta.faces"),
    JAKARTA_INJECT("jakarta.inject"),
    JAKARTA_INTERCEPTOR("jakarta.interceptor"),
    JAKARTA_JMS("jakarta.jms"),
    JAKARTA_JSON("jakarta.json", "jakarta.json.bind"),
    JAKARTA_JSON_BIND("jakarta.json.bind"),
    JAKARTA_JWS("jakarta.jws"),
    JAKARTA_MAIL("jakarta.mail"),
    JAKARTA_PERSISTENCE("jakarta.persistence"),
    JAKARTA_RESOURCE("jakarta.resource"),
    JAKARTA_SECURITY_AUTH_MESSAGE("jakarta.security.auth.message"),
    JAKARTA_SECURITY_ENTERPRISE("jakarta.security.enterprise"),
    JAKARTA_SECURITY_JACC("jakarta.security.jacc"),
    JAKARTA_SERVLET("jakarta.servlet", "jakarta.servlet.jsp"),
    JAKARTA_SERVLET_JSP("jakarta.servlet.jsp", "jakarta.servlet.jsp.jstl"),
    JAKARTA_SERVLET_JSP_JSTL("jakarta.servlet.jsp.jstl"),
    JAKARTA_TRANSACTION("jakarta.transaction"),
    JAKARTA_VALIDATION("jakarta.validation"),
    JAKARTA_WEBSOCKET("jakarta.websocket"),
    JAKARTA_WS_RS("jakarta.ws.rs"),
    JAKARTA_XML_BIND("jakarta.xml.bind"),
    JAKARTA_XML_SOAP("jakarta.xml.soap"),
    JAKARTA_XML_WS("jakarta.xml.ws"),
    ;
    private final String name;
    private final String[] excluded;

    Package(final String name, final String... excluded) {
        this.name = name;
        this.excluded = excluded;
    }

    public String getName() {
        return name;
    }

    public boolean matches(final String classOrPackage) {
        for (final String s : excluded) {
            if (classOrPackage.startsWith(s)) return false;
        }
        return classOrPackage.startsWith(name);
    }

    public static Stream<String> names() {
        return Stream.of(Package.values())
                .map(Package::getName);
    }
}
