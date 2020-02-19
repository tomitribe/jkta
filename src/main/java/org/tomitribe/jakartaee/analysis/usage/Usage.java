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

import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class is intentionally a pure count with no other context object.
 *
 * It could be the total of a class.  It could be the total of a jar.  It could be the total of a groupId
 * It could be the total of a local maven repo.  It could be the total of all of Maven Central.
 */
public class Usage<Context> {

    private final Context context;
    private int javax = 0;
    private int jakarta = 0;
    private final int[] packages = new int[Package.values().length];

    public Usage() {
        this(null);
    }

    public Usage(final Context context) {
        this.context = context;
    }

    public void visit(final String reference) {
        final Package match = match(reference);
        if (match == null) return;

        packages[match.ordinal()]++;

        if (match.getName().startsWith("javax")) javax++;
        if (match.getName().startsWith("jakarta")) jakarta++;
    }

    private Package match(final String reference) {
        for (final Package aPackage : Package.values()) {
            if (aPackage.matches(reference)) return aPackage;
        }
        return null;
    }

    public Context getContext() {
        return context;
    }

    public int getJavax() {
        return javax;
    }

    public int getJakarta() {
        return jakarta;
    }

    public int[] getPackages() {
        return packages;
    }

    public int get(final Package aPackage) {
        return packages[aPackage.ordinal()];
    }

    /**
     * Used when aggregating results together.
     * Several Usage instances each representing a class could be
     * added together to represent a jar.
     *
     * Several jar Usage instances could be added together to
     * represent a groupId, or local repo.
     *
     * Several groupId Usage instances could be added together
     * to represent Maven Central
     */
    public Usage<Context> add(final Usage that) {
        final Usage<Context> total = new Usage(this.context);
        total.javax = this.javax + that.javax;
        total.jakarta = this.jakarta + that.jakarta;
        for (int i = 0; i < packages.length; i++) {
            total.packages[i] = this.packages[i] + that.packages[i];
        }

        return total;
    }

    public String toTsv() {
        final String t = "\t";
        final StringBuilder sb = new StringBuilder(packages.length * 10);
        sb.append(javax).append(t).append(jakarta);
        for (final int count : packages) {
            sb.append(t).append(count);
        }
        return sb.toString();
    }

    public static Usage fromTsv(final String line) {
        return fromTsv(null, line);
    }

    public static <Context> Usage<Context> fromTsv(final Context context, final String line) {
        final Iterator<Integer> counts = Stream.of(line.split("\t"))
                .map(Integer::new)
                .collect(Collectors.toList())
                .iterator();

        final Usage<Context> usage = new Usage<>(context);
        usage.javax = counts.next();
        usage.jakarta = counts.next();

        for (int i = 0; counts.hasNext(); i++) {
            usage.packages[i] = counts.next();
        }

        return usage;
    }

}
