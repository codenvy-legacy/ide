/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide.actions;

import com.google.gwt.core.client.Callback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.HandlerRegistration;

import org.eclipse.che.api.project.shared.dto.ProjectDescriptor;
import org.eclipse.che.api.promises.client.Promise;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper;
import org.eclipse.che.api.promises.client.callback.CallbackPromiseHelper.Call;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.action.PromisableAction;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.app.CurrentProject;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.event.ActivePartChangedEvent;
import org.eclipse.che.ide.api.event.ActivePartChangedHandler;
import org.eclipse.che.ide.api.event.FileEvent;
import org.eclipse.che.ide.api.event.ProjectActionEvent;
import org.eclipse.che.ide.api.event.ProjectActionHandler;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.util.loging.Log;

import javax.inject.Inject;
import javax.inject.Singleton;

import static org.eclipse.che.ide.api.notification.Notification.Type.WARNING;

/**
 * @author Sergii Leschenko
 */
@Singleton
public class OpenFileAction extends Action implements PromisableAction {
    private final EventBus                 eventBus;
    private final AppContext               appContext;
    private final NotificationManager      notificationManager;
    private final CoreLocalizationConstant localization;

    private HandlerRegistration reopenFileHandler;
    private HandlerRegistration handlerRegistration;

    @Inject
    public OpenFileAction(EventBus eventBus,
                          AppContext appContext,
                          NotificationManager notificationManager,
                          CoreLocalizationConstant localization) {
        this.eventBus = eventBus;
        this.appContext = appContext;
        this.notificationManager = notificationManager;
        this.localization = localization;
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
                    if (result == null) {
                        notificationManager.showNotification(new Notification(localization.unableOpenFile(filePath), WARNING));
                        return;
                    }

                    if (result instanceof FileNode) {
                        eventBus.fireEvent(new FileEvent((FileNode)result, FileEvent.FileOperation.OPEN));
                    }
                }

                @Override
                public void onFailure(Throwable caught) {
                    notificationManager.showNotification(new Notification(localization.unableOpenFile(filePath), WARNING));
                }
            });
        }
    }

    @Override
    public Promise<Void> promise(ActionEvent e) {
        final String activeProjectPath = appContext.getCurrentProject().getRootProject().getPath();
        final String relPathToOpen = e.getParameters().get("file");

        final Call<Void, Throwable> call = new Call<Void, Throwable>() {
            @Override
            public void makeCall(final Callback<Void, Throwable> callback) {
                handlerRegistration = eventBus.addHandler(ActivePartChangedEvent.TYPE, new ActivePartChangedHandler() {
                    @Override
                    public void onActivePartChanged(ActivePartChangedEvent event) {
                        if (event.getActivePart() instanceof EditorPartPresenter) {
                            EditorPartPresenter editor = (EditorPartPresenter)event.getActivePart();
                            if ((activeProjectPath + "/" + relPathToOpen).equals(editor.getEditorInput().getFile().getPath())) {
                                handlerRegistration.removeHandler();
                                callback.onSuccess(null);
                            }
                        }
                    }
                });
            }
        };

        actionPerformed(e);

        return CallbackPromiseHelper.createFromCallback(call);
    }
}