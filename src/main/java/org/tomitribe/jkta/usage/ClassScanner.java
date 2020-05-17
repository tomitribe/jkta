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
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class ClassScanner extends ClassVisitor {

    private final BytecodeUsage bytecodeUsage;

    public ClassScanner(final Usage usage) {
        super(Opcodes.ASM8);
        this.bytecodeUsage = new BytecodeUsage(usage);
    }

    public Usage getUsage() {
        return bytecodeUsage.getUsage();
    }

    @Override
    public void visit(final int version, final int access, final String name, final String signature, final String superName, final String[] interfaces) {
        if (signature == null) {
            bytecodeUsage.addName(superName);
            bytecodeUsage.addNames(interfaces);
        } else {
            bytecodeUsage.addSignature(signature);
        }
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return super.visitAnnotation(descriptor, visible);
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

        return super.visitField(access, name, descriptor, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(final int access, final String name, final String descriptor, final String signature, final String[] exceptions) {
        if (signature == null) {
            bytecodeUsage.addMethodDesc(descriptor);
        } else {
            bytecodeUsage.addSignature(signature);
        }

        bytecodeUsage.addNames(exceptions);

        return super.visitMethod(access, name, descriptor, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
