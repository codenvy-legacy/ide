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
package com.codenvy.ide.jseditor.client.texteditor;

import com.codenvy.ide.jseditor.client.document.DocumentStorage.EmbeddedDocumentCallback;
import com.codenvy.ide.jseditor.client.texteditor.EditorModule.EditorModuleReadyCallback;

/**
 * Composite callback that waits for both the editor module initialization and the document content.
 * @param <T> the type of the editor widget
 */
abstract class EditorInitCallback<T extends EditorWidget> implements EmbeddedDocumentCallback, EditorModuleReadyCallback {

    /** Flag that tells if the editor intiialization was finished. */
    private boolean editorModuleReady;
    /** The content of the document to open. */
    private String receivedContent;

    /**
     * Constructor.
     * @param moduleAlreadyReady if set to true, the callback will not wait for editor module initialization.
     */
    public EditorInitCallback(final boolean moduleAlreadyReady) {
        this.editorModuleReady = moduleAlreadyReady;
    }

    @Override
    public void onEditorModuleReady() {
        this.editorModuleReady = true;
        checkReadyAndContinue();
    }

    @Override
    public void onEditorModuleError() {
        onError();
    }

    @Override
    public void onDocumentReceived(final String content) {
        if (content != null) {
            this.receivedContent = content;
        } else {
            this.receivedContent = "";
        }
        checkReadyAndContinue();
    }

    private void checkReadyAndContinue() {
        if (this.receivedContent != null && this.editorModuleReady) {
            onReady(this.receivedContent);
        }
    }

    /**
     * Action when the editor is ready AND we have the document content.
     * @param content the content
     */
    public abstract void onReady(final String content);

    /**
     * Action when something failed.
     */
    public abstract void onError();

}
