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

import com.google.gwt.core.client.JavaScriptObject;

/**
 * A list of key bindings.
 * 
 * @author "Mickaël Leduque"
 */
public class KeyBindings extends JavaScriptObject {

    protected KeyBindings() {
    }

    public final native void addBinding(String keySpec, KeyBindingAction keyBindingAction) /*-{
        this[keySpec] = function(editor) {
            keyBindingAction.@com.codenvy.ide.texteditor.embeddedimpl.codemirror.KeyBindingAction::action()();
        }
    }-*/;

    public final static native KeyBindings create() /*-{
        return {};
    }-*/;
}
