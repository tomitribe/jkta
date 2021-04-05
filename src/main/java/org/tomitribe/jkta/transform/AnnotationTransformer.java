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
package org.tomitribe.jkta.transform;

import org.objectweb.asm.AnnotationVisitor;
import org.tomitribe.jkta.usage.scan.BytecodeUsage;

public class AnnotationTransformer extends AnnotationVisitor {

    private final BytecodeUsage bytecodeUsage;

    public AnnotationTransformer(final int api, final BytecodeUsage bytecodeUsage) {
        super(api);
        this.bytecodeUsage = bytecodeUsage;
    }

    @Override
    public void visit(final String name, final Object value) {
        bytecodeUsage.addHandleArgs(value);
    }

    @Override
    public void visitEnum(final String name, final String descriptor, final String value) {
        bytecodeUsage.addDesc(descriptor);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String name, final String descriptor) {
        bytecodeUsage.addDesc(descriptor);
        return this;
    }

    @Override
    public AnnotationVisitor visitArray(final String name) {
        return this;
    }
}
