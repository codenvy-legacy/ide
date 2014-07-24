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
package com.codenvy.ide.ext.tutorials.client.update;

import com.codenvy.ide.api.AppContext;
import com.codenvy.ide.api.CurrentProject;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.ui.workspace.WorkspaceAgent;
import com.codenvy.ide.ext.tutorials.client.TutorialsLocalizationConstant;
import com.codenvy.ide.extension.runner.client.console.RunnerConsolePresenter;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.inject.Singleton;

import javax.inject.Inject;

import static com.codenvy.ide.api.notification.Notification.Status.FINISHED;
import static com.codenvy.ide.api.notification.Notification.Status.PROGRESS;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * @author Vitaly Parfonov
 */
@Singleton
public class ExtensionUpdater implements Notification.OpenNotificationHandler  {

    private UpdateServiceClient    updateServiceClient;
    private WorkspaceAgent         workspaceAgent;
    private NotificationManager    notificationManager;
    private RunnerConsolePresenter console;
    private AppContext appContext;
    private TutorialsLocalizationConstant localizationConstant;

    @Inject
    public ExtensionUpdater(UpdateServiceClient updateServiceClient,
                            WorkspaceAgent workspaceAgent,
                            NotificationManager notificationManager,
                            RunnerConsolePresenter console,
                            AppContext appContext,
                            TutorialsLocalizationConstant localizationConstant) {
        this.updateServiceClient = updateServiceClient;
        this.workspaceAgent = workspaceAgent;
        this.notificationManager = notificationManager;
        this.console = console;
        this.appContext = appContext;
        this.localizationConstant = localizationConstant;
    }

    /** Updates launched Codenvy Extension. */
    public void updateExtension() {
        final CurrentProject currentProject = appContext.getCurrentProject();
        final Notification notification =
                new Notification(localizationConstant.applicationUpdating(currentProject.getProjectDescription().getName()), PROGRESS,
                                 ExtensionUpdater.this);
        notificationManager.showNotification(notification);
        try {
            updateServiceClient.update(currentProject.getProcessDescriptor(), new RequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    notification.setStatus(FINISHED);
                    notification.setMessage(localizationConstant.applicationUpdated(currentProject.getProjectDescription().getName()));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    notification.setStatus(FINISHED);
                    notification.setType(ERROR);
                    notification.setMessage(localizationConstant.updateApplicationFailed(currentProject.getProjectDescription().getName()));

                    if (exception != null && exception.getMessage() != null) {
                        console.print(exception.getMessage());
                    }
                }
            });
        } catch (WebSocketException e) {
            notification.setStatus(FINISHED);
            notification.setType(ERROR);
            notification.setMessage(localizationConstant.updateApplicationFailed(currentProject.getProjectDescription().getName()));
        }
    }

    @Override
    public void onOpenClicked() {
        workspaceAgent.setActivePart(console);
    }
}
