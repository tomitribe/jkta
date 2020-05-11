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
/**
 * Maven Central and Bytecode Analysis
 *
 * This package contains code used to analyze the references between
 * each Jakarta EE API.
 *
 * This code will:
 *
 *  - walk the `jakarta` groupId in Maven Central
 *  - download each jar and cache it locally
 *  - extract each jar into a temp directory
 *  - parse the bytecode code via ASM
 *
 *  All the above has been done and a jakartaee-classes.json file exists
 *  in this repo and can be used to do analysis, skipping the above steps.
 */
package org.tomitribe.jkta.deps;