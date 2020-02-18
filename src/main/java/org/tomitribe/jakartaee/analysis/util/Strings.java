/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.tomitribe.jakartaee.analysis.util;

import org.tomitribe.util.Join;
import org.tomitribe.util.collect.ObjectMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class Strings {

    public static String toString(final Map.Entry<String, Object> entry) {
        final String key = entry.getKey();
        try {
            return key + ": " + toString(entry.getValue());
        } catch (Exception e) {
            return key + ": " + e.getClass().getSimpleName() + " " + e.getMessage();
        }
    }

    public static String toString(final Object value) {
        if (value == null) return "";
        if (value instanceof Collection) {
            final Collection collection = (Collection) value;
            return Join.join(", ", Strings::toString, collection);
        }
        if (value.getClass().getSimpleName().startsWith("GH")) return betterToString(value);
        final String s = value + "";
        if (s.startsWith(value.getClass().getName())) return betterToString(value);
        return s;
    }

    private static String betterToString(final Object value) {
        final String simpleName = value.getClass().getSimpleName();
        final String string = value + "";

        if (!string.startsWith(simpleName)) return string;

        final ObjectMap map = new ObjectMap(value);

        final List<String> list = new ArrayList<>(map.keySet());
        list.add(0, "key");
        list.add(0, "id");
        list.add(0, "name");

        for (final String key : list) {
            final String v = value(map, key);
            if (v != null) return v;
        }

        return string;
    }

    private static String value(final ObjectMap map, final String key) {
        try {
            final Object o = map.get(key);
            if (o != null) return toString(o);
        } catch (Exception e) {
        }
        return null;
    }
}
