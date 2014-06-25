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
package com.codenvy.ide.texteditor.embeddedimpl.orion.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class OrionKeyStrokeOverlay extends JavaScriptObject {

    protected OrionKeyStrokeOverlay() {
    }

    /**
     * Constructs a new key stroke with the given key code, modifiers and event type.
     * 
     * @param keyCode the key code.
     * @param mod1 the primary modifier (usually Command on Mac and Control on other platforms).
     * @param mod2 the secondary modifier (usually Shift).
     * @param mod3 the third modifier (usually Alt).
     * @param mod4 the fourth modifier (usually Control on the Mac).
     * @param type the type of event that the keybinding matches; either "keydown" or "keypress".
     */
    public static final native OrionKeyStrokeOverlay create(String keyCode,
                                                            boolean modifier1,
                                                            boolean modifier2,
                                                            boolean modifier3,
                                                            boolean modifier4,
                                                            String type, JavaScriptObject keyBindingModule) /*-{
        return new keyBindingModule.KeyStroke(keyCode, modifier1, modifier2, modifier3, modifier4, type);
    }-*/;
}
