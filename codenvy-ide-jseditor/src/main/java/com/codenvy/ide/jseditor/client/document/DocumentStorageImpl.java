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

import javax.inject.Inject;

import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

public class DocumentStorageImpl implements DocumentStorage {

    private final EventBus eventBus;

    @Inject
    public DocumentStorageImpl(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void getDocument(final File file, final EmbeddedDocumentCallback callback) {
        file.getProject().getContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                Log.debug(DocumentStorageImpl.class, "Document retrieved (" + file.getRelativePath() + ").");
                callback.onDocumentReceived(result.getContent());
            }

            @Override
            public void onFailure(final Throwable caught) {
                Log.error(DocumentStorageImpl.class, "Could not retrieve document (" + file.getRelativePath() + ").", caught);
            }
        });
    }

    @Override
    public void saveDocument(final EditorInput editorInput, final EmbeddedDocument document,
                             final boolean overwrite, final AsyncCallback<EditorInput> callback) {
        final File file = editorInput.getFile();
        file.setContent(document.getContents());
        file.getProject().updateContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(final File result) {
                Log.debug(DocumentStorageImpl.class, "Document saved (" + file.getRelativePath() + ").");
                DocumentStorageImpl.this.eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.SAVE));
                callback.onSuccess(editorInput);
            }

            @Override
            public void onFailure(final Throwable caught) {
                Log.error(DocumentStorageImpl.class, "Document save failed (" + file.getRelativePath() + ").", caught);
                callback.onFailure(caught);
            }
        });
    }

    @Override
    public void documentClosed(final EmbeddedDocument document) {
    }

}
