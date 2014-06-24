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
import com.google.inject.Provider;

/**
 * Factory for {@link CodenvyTextEditor}.
 * 
 * @author "Mickaël Leduque"
 */
public class CodenvyTextEditorFactory {

    @Inject
    private Provider<com.codenvy.ide.texteditor.TextEditorPresenter> collideImplProvider;

    @Inject
    private EditorTypeSelection                                      editorTypeSelection;

    public CodenvyTextEditor get() {
        final EditorType editorType = editorTypeSelection.getEditorType();
        switch (editorType) {
            case CLASSIC:
                return collideImplProvider.get();
            default:
                throw new IllegalArgumentException("Text editor type " + editorType + " is unknown");
        }
    }
}
