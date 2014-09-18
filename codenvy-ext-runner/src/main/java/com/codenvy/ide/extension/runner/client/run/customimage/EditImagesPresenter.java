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
package com.codenvy.ide.extension.runner.client.run.customimage;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Drives of editing custom images.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditImagesPresenter implements EditImagesView.ActionDelegate {
    private ProjectServiceClient   projectServiceClient;
    private DtoUnmarshallerFactory dtoUnmarshallerFactory;
    private EventBus               eventBus;
    private AppContext             appContext;
    private ImageActionManager     imageActionManager;
    private EditImagesView         view;
    private ItemReference          selectedImage;

    /** Create presenter. */
    @Inject
    protected EditImagesPresenter(EditImagesView view, EventBus eventBus, AppContext appContext, ImageActionManager imageActionManager,
                                  ProjectServiceClient projectServiceClient, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.imageActionManager = imageActionManager;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);

        updateComponents();
    }

    private void updateComponents() {
        view.setEditButtonEnabled(selectedImage != null);
    }

    /** {@inheritDoc} */
    @Override
    public void onEditClicked() {
        eventBus.fireEvent(new FileEvent(new FileNode(null, selectedImage, eventBus, projectServiceClient, dtoUnmarshallerFactory),
                                         FileEvent.FileOperation.OPEN));
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onImageSelected(ItemReference projectName) {
        this.selectedImage = projectName;

        updateComponents();
    }

    /** Show dialog. */
    public void showDialog() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject == null) {
            return;
        }

        imageActionManager.retrieveCustomScripts(currentProject.getProjectDescription(), new AsyncCallback<Array<ItemReference>>() {
            @Override
            public void onSuccess(Array<ItemReference> result) {
                view.setImages(result);
                view.showDialog();
            }

            @Override
            public void onFailure(Throwable ignore) {
                // no scripts are found
            }
        });
    }
}
