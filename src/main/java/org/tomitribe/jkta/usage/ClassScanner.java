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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

import java.lang.reflect.Array;

public class ClassScanner extends ClassVisitor {

    private final BytecodeUsage bytecodeUsage;
    private boolean includeStrings;
    private int version;

    public ClassScanner(final Usage usage) {
        this(usage, false);
    }

    public ClassScanner(final Usage usage, boolean includeStrings) {
        super(Opcodes.ASM8);
        this.bytecodeUsage = new BytecodeUsage(usage, this.api);
        this.includeStrings = includeStrings;
    }

    public Usage getUsage() {
        return bytecodeUsage.getUsage();
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        this.version = version;
        if (signature == null) {
            bytecodeUsage.addName(superName);
            bytecodeUsage.addNames(interfaces);
        } else {
            bytecodeUsage.addSignature(signature);
        }
    }

    public int getVersion() {
        return version;
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationScanner(this.api, bytecodeUsage, includeStrings);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    @Override
    public FieldVisitor visitField(final int access, final String name, final String descriptor, final String signature, final Object value) {
        if (signature == null) {
            bytecodeUsage.addDesc(descriptor);
        } else {
            bytecodeUsage.addSignature(signature);
        }

        if (value instanceof Type) {
            bytecodeUsage.addType((Type) value);
        }

        if (includeStrings) {
            if (value != null) {
                final Class<?> valueClass = value.getClass();
                if (String.class.equals(valueClass)) {
                    bytecodeUsage.visitString((String) value);
                }

                if (valueClass.isArray()) {
                    scanArray(value);
                }
            }
        }

        return new FieldScanner(this.api, bytecodeUsage, includeStrings);
    }

    private void scanArray(final Object array) {
        for (int i = 0; i < Array.getLength(array); i++) {
            final Object value = Array.get(array, i);

            if (value == null) continue;

            final Class<?> valueClass = value.getClass();
            if (valueClass.isArray()) {
                scanArray(value);
            }

            if (String.class.equals(valueClass)) {
                bytecodeUsage.visitString((String) value);
            }
        }
    }

    @Override
    public MethodScanner visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        if (signature == null) {
            bytecodeUsage.addMethodDesc(descriptor);
        } else {
            bytecodeUsage.addSignature(signature);
        }

        bytecodeUsage.addNames(exceptions);

        return new MethodScanner(this.api, bytecodeUsage, includeStrings);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
