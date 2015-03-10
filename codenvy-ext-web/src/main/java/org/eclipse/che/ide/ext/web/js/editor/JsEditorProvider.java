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
package org.eclipse.che.ide.ext.web.js.editor;

import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.editor.EditorProvider;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.jseditor.client.defaulteditor.DefaultEditorProvider;
import org.eclipse.che.ide.jseditor.client.texteditor.ConfigurableTextEditor;
import com.google.inject.Inject;
import com.google.inject.Provider;


/**
 * EditorProvider for JavaScript css type
 * 
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class JsEditorProvider implements EditorProvider {
    private final DefaultEditorProvider editorProvider;
    private final NotificationManager notificationManager;

    /**
     * JS editor configuration.
     */
    private Provider<JsEditorConfiguration> jsEditorConfigurationProvider;

    /** @param documentProvider */
    @Inject
    public JsEditorProvider(final DefaultEditorProvider editorProvider,
                            NotificationManager notificationManager,
                            Provider<JsEditorConfiguration> jsEditorConfigurationProvider) {
        this.editorProvider = editorProvider;
        this.notificationManager = notificationManager;
        this.jsEditorConfigurationProvider = jsEditorConfigurationProvider;
    }

    @Override
    public String getId() {
        return "codenvyJavaScriptEditor";
    }

    @Override
    public String getDescription() {
        return "Codenvy JavaScript Editor";
    }

    /** {@inheritDoc} */
    @Override
    public EditorPartPresenter getEditor() {
        ConfigurableTextEditor textEditor = editorProvider.getEditor();
        textEditor.initialize(jsEditorConfigurationProvider.get(), notificationManager);
        return textEditor;
    }
}
