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
package com.codenvy.ide.rename;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for changing resource's name.
 *
 * @author Ann Shumilova
 */
@Singleton
public class RenameResourcePresenter implements RenameResourceView.ActionDelegate {

    private       RenameResourceView   view;
    private final ProjectServiceClient projectServiceClient;
    private       EditorAgent          editorAgent;
    private       ResourceProvider     resourceProvider;
    private       Resource             resource;
    private       NotificationManager  notificationManager;

    @Inject
    public RenameResourcePresenter(RenameResourceView view,
                                   EditorAgent editorAgent,
                                   ResourceProvider resourceProvider,
                                   NotificationManager notificationManager,
                                   ProjectServiceClient projectServiceClient) {
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        view.setDelegate(this);
        this.editorAgent = editorAgent;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
    }

    /**
     * Perform the resource renaming.
     *
     * @param resource
     *         resource to rename
     */
    public void renameResource(@NotNull Resource resource) {
        this.resource = resource;
        view.setName(resource.getName());
        view.setEnableRenameButton(false);
        view.showDialog();
        String namePart = (resource instanceof File) ? resource.getName().substring(0, resource.getName().lastIndexOf("."))
                                                     : resource.getName();
        view.selectText(namePart);
    }

    /** {@inheritDoc} */
    @Override
    public void onRenameClicked() {
        final String newName = view.getName();
        Project activeProject = resourceProvider.getActiveProject();

        // rename project in project list (when no active project)
        if (activeProject == null) {
            projectServiceClient.rename(resource.getPath(), newName, resource.getMimeType(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    resourceProvider.refreshRoot();
                }

                @Override
                protected void onFailure(Throwable throwable) {
                    notificationManager.showNotification(new Notification(throwable.getMessage(), ERROR));
                }
            });
        } else {
            // rename project's child resource
            activeProject.rename(resource, newName, new AsyncCallback<Resource>() {
                @Override
                public void onSuccess(Resource result) {
                    if (result instanceof File) {
                        //Change renamed file for all opened editors:
                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                            if (editor.getEditorInput().getFile().getId().equals(resource.getId())) {
                                editor.getEditorInput().setFile((File)result);
                                editor.onFileChanged();
                                break;
                            }
                        }
                    } else if (result instanceof Folder) {
                        //Check whether opened file's parent was renamed, then change file in editor, because
                        //rename changes the id of the file too.
                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                            //Find parent of the opened file by it's old id:
                            Folder parent = getParentById(resource.getId(), editor.getEditorInput().getFile());
                            if (parent != null) {
                                //New path of the file:
                                String path = editor.getEditorInput().getFile().getPath().replaceFirst(parent.getPath(), result.getPath());
                                Resource updatedResource = findResourceByPath((Folder)result, path);
                                if (updatedResource != null && (updatedResource instanceof File)) {
                                    editor.getEditorInput().setFile((File)updatedResource);
                                }
                            }
                        }
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    notificationManager.showNotification(new Notification(caught.getMessage(), ERROR));
                }
            });
        }

        view.close();
    }

    /**
     * Find parent of the resource by id going to upper levels.
     *
     * @param id
     *         parent's id
     * @param resource
     *         resource for which to find parent
     * @return {@link Folder} found parent or <code>null</code>
     */
    private Folder getParentById(String id, Resource resource) {
        Folder parent = resource.getParent();
        while (parent != null) {
            if (parent.getId().equals(id)) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Find resource by its path.
     *
     * @param parent
     *         parent to start search
     * @param path
     *         path of the resource
     * @return {@link Resource} found resource or <code>null</code>
     */
    private Resource findResourceByPath(Folder parent, String path) {
        String[] names = path.split("/");
        for (int i = 0; i < names.length; i++) {
            for (Resource child : parent.getChildren().asIterable()) {
                if ((i == (names.length - 1)) && child.getName().equals(names[i])) {
                    return child;
                } else if (child instanceof Folder && child.getName().equals(names[i])) {
                    parent = (Folder)child;
                    break;
                }
            }
        }
        return null;
    }


    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        boolean enable = (view.getName() != null && !view.getName().isEmpty() && !view.getName().equals(resource.getName()));
        view.setEnableRenameButton(enable);
    }

}
