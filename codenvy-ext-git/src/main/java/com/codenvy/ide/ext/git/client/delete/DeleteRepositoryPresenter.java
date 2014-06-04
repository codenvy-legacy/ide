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