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
package com.codenvy.ide.ext.git.client.status;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Handler to process actions with displaying the status of the Git work tree.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class StatusCommandPresenter {
    private GitServiceClient        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private ConsolePart             console;
    private NotificationManager     notificationManager;

    /**
     * Create presenter.
     *
     * @param service
     * @param resourceProvider
     * @param console
     * @param constant
     * @param notificationManager
     */
    @Inject
    public StatusCommandPresenter(GitServiceClient service, ResourceProvider resourceProvider, ConsolePart console,
                                  GitLocalizationConstant constant, NotificationManager notificationManager) {
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
        this.notificationManager = notificationManager;
    }

    /** Show status. */
    public void showStatus() {
        Project project = resourceProvider.getActiveProject();
        if (project == null) {
            return;
        }

        service.statusText(project.getId(), false,
                           new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                               @Override
                               protected void onSuccess(String result) {
                                   printGitStatus(result);
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   String errorMessage = exception.getMessage() != null ? exception.getMessage() : constant.statusFailed();
                                   Notification notification = new Notification(errorMessage, ERROR);
                                   notificationManager.showNotification(notification);
                               }
                           });
    }

    /**
     * Print colored Git status to Output
     *
     * @param statusText text to be printed
     */
    private void printGitStatus(String statusText) {
        console.print("");
        String []lines = statusText.split("\n");
        for (String line : lines) {

            if (line.startsWith("\tmodified:") || line.startsWith("#\tmodified:")) {
                console.printError(line);
                continue;
            }

            if (line.startsWith("\t") || line.startsWith("#\t")) {
                console.printInfo(line);
                continue;
            }

            console.print(line);
        }
    }

}
