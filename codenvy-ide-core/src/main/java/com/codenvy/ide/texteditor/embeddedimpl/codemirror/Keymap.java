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
package com.codenvy.ide.texteditor.embeddedimpl.codemirror;

/**
 * Enum for keymaps supported by CodeMirror.
 * 
 * @author "Mickaël Leduque"
 */
public enum Keymap {
    DEFAULT(0, "default"),
    EMACS(1, "emacs"),
    VIM(2, "vim"),
    SUBLIME(3, "sublime");

    private String codemirrorKey;
    private int    index;

    private Keymap(final int index, final String key) {
        this.index = index;
        this.codemirrorKey = key;
    }

    public String getCodeMirrorKey() {
        return this.codemirrorKey;
    }

    public int getIndex() {
        return this.index;
    }

    public static Keymap fromCodeMirrorKey(final String key) {
        for (Keymap value : values()) {
            if (value.codemirrorKey.equals(key)) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown keymap code");
    }

    public static Keymap fromIndex(final int index) {
        for (Keymap value : values()) {
            if (value.index == index) {
                return value;
            }
        }
        throw new IllegalArgumentException("Unknown keymap index");
    }
}
