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
package org.tomitribe.jkta.review;

import org.tomitribe.util.dir.Dir;
import org.tomitribe.util.dir.Name;

import java.io.File;

public interface Version extends org.tomitribe.util.dir.Dir {

    @Name("_index.md")
    public File indexMd();

    default String name() {
        return get().getName();
    }

    default Specification specification() {
        return Specification.from(parent());
    }

    default String version() {
        return get().getParentFile().getParentFile().getName();
    }

//    default File specHtml() {
//        return Stream.of(get().listFiles())
//                .filter(file -> file.getName().endsWith(".html"));
//    }

    ReviewCommand.Apidocs apidocs();

    static Version from(final String name) {
        return from(new File(name));
    }

    static Version from(final File file) {
        return Dir.of(Version.class, file);
    }

}
