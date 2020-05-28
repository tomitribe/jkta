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

import org.junit.Test;

import static org.tomitribe.jkta.usage.Scan.assertUsage;
import static org.tomitribe.jkta.usage.Scan.usage;

/**
 * Explicit tests for some notable exclusions
 */
public class ExclusionsTest {

    /**
     * The base package javax.annotation is part of Jakarta
     * The sub-package javax.annotation.processor is part of Java SE
     */
    @Test
    public void javaxAnnotationProcessor() {

        { // Count javax.annotation
            final Usage<Jar> usage = usage(new Object() {
                public Object m1(final javax.annotation.Generated foo) {
                    return foo;
                }
            });

            assertUsage(usage, Package.JAVAX_ANNOTATION);
        }

        { // Do NOT count javax.annotation.processor
            final Usage<Jar> usage = usage(new Object() {
                public Object m1(final javax.annotation.processing.AbstractProcessor abstractProcessor) {
                    return abstractProcessor;
                }
            });

            assertUsage(usage);
        }
    }

    // ------------------------------------------------------

    /**
     * The base package javax.transaction is part of Jakarta
     * The sub-package javax.transaction.xa is part of Java SE
     */
    @Test
    public void javaxTransactionXa() {

        { // Count javax.annotation
            final Usage<Jar> usage = usage(new Object() {
                public Object m1(final javax.transaction.Transaction foo) {
                    return foo;
                }
            });

            assertUsage(usage, Package.JAVAX_TRANSACTION);
        }

        { // Do NOT count javax.annotation.processor
            final Usage<Jar> usage = usage(new Object() {
                public Object m1(final javax.transaction.xa.XAResource foo) {
                    return foo;
                }
            });

            assertUsage(usage);
        }
    }

}
