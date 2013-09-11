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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.server.util.PathUtil;

import java.util.Arrays;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
final class Path {
    static Path fromString(String path) {
        return new Path(PathUtil.parse(path));
    }

    static final Path ROOT = new Path();

    private final    String[] elements;
    private volatile int      hashCode;
    private volatile String   asString;
    private volatile String   ioPath;

    private Path(String... elements) {
        this.elements = elements;
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
        final String[] relative = PathUtil.parse(name);
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

    /* Relative system path */
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
