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
package com.codenvy.ide.rename;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Folder;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.rest.AsyncRequestCallback;
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

    private RenameResourceView   view;
    private ProjectServiceClient projectServiceClient;
    private EditorAgent          editorAgent;
    private Resource             resource;
    private NotificationManager  notificationManager;
    private AppContext           appContext;

    @Inject
    public RenameResourcePresenter(RenameResourceView view,
                                   EditorAgent editorAgent,
                                   NotificationManager notificationManager,
                                   ProjectServiceClient projectServiceClient,
                                   AppContext appContext) {
        this.view = view;
        this.projectServiceClient = projectServiceClient;
        this.editorAgent = editorAgent;
        this.notificationManager = notificationManager;
        this.appContext = appContext;

        view.setDelegate(this);
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
        ProjectDescriptor activeProject = appContext.getCurrentProject();

        // rename project in project list (when no active project)
        if (activeProject == null) {
            projectServiceClient.rename(resource.getPath(), newName, resource.getMimeType(), new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
//                    resourceProvider.refreshRoot();
                }

                @Override
                protected void onFailure(Throwable throwable) {
                    notificationManager.showNotification(new Notification(throwable.getMessage(), ERROR));
                }
            });
        } else {
            // rename opened project or its child resource
//            activeProject.rename(resource, newName, new AsyncCallback<Resource>() {
//                @Override
//                public void onSuccess(Resource result) {
//                    if (result instanceof File) {
//                        // change renamed file for all opened editors
//                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
//                            if (editor.getEditorInput().getFile().getPath().equals(resource.getPath())) {
//                                editor.getEditorInput().setFile((File)result);
//                                editor.onFileChanged();
//                                break;
//                            }
//                        }
//                    } else if (result instanceof Folder) {
//                        // Check whether opened file's parent was renamed, then change file in editor,
//                        // because rename changes the path of the file too.
//                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
//                            // find parent of the opened file by its old id
//                            Folder parent = getParentByPath(resource.getPath(), editor.getEditorInput().getFile());
//                            if (parent != null) {
//                                // new path of the file
//                                String path = editor.getEditorInput().getFile().getPath().replaceFirst(parent.getPath(), result.getPath());
//                                Resource updatedResource = findChildByPath((Folder)result, path);
//                                if (updatedResource != null && (updatedResource instanceof File)) {
//                                    editor.getEditorInput().setFile((File)updatedResource);
//                                }
//                            }
//                        }
//                    }
//                }
//
//                @Override
//                public void onFailure(Throwable caught) {
//                    notificationManager.showNotification(new Notification(caught.getMessage(), ERROR));
//                }
//            });
        }

        view.close();
    }

    /**
     * Find parent of the resource by path going to upper levels.
     *
     * @param oldPath
     *         parent's path before rename
     * @param resource
     *         resource for which to find parent
     * @return {@link Folder} found parent or <code>null</code>
     */
    private Folder getParentByPath(String oldPath, Resource resource) {
        Folder parent = resource.getParent();
        while (parent != null) {
            if (parent.getPath().equals(oldPath)) {
                return parent;
            }
            parent = parent.getParent();
        }
        return null;
    }

    /**
     * Find child resource by its path.
     *
     * @param parent
     *         parent to start search
     * @param path
     *         path of the resource
     * @return {@link Resource} found resource or <code>null</code>
     */
    private Resource findChildByPath(Folder parent, String path) {
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
