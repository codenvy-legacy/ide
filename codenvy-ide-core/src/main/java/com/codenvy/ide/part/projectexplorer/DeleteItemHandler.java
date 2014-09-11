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
package com.codenvy.ide.part.projectexplorer;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.FolderNode;
import com.codenvy.ide.api.projecttree.generic.ProjectRootNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.api.runner.ApplicationStatus.NEW;
import static com.codenvy.api.runner.ApplicationStatus.RUNNING;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Is used for performing the item deleting.
 * 
 * @author Ann Shumilova
 */
public class DeleteItemHandler {
    private NotificationManager      notificationManager;
    private EventBus                 eventBus;
    private CoreLocalizationConstant localization;
    private RunnerServiceClient      runnerServiceClient;
    private DtoUnmarshallerFactory   dtoUnmarshallerFactory;

    @Inject
    public DeleteItemHandler(NotificationManager notificationManager,
                               EventBus eventBus,
                               CoreLocalizationConstant localization,
                               RunnerServiceClient runnerServiceClient,
                               DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.notificationManager = notificationManager;
        this.eventBus = eventBus;
        this.localization = localization;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /**
     * Delete the pointed node.
     * 
     * @param nodeToDelete node to be deleted
     */
    public void delete(final AbstractTreeNode nodeToDelete) {
        if (nodeToDelete instanceof ProjectRootNode || nodeToDelete instanceof ProjectListStructure.ProjectNode) {
                checkRunningProcessesForProject(nodeToDelete, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean hasRunningProcesses) {
                        if (hasRunningProcesses) {
                            new Info(localization.stopProcessesBeforeDeletingProject()).show();
                        } else {
                            askForDeletingNode(nodeToDelete);
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        askForDeletingNode(nodeToDelete);
                    }
                });
            } else {
                askForDeletingNode(nodeToDelete);
            }
    }

    /**
     * Ask the user to confirm the delete operation.
     * 
     * @param nodeToDelete
     */
    private void askForDeletingNode(final AbstractTreeNode nodeToDelete) {
        new Ask(getDialogTitle(nodeToDelete), getDialogQuestion(nodeToDelete), new AskHandler() {
            @SuppressWarnings("unchecked")
            @Override
            public void onOk() {
                nodeToDelete.delete(new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        eventBus.fireEvent(new RefreshProjectTreeEvent());
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        notificationManager.showNotification(new Notification(caught.getMessage(), ERROR));
                    }
                });
            }
        }).show();
    }

    /**
     * Check whether there are running processes for the resource that will be deleted.
     * 
     * @param projectNode
     * @param callback callback returns true if project has any running processes and false - otherwise
     */
    private void checkRunningProcessesForProject(AbstractTreeNode projectNode, final AsyncCallback<Boolean> callback) {
        String projectPath = "";
        if (projectNode instanceof ProjectRootNode) {
            projectPath = ((ProjectRootNode)projectNode).getPath();
        } else if (projectNode instanceof ProjectListStructure.ProjectNode) {
            projectPath = ((ProjectListStructure.ProjectNode)projectNode).getData().getPath();
        }

        Unmarshallable<Array<ApplicationProcessDescriptor>> unmarshaller =
                                                                           dtoUnmarshallerFactory.newArrayUnmarshaller(ApplicationProcessDescriptor.class);
        runnerServiceClient.getRunningProcesses(projectPath,
                                                new AsyncRequestCallback<Array<ApplicationProcessDescriptor>>(unmarshaller) {
                                                    @Override
                                                    protected void onSuccess(Array<ApplicationProcessDescriptor> result) {
                                                        boolean hasRunningProcesses = false;
                                                        for (ApplicationProcessDescriptor descriptor : result.asIterable()) {
                                                            if (descriptor.getStatus() == NEW || descriptor.getStatus() == RUNNING) {
                                                                hasRunningProcesses = true;
                                                                break;
                                                            }
                                                        }
                                                        callback.onSuccess(hasRunningProcesses);
                                                    }

                                                    @Override
                                                    protected void onFailure(Throwable exception) {
                                                        callback.onFailure(exception);
                                                    }
                                                });
    }

    /**
     * Return the title of the deletion dialog due the resource type.
     * 
     * @param node
     * @return {@link String} title
     */
    private String getDialogTitle(AbstractTreeNode node) {
        if (node instanceof FileNode) {
            return localization.deleteFileDialogTitle();
        } else if (node instanceof FolderNode) {
            return localization.deleteFolderDialogTitle();
        } else if (node instanceof ProjectRootNode || node instanceof ProjectListStructure.ProjectNode) {
            return localization.deleteProjectDialogTitle();
        }
        return localization.deleteNodeDialogTitle();
    }

    /**
     * Return the content of the deletion dialog due the resource type.
     * 
     * @param node
     * @return {@link String} content 
     */
    private String getDialogQuestion(AbstractTreeNode node) {
        if (node instanceof FileNode) {
            return localization.deleteFileDialogQuestion(node.getPresentation().getDisplayName());
        } else if (node instanceof FolderNode) {
            return localization.deleteFolderDialogQuestion(node.getPresentation().getDisplayName());
        } else if (node instanceof ProjectRootNode || node instanceof ProjectListStructure.ProjectNode) {
            return localization.deleteProjectDialogQuestion(node.getPresentation().getDisplayName());
        }
        return localization.deleteNodeDialogQuestion(node.getPresentation().getDisplayName());
    }
}
