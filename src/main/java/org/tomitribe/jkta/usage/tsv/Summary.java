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
package org.tomitribe.jkta.usage.tsv;

import org.tomitribe.jkta.usage.Jar;
import org.tomitribe.jkta.usage.PackageUsage;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class Summary {
    final AtomicInteger scanned = new AtomicInteger();
    final AtomicInteger affected = new AtomicInteger();
    final AtomicReference<PackageUsage<?>> total = new AtomicReference<>(new PackageUsage<>());

    public void add(final PackageUsage<Jar> usage) {
        total.accumulateAndGet(usage, PackageUsage::add);
        scanned.incrementAndGet();
        if (usage.getJavax() > 0) affected.incrementAndGet();
    }

    public int getScanned() {
        return scanned.get();
    }

    public int getAffected() {
        return affected.get();
    }

    public PackageUsage<?> getTotal() {
        return total.get();
    }

    public String summary() {
        final double affected = this.affected.get();
        final double scanned = this.scanned.get();
        final int percent = (int) ((affected / scanned) * 100);
        return String.format("total affected %s%% (%s of %s scanned)", percent, (int) affected, (int) scanned);
    }
}
