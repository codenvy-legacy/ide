/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package com.codenvy.ide.ext.git.client.remote;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.remote.add.AddRemoteRepositoryPresenter;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for working with remote repository list (view, add and delete).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 18, 2011 11:13:30 AM anya $
 */
@Singleton
public class RemotePresenter implements RemoteView.ActionDelegate {
    private RemoteView                   view;
    private GitClientService             service;
    private ResourceProvider             resourceProvider;
    private GitLocalizationConstant      constant;
    private AddRemoteRepositoryPresenter addRemoteRepositoryPresenter;
    private NotificationManager          notificationManager;
    private Remote                       selectedRemote;
    private String                       projectId;
    private DtoFactory                   dtoFactory;

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
    public RemotePresenter(RemoteView view, GitClientService service, ResourceProvider resourceProvider, GitLocalizationConstant constant,
                           AddRemoteRepositoryPresenter addRemoteRepositoryPresenter, NotificationManager notificationManager,
                           DtoFactory dtoFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.addRemoteRepositoryPresenter = addRemoteRepositoryPresenter;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    /** Show dialog. */
    public void showDialog() {
        projectId = resourceProvider.getActiveProject().getId();
        getRemotes();
    }

    /**
     * Get the list of remote repositories for local one. If remote repositories are found, then get the list of branches (remote and
     * local).
     */
    private void getRemotes() {
        try {
            service.remoteList(resourceProvider.getVfsId(), projectId, null, true,
                               new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                   @Override
                                   protected void onSuccess(String result) {
                                       Array<Remote> remotes = dtoFactory.createListDtoFromJson(result, Remote.class);
                                       view.setEnableDeleteButton(false);
                                       view.setRemotes(remotes);
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
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteListFailed();
            Window.alert(errorMessage);
        }
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

        String name = selectedRemote.getName();
        boolean needToDelete = Window.confirm(constant.deleteRemoteRepositoryQuestion(name));
        if (needToDelete) {
            try {
                service.remoteDelete(resourceProvider.getVfsId(), projectId, name, new AsyncRequestCallback<String>() {
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
            } catch (RequestException e) {
                String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteDeleteFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoteSelected(@NotNull Remote remote) {
        selectedRemote = remote;
        view.setEnableDeleteButton(true);
    }
}