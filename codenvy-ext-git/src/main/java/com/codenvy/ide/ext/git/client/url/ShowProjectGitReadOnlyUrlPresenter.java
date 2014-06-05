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
package com.codenvy.ide.ext.git.client.url;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Presenter for showing git url.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class ShowProjectGitReadOnlyUrlPresenter implements ShowProjectGitReadOnlyUrlView.ActionDelegate {
    private ShowProjectGitReadOnlyUrlView view;
    private GitServiceClient              service;
    private ResourceProvider              resourceProvider;
    private GitLocalizationConstant       constant;
    private NotificationManager           notificationManager;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param notificationManager
     */
    @Inject
    public ShowProjectGitReadOnlyUrlPresenter(ShowProjectGitReadOnlyUrlView view, GitServiceClient service,
                                              ResourceProvider resourceProvider, GitLocalizationConstant constant,
                                              NotificationManager notificationManager) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show dialog. */
    public void showDialog() {
        String projectId = resourceProvider.getActiveProject().getId();

        service.getGitReadOnlyUrl(projectId,
                                  new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                      @Override
                                      protected void onSuccess(String result) {
                                          view.setUrl(result);
                                          view.showDialog();
                                      }

                                      @Override
                                      protected void onFailure(Throwable exception) {
                                          String errorMessage =
                                                  exception.getMessage() != null && !exception.getMessage().isEmpty() ? exception
                                                          .getMessage()
                                                                                                                      : constant
                                                          .initFailed();
                                          Notification notification = new Notification(errorMessage, ERROR);
                                          notificationManager.showNotification(notification);
                                      }
                                  });
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }
}