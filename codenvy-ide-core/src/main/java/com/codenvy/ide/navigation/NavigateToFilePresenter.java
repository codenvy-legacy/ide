/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.navigation;

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
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

    private       NavigateToFileView     view;
    private       ResourceProvider       resourceProvider;
    private       EventBus               eventBus;
    private final MessageBus             wsMessageBus;
    private final DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private       Project                rootProject;
    final private String                 SEARCH_URL;

    @Inject
    public NavigateToFilePresenter(NavigateToFileView view, ResourceProvider resourceProvider, EventBus eventBus, MessageBus wsMessageBus,
                                   @Named("workspaceId") String workspaceId, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.resourceProvider = resourceProvider;
        this.view = view;
        this.eventBus = eventBus;
        this.wsMessageBus = wsMessageBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        SEARCH_URL = "/project/" + workspaceId + "/search";
        view.setDelegate(this);
    }

    /** Show dialog with view for navigation. */
    public void showDialog() {
        // Get root-project path in order to allow to search for files
        // in the entire project, not just in the current sub-module.
        rootProject = getRootProject(resourceProvider.getActiveProject());
        view.showDialog();
        view.clearInput();
        view.focusInput();
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestSuggestions(String query, final AsyncCallback<Array<String>> callback) {
        // add '*' to allow search files by first letters
        search(query + "*", new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                Array<String> suggestions = Collections.createArray();
                for (ItemReference item : result.asIterable()) {
                    // skip hidden items
                    if (!item.getPath().contains("/.")) {
                        suggestions.add(item.getPath());
                    }
                }
                callback.onSuccess(suggestions);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    private void search(String fileName, final AsyncCallback<Array<ItemReference>> callback) {
        final String url = SEARCH_URL + rootProject.getPath() + "?name=" + fileName;
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

    /** Makes sense for multi-module projects. */
    private Project getRootProject(Project project) {
        Folder parentFolder = project;
        while (!resourceProvider.getRootId().equals(parentFolder.getParent().getId())) {
            parentFolder = parentFolder.getParent();
        }
        return (Project)parentFolder;
    }

    /** {@inheritDoc} */
    @Override
    public void onFileSelected() {
        view.close();

        final String path = view.getItemPath();
        refreshPath(rootProject, path, new AsyncCallback<Resource>() {
            @Override
            public void onSuccess(Resource result) {
                eventBus.fireEvent(new FileEvent((File)result, FileOperation.OPEN));
            }

            @Override
            public void onFailure(Throwable caught) {
                Log.error(NavigateToFilePresenter.class, "Unable to open a file " + path);
            }
        });
    }

    private void refreshPath(Folder rootFolder, final String pathToRefresh, final AsyncCallback<Resource> callback) {
        // Avoid redundant requests. Use cached project structure.
        if (!rootFolder.getChildren().isEmpty()) {
            for (Resource child : rootFolder.getChildren().asIterable()) {
                if (pathToRefresh.equals(child.getPath())) {
                    callback.onSuccess(child);
                    break;
                } else if (pathToRefresh.startsWith(child.getPath())) {
                    refreshPath((Folder)child, pathToRefresh, callback);
                    break;
                }
            }
        } else {
            rootProject.refreshChildren(rootFolder, new AsyncCallback<Folder>() {
                @Override
                public void onSuccess(Folder result) {
                    refreshPath(result, pathToRefresh, callback);
                }

                @Override
                public void onFailure(Throwable caught) {
                    callback.onFailure(caught);
                }
            });
        }
    }

}
