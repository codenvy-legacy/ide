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

public class OrionTextViewShowOptionsOverlay extends JavaScriptObject {

    protected OrionTextViewShowOptionsOverlay() {
    }

    public final native String getScrollPolicy() /*-{
        return this.scrollPolicy;
    }-*/;

    public final native void setScrollPolicy(final String newValue) /*-{
        this.scrollPolicy = newValue;
    }-*/;

    public final native String getSelectionAnchor() /*-{
        return this.selectionAnchor;
    }-*/;

    public final native void setSelectionAnchor(final String newValue) /*-{
        this.selectionAnchor = newValue;
    }-*/;

    public final native String getViewAnchor() /*-{
        return this.viewAnchor;
    }-*/;

    public final native void setViewAnchor(final String newValue) /*-{
        this.viewAnchor = newValue;
    }-*/;

    public final native String getViewAnchorOffset() /*-{
        return this.viewAnchorOffset;
    }-*/;

    public final native void setViewAnchorOffset(final String newValue) /*-{
        this.viewAnchorOffset = newValue;
    }-*/;
}
