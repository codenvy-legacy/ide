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
package com.codenvy.ide.ext.web.css.editor;

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.core.editor.CodenvyTextEditorFactory;
import com.google.inject.Inject;


/**
 * EditorProvider for Css css type
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class CssEditorProvider implements EditorProvider {
    private final DocumentProvider    documentProvider;
    private CodenvyTextEditorFactory  editorProvider;
    private final CssResources        cssRes;
    private final NotificationManager notificationManager;

    /** @param documentProvider */
    @Inject
    public CssEditorProvider(DocumentProvider documentProvider, CssResources cssRes, CodenvyTextEditorFactory editorProvider,
                             NotificationManager notificationManager) {
        this.documentProvider = documentProvider;
        this.editorProvider = editorProvider;
        this.cssRes = cssRes;
        this.notificationManager = notificationManager;
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {
        CodenvyTextEditor textEditor = editorProvider.get();
        textEditor.initialize(new CssEditorConfiguration(cssRes), documentProvider, notificationManager);
        return textEditor;
    }
}
