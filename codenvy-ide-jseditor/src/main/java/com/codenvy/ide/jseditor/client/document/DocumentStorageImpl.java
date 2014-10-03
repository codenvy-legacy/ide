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
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Implementation of {@link DocumentStorage}.
 */
public class DocumentStorageImpl implements DocumentStorage {

    private final EventBus eventBus;

    @Inject
    public DocumentStorageImpl(final EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void getDocument(final FileNode file, final EmbeddedDocumentCallback callback) {
        file.getContent(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.debug(DocumentStorageImpl.class, "Document retrieved (" + file.getPath() + ").");
                try {
                    callback.onDocumentReceived(result);
                } catch (final Exception e) {
                    Log.warn(DocumentStorageImpl.class, "Exception during doc retrieve success callback: ", e);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(DocumentStorageImpl.class, "Could not retrieve document (" + file.getPath() + ").", caught);
            }
        });
    }

    @Override
    public void saveDocument(final EditorInput editorInput, final EmbeddedDocument document,
                             final boolean overwrite, final AsyncCallback<EditorInput> callback) {
        final FileNode file = editorInput.getFile();
        file.updateContent(document.getContents(), new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                Log.debug(DocumentStorageImpl.class, "Document saved (" + file.getPath() + ").");
                DocumentStorageImpl.this.eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.SAVE));
                try {
                    callback.onSuccess(editorInput);
                } catch (final Exception e) {
                    Log.warn(DocumentStorageImpl.class, "Exception during save success callback: ", e);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(DocumentStorageImpl.class, "Document save failed (" + file.getPath() + ").", caught);
                try {
                    callback.onFailure(caught);
                } catch (final Exception e) {
                    Log.warn(DocumentStorageImpl.class, "Exception during save failure callback: ", e);
                }
            }
        });
    }

    @Override
    public void documentClosed(final EmbeddedDocument document) {
    }

}
