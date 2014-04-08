/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.core.editor;

import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;


/**
 * Document provider implementation on Resource API
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 */
public class ResourceDocumentProvider implements DocumentProvider {
    private DocumentFactory documentFactory;
    private EventBus        eventBus;

    @Inject
    public ResourceDocumentProvider(DocumentFactory documentFactory, EventBus eventBus) {
        this.documentFactory = documentFactory;
        this.eventBus = eventBus;
    }

    /**
     * {@inheritDoc}
     * This implementation return null
     */
    @Nullable
    @Override
    public AnnotationModel getAnnotationModel(@Nullable EditorInput input) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public void getDocument(@NotNull EditorInput input, @NotNull final DocumentCallback callback) {
        final File file = input.getFile();
        file.getProject().getContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                contentReceived(result, callback);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResourceDocumentProvider.class, caught);
            }
        });
    }

    /**
     * @param file
     * @param callback
     */
    private void contentReceived(@NotNull File file, @NotNull DocumentCallback callback) {
        Document document = documentFactory.get(file.getContent());
        callback.onDocument(document);
    }

    /** {@inheritDoc} */
    @Override
    public void saveDocument(@Nullable final EditorInput input, @NotNull Document document, boolean overwrite,
                             @NotNull final AsyncCallback<EditorInput> callback) {
        final File file = input.getFile();
        file.setContent(document.get());
        file.getProject().updateContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.SAVE));
                callback.onSuccess(input);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void saveDocumentAs(@NotNull EditorInput input, @NotNull Document document, boolean overwrite) {
        final File file = input.getFile();
        file.getProject().createFile(file.getParent(), file.getName(), file.getContent(), file.getMimeType(), new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.SAVE));
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResourceDocumentProvider.class, caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void documentClosed(@NotNull Document document) {

    }
}
