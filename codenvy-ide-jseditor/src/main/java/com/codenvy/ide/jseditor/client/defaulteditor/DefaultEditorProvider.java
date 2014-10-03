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
package com.codenvy.ide.jseditor.client.defaulteditor;

import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.editor.EditorProvider;
import com.codenvy.ide.jseditor.client.JsEditorExtension;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.jseditor.client.prefmodel.DefaultEditorTypePrefReader;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;

import javax.inject.Named;


public class DefaultEditorProvider implements EditorProvider {

    private final DefaultEditorTypePrefReader  defaultEditorTypePrefReader;
    private final EditorTypeRegistry editorTypeRegistry;
    private final EditorType         defaultEditorType;

    @Inject
    public DefaultEditorProvider(final DefaultEditorTypePrefReader defaultEditorTypePrefReader,
                                 final EditorTypeRegistry editorTypeRegistry,
                                 final @Named(JsEditorExtension.DEFAULT_EDITOR_TYPE_INSTANCE) EditorType defaultEditorType) {
        this.defaultEditorTypePrefReader = defaultEditorTypePrefReader;
        this.editorTypeRegistry = editorTypeRegistry;
        this.defaultEditorType = defaultEditorType;
    }

    @Override
    public String getId() {
        return "codenvyDefaultEditor";
    }

    @Override
    public String getDescription() {
        return "Codenvy Default Editor";
    }

    @Override
    public EditorPartPresenter getEditor() {
        final EditorType editorType = this.defaultEditorTypePrefReader.readPref();
        Log.debug(DefaultEditorProvider.class, "Editor type used: " + editorType);
        EditorBuilder provider = this.editorTypeRegistry.getRegisteredBuilder(editorType);
        if (provider == null) {
            Log.debug(DefaultEditorProvider.class, "No builder registered for editor type " + editorType
                                                   + " - attempt to fallback to " + defaultEditorType);
            provider = this.editorTypeRegistry.getRegisteredBuilder(defaultEditorType);
            if (provider == null) {
                Log.debug(DefaultEditorProvider.class, "No builder registered for default editor type - giving up.");
                return null;
            }
        }
        return provider.buildEditor();
    }
}
