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

import org.tomitribe.jkta.usage.scan.Usage;

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is intentionally a pure count with no other context object.
 *
 * It could be the total of a class.  It could be the total of a jar.  It could be the total of a groupId
 * It could be the total of a local maven repo.  It could be the total of all of Maven Central.
 */
public class PackageUsage<Context> implements Usage<Context> {

    private final Context context;
    protected int javax = 0;
    protected int jakarta = 0;

    protected final int[] packages = new int[Package.values().length];

    public PackageUsage() {
        this(null);
    }

    public PackageUsage(final Context context, final int javax, final int jakarta, final int[] packages) {
        this.context = context;
        this.javax = javax;
        this.jakarta = jakarta;
        if (packages.length != this.packages.length) {
            final String message = String.format("Packages array length should be %s, but found %s", this.packages.length, packages.length);
            throw new ArrayIndexOutOfBoundsException(message);
        }
        System.arraycopy(packages, 0, this.packages, 0, packages.length);
    }

    public PackageUsage(final Context context) {
        this.context = context;
    }

    @Override
    public boolean test(final String reference) {
        final Package match = match(reference);
        if (match == null) return false;

        packages[match.ordinal()]++;

        if (match.getName().startsWith("javax")) javax++;
        if (match.getName().startsWith("jakarta")) jakarta++;
        return true;
    }

    private Package match(final String reference) {
        for (final Package aPackage : Package.values()) {
            if (aPackage.matches(reference)) return aPackage;
        }
        return null;
    }

    @Override
    public Context getContext() {
        return context;
    }

    public int getJavax() {
        return javax;
    }

    public int getJakarta() {
        return jakarta;
    }

    public int[] getPackages() {
        return packages;
    }

    public int get(final Package aPackage) {
        return packages[aPackage.ordinal()];
    }

    /**
     * Used when aggregating results together.
     * Several Usage instances each representing a class could be
     * added together to represent a jar.
     *
     * Several jar Usage instances could be added together to
     * represent a groupId, or local repo.
     *
     * Several groupId Usage instances could be added together
     * to represent Maven Central
     */
    public PackageUsage<Context> add(final PackageUsage that) {
        final PackageUsage<Context> total = new PackageUsage(this.context);
        total.javax = this.javax + that.javax;
        total.jakarta = this.jakarta + that.jakarta;
        for (int i = 0; i < packages.length; i++) {
            total.packages[i] = this.packages[i] + that.packages[i];
        }

        return total;
    }

    public String toTsv() {
        final String t = "\t";
        final StringBuilder sb = new StringBuilder(packages.length * 10);
        sb.append(javax).append(t).append(jakarta);
        for (final int count : packages) {
            sb.append(t).append(count);
        }
        return sb.toString();
    }

    public static Usage fromTsv(final String line) {
        return fromTsv(null, line);
    }

    public static <Context> PackageUsage<Context> fromTsv(final Context context, final String line) {
        final Iterator<Integer> counts = Stream.of(line.split("\t"))
                .map(Integer::new)
                .collect(Collectors.toList())
                .iterator();

        final PackageUsage<Context> usage = new PackageUsage<>(context);
        usage.javax = counts.next();
        usage.jakarta = counts.next();

        for (int i = 0; counts.hasNext(); i++) {
            usage.packages[i] = counts.next();
        }

        return usage;
    }

