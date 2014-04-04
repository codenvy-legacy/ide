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