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
 * Github Project and Source Code Analysis
 *
 * This package contains code used to analyze the ability to split
 * certain EE4J repos out into the jakartaee org.
 *
 * This code will:
 *
 *  - walk the eclipse-ee4j org in Github via the Github API
 *  - clone each repo via jgit
 *  - parse the source code via JavaParser
 *
 *  A command line version of this is provided, but not really needed
 */
package org.tomitribe.jkta.repos;