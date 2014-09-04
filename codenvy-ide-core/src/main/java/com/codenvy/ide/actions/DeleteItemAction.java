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

import com.codenvy.api.analytics.logger.AnalyticsEventLogger;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.event.RefreshProjectTreeEvent;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.FolderNode;
import com.codenvy.ide.api.projecttree.generic.ProjectRootNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.part.projectexplorer.ProjectListStructure;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import static com.codenvy.api.runner.ApplicationStatus.NEW;
import static com.codenvy.api.runner.ApplicationStatus.RUNNING;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;

/**
 * Action for renaming an item which is selected in 'Project Explorer'.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class DeleteItemAction extends Action {
    private AnalyticsEventLogger     eventLogger;
    private NotificationManager      notificationManager;
    private EventBus                 eventBus;
    private CoreLocalizationConstant localization;
    private RunnerServiceClient      runnerServiceClient;
    private DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private SelectionAgent           selectionAgent;

    @Inject
    public DeleteItemAction(Resources resources,
                            AnalyticsEventLogger eventLogger,
                            SelectionAgent selectionAgent,
                            NotificationManager notificationManager,
                            EventBus eventBus,
                            CoreLocalizationConstant localization,
                            RunnerServiceClient runnerServiceClient,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(localization.deleteItemActionText(), localization.deleteItemActionDescription(), null, resources.delete());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
        this.notificationManager = notificationManager;
        this.eventBus = eventBus;
        this.localization = localization;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log("IDE: Delete file");

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof AbstractTreeNode) {
            final AbstractTreeNode selectedNode = (AbstractTreeNode)selection.getFirstElement();

            if (selectedNode instanceof ProjectRootNode || selectedNode instanceof ProjectListStructure.ProjectNode) {
                checkRunningProcessesForProject(selectedNode, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean hasRunningProcesses) {
                        if (hasRunningProcesses) {
                            new Info(localization.stopProcessesBeforeDeletingProject()).show();
                        } else {
                            askForDeletingNode(selectedNode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        askForDeletingNode(selectedNode);
                    }
                });
            } else {
                askForDeletingNode(selectedNode);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        boolean isEnabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() instanceof AbstractTreeNode) {
            isEnabled = ((AbstractTreeNode)selection.getFirstElement()).isDeletable();
        }
        e.getPresentation().setEnabled(isEnabled);
    }

    private void askForDeletingNode(final AbstractTreeNode nodeToDelete) {
        new Ask(getDialogTitle(nodeToDelete), getDialogQuestion(nodeToDelete), new AskHandler() {
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
     * @param callback
     *         callback returns true if project has any running processes and false - otherwise
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
