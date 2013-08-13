/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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