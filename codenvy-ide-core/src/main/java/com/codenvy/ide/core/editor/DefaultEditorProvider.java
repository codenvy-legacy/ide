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

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.google.inject.Inject;


/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class DefaultEditorProvider implements EditorProvider {
    private final DocumentProvider         documentProvider;
    private final CodenvyTextEditorFactory editorProvider;
    private final TextEditorConfiguration  configuration;
    private final NotificationManager      notificationManager;

    @Inject
    public DefaultEditorProvider(final DocumentProvider documentProvider,
                                 final CodenvyTextEditorFactory editorProvider,
                                 final NotificationManager notificationManager) {
        super();
        this.documentProvider = documentProvider;
        this.editorProvider = editorProvider;
        this.notificationManager = notificationManager;
        this.configuration = new TextEditorConfiguration();
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {
        CodenvyTextEditor editor = editorProvider.get();
        editor.initialize(configuration, documentProvider, notificationManager);
        return editor;
    }
}
