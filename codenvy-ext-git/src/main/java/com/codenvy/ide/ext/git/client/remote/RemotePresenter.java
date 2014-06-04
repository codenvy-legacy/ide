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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.remote.add.AddRemoteRepositoryPresenter;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for working with remote repository list (view, add and delete).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class RemotePresenter implements RemoteView.ActionDelegate {
    private final DtoUnmarshallerFactory       dtoUnmarshallerFactory;
    private       RemoteView                   view;
    private       GitServiceClient             service;
    private       ResourceProvider             resourceProvider;
    private       GitLocalizationConstant      constant;
    private       AddRemoteRepositoryPresenter addRemoteRepositoryPresenter;
    private       NotificationManager          notificationManager;
    private       Remote                       selectedRemote;
    private       String                       projectId;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param addRemoteRepositoryPresenter
     * @param notificationManager
     */
    @Inject
    public RemotePresenter(RemoteView view, GitServiceClient service, ResourceProvider resourceProvider, GitLocalizationConstant constant,
                           AddRemoteRepositoryPresenter addRemoteRepositoryPresenter, NotificationManager notificationManager,
                           DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.addRemoteRepositoryPresenter = addRemoteRepositoryPresenter;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        projectId = resourceProvider.getActiveProject().getId();
        getRemotes();
    }

    /**
     * Get the list of remote repositories for local one. If remote repositories are found,
     * then get the list of branches (remote and local).
     */
    private void getRemotes() {
        service.remoteList(projectId, null, true,
                           new AsyncRequestCallback<Array<Remote>>(dtoUnmarshallerFactory.newArrayUnmarshaller(Remote.class)) {
                               @Override
                               protected void onSuccess(Array<Remote> result) {
                                   view.setEnableDeleteButton(false);
                                   view.setRemotes(result);
                                   if (!view.isShown()) {
                                       view.showDialog();
                                   }
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   String errorMessage =
                                           exception.getMessage() != null ? exception.getMessage() : constant.remoteListFailed();
                                   Window.alert(errorMessage);
                               }
                           }
                          );
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onAddClicked() {
        addRemoteRepositoryPresenter.showDialog(new AsyncCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                getRemotes();
            }

            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = caught.getMessage() != null ? caught.getMessage() : constant.remoteAddFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        if (selectedRemote == null) {
            Window.alert(constant.selectRemoteRepositoryFail());
            return;
        }

        final String name = selectedRemote.getName();
        service.remoteDelete(projectId, name, new AsyncRequestCallback<String>() {
            @Override
            protected void onSuccess(String result) {
                getRemotes();
            }

            @Override
            protected void onFailure(Throwable exception) {
                String errorMessage = exception.getMessage() != null ? exception.getMessage() : constant.remoteDeleteFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoteSelected(@NotNull Remote remote) {
        selectedRemote = remote;
        view.setEnableDeleteButton(true);
    }
}