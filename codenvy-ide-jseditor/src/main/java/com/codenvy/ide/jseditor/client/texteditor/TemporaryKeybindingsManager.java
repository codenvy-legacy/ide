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
package com.codenvy.ide.jseditor.client.texteditor;

import java.util.ArrayList;
import java.util.List;

import com.codenvy.ide.jseditor.client.keymap.Keybinding;

/** Hold {@link Keybinding} until the editor is ready to accept them. */
public class TemporaryKeybindingsManager implements HasKeybindings {

    private final List<Keybinding> bindings = new ArrayList<>();

    @Override
    public void addKeybinding(final Keybinding keybinding) {
        this.bindings.add(keybinding);
    }

    public List<Keybinding> getbindings() {
        return this.bindings;
    }
}
