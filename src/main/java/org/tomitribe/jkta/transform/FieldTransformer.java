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
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.TypePath;
import org.tomitribe.jkta.usage.scan.BytecodeUsage;

public class FieldTransformer extends FieldVisitor {

    private final BytecodeUsage bytecodeUsage;

    public FieldTransformer(final int api, final BytecodeUsage bytecodeUsage) {
        super(api);
        this.bytecodeUsage = bytecodeUsage;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationTransformer(this.api, bytecodeUsage);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationTransformer(this.api, bytecodeUsage);
    }
}
