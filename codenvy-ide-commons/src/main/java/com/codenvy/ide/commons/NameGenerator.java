/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.commons;

import java.util.Random;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class NameGenerator {
    private static final Random RANDOM = new Random();

    private static final char[] CHARS = new char[36];

    static {
        int i = 0;
        for (int c = 48; c <= 57; c++) {
            CHARS[i++] = (char)c;
        }
        for (int c = 97; c <= 122; c++) {
            CHARS[i++] = (char)c;
        }
    }

    public static String generate(String prefix, int length) {
        StringBuilder b;
        if (prefix == null || prefix.isEmpty()) {
            b = new StringBuilder(length);
        } else {
            b = new StringBuilder(length + prefix.length());
            b.append(prefix);
        }
        for (int i = 0; i < length; i++) {
            b.append(CHARS[RANDOM.nextInt(CHARS.length)]);
        }
        return b.toString();
    }

    private NameGenerator() {
    }
}