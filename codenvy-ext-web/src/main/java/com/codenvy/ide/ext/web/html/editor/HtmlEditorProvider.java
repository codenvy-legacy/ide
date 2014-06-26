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
package com.codenvy.ide.ext.web.html.editor;

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.core.editor.CodenvyTextEditorFactory;
import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * EditorProvider for HTML css type
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class HtmlEditorProvider implements EditorProvider {
    private final DocumentProvider            documentProvider;
    private final CodenvyTextEditorFactory    editorFactory;
    private final NotificationManager         notificationManager;

    /**
     * HTML editor configuration.
     */
    private Provider<HtmlEditorConfiguration> htmlEditorConfigurationProvider;

    /** @param documentProvider */
    @Inject
    public HtmlEditorProvider(DocumentProvider documentProvider,
                              CodenvyTextEditorFactory editorFactory,
                              NotificationManager notificationManager,
                              Provider<HtmlEditorConfiguration> htmlEditorConfigurationProvider) {
        super();
        this.documentProvider = documentProvider;
        this.editorFactory = editorFactory;
        this.notificationManager = notificationManager;
        this.htmlEditorConfigurationProvider = htmlEditorConfigurationProvider;
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {
        CodenvyTextEditor textEditor = editorFactory.get();
        textEditor.initialize(htmlEditorConfigurationProvider.get(), documentProvider, notificationManager);
        return textEditor;
    }
}
