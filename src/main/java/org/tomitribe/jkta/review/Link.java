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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Link {

    private final static Pattern format = Pattern.compile("\\[(.+)\\]\\((.*)\\)");
    private final String title;
    private final String href;

    public Link(final String title, final String href) {
        this.title = title;
        this.href = href;
    }

    public String getTitle() {
        return title;
    }

    public String getHref() {
        return href;
    }

    public static Link parse(final String text) {
        final Matcher matcher = format.matcher(text);
        if (!matcher.find()) return null;

        final String title = matcher.group(1);
        final String href = matcher.group(2);
        return new Link(title, href);
    }

    @Override
    public String toString() {
        return "Link{" +
                "title='" + title + '\'' +
                ", href='" + href + '\'' +
                '}';
    }
}
