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
package com.codenvy.ide.extension.runner.client.run.customimages;

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.event.FileEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.inject.name.Named;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Drives the process of editing custom images.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class EditImagesPresenter implements EditImagesView.ActionDelegate {
    private final String                     recipesFolderPath;
    private       NotificationManager        notificationManager;
    private       RunnerLocalizationConstant constants;
    private       ProjectServiceClient       projectServiceClient;
    private       DtoUnmarshallerFactory     dtoUnmarshallerFactory;
    private       EventBus                   eventBus;
    private       AppContext                 appContext;
    private       ImageActionManager         imageActionManager;
    private       EditImagesView             view;
    private       ItemReference              selectedImage;

    /** Create presenter. */
    @Inject
    protected EditImagesPresenter(@Named("recipesFolderPath") String recipesFolderPath, EditImagesView view, EventBus eventBus,
                                  AppContext appContext, ImageActionManager imageActionManager, ProjectServiceClient projectServiceClient,
                                  DtoUnmarshallerFactory dtoUnmarshallerFactory, NotificationManager notificationManager,
                                  RunnerLocalizationConstant constants) {
        this.recipesFolderPath = recipesFolderPath;
        this.view = view;
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.imageActionManager = imageActionManager;
        this.projectServiceClient = projectServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.notificationManager = notificationManager;
        this.constants = constants;
        this.view.setDelegate(this);

        updateView();
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        new AskValueDialog(constants.editImagesViewAddNewScriptTitle(),
                           constants.editImagesViewAddNewScriptMessage(),
                           new AskValueCallback() {
                               @Override
                               public void onOk(final String value) {
                                   final String name = value.endsWith(".dc5y") ? value : value + ".dc5y";
                                   createScript(name);
                               }
                           }).show();
    }

    private void createScript(final String name) {
        final Unmarshallable<ItemReference> unmarshaller = dtoUnmarshallerFactory.newUnmarshaller(ItemReference.class);
        projectServiceClient.createFile(
                appContext.getCurrentProject().getProjectDescription().getPath() + '/' + recipesFolderPath, name, "", null,
                new AsyncRequestCallback<ItemReference>(unmarshaller) {
                    @Override
                    protected void onSuccess(ItemReference result) {
                        imageActionManager.addActionForScript(result);
                        refreshImagesList();
                        updateView();
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
                    }
                });
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoveClicked() {
        new Ask(constants.editImagesViewRemoveScriptTitle(),
                constants.editImagesViewRemoveScriptMessage(selectedImage.getName()),
                new AskHandler() {
                    @Override
                    public void onOk() {
                        removeSelectedScript();
                    }
                }).show();
    }

    private void removeSelectedScript() {
        projectServiceClient.delete(selectedImage.getPath(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                imageActionManager.removeActionForScript(selectedImage);
                selectedImage = null;
                updateView();
                refreshImagesList();
            }

            @Override
            protected void onFailure(Throwable exception) {
                notificationManager.showNotification(new Notification(exception.getMessage(), ERROR));
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onEditClicked() {
        eventBus.fireEvent(new FileEvent(new FileNode(null, selectedImage, eventBus, projectServiceClient, dtoUnmarshallerFactory),
                                         FileEvent.FileOperation.OPEN));
        view.closeDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.closeDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onImageSelected(ItemReference image) {
        selectedImage = image;
        updateView();
    }

    /** Show dialog. */
    public void showDialog() {
        selectedImage = null;
        updateView();
        view.showDialog();
        refreshImagesList();
    }

    private void updateView() {
        view.setEditButtonEnabled(selectedImage != null);
        view.setRemoveButtonEnabled(selectedImage != null);
    }

    private void refreshImagesList() {
        view.setImages(Collections.<ItemReference>createArray());

        imageActionManager.retrieveCustomImages(
                appContext.getCurrentProject().getProjectDescription(),
                new AsyncCallback<Array<ItemReference>>() {
                    @Override
                    public void onSuccess(Array<ItemReference> result) {
                        view.setImages(result);
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        if (caught.getMessage().contains("doesn't exist")) {
                            createRecipesFolder();
                        } else {
                            Notification notification = new Notification(constants.retrievingImagesFailed(caught.getMessage()), ERROR);
                            notificationManager.showNotification(notification);
                        }
                    }
                });
    }

    private void createRecipesFolder() {
        projectServiceClient.createFolder(
                appContext.getCurrentProject().getProjectDescription().getPath() + '/' + recipesFolderPath,
                new AsyncRequestCallback<ItemReference>() {
                    @Override
                    protected void onSuccess(ItemReference result) {
                    }

                    @Override
                    protected void onFailure(Throwable exception) {
                        Log.error(EditImagesPresenter.class, exception.getMessage());
                    }
                });
    }
}
