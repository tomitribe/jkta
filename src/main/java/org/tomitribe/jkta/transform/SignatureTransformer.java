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

import org.objectweb.asm.signature.SignatureVisitor;
import org.tomitribe.jkta.usage.scan.BytecodeUsage;

public class SignatureTransformer extends SignatureVisitor {
    private final BytecodeUsage bytecodeUsage;

    public SignatureTransformer(final int api, final BytecodeUsage bytecodeUsage) {
        super(api);
        this.bytecodeUsage = bytecodeUsage;
    }

    @Override
    public SignatureVisitor visitArrayType() {
        return this;
    }

    @Override
    public void visitClassType(final String name) {
        bytecodeUsage.addName(name);
    }

    @Override
    public void visitInnerClassType(final String name) {
        bytecodeUsage.addName(name);
    }

    @Override
    public SignatureVisitor visitTypeArgument(final char wildcard) {
        return this;
    }
}
