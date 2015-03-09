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
package org.eclipse.che.ide.jseditor.client.document;

import org.eclipse.che.ide.api.editor.EditorInput;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;

import javax.inject.Inject;

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
    public void getDocument(final VirtualFile file, final EmbeddedDocumentCallback callback) {
        file.getContent(new AsyncCallback<String>() {
            @Override
            public void onSuccess(final String result) {
                Log.debug(DocumentStorageImpl.class, "Document retrieved (" + file.getPath() + ").");
                try {
                    callback.onDocumentReceived(result);
                } catch (final Exception e) {
                    Log.warn(DocumentStorageImpl.class, "Exception during doc retrieve success callback: ", e);
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                try {
                    callback.onDocumentLoadFailure(caught);
                } catch (final Exception e) {
                    Log.warn(DocumentStorageImpl.class, "Exception during doc retrieve failure callback: ", e);
                }
                Log.error(DocumentStorageImpl.class, "Could not retrieve document (" + file.getPath() + ").", caught);
            }
        });
    }

    @Override
    public void saveDocument(final EditorInput editorInput, final Document document,
                             final boolean overwrite, final AsyncCallback<EditorInput> callback) {
        final VirtualFile file = editorInput.getFile();
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
    public void documentClosed(final Document document) {
    }

}
