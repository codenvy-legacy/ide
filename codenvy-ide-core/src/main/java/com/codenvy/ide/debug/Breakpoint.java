/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package com.codenvy.ide.debug;

/** @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a> */
public class Breakpoint {
    public enum Type {
        BREAKPOINT, DISABLED, CONDITIONAL, CURRENT
    }

    protected int    lineNumber;
    private   Type   type;
    private   String message;
    private   String path;

    /**
     * @param type
     * @param lineNumber
     */
    public Breakpoint(Type type, int lineNumber, String path) {
        this(type, lineNumber, path, null);
    }

    /**
     * @param type
     * @param lineNumber
     * @param message
     */
    public Breakpoint(Type type, int lineNumber, String path, String message) {
        super();
        this.type = type;
        this.lineNumber = lineNumber;
        this.path = path;
        this.message = message;
    }

    /** @return the type */
    public Type getType() {
        return type;
    }

    /** @return the lineNumber */
    public int getLineNumber() {
        return lineNumber;
    }

    /** @return the message */
    public String getMessage() {
        return message;
    }

    /** @return file path */
    public String getPath() {
        return path;
    }
}