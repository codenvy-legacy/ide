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
package com.codenvy.ide.texteditor;

import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ProvidesResize;
import com.google.gwt.user.client.ui.RequiresResize;

/**
 * Widget for {@link TextEditorViewImpl} element to catch {@link RequiresResize#onResize()} event.
 * 
 * @author Ann Shumilova
 */
public class TextEditorViewResizable extends HTML implements RequiresResize, ProvidesResize {

    private final TextEditorViewImpl editorView;

    public TextEditorViewResizable(TextEditorViewImpl editorView) {
        this.editorView = editorView;
        getElement().appendChild(editorView.getElement());
    }


    /** {@inheritDoc} */
    @Override
    public void onResize() {
        editorView.onResize();
    }


    /**
     * @return the editorView
     */
    public TextEditorViewImpl getEditorView() {
        return editorView;
    }
}
