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
package com.codenvy.ide.ext.git.client.remote.add;

import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

/**
 * Presenter for adding remote repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 19, 2011 11:12:44 AM anya $
 */
@Singleton
public class AddRemoteRepositoryPresenter implements AddRemoteRepositoryView.ActionDelegate {
    private AddRemoteRepositoryView view;
    private GitServiceClient        service;
    private ResourceProvider        resourceProvider;
    private AsyncCallback<Void>     callback;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     */
    @Inject
    public AddRemoteRepositoryPresenter(AddRemoteRepositoryView view, GitServiceClient service, ResourceProvider resourceProvider) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
    }

    /** Show dialog. */
    public void showDialog(@NotNull AsyncCallback<Void> callback) {
        this.callback = callback;
        view.setUrl("");
        view.setName("");
        view.setEnableOkButton(false);
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        String name = view.getName();
        String url = view.getUrl();
        final String projectId = resourceProvider.getActiveProject().getId();

        service.remoteAdd(projectId, name, url, new AsyncRequestCallback<String>() {
            @Override
            protected void onSuccess(String result) {
                callback.onSuccess(null);
                view.close();
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        String name = view.getName();
        String url = view.getUrl();
        boolean isEnabled = !name.isEmpty() && !url.isEmpty();
        view.setEnableOkButton(isEnabled);
    }
}