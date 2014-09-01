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
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
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
public class RenameItemAction extends Action {
    private final AnalyticsEventLogger     eventLogger;
    private final NotificationManager      notificationManager;
    private final EventBus                 eventBus;
    private final CoreLocalizationConstant localization;
    private final RunnerServiceClient      runnerServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final SelectionAgent           selectionAgent;

    @Inject
    public RenameItemAction(Resources resources,
                            AnalyticsEventLogger eventLogger,
                            SelectionAgent selectionAgent,
                            NotificationManager notificationManager,
                            EventBus eventBus,
                            CoreLocalizationConstant localization,
                            RunnerServiceClient runnerServiceClient,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(localization.renameItemActionText(), localization.renameItemActionDescription(), null, resources.rename());
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
        eventLogger.log("IDE: File rename");

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof AbstractTreeNode) {
            final AbstractTreeNode selectedNode = (AbstractTreeNode)selection.getFirstElement();

            if (selectedNode instanceof ProjectRootNode || selectedNode instanceof ProjectListStructure.ProjectNode) {
                checkRunningProcessesForProject(selectedNode, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean hasRunningProcesses) {
                        if (hasRunningProcesses) {
                            new Info(localization.stopProcessesBeforeRenamingProject()).show();
                        } else {
                            askForRenamingNode(selectedNode);
                        }
                    }

                    @Override
                    public void onFailure(Throwable caught) {
                        askForRenamingNode(selectedNode);
                    }
                });
            } else {
                askForRenamingNode(selectedNode);
            }
        }
    }

    /** {@inheritDoc} */
    @Override
    public void update(ActionEvent e) {
        boolean isEnabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() instanceof AbstractTreeNode) {
            isEnabled = ((AbstractTreeNode)selection.getFirstElement()).isRenemable();
        }
        e.getPresentation().setEnabledAndVisible(isEnabled);
    }

    private void askForRenamingNode(final AbstractTreeNode nodeToRename) {
        new AskValueDialog(getDialogTitle(nodeToRename), localization.renameDialogNewNameLabel(), new AskValueCallback() {
            @Override
            public void onOk(final String value) {
                nodeToRename.rename(value, new AsyncCallback<Void>() {
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
            return localization.renameFileDialogTitle();
        } else if (node instanceof FolderNode) {
            return localization.renameFolderDialogTitle();
        } else if (node instanceof ProjectRootNode || node instanceof ProjectListStructure.ProjectNode) {
            return localization.renameProjectDialogTitle();
        }
        return localization.renameNodeDialogTitle();
    }
}
