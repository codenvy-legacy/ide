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
package com.codenvy.ide.core.editor;

import javax.inject.Inject;

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Provider;

/**
 * Factory for {@link CodenvyTextEditor}.
 * 
 * @author "Mickaël Leduque"
 */
public class CodenvyTextEditorFactory {

    @Inject
    private Provider<com.codenvy.ide.texteditor.TextEditorPresenter>       collideImplProvider;

    @Inject
    private Provider<com.codenvy.ide.texteditor.embeddedimpl.common.EmbeddedTextEditorPresenter> jsImplProvider;

    @Inject
    private EditorTypeSelection                                            editorTypeSelection;

    public CodenvyTextEditor get() {
        final EditorType editorType = editorTypeSelection.getEditorType();
        switch (editorType) {
            case CLASSIC:
                Log.info(CodenvyTextEditorFactory.class, "Editor requested, providing a classic implementation.");
                return collideImplProvider.get();
            case CODEMIRROR:
            case ORION:
                Log.info(CodenvyTextEditorFactory.class, "Editor requested, providing a CodeMirror/Orion implementation.");
                return jsImplProvider.get();
            default:
                throw new IllegalArgumentException("Text editor type " + editorType + " is not supported");
        }
    }
}
