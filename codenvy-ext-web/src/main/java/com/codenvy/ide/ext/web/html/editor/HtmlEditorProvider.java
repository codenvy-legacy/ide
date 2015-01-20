/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.ext.web.html.editor;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.jseditor.client.defaulteditor.DefaultEditorProvider;
import com.codenvy.ide.jseditor.client.texteditor.ConfigurableTextEditor;
import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * {@link EditorProvider} for HTML files.
 */
public class HtmlEditorProvider implements EditorProvider {

    private final DefaultEditorProvider editorProvider;
    private final NotificationManager notificationManager;

    /**
     * HTML editor configuration.
     */
    private Provider<HtmlEditorConfiguration> htmlEditorConfigurationProvider;

    /** @param documentProvider */
    @Inject
    public HtmlEditorProvider(final DefaultEditorProvider editorProvider,
                              final NotificationManager notificationManager,
                              final Provider<HtmlEditorConfiguration> htmlEditorConfigurationProvider) {
        this.editorProvider = editorProvider;
        this.notificationManager = notificationManager;
        this.htmlEditorConfigurationProvider = htmlEditorConfigurationProvider;
    }

    @Override
    public String getId() {
        return "codenvyHTMLEditor";
    }

    @Override
    public String getDescription() {
        return "Codenvy HTML Editor";
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {
        ConfigurableTextEditor textEditor = editorProvider.getEditor();
        HtmlEditorConfiguration configuration = this.htmlEditorConfigurationProvider.get();
        textEditor.initialize(configuration, notificationManager);
        return textEditor;
    }
}
