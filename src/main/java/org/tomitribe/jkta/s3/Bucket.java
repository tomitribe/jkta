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
package org.tomitribe.jkta.s3;

import com.amazonaws.services.s3.AmazonS3;

import java.io.OutputStream;

public class Bucket {
    private final AmazonS3 client;
    private final String name;

    public Bucket(final AmazonS3 client, final String name) {
        this.client = client;
        this.name = name;
    }

    public OutputStream upload(final String key) {
        return new S3OutputStream(client, name, key);
    }
}
