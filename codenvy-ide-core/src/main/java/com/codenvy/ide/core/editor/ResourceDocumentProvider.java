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
package com.codenvy.ide.core.editor;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.text.Document;
import com.codenvy.ide.api.text.DocumentFactory;
import com.codenvy.ide.api.text.annotation.AnnotationModel;
import com.codenvy.ide.rest.AsyncRequestCallback;
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
 */
public class ResourceDocumentProvider implements DocumentProvider {
    private DocumentFactory      documentFactory;
    private EventBus             eventBus;
    private ProjectServiceClient projectServiceClient;

    @Inject
    public ResourceDocumentProvider(DocumentFactory documentFactory, EventBus eventBus, ProjectServiceClient projectServiceClient) {
        this.documentFactory = documentFactory;
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
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
        input.getFile().getContent(new AsyncCallback<String>() {
            @Override
            public void onSuccess(String result) {
                contentReceived(result, callback);
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(ResourceDocumentProvider.class, caught);
            }
        });
    }

    private void contentReceived(@NotNull String content, @NotNull DocumentCallback callback) {
        Document document = documentFactory.get(content);
        callback.onDocument(document);
    }

    /** {@inheritDoc} */
    @Override
    public void saveDocument(@Nullable final EditorInput input, @NotNull Document document, boolean overwrite,
                             @NotNull final AsyncCallback<EditorInput> callback) {
        final FileNode file = input.getFile();
        file.updateContent(document.get(), new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
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
        final FileNode file = input.getFile();
        final String path = file.getPath();
        final String parentPath = path.substring(0, path.length() - file.getName().length());
        projectServiceClient.createFile(parentPath, file.getName(), document.get(), file.getData().getMediaType(), new AsyncRequestCallback<ItemReference>() {
            @Override
            public void onSuccess(ItemReference result) {
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
