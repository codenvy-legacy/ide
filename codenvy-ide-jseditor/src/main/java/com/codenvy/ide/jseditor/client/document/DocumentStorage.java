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
package com.codenvy.ide.jseditor.client.document;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Interface for file retrieval and storage operations.
 */

@ImplementedBy(DocumentStorageImpl.class)
public interface DocumentStorage {

    /**
     * Retrieves the file content.
     * @param file the file
     * @param callback operation to do when the content is ready
     */
    void getDocument(@Nonnull FileNode file,
                     @Nonnull final EmbeddedDocumentCallback callback);

    /**
     * Saves the file content.
     * @param editorInput the editor input
     * @param document the document
     * @param overwrite
     * @param callback operation to do when the content is ready
     */
    void saveDocument(@Nullable final EditorInput editorInput,
                      @Nonnull EmbeddedDocument document,
                      boolean overwrite,
                      @Nonnull final AsyncCallback<EditorInput> callback);

    /**
     * Action taken when the document is closed.
     * @param document the document
     */
    public void documentClosed(@Nonnull EmbeddedDocument document);

    /**
     * Action taken when retrieve action is successful.
     */
    public interface EmbeddedDocumentCallback {
        /**
         * Action taken when retrieve action is successful.
         * @param content the content that was received
         */
        void onDocumentReceived(String content);
    }
}
