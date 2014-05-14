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
package com.codenvy.ide.ext.git.client.delete;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;

/**
 * Delete repository command handler, performs deleting Git repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 21, 2011 5:57:30 PM anya $
 */
@Singleton
public class DeleteRepositoryPresenter {
    private GitServiceClient        service;
    private EventBus                eventBus;
    private GitLocalizationConstant constant;
    private ResourceProvider        resourceProvider;
    private Project                 project;
    private NotificationManager     notificationManager;

    /**
     * Create presenter.
     *
     * @param service
     * @param eventBus
     * @param constant
     * @param resourceProvider
     * @param notificationManager
     */
    @Inject
    public DeleteRepositoryPresenter(GitServiceClient service,
                                     EventBus eventBus,
                                     GitLocalizationConstant constant,
                                     ResourceProvider resourceProvider,
                                     NotificationManager notificationManager) {
        this.service = service;
        this.eventBus = eventBus;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
    }

    /** Delete Git repository. */
    public void deleteRepository() {
        project = resourceProvider.getActiveProject();
        service.deleteRepository(project.getId(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                Notification notification = new Notification(constant.deleteGitRepositorySuccess(), INFO);
                notificationManager.showNotification(notification);
            }

            @Override
            protected void onFailure(Throwable exception) {
                eventBus.fireEvent(new ExceptionThrownEvent(exception));
                Notification notification = new Notification(exception.getMessage(), ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

}