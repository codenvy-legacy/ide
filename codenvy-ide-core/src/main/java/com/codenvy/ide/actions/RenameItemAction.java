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
import com.codenvy.api.project.gwt.client.ProjectServiceClient;
import com.codenvy.api.project.gwt.client.QueryExpression;
import com.codenvy.api.project.shared.dto.ItemReference;
import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.gwt.client.RunnerServiceClient;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.Resources;
import com.codenvy.ide.api.action.Action;
import com.codenvy.ide.api.action.ActionEvent;
import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.projecttree.AbstractTreeNode;
import com.codenvy.ide.api.projecttree.generic.FileNode;
import com.codenvy.ide.api.projecttree.generic.FolderNode;
import com.codenvy.ide.api.projecttree.generic.ItemNode;
import com.codenvy.ide.api.projecttree.generic.ProjectNode;
import com.codenvy.ide.api.projecttree.generic.StorableNode;
import com.codenvy.ide.api.selection.Selection;
import com.codenvy.ide.api.selection.SelectionAgent;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.part.projectexplorer.ProjectListStructure;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.codenvy.ide.ui.dialogs.InputCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.api.runner.ApplicationStatus.NEW;
import static com.codenvy.api.runner.ApplicationStatus.RUNNING;
import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.projecttree.TreeNode.RenameCallback;

/**
 * Action for renaming an item which is selected in 'Project Explorer'.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class RenameItemAction extends Action {
    private final AnalyticsEventLogger     eventLogger;
    private final NotificationManager      notificationManager;
    private final EditorAgent              editorAgent;
    private final CoreLocalizationConstant localization;
    private final ProjectServiceClient     projectServiceClient;
    private final RunnerServiceClient      runnerServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final DialogFactory            dialogFactory;
    private final SelectionAgent           selectionAgent;

    @Inject
    public RenameItemAction(Resources resources,
                            AnalyticsEventLogger eventLogger,
                            SelectionAgent selectionAgent,
                            NotificationManager notificationManager,
                            EditorAgent editorAgent,
                            CoreLocalizationConstant localization,
                            ProjectServiceClient projectServiceClient,
                            RunnerServiceClient runnerServiceClient,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory,
                            DialogFactory dialogFactory) {
        super(localization.renameItemActionText(), localization.renameItemActionDescription(), null, resources.rename());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
        this.notificationManager = notificationManager;
        this.editorAgent = editorAgent;
        this.localization = localization;
        this.projectServiceClient = projectServiceClient;
        this.runnerServiceClient = runnerServiceClient;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.dialogFactory = dialogFactory;
    }

    /** {@inheritDoc} */
    @Override
    public void actionPerformed(ActionEvent e) {
        eventLogger.log(this);

        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() != null && selection.getFirstElement() instanceof StorableNode) {
            final StorableNode selectedNode = (StorableNode)selection.getFirstElement();

            if (selectedNode instanceof ProjectNode) {
                dialogFactory.createMessageDialog("", localization.closeProjectBeforeRenaming(), null).show();
            } else if (selectedNode instanceof ProjectListStructure.ProjectNode) {
                checkRunningProcessesForProject(selectedNode, new AsyncCallback<Boolean>() {
                    @Override
                    public void onSuccess(Boolean hasRunningProcesses) {
                        if (hasRunningProcesses) {
                            dialogFactory.createMessageDialog("", localization.stopProcessesBeforeRenamingProject(), null).show();
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
            isEnabled =
                    ((AbstractTreeNode)selection.getFirstElement()).isRenamable() && selection.getFirstElement() instanceof StorableNode;
        }
        e.getPresentation().setEnabled(isEnabled);
    }

    private void askForRenamingNode(final StorableNode nodeToRename) {
        dialogFactory.createInputDialog(
                getDialogTitle(nodeToRename),
                localization.renameDialogNewNameLabel(),
                nodeToRename.getName(),
                0,
                nodeToRename.getName().indexOf('.') >= 0 ? nodeToRename.getName().lastIndexOf('.') : nodeToRename.getName().length(),
                new InputCallback() {
                    @Override
                    public void accepted(final String value) {
                        ItemReference itemReferenceBeforeRenaming = null;
                        if (nodeToRename instanceof ItemNode) {
                            itemReferenceBeforeRenaming = ((ItemNode)nodeToRename).getData();
                        }

                        final ItemReference finalItemReferenceBeforeRenaming = itemReferenceBeforeRenaming;
                        nodeToRename.rename(value, new RenameCallback() {
                            @Override
                            public void onRenamed() {
                                if (finalItemReferenceBeforeRenaming != null) {
                                    checkOpenedFiles(finalItemReferenceBeforeRenaming, value);
                                }
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

    private void checkOpenedFiles(ItemReference itemBeforeRenaming, String newName) {
        final String itemPathBeforeRenaming = itemBeforeRenaming.getPath();
        final String parentPathBeforeRenaming =
                itemPathBeforeRenaming.substring(0, itemPathBeforeRenaming.length() - itemBeforeRenaming.getName().length());
        final String itemPathAfterRenaming = parentPathBeforeRenaming + newName;

        if ("file".equals(itemBeforeRenaming.getType())) {
            checkEditor(itemPathBeforeRenaming, itemPathAfterRenaming);
        } else if ("folder".equals(itemBeforeRenaming.getType())) {
            QueryExpression query = new QueryExpression().setPath(itemPathAfterRenaming);
            Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
            projectServiceClient.search(query, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                @Override
                protected void onSuccess(Array<ItemReference> result) {
                    StringMap<ItemReference> children = Collections.createStringMap();
                    for (ItemReference itemReference : result.asIterable()) {
                        children.put(itemReference.getPath(), itemReference);
                    }

                    for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                        FileNode openedFile = editor.getEditorInput().getFile();

                        if (children.get(openedFile.getPath()) != null) {
                            String pathBeforeRenaming = openedFile.getPath().replaceFirst(itemPathAfterRenaming, itemPathBeforeRenaming);
                            checkEditor(pathBeforeRenaming, openedFile.getPath());
                        }
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    Log.error(RenameItemAction.class, exception);
                }
            });
        }
    }

    private void checkEditor(String filePathBeforeRename, String filePathAfterRename) {
        final EditorPartPresenter editor = editorAgent.getOpenedEditors().remove(filePathBeforeRename);
        if (editor != null) {
            editorAgent.getOpenedEditors().put(filePathAfterRename, editor);
            editor.onFileChanged();
        }
    }

    private String getDialogTitle(StorableNode node) {
        if (node instanceof FileNode) {
            return localization.renameFileDialogTitle();
        } else if (node instanceof FolderNode) {
            return localization.renameFolderDialogTitle();
        } else if (node instanceof ProjectNode || node instanceof ProjectListStructure.ProjectNode) {
            return localization.renameProjectDialogTitle();
        }
        return localization.renameNodeDialogTitle();
    }
}
