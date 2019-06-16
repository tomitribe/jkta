/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.eclipse.wg.jakartaee.deps;


import org.eclipse.wg.jakartaee.repos.Repos;
import org.junit.Assert;
import org.junit.Test;
import org.tomitribe.crest.Main;

public class ReposTest extends Assert {

    @Test
    public void list() throws Exception {
        final Main main = new Main(Repos.class);

        final Object output = main.exec("repos", "list");

        assertEquals("git@github.com:eclipse-ee4j/bvtck-porting.git\n" +
                "git@github.com:eclipse-ee4j/cditck-porting.git\n" +
                "git@github.com:eclipse-ee4j/common-annotations-api.git\n" +
                "git@github.com:eclipse-ee4j/concurrency-api.git\n" +
                "git@github.com:eclipse-ee4j/concurrency-ri.git\n" +
                "git@github.com:eclipse-ee4j/debugging-support-for-other-languages-tck.git\n" +
                "git@github.com:eclipse-ee4j/ditck-porting.git\n" +
                "git@github.com:eclipse-ee4j/eclipselink-examples.git\n" +
                "git@github.com:eclipse-ee4j/eclipselink-oracleddlparser.git\n" +
                "git@github.com:eclipse-ee4j/eclipselink-workbench.git\n" +
                "git@github.com:eclipse-ee4j/eclipselink.git\n" +
                "git@github.com:eclipse-ee4j/ee4j-website.git\n" +
                "git@github.com:eclipse-ee4j/ee4j.git\n" +
                "git@github.com:eclipse-ee4j/ejb-api.git\n" +
                "git@github.com:eclipse-ee4j/el-ri.git\n" +
                "git@github.com:eclipse-ee4j/enterprise-deployment.git\n" +
                "git@github.com:eclipse-ee4j/genericjmsra.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-build-maven-plugin.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-cdi-porting-tck.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-copyright-plugin.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-doc-plugin.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-fighterfish.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-ha-api.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-hk2-extra.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-hk2.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-infra.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-jsftemplating.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-logging-annotation-processor.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-maven-embedded-plugin.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-repackaged.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-security-plugin.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-shoal.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-spec-version-maven-plugin.git\n" +
                "git@github.com:eclipse-ee4j/glassfish-woodstock.git\n" +
                "git@github.com:eclipse-ee4j/glassfish.git\n" +
                "git@github.com:eclipse-ee4j/grizzly-ahc.git\n" +
                "git@github.com:eclipse-ee4j/grizzly-memcached.git\n" +
                "git@github.com:eclipse-ee4j/grizzly-npn.git\n" +
                "git@github.com:eclipse-ee4j/grizzly-thrift.git\n" +
                "git@github.com:eclipse-ee4j/grizzly.git\n" +
                "git@github.com:eclipse-ee4j/interceptor-api.git\n" +
                "git@github.com:eclipse-ee4j/jacc.git\n" +
                "git@github.com:eclipse-ee4j/jaf-tck.git\n" +
                "git@github.com:eclipse-ee4j/jaf.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-api.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-firstcup-examples.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-firstcup.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-platform.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-schemas.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-tck-tools.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-tck.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-tutorial-examples.git\n" +
                "git@github.com:eclipse-ee4j/jakartaee-tutorial.git\n" +
                "git@github.com:eclipse-ee4j/jaspic.git\n" +
                "git@github.com:eclipse-ee4j/javamail-tck.git\n" +
                "git@github.com:eclipse-ee4j/javamail.git\n" +
                "git@github.com:eclipse-ee4j/jax-rpc-api.git\n" +
                "git@github.com:eclipse-ee4j/jax-rpc-ri.git\n" +
                "git@github.com:eclipse-ee4j/jax-ws-api.git\n" +
                "git@github.com:eclipse-ee4j/jaxb-api.git\n" +
                "git@github.com:eclipse-ee4j/jaxb-dtd-parser.git\n" +
                "git@github.com:eclipse-ee4j/jaxb-fi.git\n" +
                "git@github.com:eclipse-ee4j/jaxb-istack-commons.git\n" +
                "git@github.com:eclipse-ee4j/jaxb-ri.git\n" +
                "git@github.com:eclipse-ee4j/jaxb-stax-ex.git\n" +
                "git@github.com:eclipse-ee4j/jaxb-tck.git\n" +
                "git@github.com:eclipse-ee4j/jaxr-api.git\n" +
                "git@github.com:eclipse-ee4j/jaxr-ri.git\n" +
                "git@github.com:eclipse-ee4j/jaxrs-api.git\n" +
                "git@github.com:eclipse-ee4j/jca-api.git\n" +
                "git@github.com:eclipse-ee4j/jersey-web.git\n" +
                "git@github.com:eclipse-ee4j/jersey.git\n" +
                "git@github.com:eclipse-ee4j/jersey.github.io.git\n" +
                "git@github.com:eclipse-ee4j/jms-api.git\n" +
                "git@github.com:eclipse-ee4j/jpa-api.git\n" +
                "git@github.com:eclipse-ee4j/jsonb-api.git\n" +
                "git@github.com:eclipse-ee4j/jsonp.git\n" +
                "git@github.com:eclipse-ee4j/jsp-api.git\n" +
                "git@github.com:eclipse-ee4j/jstl-api.git\n" +
                "git@github.com:eclipse-ee4j/jta-api.git\n" +
                "git@github.com:eclipse-ee4j/jws-api.git\n" +
                "git@github.com:eclipse-ee4j/krazo.git\n" +
                "git@github.com:eclipse-ee4j/management-api.git\n" +
                "git@github.com:eclipse-ee4j/metro-jax-ws.git\n" +
                "git@github.com:eclipse-ee4j/metro-jwsdp-samples.git\n" +
                "git@github.com:eclipse-ee4j/metro-mimepull.git\n" +
                "git@github.com:eclipse-ee4j/metro-package-rename-task.git\n" +
                "git@github.com:eclipse-ee4j/metro-policy.git\n" +
                "git@github.com:eclipse-ee4j/metro-saaj.git\n" +
                "git@github.com:eclipse-ee4j/metro-ws-test-harness.git\n" +
                "git@github.com:eclipse-ee4j/metro-wsit.git\n" +
                "git@github.com:eclipse-ee4j/metro-xmlstreambuffer.git\n" +
                "git@github.com:eclipse-ee4j/mojarra-jsf-extensions.git\n" +
                "git@github.com:eclipse-ee4j/mojarra.git\n" +
                "git@github.com:eclipse-ee4j/openmq.git\n" +
                "git@github.com:eclipse-ee4j/orb-gmbal-commons.git\n" +
                "git@github.com:eclipse-ee4j/orb-gmbal-pfl.git\n" +
                "git@github.com:eclipse-ee4j/orb-gmbal.git\n" +
                "git@github.com:eclipse-ee4j/orb.git\n" +
                "git@github.com:eclipse-ee4j/saaj-api.git\n" +
                "git@github.com:eclipse-ee4j/security-api.git\n" +
                "git@github.com:eclipse-ee4j/security-examples.git\n" +
                "git@github.com:eclipse-ee4j/servlet-api.git\n" +
                "git@github.com:eclipse-ee4j/soteria.git\n" +
                "git@github.com:eclipse-ee4j/tyrus.git\n" +
                "git@github.com:eclipse-ee4j/websocket-api.git\n" +
                "git@github.com:eclipse-ee4j/yasson.git\n", output);
    }
}
