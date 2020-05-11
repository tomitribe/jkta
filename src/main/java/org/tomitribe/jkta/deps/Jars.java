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
package org.tomitribe.jkta.deps;

import org.tomitribe.util.IO;

import javax.json.bind.JsonbBuilder;
import java.io.File;
import java.io.FileNotFoundException;

public class Jars {

    private Jars() {
    }

    public static void toJson(final File dest, final Jar jar) throws FileNotFoundException {
        JsonbBuilder.create().toJson(jar, IO.write(new File(dest, jar.getName() + ".json")));
    }

    public static Jar fromJson(final File file)  {
        try {
            return JsonbBuilder.create().fromJson(IO.read(file), Jar.class);
        } catch (FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }
}
