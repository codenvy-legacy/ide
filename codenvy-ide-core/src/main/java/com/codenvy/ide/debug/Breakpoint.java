/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.debug;

import com.codenvy.ide.api.projecttree.generic.FileNode;

/** @author Evgen Vidolob */
public class Breakpoint {
    protected int      lineNumber;
    protected FileNode file;
    private   Type     type;
    private   String   message;
    private   String   path;

    /**
     * @param type
     * @param lineNumber
     * @param path
     * @param file
     */
    public Breakpoint(Type type, int lineNumber, String path, FileNode file) {
        this(type, lineNumber, path, file, null);
    }

    /**
     * @param type
     * @param lineNumber
     * @param path
     * @param file
     * @param message
     */
    public Breakpoint(Type type, int lineNumber, String path, FileNode file, String message) {
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
    public FileNode getFile() {
        return file;
    }

    public enum Type {
        BREAKPOINT, DISABLED, CONDITIONAL, CURRENT
    }
}