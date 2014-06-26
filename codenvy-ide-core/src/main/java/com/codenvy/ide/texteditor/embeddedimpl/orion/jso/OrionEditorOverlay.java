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
import com.google.gwt.dom.client.Element;

public class OrionEditorOverlay extends JavaScriptObject {

    protected OrionEditorOverlay() {
    }

    public final native String getText() /*-{
        return this.getText();
    }-*/;

    public final native void setText(final String newValue) /*-{
        this.setText(newValue);
    }-*/;

    public final native OrionTextViewOverlay getTextView() /*-{
        return this.getTextView();
    }-*/;

    public final native void focus() /*-{
        this.focus();
    }-*/;

    public final native OrionTextModelOverlay getModel() /*-{
        return this.getModel();
    }-*/;

    public final native OrionSelectionOverlay getSelection() /*-{
        return this.getSelection();
    }-*/;

    public final native boolean isDirty() /*-{
        return this.isDirty();
    }-*/;

    public final native void setDirty(final boolean newValue) /*-{
        this.setDirty(newValue);
    }-*/;

    public final static native OrionEditorOverlay createEditor(final Element element,
                                                               final JavaScriptObject options,
                                                               final JavaScriptObject orionEditor) /*-{

        options.parent = element;
        var editor = orionEditor(options);
        return editor;
    }-*/;
}
