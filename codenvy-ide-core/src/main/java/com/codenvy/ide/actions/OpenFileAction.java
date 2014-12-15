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
package com.codenvy.ide.actions;

import com.codenvy.api.project.shared.dto.ProjectDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.app.CurrentProject;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.event.ProjectActionEvent;
import com.codenvy.ide.api.event.ProjectActionHandler;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.TreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import javax.inject.Inject;
import javax.inject.Singleton;

import static com.codenvy.ide.api.notification.Notification.Type.WARNING;

/**
 * @author Sergii Leschenko
 */
@Singleton
public class OpenFileAction extends Action {
    private final EventBus                 eventBus;
    private final AppContext               appContext;
    private final NotificationManager      notificationManager;
    private final CoreLocalizationConstant localization;
    private final EditorAgent              editorAgent;

    private HandlerRegistration reopenFileHandler;

    @Inject
    public OpenFileAction(EventBus eventBus,
                          AppContext appContext,
                          NotificationManager notificationManager,
                          CoreLocalizationConstant localization,
                          EditorAgent editorAgent) {
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.notificationManager = notificationManager;
        this.localization = localization;
        this.editorAgent = editorAgent;
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (appContext.getCurrentProject() == null || appContext.getCurrentProject().getRootProject() == null) {
            return;
        }

        final ProjectDescriptor activeProject = appContext.getCurrentProject().getRootProject();
        if (event.getParameters() == null) {
            Log.error(getClass(), "Can't open file without parameters");
        }

        final String path = event.getParameters().get("file");
        final String filePathToOpen = activeProject.getPath() + (!path.startsWith("/") ? "/".concat(path) : path);

        openFileByPath(filePathToOpen);

        //Reopens file after select project type
        reopenFileHandler = eventBus.addHandler(ProjectActionEvent.TYPE, new ProjectActionHandler() {
            @Override
            public void onProjectOpened(ProjectActionEvent event) {
                final String openedProject = event.getProject().getName();
                if (openedProject.equals(activeProject.getName())) {
                    ProjectDescriptor activeProject = event.getProject();
                    final String filePathToOpen = activeProject.getPath() + (!path.startsWith("/") ? "/".concat(path) : path);

                    openFileByPath(filePathToOpen);
                }
                reopenFileHandler.removeHandler();
            }

            @Override
            public void onProjectClosed(ProjectActionEvent event) {
                //do nothing
            }
        });
    }

    private void openFileByPath(final String filePath) {
        final CurrentProject currentProject = appContext.getCurrentProject();
        if (currentProject != null) {
            currentProject.getCurrentTree().getNodeByPath(filePath, new AsyncCallback<TreeNode<?>>() {
                @Override
                public void onSuccess(TreeNode<?> result) {
                    if (result instanceof FileNode) {
                        editorAgent.openEditor((FileNode)result);
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    notificationManager.showNotification(new Notification(localization.unableOpenFile(filePath), WARNING));
                }
            });
        }
    }
}