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

import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;

public class BytecodeUsage {

    private final Usage usage;

    public BytecodeUsage() {
        this(new Usage());
    }

    public BytecodeUsage(final Usage usage) {
        this.usage = usage;
    }

    public void addName(final String name) {
        if (name == null) {
            return;
        }

        final String className = name.replace('/', '.');

        usage.visit(className);
    }

    public Usage getUsage() {
        return usage;
    }

    public void addNames(final String[] names) {
        for (int i = 0; names != null && i < names.length; i++) {
            addName(names[i]);
        }
    }

    public void addDesc(final String desc) {
        addType(Type.getType(desc));
    }

    public void addSignature(final String signature) {
        if (signature != null) {
            new SignatureReader(signature).accept(new SignatureScanner(this));
        }
    }

    public void addTypeSignature(final String signature) {
        if (signature != null) {
            new SignatureReader(signature).acceptType(new SignatureScanner(this));
        }
    }

    public void addMethodDesc(final String desc) {
        addType(Type.getReturnType(desc));
        final Type[] types = Type.getArgumentTypes(desc);
        for (Type type : types) {
            addType(type);
        }
    }

    public void addType(final Type t) {
        switch (t.getSort()) {
            case Type.ARRAY:
                addType(t.getElementType());
                break;
            case Type.OBJECT:
                addName(t.getClassName().replace('.', '/'));
                break;
            default: { /* ignored */}
        }
    }
}
