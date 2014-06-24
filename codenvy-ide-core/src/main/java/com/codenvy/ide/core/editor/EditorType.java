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
package com.codenvy.ide.core.editor;


public enum EditorType {
    CLASSIC(0, "Classic"),
    CODEMIRROR(1, "CodeMirror"),
    ORION(2, "Orion");

    private final int    index;
    private final String name;

    private EditorType(int index, String name) {
        this.index = index;
        this.name = name;
    }

    public int getIndex() {
        return this.index;
    }

    public String getName() {
        return this.name;
    }

    public static EditorType fromIndex(final int idx) {
        for (final EditorType type : values()) {
            if (type.getIndex() == idx) {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid editor type index: " + idx);
    }
}