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
package com.codenvy.ide.texteditor.embeddedimpl.codemirror.jso;

import com.google.gwt.core.client.JavaScriptObject;

public class CMSetSelectionOptions extends JavaScriptObject {

    protected CMSetSelectionOptions() {
    }

    /*
     * Should we scroll to the selection head ? Default to true
     */

    public final native boolean getScroll() /*-{
        return this.scroll;
    }-*/;

    public final native void setScroll(boolean newValue) /*-{
        this.scroll = newValue;
    }-*/;

    /*
     * Detemines whether the selection history event may be merged with the previous one
     */


    public final native boolean getOrigin() /*-{
        return this.origin;
    }-*/;

    public final native void setOrigin(String newValue) /*-{
        this.origin = newValue;
    }-*/;

    /*
     * Adjustement direction when the range is atomic (the cursor can't go inside). 1 or -1. By default, depends on the relative position of
     * the old selection
     */

    public final native boolean getBias() /*-{
        return this.bias;
    }-*/;

    public final native void setBias(int newValue) /*-{
        this.bias = newValue;
    }-*/;
}
