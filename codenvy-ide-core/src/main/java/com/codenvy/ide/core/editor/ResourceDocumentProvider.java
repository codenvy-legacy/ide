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

import com.codenvy.api.core.rest.shared.dto.Link;
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.editor.DocumentProvider;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.text.Document;
import com.codenvy.ide.text.DocumentFactory;
import com.codenvy.ide.text.annotation.AnnotationModel;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
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
        for (Link link : input.getFile().getLinks()) {
            if ("get content".equals(link.getRel())) {
                try {
                    new RequestBuilder(RequestBuilder.GET, link.getHref()).sendRequest("", new RequestCallback() {
                        @Override
                        public void onResponseReceived(Request request, Response response) {
                            contentReceived(response.getText(), callback);
                        }

                        @Override
                        public void onError(Request request, Throwable exception) {
                            Log.error(ResourceDocumentProvider.class, exception);
                        }
                    });
                } catch (RequestException e) {
                    Log.error(ResourceDocumentProvider.class, e);
                }
                break;
            }
        }
    }

    private void contentReceived(@NotNull String content, @NotNull DocumentCallback callback) {
        Document document = documentFactory.get(content);
        callback.onDocument(document);
    }

    /** {@inheritDoc} */
    @Override
    public void saveDocument(@Nullable final EditorInput input, @NotNull Document document, boolean overwrite,
                             @NotNull final AsyncCallback<EditorInput> callback) {
        final ItemReference file = input.getFile();
        projectServiceClient.updateFile(file.getPath(), document.get(), file.getMediaType(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.SAVE));
                callback.onSuccess(input);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void saveDocumentAs(@NotNull EditorInput input, @NotNull Document document, boolean overwrite) {
        final ItemReference file = input.getFile();
        final String path = file.getPath();
        final String parentPath = path.substring(0, path.length() - file.getName().length());
        projectServiceClient.createFile(parentPath, file.getName(), document.get(), file.getMediaType(), new AsyncRequestCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
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
