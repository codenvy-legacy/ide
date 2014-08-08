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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;

public class DocumentStorageImpl implements DocumentStorage {

    private final EventBus             eventBus;
    private final ProjectServiceClient projectServiceClient;

    @Inject
    public DocumentStorageImpl(final EventBus eventBus,
                               ProjectServiceClient projectServiceClient) {
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
    }

    @Override
    public void getDocument(final ItemReference file, final EmbeddedDocumentCallback callback) {
        projectServiceClient.getFileContent(file.getPath(), new AsyncRequestCallback<String>(new StringUnmarshaller()) {
            @Override
            protected void onSuccess(String result) {
                Log.debug(DocumentStorageImpl.class, "Document retrieved (" + file.getPath() + ").");
                callback.onDocumentReceived(result);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(DocumentStorageImpl.class, "Could not retrieve document (" + file.getPath() + ").", throwable);
            }
        });
    }

    @Override
    public void saveDocument(final EditorInput editorInput, final EmbeddedDocument document,
                             final boolean overwrite, final AsyncCallback<EditorInput> callback) {
        final ItemReference file = editorInput.getFile();
        projectServiceClient.updateFile(file.getPath(), document.getContents(), null, new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Log.debug(DocumentStorageImpl.class, "Document saved (" + file.getPath() + ").");
                DocumentStorageImpl.this.eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.SAVE));
                callback.onSuccess(editorInput);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                Log.error(DocumentStorageImpl.class, "Document save failed (" + file.getPath() + ").", throwable);
                callback.onFailure(throwable);
            }
        });
    }

    @Override
    public void documentClosed(final EmbeddedDocument document) {
    }

}
