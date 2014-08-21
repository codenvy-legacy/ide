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
package com.codenvy.ide.navigation;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.Message;
import com.codenvy.ide.websocket.MessageBuilder;
import com.codenvy.ide.websocket.MessageBus;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.codenvy.ide.websocket.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.MimeType.APPLICATION_JSON;
import static com.codenvy.ide.rest.HTTPHeader.ACCEPT;
import static com.google.gwt.http.client.RequestBuilder.GET;

/**
 * Presenter for file navigation (find file by name and open it).
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NavigateToFilePresenter implements NavigateToFileView.ActionDelegate {

    private final String                   SEARCH_URL;
    private       MessageBus               wsMessageBus;
    private       DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private       ProjectServiceClient     projectServiceClient;
    private       NavigateToFileView       view;
    private       AppContext               appContext;
    private       EventBus                 eventBus;
    private       StringMap<ItemReference> resultMap;

    @Inject
    public NavigateToFilePresenter(NavigateToFileView view,
                                   AppContext appContext,
                                   EventBus eventBus,
                                   MessageBus wsMessageBus,
                                   @Named("workspaceId") String workspaceId,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                   ProjectServiceClient projectServiceClient) {
        this.view = view;
        this.appContext = appContext;
        this.eventBus = eventBus;
        this.wsMessageBus = wsMessageBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.projectServiceClient = projectServiceClient;

        resultMap = Collections.createStringMap();

        SEARCH_URL = "/project/" + workspaceId + "/search";
        view.setDelegate(this);
    }

    /** Show dialog with view for navigation. */
    public void showDialog() {
        view.showDialog();
        view.clearInput();
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestSuggestions(String query, final AsyncCallback<Array<ItemReference>> callback) {
        resultMap = Collections.createStringMap();

        // add '*' to allow search files by first letters
        search(query + "*", new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                for (ItemReference item : result.asIterable()) {
                    // skip hidden items
                    if (!item.getPath().contains("/.")) {
                        resultMap.put(item.getPath(), item);
                    }
                }
                callback.onSuccess(resultMap.getValues());
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onFileSelected() {
        view.close();
        ItemReference selectedItem = resultMap.get(view.getItemPath());
        FileNode file = new FileNode(null, selectedItem, eventBus, projectServiceClient);
        eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.OPEN));
    }

    private void search(String fileName, final AsyncCallback<Array<ItemReference>> callback) {
        final String projectPath = appContext.getCurrentProject().getProjectDescription().getPath();
        final String url = SEARCH_URL + projectPath + "?name=" + fileName;
        Message message = new MessageBuilder(GET, url).header(ACCEPT, APPLICATION_JSON).build();
        Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newWSArrayUnmarshaller(ItemReference.class);
        try {
            wsMessageBus.send(message, new RequestCallback<Array<ItemReference>>(unmarshaller) {
                @Override
                protected void onSuccess(Array<ItemReference> result) {
                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                }
            });
        } catch (WebSocketException e) {
            callback.onFailure(e);
        }
    }
}