    public Mutable<Context> mutate() {
        return new Mutable<Context>(new PackageUsage<>(this.context, this.javax, this.jakarta, this.packages));
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Mutable<Context> {
        private final PackageUsage<Context> usage;

        public Mutable() {
            this(new PackageUsage<>());
        }

        private Mutable(final PackageUsage<Context> usage) {
            this.usage = usage;
        }

        public Usage<Context> get() {
            return usage;
        }

        public <NewContext> Usage<NewContext> get(final NewContext context) {
            return new PackageUsage<>(context, usage.javax, usage.jakarta, usage.packages);
        }

        public int getJavax() {
            return usage.getJavax();
        }

        public int getJakarta() {
            return usage.getJakarta();
        }

        public int getJavaxActivation() {
            return usage.get(Package.JAVAX_ACTIVATION);
        }

        public int getJavaxAnnotation() {
            return usage.get(Package.JAVAX_ANNOTATION);
        }

        public int getJavaxBatch() {
            return usage.get(Package.JAVAX_BATCH);
        }

        public int getJavaxDecorator() {
            return usage.get(Package.JAVAX_DECORATOR);
        }

        public int getJavaxEjb() {
            return usage.get(Package.JAVAX_EJB);
        }

        public int getJavaxEl() {
            return usage.get(Package.JAVAX_EL);
        }

        public int getJavaxEnterprise() {
            return usage.get(Package.JAVAX_ENTERPRISE);
        }

        public int getJavaxEnterpriseConcurrent() {
            return usage.get(Package.JAVAX_ENTERPRISE_CONCURRENT);
        }

        public int getJavaxEnterpriseDeploy() {
            return usage.get(Package.JAVAX_ENTERPRISE_DEPLOY);
        }

        public int getJavaxFaces() {
            return usage.get(Package.JAVAX_FACES);
        }

        public int getJavaxInject() {
            return usage.get(Package.JAVAX_INJECT);
        }

        public int getJavaxInterceptor() {
            return usage.get(Package.JAVAX_INTERCEPTOR);
        }

        public int getJavaxJms() {
            return usage.get(Package.JAVAX_JMS);
        }

        public int getJavaxJson() {
            return usage.get(Package.JAVAX_JSON);
        }

        public int getJavaxJsonBind() {
            return usage.get(Package.JAVAX_JSON_BIND);
        }

        public int getJavaxJws() {
            return usage.get(Package.JAVAX_JWS);
        }

        public int getJavaxMail() {
            return usage.get(Package.JAVAX_MAIL);
        }

        public int getJavaxManagementJ2ee() {
            return usage.get(Package.JAVAX_MANAGEMENT_J2EE);
        }

        public int getJavaxPersistence() {
            return usage.get(Package.JAVAX_PERSISTENCE);
        }

        public int getJavaxResource() {
            return usage.get(Package.JAVAX_RESOURCE);
        }

        public int getJavaxSecurityAuthMessage() {
            return usage.get(Package.JAVAX_SECURITY_AUTH_MESSAGE);
        }

        public int getJavaxSecurityEnterprise() {
            return usage.get(Package.JAVAX_SECURITY_ENTERPRISE);
        }

        public int getJavaxSecurityJacc() {
            return usage.get(Package.JAVAX_SECURITY_JACC);
        }

        public int getJavaxServlet() {
            return usage.get(Package.JAVAX_SERVLET);
        }

        public int getJavaxServletJsp() {
            return usage.get(Package.JAVAX_SERVLET_JSP);
        }

        public int getJavaxServletJspJstl() {
            return usage.get(Package.JAVAX_SERVLET_JSP_JSTL);
        }

        public int getJavaxTransaction() {
            return usage.get(Package.JAVAX_TRANSACTION);
        }

        public int getJavaxValidation() {
            return usage.get(Package.JAVAX_VALIDATION);
        }

        public int getJavaxWebsocket() {
            return usage.get(Package.JAVAX_WEBSOCKET);
        }

        public int getJavaxWsRs() {
            return usage.get(Package.JAVAX_WS_RS);
        }

        public int getJavaxXmlBind() {
            return usage.get(Package.JAVAX_XML_BIND);
        }

        public int getJavaxXmlRegistry() {
            return usage.get(Package.JAVAX_XML_REGISTRY);
        }

        public int getJavaxXmlRpc() {
            return usage.get(Package.JAVAX_XML_RPC);
        }

        public int getJavaxXmlSoap() {
            return usage.get(Package.JAVAX_XML_SOAP);
        }

        public int getJavaxXmlWs() {
            return usage.get(Package.JAVAX_XML_WS);
        }

        public int getJakartaActivation() {
            return usage.get(Package.JAKARTA_ACTIVATION);
        }

        public int getJakartaAnnotation() {
            return usage.get(Package.JAKARTA_ANNOTATION);
        }

        public int getJakartaBatch() {
            return usage.get(Package.JAKARTA_BATCH);
        }

        public int getJakartaDecorator() {
            return usage.get(Package.JAKARTA_DECORATOR);
        }

        public int getJakartaEjb() {
            return usage.get(Package.JAKARTA_EJB);
        }

        public int getJakartaEl() {
            return usage.get(Package.JAKARTA_EL);
        }

        public int getJakartaEnterprise() {
            return usage.get(Package.JAKARTA_ENTERPRISE);
        }

        public int getJakartaEnterpriseConcurrent() {
            return usage.get(Package.JAKARTA_ENTERPRISE_CONCURRENT);
        }

        public int getJakartaFaces() {
            return usage.get(Package.JAKARTA_FACES);
        }

        public int getJakartaInject() {
            return usage.get(Package.JAKARTA_INJECT);
        }

        public int getJakartaInterceptor() {
            return usage.get(Package.JAKARTA_INTERCEPTOR);
        }

        public int getJakartaJms() {
            return usage.get(Package.JAKARTA_JMS);
        }

        public int getJakartaJson() {
            return usage.get(Package.JAKARTA_JSON);
        }

        public int getJakartaJsonBind() {
            return usage.get(Package.JAKARTA_JSON_BIND);
        }

        public int getJakartaJws() {
            return usage.get(Package.JAKARTA_JWS);
        }

        public int getJakartaMail() {
            return usage.get(Package.JAKARTA_MAIL);
        }

        public int getJakartaPersistence() {
            return usage.get(Package.JAKARTA_PERSISTENCE);
        }

        public int getJakartaResource() {
            return usage.get(Package.JAKARTA_RESOURCE);
        }

        public int getJakartaSecurityAuthMessage() {
            return usage.get(Package.JAKARTA_SECURITY_AUTH_MESSAGE);
        }

        public int getJakartaSecurityEnterprise() {
            return usage.get(Package.JAKARTA_SECURITY_ENTERPRISE);
        }

        public int getJakartaSecurityJacc() {
            return usage.get(Package.JAKARTA_SECURITY_JACC);
        }

        public int getJakartaServlet() {
            return usage.get(Package.JAKARTA_SERVLET);
        }

        public int getJakartaServletJsp() {
            return usage.get(Package.JAKARTA_SERVLET_JSP);
        }

        public int getJakartaServletJspJstl() {
            return usage.get(Package.JAKARTA_SERVLET_JSP_JSTL);
        }

        public int getJakartaTransaction() {
            return usage.get(Package.JAKARTA_TRANSACTION);
        }

        public int getJakartaValidation() {
            return usage.get(Package.JAKARTA_VALIDATION);
        }

        public int getJakartaWebsocket() {
            return usage.get(Package.JAKARTA_WEBSOCKET);
        }

        public int getJakartaWsRs() {
            return usage.get(Package.JAKARTA_WS_RS);
        }

        public int getJakartaXmlBind() {
            return usage.get(Package.JAKARTA_XML_BIND);
        }

        public int getJakartaXmlSoap() {
            return usage.get(Package.JAKARTA_XML_SOAP);
        }

        public int getJakartaXmlWs() {
            return usage.get(Package.JAKARTA_XML_WS);
        }

        public void setJavax(final int i) {
            usage.javax = i;
        }

        public void setJakarta(final int i) {
            usage.jakarta = i;
        }

        public void setJavaxActivation(final int i) {
            usage.getPackages()[Package.JAVAX_ACTIVATION.ordinal()] = i;
        }

        public void setJavaxAnnotation(final int i) {
            usage.getPackages()[Package.JAVAX_ANNOTATION.ordinal()] = i;
        }

        public void setJavaxBatch(final int i) {
            usage.getPackages()[Package.JAVAX_BATCH.ordinal()] = i;
        }

        public void setJavaxDecorator(final int i) {
            usage.getPackages()[Package.JAVAX_DECORATOR.ordinal()] = i;
        }

        public void setJavaxEjb(final int i) {
            usage.getPackages()[Package.JAVAX_EJB.ordinal()] = i;
        }

        public void setJavaxEl(final int i) {
            usage.getPackages()[Package.JAVAX_EL.ordinal()] = i;
        }

        public void setJavaxEnterprise(final int i) {
            usage.getPackages()[Package.JAVAX_ENTERPRISE.ordinal()] = i;
        }

        public void setJavaxEnterpriseConcurrent(final int i) {
            usage.getPackages()[Package.JAVAX_ENTERPRISE_CONCURRENT.ordinal()] = i;
        }

        public void setJavaxEnterpriseDeploy(final int i) {
            usage.getPackages()[Package.JAVAX_ENTERPRISE_DEPLOY.ordinal()] = i;
        }

        public void setJavaxFaces(final int i) {
            usage.getPackages()[Package.JAVAX_FACES.ordinal()] = i;
        }

        public void setJavaxInject(final int i) {
            usage.getPackages()[Package.JAVAX_INJECT.ordinal()] = i;
        }

        public void setJavaxInterceptor(final int i) {
            usage.getPackages()[Package.JAVAX_INTERCEPTOR.ordinal()] = i;
        }

        public void setJavaxJms(final int i) {
            usage.getPackages()[Package.JAVAX_JMS.ordinal()] = i;
        }

        public void setJavaxJson(final int i) {
            usage.getPackages()[Package.JAVAX_JSON.ordinal()] = i;
        }

        public void setJavaxJsonBind(final int i) {
            usage.getPackages()[Package.JAVAX_JSON_BIND.ordinal()] = i;
        }

        public void setJavaxJws(final int i) {
            usage.getPackages()[Package.JAVAX_JWS.ordinal()] = i;
        }

        public void setJavaxMail(final int i) {
            usage.getPackages()[Package.JAVAX_MAIL.ordinal()] = i;
        }

        public void setJavaxManagementJ2ee(final int i) {
            usage.getPackages()[Package.JAVAX_MANAGEMENT_J2EE.ordinal()] = i;
        }

        public void setJavaxPersistence(final int i) {
            usage.getPackages()[Package.JAVAX_PERSISTENCE.ordinal()] = i;
        }

        public void setJavaxResource(final int i) {
            usage.getPackages()[Package.JAVAX_RESOURCE.ordinal()] = i;
        }

        public void setJavaxSecurityAuthMessage(final int i) {
            usage.getPackages()[Package.JAVAX_SECURITY_AUTH_MESSAGE.ordinal()] = i;
        }

        public void setJavaxSecurityEnterprise(final int i) {
            usage.getPackages()[Package.JAVAX_SECURITY_ENTERPRISE.ordinal()] = i;
        }

        public void setJavaxSecurityJacc(final int i) {
            usage.getPackages()[Package.JAVAX_SECURITY_JACC.ordinal()] = i;
        }

        public void setJavaxServlet(final int i) {
            usage.getPackages()[Package.JAVAX_SERVLET.ordinal()] = i;
        }

        public void setJavaxServletJsp(final int i) {
            usage.getPackages()[Package.JAVAX_SERVLET_JSP.ordinal()] = i;
        }

        public void setJavaxServletJspJstl(final int i) {
            usage.getPackages()[Package.JAVAX_SERVLET_JSP_JSTL.ordinal()] = i;
        }

        public void setJavaxTransaction(final int i) {
            usage.getPackages()[Package.JAVAX_TRANSACTION.ordinal()] = i;
        }

        public void setJavaxValidation(final int i) {
            usage.getPackages()[Package.JAVAX_VALIDATION.ordinal()] = i;
        }

        public void setJavaxWebsocket(final int i) {
            usage.getPackages()[Package.JAVAX_WEBSOCKET.ordinal()] = i;
        }

        public void setJavaxWsRs(final int i) {
            usage.getPackages()[Package.JAVAX_WS_RS.ordinal()] = i;
        }

        public void setJavaxXmlBind(final int i) {
            usage.getPackages()[Package.JAVAX_XML_BIND.ordinal()] = i;
        }

        public void setJavaxXmlRegistry(final int i) {
            usage.getPackages()[Package.JAVAX_XML_REGISTRY.ordinal()] = i;
        }

        public void setJavaxXmlRpc(final int i) {
            usage.getPackages()[Package.JAVAX_XML_RPC.ordinal()] = i;
        }

        public void setJavaxXmlSoap(final int i) {
            usage.getPackages()[Package.JAVAX_XML_SOAP.ordinal()] = i;
        }

        public void setJavaxXmlWs(final int i) {
            usage.getPackages()[Package.JAVAX_XML_WS.ordinal()] = i;
        }

        public void setJakartaActivation(final int i) {
            usage.getPackages()[Package.JAKARTA_ACTIVATION.ordinal()] = i;
        }

        public void setJakartaAnnotation(final int i) {
            usage.getPackages()[Package.JAKARTA_ANNOTATION.ordinal()] = i;
        }

        public void setJakartaBatch(final int i) {
            usage.getPackages()[Package.JAKARTA_BATCH.ordinal()] = i;
        }

        public void setJakartaDecorator(final int i) {
            usage.getPackages()[Package.JAKARTA_DECORATOR.ordinal()] = i;
        }

        public void setJakartaEjb(final int i) {
            usage.getPackages()[Package.JAKARTA_EJB.ordinal()] = i;
        }

        public void setJakartaEl(final int i) {
            usage.getPackages()[Package.JAKARTA_EL.ordinal()] = i;
        }

        public void setJakartaEnterprise(final int i) {
            usage.getPackages()[Package.JAKARTA_ENTERPRISE.ordinal()] = i;
        }

        public void setJakartaEnterpriseConcurrent(final int i) {
            usage.getPackages()[Package.JAKARTA_ENTERPRISE_CONCURRENT.ordinal()] = i;
        }

        public void setJakartaFaces(final int i) {
            usage.getPackages()[Package.JAKARTA_FACES.ordinal()] = i;
        }

        public void setJakartaInject(final int i) {
            usage.getPackages()[Package.JAKARTA_INJECT.ordinal()] = i;
        }

        public void setJakartaInterceptor(final int i) {
            usage.getPackages()[Package.JAKARTA_INTERCEPTOR.ordinal()] = i;
        }

        public void setJakartaJms(final int i) {
            usage.getPackages()[Package.JAKARTA_JMS.ordinal()] = i;
        }

        public void setJakartaJson(final int i) {
            usage.getPackages()[Package.JAKARTA_JSON.ordinal()] = i;
        }

        public void setJakartaJsonBind(final int i) {
            usage.getPackages()[Package.JAKARTA_JSON_BIND.ordinal()] = i;
        }

        public void setJakartaJws(final int i) {
            usage.getPackages()[Package.JAKARTA_JWS.ordinal()] = i;
        }

        public void setJakartaMail(final int i) {
            usage.getPackages()[Package.JAKARTA_MAIL.ordinal()] = i;
        }

        public void setJakartaPersistence(final int i) {
            usage.getPackages()[Package.JAKARTA_PERSISTENCE.ordinal()] = i;
        }

        public void setJakartaResource(final int i) {
            usage.getPackages()[Package.JAKARTA_RESOURCE.ordinal()] = i;
        }

        public void setJakartaSecurityAuthMessage(final int i) {
            usage.getPackages()[Package.JAKARTA_SECURITY_AUTH_MESSAGE.ordinal()] = i;
        }

        public void setJakartaSecurityEnterprise(final int i) {
            usage.getPackages()[Package.JAKARTA_SECURITY_ENTERPRISE.ordinal()] = i;
        }

        public void setJakartaSecurityJacc(final int i) {
            usage.getPackages()[Package.JAKARTA_SECURITY_JACC.ordinal()] = i;
        }

        public void setJakartaServlet(final int i) {
            usage.getPackages()[Package.JAKARTA_SERVLET.ordinal()] = i;
        }

        public void setJakartaServletJsp(final int i) {
            usage.getPackages()[Package.JAKARTA_SERVLET_JSP.ordinal()] = i;
        }

        public void setJakartaServletJspJstl(final int i) {
            usage.getPackages()[Package.JAKARTA_SERVLET_JSP_JSTL.ordinal()] = i;
        }

        public void setJakartaTransaction(final int i) {
            usage.getPackages()[Package.JAKARTA_TRANSACTION.ordinal()] = i;
        }

        public void setJakartaValidation(final int i) {
            usage.getPackages()[Package.JAKARTA_VALIDATION.ordinal()] = i;
        }

        public void setJakartaWebsocket(final int i) {
            usage.getPackages()[Package.JAKARTA_WEBSOCKET.ordinal()] = i;
        }

        public void setJakartaWsRs(final int i) {
            usage.getPackages()[Package.JAKARTA_WS_RS.ordinal()] = i;
        }

        public void setJakartaXmlBind(final int i) {
            usage.getPackages()[Package.JAKARTA_XML_BIND.ordinal()] = i;
        }

        public void setJakartaXmlSoap(final int i) {
            usage.getPackages()[Package.JAKARTA_XML_SOAP.ordinal()] = i;
        }

        public void setJakartaXmlWs(final int i) {
            usage.getPackages()[Package.JAKARTA_XML_WS.ordinal()] = i;
        }
    }

    public static class Builder {
        private final int[] packages = new int[Package.values().length];

        public Builder javaxActivation(final int i) {
            packages[Package.JAVAX_ACTIVATION.ordinal()] = i;
            return this;
        }

        public Builder javaxAnnotation(final int i) {
            packages[Package.JAVAX_ANNOTATION.ordinal()] = i;
            return this;
        }

        public Builder javaxBatch(final int i) {
            packages[Package.JAVAX_BATCH.ordinal()] = i;
            return this;
        }

        public Builder javaxDecorator(final int i) {
            packages[Package.JAVAX_DECORATOR.ordinal()] = i;
            return this;
        }

        public Builder javaxEjb(final int i) {
            packages[Package.JAVAX_EJB.ordinal()] = i;
            return this;
        }

        public Builder javaxEl(final int i) {
            packages[Package.JAVAX_EL.ordinal()] = i;
            return this;
        }

        public Builder javaxEnterprise(final int i) {
            packages[Package.JAVAX_ENTERPRISE.ordinal()] = i;
            return this;
        }

        public Builder javaxEnterpriseConcurrent(final int i) {
            packages[Package.JAVAX_ENTERPRISE_CONCURRENT.ordinal()] = i;
            return this;
        }

        public Builder javaxEnterpriseDeploy(final int i) {
            packages[Package.JAVAX_ENTERPRISE_DEPLOY.ordinal()] = i;
            return this;
        }

        public Builder javaxFaces(final int i) {
            packages[Package.JAVAX_FACES.ordinal()] = i;
            return this;
        }

        public Builder javaxInject(final int i) {
            packages[Package.JAVAX_INJECT.ordinal()] = i;
            return this;
        }

        public Builder javaxInterceptor(final int i) {
            packages[Package.JAVAX_INTERCEPTOR.ordinal()] = i;
            return this;
        }

        public Builder javaxJms(final int i) {
            packages[Package.JAVAX_JMS.ordinal()] = i;
            return this;
        }

        public Builder javaxJson(final int i) {
            packages[Package.JAVAX_JSON.ordinal()] = i;
            return this;
        }

        public Builder javaxJsonBind(final int i) {
            packages[Package.JAVAX_JSON_BIND.ordinal()] = i;
            return this;
        }

        public Builder javaxJws(final int i) {
            packages[Package.JAVAX_JWS.ordinal()] = i;
            return this;
        }

        public Builder javaxMail(final int i) {
            packages[Package.JAVAX_MAIL.ordinal()] = i;
            return this;
        }

        public Builder javaxManagementJ2ee(final int i) {
            packages[Package.JAVAX_MANAGEMENT_J2EE.ordinal()] = i;
            return this;
        }

        public Builder javaxPersistence(final int i) {
            packages[Package.JAVAX_PERSISTENCE.ordinal()] = i;
            return this;
        }

        public Builder javaxResource(final int i) {
            packages[Package.JAVAX_RESOURCE.ordinal()] = i;
            return this;
        }

        public Builder javaxSecurityAuthMessage(final int i) {
            packages[Package.JAVAX_SECURITY_AUTH_MESSAGE.ordinal()] = i;
            return this;
        }

        public Builder javaxSecurityEnterprise(final int i) {
            packages[Package.JAVAX_SECURITY_ENTERPRISE.ordinal()] = i;
            return this;
        }

        public Builder javaxSecurityJacc(final int i) {
            packages[Package.JAVAX_SECURITY_JACC.ordinal()] = i;
            return this;
        }

        public Builder javaxServlet(final int i) {
            packages[Package.JAVAX_SERVLET.ordinal()] = i;
            return this;
        }

        public Builder javaxServletJsp(final int i) {
            packages[Package.JAVAX_SERVLET_JSP.ordinal()] = i;
            return this;
        }

        public Builder javaxServletJspJstl(final int i) {
            packages[Package.JAVAX_SERVLET_JSP_JSTL.ordinal()] = i;
            return this;
        }

        public Builder javaxTransaction(final int i) {
            packages[Package.JAVAX_TRANSACTION.ordinal()] = i;
            return this;
        }

        public Builder javaxValidation(final int i) {
            packages[Package.JAVAX_VALIDATION.ordinal()] = i;
            return this;
        }

        public Builder javaxWebsocket(final int i) {
            packages[Package.JAVAX_WEBSOCKET.ordinal()] = i;
            return this;
        }

        public Builder javaxWsRs(final int i) {
            packages[Package.JAVAX_WS_RS.ordinal()] = i;
            return this;
        }

        public Builder javaxXmlBind(final int i) {
            packages[Package.JAVAX_XML_BIND.ordinal()] = i;
            return this;
        }

        public Builder javaxXmlRegistry(final int i) {
            packages[Package.JAVAX_XML_REGISTRY.ordinal()] = i;
            return this;
        }

        public Builder javaxXmlRpc(final int i) {
            packages[Package.JAVAX_XML_RPC.ordinal()] = i;
            return this;
        }

        public Builder javaxXmlSoap(final int i) {
            packages[Package.JAVAX_XML_SOAP.ordinal()] = i;
            return this;
        }

        public Builder javaxXmlWs(final int i) {
            packages[Package.JAVAX_XML_WS.ordinal()] = i;
            return this;
        }

        public Builder jakartaActivation(final int i) {
            packages[Package.JAKARTA_ACTIVATION.ordinal()] = i;
            return this;
        }

        public Builder jakartaAnnotation(final int i) {
            packages[Package.JAKARTA_ANNOTATION.ordinal()] = i;
            return this;
        }

        public Builder jakartaBatch(final int i) {
            packages[Package.JAKARTA_BATCH.ordinal()] = i;
            return this;
        }

        public Builder jakartaDecorator(final int i) {
            packages[Package.JAKARTA_DECORATOR.ordinal()] = i;
            return this;
        }

        public Builder jakartaEjb(final int i) {
            packages[Package.JAKARTA_EJB.ordinal()] = i;
            return this;
        }

        public Builder jakartaEl(final int i) {
            packages[Package.JAKARTA_EL.ordinal()] = i;
            return this;
        }

        public Builder jakartaEnterprise(final int i) {
            packages[Package.JAKARTA_ENTERPRISE.ordinal()] = i;
            return this;
        }

        public Builder jakartaEnterpriseConcurrent(final int i) {
            packages[Package.JAKARTA_ENTERPRISE_CONCURRENT.ordinal()] = i;
            return this;
        }

        public Builder jakartaFaces(final int i) {
            packages[Package.JAKARTA_FACES.ordinal()] = i;
            return this;
        }

        public Builder jakartaInject(final int i) {
            packages[Package.JAKARTA_INJECT.ordinal()] = i;
            return this;
        }

        public Builder jakartaInterceptor(final int i) {
            packages[Package.JAKARTA_INTERCEPTOR.ordinal()] = i;
            return this;
        }

        public Builder jakartaJms(final int i) {
            packages[Package.JAKARTA_JMS.ordinal()] = i;
            return this;
        }

        public Builder jakartaJson(final int i) {
            packages[Package.JAKARTA_JSON.ordinal()] = i;
            return this;
        }

        public Builder jakartaJsonBind(final int i) {
            packages[Package.JAKARTA_JSON_BIND.ordinal()] = i;
            return this;
        }

        public Builder jakartaJws(final int i) {
            packages[Package.JAKARTA_JWS.ordinal()] = i;
            return this;
        }

        public Builder jakartaMail(final int i) {
            packages[Package.JAKARTA_MAIL.ordinal()] = i;
            return this;
        }

        public Builder jakartaPersistence(final int i) {
            packages[Package.JAKARTA_PERSISTENCE.ordinal()] = i;
            return this;
        }

        public Builder jakartaResource(final int i) {
            packages[Package.JAKARTA_RESOURCE.ordinal()] = i;
            return this;
        }

        public Builder jakartaSecurityAuthMessage(final int i) {
            packages[Package.JAKARTA_SECURITY_AUTH_MESSAGE.ordinal()] = i;
            return this;
        }

        public Builder jakartaSecurityEnterprise(final int i) {
            packages[Package.JAKARTA_SECURITY_ENTERPRISE.ordinal()] = i;
            return this;
        }

        public Builder jakartaSecurityJacc(final int i) {
            packages[Package.JAKARTA_SECURITY_JACC.ordinal()] = i;
            return this;
        }

        public Builder jakartaServlet(final int i) {
            packages[Package.JAKARTA_SERVLET.ordinal()] = i;
            return this;
        }

        public Builder jakartaServletJsp(final int i) {
            packages[Package.JAKARTA_SERVLET_JSP.ordinal()] = i;
            return this;
        }

        public Builder jakartaServletJspJstl(final int i) {
            packages[Package.JAKARTA_SERVLET_JSP_JSTL.ordinal()] = i;
            return this;
        }

        public Builder jakartaTransaction(final int i) {
            packages[Package.JAKARTA_TRANSACTION.ordinal()] = i;
            return this;
        }

        public Builder jakartaValidation(final int i) {
            packages[Package.JAKARTA_VALIDATION.ordinal()] = i;
            return this;
        }

        public Builder jakartaWebsocket(final int i) {
            packages[Package.JAKARTA_WEBSOCKET.ordinal()] = i;
            return this;
        }

        public Builder jakartaWsRs(final int i) {
            packages[Package.JAKARTA_WS_RS.ordinal()] = i;
            return this;
        }

        public Builder jakartaXmlBind(final int i) {
            packages[Package.JAKARTA_XML_BIND.ordinal()] = i;
            return this;
        }

        public Builder jakartaXmlSoap(final int i) {
            packages[Package.JAKARTA_XML_SOAP.ordinal()] = i;
            return this;
        }

        public Builder jakartaXmlWs(final int i) {
            packages[Package.JAKARTA_XML_WS.ordinal()] = i;
            return this;
        }

        public Usage build() {
            return build(null);
        }

        public <Context> Usage<Context> build(final Context context) {
            int javax = 0;
            int jakarta = 0;
            for (final Package aPackage : Package.values()) {
                final int count = packages[aPackage.ordinal()];
                if (aPackage.getName().startsWith("javax.")) {
                    javax += count;
                } else if (aPackage.getName().startsWith("jakarta.")) {
                    jakarta += count;
                } else {
                    throw new UnsupportedOperationException("Unknown namespace: " + aPackage.getName());
                }
            }
            return new PackageUsage<>(context, javax, jakarta, packages);
        }
    }
}
