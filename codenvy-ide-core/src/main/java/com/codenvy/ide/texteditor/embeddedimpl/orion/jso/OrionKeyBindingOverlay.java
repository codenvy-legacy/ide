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
import com.google.gwt.core.client.JsArray;

public class OrionKeyBindingOverlay extends JavaScriptObject {

    protected OrionKeyBindingOverlay() {
    }

    public static final native OrionKeyBindingOverlay createKeyStroke(String keyCode,
                                                                      boolean modifier1,
                                                                      boolean modifier2,
                                                                      boolean modifier3,
                                                                      boolean modifier4,
                                                                      String type, JavaScriptObject keyBindingModule) /*-{
        return new keyBindingModule.KeyStroke(keyCode, modifier1, modifier2, modifier3, modifier4, type);
    }-*/;

    public static final native OrionKeyBindingOverlay createKeySequence(JsArray<OrionKeyStrokeOverlay> keys,
                                                                        JavaScriptObject keyBindingModule) /*-{
        return new keyBindingModule.KeySequence(keys);
    }-*/;
}
