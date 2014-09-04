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
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.collections.StringMap;
import com.codenvy.ide.part.projectexplorer.ProjectListStructure;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.rest.Unmarshallable;
import com.codenvy.ide.ui.dialogs.askValue.AskValueCallback;
import com.codenvy.ide.ui.dialogs.askValue.AskValueDialog;
import com.codenvy.ide.ui.dialogs.info.Info;
import com.codenvy.ide.util.loging.Log;
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
    private final EditorAgent              editorAgent;
    private final CoreLocalizationConstant localization;
    private final ProjectServiceClient     projectServiceClient;
    private final RunnerServiceClient      runnerServiceClient;
    private final DtoUnmarshallerFactory   dtoUnmarshallerFactory;
    private final SelectionAgent           selectionAgent;

    @Inject
    public RenameItemAction(Resources resources,
                            AnalyticsEventLogger eventLogger,
                            SelectionAgent selectionAgent,
                            NotificationManager notificationManager,
                            EventBus eventBus,
                            EditorAgent editorAgent,
                            CoreLocalizationConstant localization,
                            ProjectServiceClient projectServiceClient,
                            RunnerServiceClient runnerServiceClient,
                            DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        super(localization.renameItemActionText(), localization.renameItemActionDescription(), null, resources.rename());
        this.selectionAgent = selectionAgent;
        this.eventLogger = eventLogger;
        this.notificationManager = notificationManager;
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.localization = localization;
        this.projectServiceClient = projectServiceClient;
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

            if (selectedNode instanceof ProjectRootNode) {
                new Info(localization.closeProjectBeforeRenaming()).show();
            } else if (selectedNode instanceof ProjectListStructure.ProjectNode) {
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
        e.getPresentation().setEnabled(isEnabled);
    }

    private void askForRenamingNode(final AbstractTreeNode nodeToRename) {
        new AskValueDialog(getDialogTitle(nodeToRename), localization.renameDialogNewNameLabel(), new AskValueCallback() {
            @Override
            public void onOk(final String newName) {
                nodeToRename.rename(newName, new AsyncCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        if (nodeToRename instanceof FileNode || nodeToRename instanceof FolderNode) {
                            checkOpenedFiles(nodeToRename, newName);
                        }
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

    private void checkOpenedFiles(final AbstractTreeNode node, String newName) {
        final ItemReference itemBeforeRenaming;
        if (node instanceof FileNode) {
            itemBeforeRenaming = ((FileNode)node).getData();
        } else if (node instanceof FolderNode) {
            itemBeforeRenaming = ((FolderNode)node).getData();
        } else {
            return;
        }

        final String itemPathBeforeRenaming = itemBeforeRenaming.getPath();
        final String parentPathBeforeRenaming =
                itemPathBeforeRenaming.substring(0, itemPathBeforeRenaming.length() - itemBeforeRenaming.getName().length());
        final String itemPathAfterRenaming = parentPathBeforeRenaming + newName;

        QueryExpression query = null;
        if ("file".equals(itemBeforeRenaming.getType())) {
            query = new QueryExpression().setPath(parentPathBeforeRenaming).setName(newName);
        } else if ("folder".equals(itemBeforeRenaming.getType())) {
            query = new QueryExpression().setPath(itemPathAfterRenaming);
        }

        if (query != null) {
            Unmarshallable<Array<ItemReference>> unmarshaller = dtoUnmarshallerFactory.newArrayUnmarshaller(ItemReference.class);
            projectServiceClient.search(query, new AsyncRequestCallback<Array<ItemReference>>(unmarshaller) {
                @Override
                protected void onSuccess(Array<ItemReference> result) {
                    if ("file".equals(itemBeforeRenaming.getType())) {
                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                            if (itemPathBeforeRenaming.equals(editor.getEditorInput().getFile().getPath())) {
                                // result array should contain one item only
                                ItemReference renamedItem = result.get(0);
                                replaceFileInEditor(editor, renamedItem);
                                break;
                            }
                        }
                    } else if ("folder".equals(itemBeforeRenaming.getType())) {
                        StringMap<ItemReference> children = Collections.createStringMap();
                        for (ItemReference itemReference : result.asIterable()) {
                            children.put(itemReference.getPath(), itemReference);
                        }

                        for (EditorPartPresenter editor : editorAgent.getOpenedEditors().getValues().asIterable()) {
                            FileNode openedFile = editor.getEditorInput().getFile();
                            if (openedFile.getPath().startsWith(itemPathBeforeRenaming)) {
                                String childFileNewPath = openedFile.getPath().replaceFirst(itemPathBeforeRenaming, itemPathAfterRenaming);
                                ItemReference renamedItem = children.get(childFileNewPath);
                                if (renamedItem != null) {
                                    replaceFileInEditor(editor, renamedItem);
                                }
                            }
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

    private void replaceFileInEditor(EditorPartPresenter editor, ItemReference renamedItem) {
        editorAgent.getOpenedEditors().remove(editor.getEditorInput().getFile().getPath());
        editorAgent.getOpenedEditors().put(renamedItem.getPath(), editor);
        editor.getEditorInput().getFile().setData(renamedItem);
        editor.onFileChanged();
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
