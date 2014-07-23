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

import com.codenvy.api.project.shared.dto.ItemReference;

/** @author Evgen Vidolob */
public class Breakpoint {
    public enum Type {
        BREAKPOINT, DISABLED, CONDITIONAL, CURRENT
    }

    protected int    lineNumber;
    private   Type   type;
    private   String message;
    private   String path;
    protected ItemReference   file;

    /**
     * @param type
     * @param lineNumber
     * @param path
     * @param file
     */
    public Breakpoint(Type type, int lineNumber, String path, ItemReference file) {
        this(type, lineNumber, path, file, null);
    }

    /**
     * @param type
     * @param lineNumber
     * @param path
     * @param file
     * @param message
     */
    public Breakpoint(Type type, int lineNumber, String path, ItemReference file, String message) {
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
    public ItemReference getFile() {
        return file;
    }
}