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
import com.codenvy.ide.api.resources.model.File;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.ImplementedBy;

@ImplementedBy(DocumentStorageImpl.class)
public interface DocumentStorage {
    void getDocument(@NotNull File file,
                     @NotNull final EmbeddedDocumentCallback callback);

    void saveDocument(@Nullable final EditorInput editorInput,
                      @NotNull EmbeddedDocument document,
                      boolean overwrite,
                      @NotNull final AsyncCallback<EditorInput> callback);

    public interface EmbeddedDocumentCallback {
        void onDocumentReceived(String content);
    }

    public void documentClosed(@NotNull EmbeddedDocument document);
}
