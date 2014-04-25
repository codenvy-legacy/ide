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
package com.codenvy.ide.debug;

import com.codenvy.ide.api.resources.model.File;

/** @author Evgen Vidolob */
public class Breakpoint {
    public enum Type {
        BREAKPOINT, DISABLED, CONDITIONAL, CURRENT
    }

    protected int    lineNumber;
    private   Type   type;
    private   String message;
    private   String path;
    protected File   file;

    /**
     * @param type
     * @param lineNumber
     * @param path
     * @param file
     */
    public Breakpoint(Type type, int lineNumber, String path, File file) {
        this(type, lineNumber, path, file, null);
    }

    /**
     * @param type
     * @param lineNumber
     * @param path
     * @param file
     * @param message
     */
    public Breakpoint(Type type, int lineNumber, String path, File file, String message) {
        super();
        this.type = type;
        this.lineNumber = lineNumber;
        this.path = path;
        this.message = message;
        this.file = file;
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

    /**
     * Returns the file with which this breakpoint is associated.
     *
     * @return file with which this breakpoint is associated
     */
    public File getFile() {
        return file;
    }
}