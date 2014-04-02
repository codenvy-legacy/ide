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

import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.api.event.RefreshBrowserEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.commons.exception.ExceptionThrownEvent;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;

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
    private GitServiceClient     service;
    private EventBus eventBus;
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
        String workDir = project.getPath();
        askBeforeDelete(workDir);
    }

    /**
     * Confirm, that user wants to delete Git repository.
     *
     * @param repository
     *         repository name
     */
    private void askBeforeDelete(@NotNull String repository) {
        boolean needToDelete = Window.confirm(constant.deleteGitRepositoryQuestion(repository));
        if (needToDelete) {
            doDeleteRepository();
        }
    }

    /** Perform deleting Git repository. */
    private void doDeleteRepository() {
        service.deleteRepository(project.getId(), new AsyncRequestCallback<Void>() {
            @Override
            protected void onSuccess(Void result) {
                resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                    @Override
                    public void onSuccess(Project result) {
                        project.setAttributes(result.getAttributes());
                        Notification notification = new Notification(constant.deleteGitRepositorySuccess(), INFO);
                        notificationManager.showNotification(notification);
                        eventBus.fireEvent(new RefreshBrowserEvent(project));
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        Log.error(DeleteRepositoryPresenter.class, caught);
                    }
                });
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