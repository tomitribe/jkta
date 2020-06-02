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
package org.tomitribe.jkta.strings;

import javax.ejb.ActivationConfigProperty;
import javax.ejb.MessageDriven;

@MessageDriven(activationConfig = {
        @ActivationConfigProperty(propertyName = "destinationType", propertyValue = "javax.jms.Topic")
})
public class StringSample {

    private static final String RS_CONSTANT = "javax.ws.rs";

    private String servletPackage = "javax.servlet";

    public void work(final String pkg) {
        final String localVariable = "javax.xml.soap";
        System.out.println(localVariable);
        System.out.println(pkg);
        System.out.println(servletPackage);
        System.out.println(RS_CONSTANT);
    }

    public void test() {
        work("javax.mail");
    }

    public static void main(String[] args) {
        new StringSample().test();
    }
}
