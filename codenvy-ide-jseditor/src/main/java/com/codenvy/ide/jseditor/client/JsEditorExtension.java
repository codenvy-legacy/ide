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
package com.codenvy.ide.jseditor.client;

import javax.inject.Named;

import com.codenvy.ide.api.editor.CodenvyTextEditor;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.extension.Extension;
import com.codenvy.ide.api.filetypes.FileType;
import com.codenvy.ide.api.filetypes.FileTypeRegistry;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.jseditor.client.defaulteditor.EditorBuilder;
import com.codenvy.ide.jseditor.client.editortype.EditorType;
import com.codenvy.ide.jseditor.client.editortype.EditorTypeRegistry;
import com.codenvy.ide.texteditor.api.TextEditorConfiguration;
import com.google.inject.Inject;
import com.google.inject.Provider;

@Extension(title = "Common editor components.", version = "3.0.0")
public class JsEditorExtension {

    /** The classic editor type key. */
    public static final String CLASSIC_EDITOR_KEY              = "classic";

    /** The default editor injection name. */
    public static final String CLASSIC_EDITOR_TYPE_INJECT_NAME = "ClassicEditorType";

    /** The default editor injection name. */
    public static final String DEFAULT_EDITOR_TYPE_INJECT_NAME = "DefaultEditorType";

    /** The default editor injection name. */
    public static final String DEFAULT_EDITOR_TYPE_INSTANCE             = "DefaultEditorType";

    /** The plain/tect filetype injection name. */
    public static final String PLAIN_TEXT_FILETYPE_INJECT_NAME = "plainTextFileType";

    @Inject
    public JsEditorExtension(final EditorTypeRegistry editorTypeRegistry,
                             final Provider<CodenvyTextEditor> editorProvider,
                             final DocumentProvider documentProvider,
                             final NotificationManager notificationManager,
                             final FileTypeRegistry fileTypeRegistry,
                             final @Named(PLAIN_TEXT_FILETYPE_INJECT_NAME) FileType plainText,
                             final JsEditorConstants constants) {

        // Register classic editor
        final EditorType classicEditorType = EditorType.fromKey(CLASSIC_EDITOR_KEY);
        final String classicEditorName = constants.classicEditorDisplayName();
        editorTypeRegistry.registerEditorType(classicEditorType, classicEditorName, new EditorBuilder() {

            @Override
            public EditorPartPresenter buildEditor() {
                final CodenvyTextEditor editor = editorProvider.get();
                editor.initialize(new TextEditorConfiguration(), documentProvider, notificationManager);
                return editor;
            }
        });

        // register text/plain file type
        fileTypeRegistry.registerFileType(plainText);
    }
}
