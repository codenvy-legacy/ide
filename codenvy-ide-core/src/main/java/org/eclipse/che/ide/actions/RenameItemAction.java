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

import org.eclipse.che.api.analytics.client.logger.AnalyticsEventLogger;
import org.eclipse.che.api.project.gwt.client.ProjectServiceClient;
import org.eclipse.che.api.project.gwt.client.QueryExpression;
import org.eclipse.che.api.project.shared.dto.ItemReference;
import org.eclipse.che.api.runner.dto.ApplicationProcessDescriptor;
import org.eclipse.che.api.runner.gwt.client.RunnerServiceClient;

import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.part.projectexplorer.ProjectListStructure;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.CoreLocalizationConstant;
import org.eclipse.che.ide.Resources;
import org.eclipse.che.ide.api.action.Action;
import org.eclipse.che.ide.api.action.ActionEvent;
import org.eclipse.che.ide.api.app.AppContext;
import org.eclipse.che.ide.api.editor.EditorAgent;
import org.eclipse.che.ide.api.editor.EditorPartPresenter;
import org.eclipse.che.ide.api.notification.Notification;
import org.eclipse.che.ide.api.notification.NotificationManager;
import org.eclipse.che.ide.api.project.tree.AbstractTreeNode;
import org.eclipse.che.ide.api.project.tree.TreeStructure;
import org.eclipse.che.ide.api.project.tree.TreeNode;
import org.eclipse.che.ide.api.project.tree.VirtualFile;
import org.eclipse.che.ide.api.project.tree.generic.FileNode;
import org.eclipse.che.ide.api.project.tree.generic.FolderNode;
import org.eclipse.che.ide.api.project.tree.generic.ItemNode;
import org.eclipse.che.ide.api.project.tree.generic.ProjectNode;
import org.eclipse.che.ide.api.project.tree.generic.StorableNode;
import org.eclipse.che.ide.api.selection.Selection;
import org.eclipse.che.ide.api.selection.SelectionAgent;
import org.eclipse.che.ide.collections.Array;
import org.eclipse.che.ide.collections.Collections;
import org.eclipse.che.ide.collections.StringMap;
import org.eclipse.che.ide.part.projectexplorer.ProjectListStructure;
import org.eclipse.che.ide.rest.AsyncRequestCallback;
import org.eclipse.che.ide.rest.DtoUnmarshallerFactory;
import org.eclipse.che.ide.rest.Unmarshallable;
import org.eclipse.che.ide.ui.dialogs.DialogFactory;
import org.eclipse.che.ide.ui.dialogs.InputCallback;
import org.eclipse.che.ide.ui.dialogs.input.InputDialog;
import org.eclipse.che.ide.ui.dialogs.input.InputValidator;
import org.eclipse.che.ide.util.NameUtils;
import org.eclipse.che.ide.util.loging.Log;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.annotation.Nullable;

import static org.eclipse.che.api.runner.ApplicationStatus.NEW;
import static org.eclipse.che.api.runner.ApplicationStatus.RUNNING;
import static org.eclipse.che.ide.api.notification.Notification.Type.ERROR;
import static org.eclipse.che.ide.api.project.tree.TreeNode.RenameCallback;

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
    private final AppContext               appContext;
    private final SelectionAgent           selectionAgent;
    private final InputValidator           fileNameValidator;
    private final InputValidator           folderNameValidator;
    private final InputValidator           projectNameValidator;

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
                            DialogFactory dialogFactory,
                            AppContext appContext) {
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
        this.appContext = appContext;
        this.fileNameValidator = new FileNameValidator();
        this.folderNameValidator = new FolderNameValidator();
        this.projectNameValidator = new ProjectNameValidator();
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
        if ((appContext.getCurrentProject() == null && !appContext.getCurrentUser().isUserPermanent()) ||
            (appContext.getCurrentProject() != null && appContext.getCurrentProject().isReadOnly())) {
            e.getPresentation().setVisible(true);
            e.getPresentation().setEnabled(false);
            return;
        }

        boolean enabled = false;
        Selection<?> selection = selectionAgent.getSelection();
        if (selection != null && selection.getFirstElement() instanceof AbstractTreeNode) {
            enabled = selection.getFirstElement() instanceof StorableNode
                      && ((AbstractTreeNode)selection.getFirstElement()).isRenamable();
        }
        e.getPresentation().setEnabled(enabled);
    }

    private void askForRenamingNode(final StorableNode nodeToRename) {
        final InputCallback inputCallback = new InputCallback() {
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
        };

        final int selectionLength = nodeToRename.getName().indexOf('.') >= 0
                                    ? nodeToRename.getName().lastIndexOf('.')
                                    : nodeToRename.getName().length();

        InputDialog inputDialog = dialogFactory.createInputDialog(getDialogTitle(nodeToRename),
                                                                  localization.renameDialogNewNameLabel(),
                                                                  nodeToRename.getName(), 0, selectionLength, inputCallback, null);
        if (nodeToRename instanceof FileNode) {
            inputDialog.withValidator(fileNameValidator);
        } else if (nodeToRename instanceof FolderNode) {
            inputDialog.withValidator(folderNameValidator);
        } else if (nodeToRename instanceof ProjectNode || nodeToRename instanceof ProjectListStructure.ProjectNode) {
            inputDialog.withValidator(projectNameValidator);
        }
        inputDialog.show();
    }

    /**
     * Check whether project has any running processes.
     *
     * @param projectNode
     *         project to check
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
                        VirtualFile openedFile = editor.getEditorInput().getFile();

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
            TreeStructure currentTreeStructure = appContext.getCurrentProject().getCurrentTree();
            currentTreeStructure.getNodeByPath(filePathAfterRename, new AsyncCallback<TreeNode<?>>() {
                @Override
                public void onFailure(Throwable caught) {

                }

                @Override
                public void onSuccess(TreeNode<?> result) {
                    FileNode fileNode = (FileNode)result;
                    editor.getEditorInput().setFile(fileNode);
                    editorAgent.getOpenedEditors().put(fileNode.getPath(), editor);
                    editor.onFileChanged();
                }
            });
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

    private class FileNameValidator implements InputValidator {
        @Nullable
        @Override
        public Violation validate(String value) {
            if (!NameUtils.checkFileName(value)) {
                return new Violation() {
                    @Override
                    public String getMessage() {
                        return localization.invalidName();
                    }
                };
            }
            return null;
        }
    }

    private class FolderNameValidator implements InputValidator {
        @Nullable
        @Override
        public Violation validate(String value) {
            if (!NameUtils.checkFolderName(value)) {
                return new Violation() {
                    @Override
                    public String getMessage() {
                        return localization.invalidName();
                    }
                };
            }
            return null;
        }
    }

    private class ProjectNameValidator implements InputValidator {
        @Nullable
        @Override
        public Violation validate(String value) {
            if (!NameUtils.checkProjectName(value)) {
                return new Violation() {
                    @Override
                    public String getMessage() {
                        return localization.invalidName();
                    }
                };
            }
            return null;
        }
    }
}
