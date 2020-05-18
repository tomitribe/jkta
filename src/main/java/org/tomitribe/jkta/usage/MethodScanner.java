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
import org.objectweb.asm.ConstantDynamic;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.TypePath;

public class MethodScanner extends MethodVisitor {
    private final BytecodeUsage bytecodeUsage;

    public MethodScanner(final int api, final BytecodeUsage bytecodeUsage) {
        super(api);
        this.bytecodeUsage = bytecodeUsage;
    }

    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return new AnnotationScanner(this.api, bytecodeUsage);
    }

    @Override
    public AnnotationVisitor visitAnnotation(final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationScanner(this.api, bytecodeUsage);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationScanner(this.api, bytecodeUsage);
    }

    @Override
    public AnnotationVisitor visitParameterAnnotation(final int parameter, final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationScanner(this.api, bytecodeUsage);
    }

    @Override
    public void visitFrame(final int type, final int numLocal, final Object[] local, final int numStack, final Object[] stack) {
        switch (type) {
            case -1:
            case 0:
                add(bytecodeUsage, local);
                add(bytecodeUsage, stack);
                break;
            case 1:
                add(bytecodeUsage, local);
                break;
            case 2:
                break;
            case 3:
                break;
            case 4:
                add(bytecodeUsage, stack);
                break;
            default:
                throw new IllegalArgumentException();
        }
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        bytecodeUsage.addObjectType(type);
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String descriptor) {
        bytecodeUsage.addDesc(descriptor);
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String descriptor, final boolean isInterface) {
        bytecodeUsage.addObjectType(owner);
        bytecodeUsage.addMethodDesc(descriptor);
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String descriptor, final Handle bootstrapMethodHandle, final Object... bootstrapMethodArguments) {
        bytecodeUsage.addMethodDesc(descriptor);
        bytecodeUsage.addHandle(bootstrapMethodHandle);
        bytecodeUsage.addHandleArgs(bootstrapMethodArguments);
    }

    @Override
    public void visitLdcInsn(final Object cst) {
        if (cst instanceof Integer) {
            // ...
        } else if (cst instanceof Float) {
            // ...
        } else if (cst instanceof Long) {
            // ...
        } else if (cst instanceof Double) {
            // ...
        } else if (cst instanceof String) {
            // ...
        } else if (cst instanceof Type) {
            bytecodeUsage.addType((Type)cst);
        } else if (cst instanceof Handle) {
            bytecodeUsage.addHandle((Handle) cst);
            // ...
        } else if (cst instanceof ConstantDynamic) {
            bytecodeUsage.addConstantDynamic((ConstantDynamic)cst);
            // ...
        } else {
            // throw an exception
        }
    }

    @Override
    public void visitMultiANewArrayInsn(final String descriptor, final int numDimensions) {
        bytecodeUsage.addDesc(descriptor);
        super.visitMultiANewArrayInsn(descriptor, numDimensions);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationScanner(this.api, bytecodeUsage);
    }

    @Override
    public void visitTryCatchBlock(final Label start, final Label end, final Label handler, final String type) {
        bytecodeUsage.addObjectType(type);
        super.visitTryCatchBlock(start, end, handler, type);
    }

    @Override
    public AnnotationVisitor visitTryCatchAnnotation(final int typeRef, final TypePath typePath, final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationScanner(this.api, bytecodeUsage);
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(final int typeRef, final TypePath typePath, final Label[] start, final Label[] end,
                                                          final int[] index, final String descriptor, final boolean visible) {
        bytecodeUsage.addDesc(descriptor);
        return new AnnotationScanner(this.api, bytecodeUsage);
    }

    private static void add(final BytecodeUsage bytecodeUsage, final Object[] references) {
        for (final Object o : references) {
            if (o instanceof String) {
                bytecodeUsage.addType(Type.getObjectType((String) o));
            }
        }
    }
}
