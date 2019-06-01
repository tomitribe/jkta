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
package org.eclipse.wg.jakartaee;

import org.tomitribe.util.Escapes;
import org.tomitribe.util.Join;
import org.tomitribe.util.StringTemplate;
import org.tomitribe.util.collect.ObjectMap;

import java.util.Collection;
import java.util.function.Function;

public class Formatter<Object> implements Function<Object, String> {

    private final StringTemplate template;

    public Formatter(final String template) {
        this.template = new StringTemplate(template);
    }

    @Override
    public String apply(final Object object) {
        final ObjectMap map = new ObjectMap(object);
        final Function<String, String> function = new StringValue(map);
        final String formatted = template.apply(function);
        return Escapes.unescape(formatted);
    }

    private static class StringValue implements Function<String, String> {
        private final ObjectMap map;

        public StringValue(final ObjectMap map) {
            this.map = map;
        }

        @Override
        public String apply(final String s) {
            final java.lang.Object value = map.get(s);
            return toString(value);
        }

        public static String toString(final java.lang.Object value) {
            if (value == null) return "";
            if (value instanceof Collection) return Join.join(", ", StringValue::toString, (Collection) value);
            return value + "";
        }
    }
}
