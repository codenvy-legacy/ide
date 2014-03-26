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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.gwt.client.QueryExpression;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.FileEvent.FileOperation;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.resources.model.File;
import com.codenvy.ide.resources.model.Folder;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter for file navigation (find file by name and open it).
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
@Singleton
public class NavigateToFilePresenter implements NavigateToFileView.ActionDelegate {

    private       NavigateToFileView       view;
    private       ResourceProvider         resourceProvider;
    private       EventBus                 eventBus;
    private final ProjectServiceClient     projectServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private       Project                  activeProject;
    private       StringMap<ItemReference> itemReferences;

    @Inject
    public NavigateToFilePresenter(NavigateToFileView view, ResourceProvider resourceProvider, EventBus eventBus,
                                   ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.resourceProvider = resourceProvider;
        this.view = view;
        this.eventBus = eventBus;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        view.setDelegate(this);
    }

    /** Show dialog with view for navigation. */
    public void showDialog() {
        itemReferences = Collections.createStringMap();
        activeProject = resourceProvider.getActiveProject();
        view.showDialog();
        view.clearInput();
        view.focusInput();
    }

    /** {@inheritDoc} */
    @Override
    public void onRequestSuggestions(String query, final AsyncCallback<Array<ItemReference>> callback) {
        search(query, new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                for (ItemReference item : result.asIterable()) {
                    itemReferences.put(item.getPath(), item);
                }
                callback.onSuccess(result);
            }

            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }
        });
    }

    // TODO make search via WebSocket
    private void search(String fileName, final AsyncCallback<Array<ItemReference>> callback) {
        QueryExpression expression = new QueryExpression().setPath(activeProject.getPath()).setName(fileName);
        final Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
        projectServiceClient.search(expression, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
            @Override
            protected void onSuccess(Array<ItemReference> itemReferenceArray) {
                callback.onSuccess(itemReferenceArray);
            }

            @Override
            protected void onFailure(Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onFileSelected() {
        final ItemReference itemToOpen = itemReferences.get(view.getItemPath());

        final String relativePath = itemToOpen.getPath().substring(activeProject.getPath().length() + 1,
                                                                   itemToOpen.getPath().length() - itemToOpen.getName().length());

        refreshPath(activeProject, relativePath, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                File fileToOpen = (File)result.findChildByName(itemToOpen.getName());
                view.close();
                eventBus.fireEvent(new FileEvent(fileToOpen, FileOperation.OPEN));
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }

    private void refreshPath(Folder rootFolder, final String pathToRefresh, final AsyncCallback<Folder> callback) {
        final String childFolderName = pathToRefresh.split("/")[0];
        final Folder childFolder = (Folder)rootFolder.findChildByName(childFolderName);
        activeProject.refreshChildren(childFolder, new AsyncCallback<Folder>() {
            @Override
            public void onSuccess(Folder result) {
                final String path = pathToRefresh.substring(childFolder.getName().length() + 1);
                if (path.isEmpty()) {
                    callback.onSuccess(result);
                } else {
                    refreshPath(childFolder, path, callback);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
            }
        });
    }
}
