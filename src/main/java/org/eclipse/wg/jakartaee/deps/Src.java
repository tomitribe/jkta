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
package org.eclipse.wg.jakartaee.deps;

import org.tomitribe.util.JarLocation;
import org.tomitribe.util.dir.Dir;
import org.tomitribe.util.dir.Name;

import java.io.File;

public class Src {

    public static Main main() {
        final File targetClasses = JarLocation.jarLocation(Src.class);
        final File projectDir = targetClasses.getParentFile().getParentFile();
        return Dir.of(Main.class, new File(projectDir, "src/main"));
    }

    interface Main {
        Resources resources();
    }

    interface Resources {
        Deps deps();
    }

    interface Deps {
        Raw raw();

        Split split();
    }

    interface Raw extends Dir {
        @Name("jakartaee-classes.json")
        File jakartaEEClassesJson();
    }

    interface Split extends Dir {
    }

}
