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

public class OrionKeyModeOverlay extends JavaScriptObject {

    protected OrionKeyModeOverlay() {
    }

    /* These two method should be modified to use an overlay type instead of JSO */
    public final native JsArray<OrionKeyBindingOverlay> getKeyBindings(String actionID) /*-{
        return this.getKeyBindings(actionID);
    }-*/;

    public final native void setKeyBinding(OrionKeyBindingOverlay keyBinding, String actionID) /*-{
        return this.setKeyBinding(keyBinding, actionID);
    }-*/;

    /* ------------------------------------------------------------------------- */

    public final native OrionTextViewOverlay getView() /*-{
        return this.getView();
    }-*/;

    public final native boolean isActive() /*-{
        return this.isActive();
    }-*/;

    public final static native OrionKeyModeOverlay getEmacsKeyMode(JavaScriptObject orionEmacsModuleObject, OrionTextViewOverlay textView) /*-{
        return new orionEmacsModuleObject.EmacsMode(textView);
    }-*/;

    public final static native OrionKeyModeOverlay getViKeyMode(JavaScriptObject orionViModuleObject, OrionTextViewOverlay textView) /*-{
        return new orionViModuleObject.VIMode(textView);
    }-*/;

    public final static native OrionKeyModeOverlay getDefaultKeyMode(OrionTextViewOverlay textView) /*-{
        var keyModes = textView.getKeyModes();
        return keyModes[0];
    }-*/;
}
