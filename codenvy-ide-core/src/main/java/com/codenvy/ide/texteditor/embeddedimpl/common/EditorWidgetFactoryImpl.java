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

package com.codenvy.ide.texteditor.embeddedimpl.common;

import javax.inject.Inject;

import com.codenvy.ide.core.editor.CodenvyTextEditorFactory;
import com.codenvy.ide.core.editor.EditorType;
import com.codenvy.ide.core.editor.EditorTypeSelection;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.texteditor.embeddedimpl.codemirror.CodeMirrorEditorWidgetFactory;
import com.codenvy.ide.texteditor.embeddedimpl.orion.OrionEditorWidgetFactory;
import com.codenvy.ide.util.loging.Log;

/**
 * Impelemntation of {@link EditorWidgetFactory} that chooses the correct implementation from the {@link EditorTypeSelection}.
 * 
 * @author "Mickaël Leduque"
 */
public class EditorWidgetFactoryImpl implements EditorWidgetFactory {

    @Inject
    private CodeMirrorEditorWidgetFactory codeMirrorImplProvider;

    @Inject
    private OrionEditorWidgetFactory      orionImplProvider;

    @Inject
    private EditorTypeSelection           editorTypeSelection;

    @Override
    public EditorWidget createEditorWidget(final String editorMode, final Document document) {
        final EditorType editorType = editorTypeSelection.getEditorType();
        switch (editorType) {
            case CODEMIRROR:
                Log.info(CodenvyTextEditorFactory.class, "Editor requested, providing a CodeMirror implementation.");
                return codeMirrorImplProvider.createEditorWidget(editorMode, document);
            case ORION:
                Log.info(CodenvyTextEditorFactory.class, "Editor requested, providing an Orion implementation.");
                return orionImplProvider.createEditorWidget(editorMode, document);
            default:
                throw new IllegalArgumentException("Text editor type " + editorType + " is not supported");
        }
    }
}
