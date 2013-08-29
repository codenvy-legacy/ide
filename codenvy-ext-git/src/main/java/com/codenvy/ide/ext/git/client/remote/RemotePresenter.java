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
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.RemoteListUnmarshaller;
import com.codenvy.ide.ext.git.client.remote.add.AddRemoteRepositoryPresenter;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    private ConsolePart                  console;
    private AddRemoteRepositoryPresenter addRemoteRepositoryPresenter;
    private Remote                       selectedRemote;
    private String                       projectId;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param console
     * @param addRemoteRepositoryPresenter
     */
    @Inject
    public RemotePresenter(RemoteView view, GitClientService service, ResourceProvider resourceProvider, GitLocalizationConstant constant,
                           ConsolePart console, AddRemoteRepositoryPresenter addRemoteRepositoryPresenter) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.console = console;
        this.addRemoteRepositoryPresenter = addRemoteRepositoryPresenter;
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
        RemoteListUnmarshaller unmarshaller = new RemoteListUnmarshaller();
        try {
            service.remoteList(resourceProvider.getVfsId(), projectId, null, true,
                               new AsyncRequestCallback<JsonArray<Remote>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Remote> result) {
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
                console.print(errorMessage);
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
                        console.print(errorMessage);
                    }
                });
            } catch (RequestException e) {
                String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteDeleteFailed();
                console.print(errorMessage);
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