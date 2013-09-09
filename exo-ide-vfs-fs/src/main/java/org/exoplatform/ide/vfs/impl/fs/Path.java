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
package org.exoplatform.ide.vfs.impl.fs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class Path {
    static Path fromString(String path) {
        return new Path(parse(path));
    }

    static final         Path     ROOT       = new Path();
    private static final String[] EMPTY_PATH = new String[0];

    private static final Pattern PATH_SPLITTER = Pattern.compile("/");

    private static String[] parse(String raw) {
        String[] parsed = ((raw == null) || raw.isEmpty() || ((raw.length() == 1) && (raw.charAt(0) == '/')))
                          ? EMPTY_PATH : PATH_SPLITTER.split(raw.charAt(0) == '/' ? raw.substring(1) : raw);
        if (parsed.length == 0) {
            return parsed;
        }
        List<String> newTokens = new ArrayList<String>(parsed.length);
        for (String token : parsed) {
            if ("..".equals(token)) {
                int size = newTokens.size();
                if (size == 0) {
                    throw new IllegalArgumentException(String.format("Invalid path '%s', '..' on root. ", raw));
                }
                newTokens.remove(size - 1);
            } else if (!".".equals(token)) {
                newTokens.add(token);
            }
        }

        return newTokens.toArray(new String[newTokens.size()]);
    }

    private final String[] elements;

    private volatile int hashCode;

    private volatile String asString;
    private volatile String ioPath;

    private Path(String... elements) {
        this.elements = elements;
//      this.elements = new String[elements.length];
//      System.arraycopy(elements, 0, this.elements, 0, elements.length);
    }

    Path getParent() {
        return isRoot() ? null : elements.length == 1 ? ROOT : subPath(0, elements.length - 1);
    }

    Path subPath(int beginIndex) {
        return subPath(beginIndex, elements.length);
    }

    Path subPath(int beginIndex, int endIndex) {
        if (beginIndex < 0 || beginIndex >= elements.length || endIndex > elements.length || beginIndex >= endIndex) {
            throw new IllegalArgumentException("Invalid end or begin index. ");
        }
        final int len = endIndex - beginIndex;
        final String[] subPath = new String[len];
        System.arraycopy(elements, beginIndex, subPath, 0, len);
        return new Path(subPath);
    }

    String getName() {
        return isRoot() ? "" : element(elements.length - 1);
    }

    String[] elements() {
        String[] copy = new String[elements.length];
        System.arraycopy(elements, 0, copy, 0, elements.length);
        return copy;
    }

    int length() {
        return elements.length;
    }

    private String element(int index) {
        if (index < 0 || index >= elements.length) {
            throw new IllegalArgumentException("Invalid index. ");
        }
        return elements[index];
    }

    boolean isRoot() {
        return elements.length == 0;
    }

    boolean isChild(Path parent) {
        if (parent.elements.length >= this.elements.length) {
            return false;
        }
        for (int i = 0, parentLength = parent.elements.length; i < parentLength; i++) {
            if (!parent.elements[i].equals(this.elements[i])) {
                return false;
            }
        }
        return true;
    }

    Path newPath(String name) {
        final String[] relative = parse(name);
        if (relative.length == 0) {
            return this; // It is safety to return this instance since it is immutable.
        }
        final String[] absolute = new String[elements.length + relative.length];
        System.arraycopy(elements, 0, absolute, 0, elements.length);
        System.arraycopy(relative, 0, absolute, elements.length, relative.length);
        return new Path(absolute);
    }

    Path newPath(Path relative) {
        final String[] absolute = new String[elements.length + relative.elements.length];
        System.arraycopy(elements, 0, absolute, 0, elements.length);
        System.arraycopy(relative.elements, 0, absolute, elements.length, relative.elements.length);
        return new Path(absolute);
    }

    /* Relative path that system-specific name-separator character. */
    String toIoPath() {
        if (isRoot()) {
            return "";
        }
        if (needConvert()) {
            if (ioPath == null) {
                ioPath = concat(java.io.File.separatorChar);
            }
            return ioPath;
        }
        // Unix like system. Use vfs path as relative i/o path.
        return toString();
    }

    private boolean needConvert() {
        return '/' != java.io.File.separatorChar;
    }

    private String concat(char separator) {
        StringBuilder builder = new StringBuilder();
        for (String element : elements) {
            builder.append(separator);
            builder.append(element);
        }
        return builder.toString();
    }

   /* ==================================================== */

    @Override
    public String toString() {
        if (isRoot()) {
            return "/";
        }
        if (asString == null) {
            asString = concat('/');
        }
        return asString;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Path)) {
            return false;
        }
        Path path = (Path)o;
        return Arrays.equals(elements, path.elements);
    }

    @Override
    public int hashCode() {
        int hash = hashCode;
        if (hash == 0) {
            hash = 8;
            hash = 31 * hash + Arrays.hashCode(elements);
            hashCode = hash;
        }
        return hash;
    }
}
