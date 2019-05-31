/* =====================================================================
 *
 * Copyright (c) 2011 David Blevins.  All rights reserved.
 *
 * =====================================================================
 */
package org.eclipse.wg.jakartaee;

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
