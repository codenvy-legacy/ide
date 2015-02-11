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
package org.eclipse.che.ide.part.projectexplorer;

import org.eclipse.che.api.runner.dto.ApplicationProcessDescriptor;
import org.eclipse.che.api.runner.gwt.client.RunnerServiceClient;

import org.eclipse.che.ide.CoreLocalizationConstant;

import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.FolderNode;
import org.eclipse.che.ide.api.project.tree.generic.ProjectNode;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.ui.dialogs.ConfirmCallback;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static org.eclipse.che.api.runner.ApplicationStatus.NEW;
import static org.eclipse.che.api.runner.ApplicationStatus.RUNNING;
import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;
import static org.eclipse.che.ide.api.project.tree.TreeNode.DeleteCallback;

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
            deleteProjectNode(nodeToDelete);
        } else {
            askForDeletingNode(nodeToDelete);
        }
    }

    private void deleteProjectNode(final StorableNode projectNodeToDelete) {
        checkRunningProcessesForProject(projectNodeToDelete, new AsyncCallback<Boolean>() {
            @Override
            public void onSuccess(final Boolean hasRunningProcesses) {
                if (hasRunningProcesses) {
                    dialogFactory.createMessageDialog("", localization.stopProcessesBeforeDeletingProject(), null).show();
                } else {
                    askForDeletingNode(projectNodeToDelete);
                }
            }

            @Override
            public void onFailure(final Throwable caught) {
                askForDeletingNode(projectNodeToDelete);
            }
        });
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
