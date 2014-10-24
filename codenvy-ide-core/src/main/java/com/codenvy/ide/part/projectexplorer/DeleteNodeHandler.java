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
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.FolderNode;
import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ConfirmCallback;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.api.runner.ApplicationStatus.NEW;
import static com.codenvy.api.runner.ApplicationStatus.RUNNING;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.projecttree.TreeNode.DeleteCallback;

/**
 * Used for deleting a {@link StorableNode}.
 *
 * @author Ann Shumilova
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DeleteNodeHandler {
    private NotificationManager      notificationManager;
    private CoreLocalizationConstant localization;
    private RunnerServiceClient      runnerServiceClient;
    private DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private DialogFactory            dialogFactory;

    @Inject
    public DeleteNodeHandler(NotificationManager notificationManager,
                             CoreLocalizationConstant localization,
                             RunnerServiceClient runnerServiceClient,
                             DtoUnmarshallerFactory dtoUnmarshallerFactory,
                             DialogFactory dialogFactory) {
        this.notificationManager = notificationManager;
        this.localization = localization;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
    }

    /**
     * Delete the specified node.
     *
     * @param nodeToDelete
     *         node to be deleted
     */
    public void delete(final StorableNode nodeToDelete) {
        if (nodeToDelete instanceof ProjectNode || nodeToDelete instanceof ProjectListStructure.ProjectNode) {
            checkRunningProcessesForProject(nodeToDelete, new AsyncCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean hasRunningProcesses) {
                    if (hasRunningProcesses) {
                        dialogFactory.createMessageDialog("", localization.stopProcessesBeforeDeletingProject(), null).show();
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
    private void askForDeletingNode(final StorableNode nodeToDelete) {
        dialogFactory.createConfirmDialog(getDialogTitle(nodeToDelete), getDialogQuestion(nodeToDelete), new ConfirmCallback() {
            @Override
            public void accepted() {
                nodeToDelete.delete(new DeleteCallback() {
                    @Override
                    public void onDeleted() {
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        notificationManager.showNotification(new Notification(caught.getMessage(), ERROR));
                    }
                });

            }
        }, null).show();
    }

    /**
     * Check whether there are running processes for the resource that will be deleted.
     *
     * @param projectNode
     * @param callback
     *         callback returns true if project has any running processes and false - otherwise
     */
    private void checkRunningProcessesForProject(StorableNode projectNode, final AsyncCallback<Boolean> callback) {
        Unmarshallable<Array<ApplicationProcessDescriptor>> unmarshaller =
                dtoUnmarshallerFactory.newArrayUnmarshaller(ApplicationProcessDescriptor.class);
        runnerServiceClient.getRunningProcesses(projectNode.getPath(),
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
    private String getDialogTitle(StorableNode node) {
        if (node instanceof FileNode) {
            return localization.deleteFileDialogTitle();
        } else if (node instanceof FolderNode) {
            return localization.deleteFolderDialogTitle();
        } else if (node instanceof ProjectNode || node instanceof ProjectListStructure.ProjectNode) {
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
    private String getDialogQuestion(StorableNode node) {
        if (node instanceof FileNode) {
            return localization.deleteFileDialogQuestion(node.getName());
        } else if (node instanceof FolderNode) {
            return localization.deleteFolderDialogQuestion(node.getName());
        } else if (node instanceof ProjectNode || node instanceof ProjectListStructure.ProjectNode) {
            return localization.deleteProjectDialogQuestion(node.getName());
        }
        return localization.deleteNodeDialogQuestion(node.getName());
    }
}
