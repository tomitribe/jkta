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
package org.tomitribe.jakartaee.analysis.usage;

import java.io.File;

public class Jar {
    private final File jar;
    private final String sha1;
    private final long lastModified;

    /**
     * @param jar the file reference for this jar
     * @param sha1 the SHA-1 hash of this jar's content
     * @param lastModified last modified time at the exact time the hash was created
     */
    public Jar(final File jar, final String sha1, final long lastModified) {
        this.jar = jar;
        this.sha1 = sha1;
        this.lastModified = lastModified;
    }

    public File getJar() {
        return jar;
    }

    public String getSha1() {
        return sha1;
    }

    public long getLastModified() {
        return lastModified;
    }
}
