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

import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
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
    private final NotificationManager    notificationManager;
    private       Project                rootProject;
    final private String                 SEARCH_URL;

    @Inject
    public NavigateToFilePresenter(NavigateToFileView view,
                                   ResourceProvider resourceProvider,
                                   EventBus eventBus,
                                   MessageBus wsMessageBus,
                                   @Named("workspaceId") String workspaceId,
                                   DtoUnmarshallerFactory dtoUnmarshallerFactory,
                                   NotificationManager notificationManager) {
        this.resourceProvider = resourceProvider;
        this.view = view;
        this.eventBus = eventBus;
        this.wsMessageBus = wsMessageBus;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
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
        while (!parentFolder.getParent().getName().equals("")) {
            parentFolder = parentFolder.getParent();
        }
        return (Project)parentFolder;
    }

    /** {@inheritDoc} */
    @Override
    public void onFileSelected() {
        view.close();

        final String path = view.getItemPath();
        rootProject.findResourceByPath(path, new AsyncCallback<Resource>() {
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

    /**
     * Open file from current opened project.
     *
     * @param path
     *         relative path to file. If user need to open file located in
     *         <code>/project/path/to/some/file.ext</code> path parameter should be <code>path/to/some/file.ext</code>.
     */
    public void openFile(final String path) {
        rootProject = getRootProject(resourceProvider.getActiveProject());
        if (rootProject != null) {
            rootProject.findResourceByPath(rootProject.getPath() + (!path.startsWith("/") ? "/".concat(path) : path),
                                           new AsyncCallback<Resource>() {
                                               @Override
                                               public void onSuccess(Resource resource) {
                                                   if (resource.isFile()) {
                                                       eventBus.fireEvent(new FileEvent((File)resource, FileOperation.OPEN));
                                                   } else {
                                                       notificationManager
                                                               .showNotification(
                                                                       new Notification("Unable to open " + path + ". It's not a file.",
                                                                                        Notification.Type.WARNING)
                                                                                );
                                                   }
                                               }

                                               @Override
                                               public void onFailure(Throwable caught) {
                                                   notificationManager.showNotification(
                                                           new Notification("Unable to open " + path, Notification.Type.WARNING));
                                               }
                                           }
                                          );
        }
    }

}
