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

import org.apache.openejb.loader.IO;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.IOException;

public class TransformClass {

    public byte[] transform(final byte[] bytecode) throws IOException {
        final ClassWriter classWriter = new ClassWriter(Opcodes.ASM8);
        final ClassTransformer classTransformer = new ClassTransformer(classWriter);
        final ClassReader classReader = new ClassReader(IO.read(bytecode));
        classReader.accept(classTransformer, 0);
        return classWriter.toByteArray();

    }
}
