/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
 *  All Rights Reserved.
 * 
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
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
