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
package com.codenvy.ide.ext.git.client.branch;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.FileEvent;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.google.web.bindery.event.shared.EventBus;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_ALL;

/**
 * Presenter for displaying and work with branches.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class BranchPresenter implements BranchView.ActionDelegate {
    private       BranchView              view;
    private       EventBus                eventBus;
    private       Project                 project;
    private       GitServiceClient        service;
    private       GitLocalizationConstant constant;
    private       EditorAgent             editorAgent;
    private       Branch                  selectedBranch;
    private       ResourceProvider        resourceProvider;
    private       NotificationManager     notificationManager;
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param constant
     * @param notificationManager
     */
    @Inject
    public BranchPresenter(BranchView view,
                           EventBus eventBus,
                           EditorAgent editorAgent,
                           GitServiceClient service,
                           GitLocalizationConstant constant,
                           ResourceProvider resourceProvider,
                           NotificationManager notificationManager,
                           DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.eventBus = eventBus;
        this.editorAgent = editorAgent;
        this.service = service;
        this.constant = constant;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        view.setEnableCheckoutButton(false);
        view.setEnableDeleteButton(false);
        view.setEnableRenameButton(false);
        getBranches(project.getId());
        view.showDialog();
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onRenameClicked() {
        final String currentBranchName = selectedBranch.getDisplayName();
        String name = Window.prompt(constant.branchTypeNew(), currentBranchName);
        if (!name.isEmpty()) {
            final String projectId = project.getId();
            service.branchRename(projectId, currentBranchName, name, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    getBranches(projectId);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    String errorMessage =
                            (exception.getMessage() != null) ? exception.getMessage()
                                                             : constant.branchRenameFailed();
                    Notification notification = new Notification(errorMessage, ERROR);
                    notificationManager.showNotification(notification);
                }
            });
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDeleteClicked() {
        final String name = selectedBranch.getName();

        final String projectId = project.getId();
        service.branchDelete(projectId, name, true, new AsyncRequestCallback<String>() {
            @Override
            protected void onSuccess(String result) {
                getBranches(projectId);
            }

            @Override
            protected void onFailure(Throwable exception) {
                String errorMessage = (exception.getMessage() != null) ? exception.getMessage() : constant.branchDeleteFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /** {@inheritDoc} */
    @Override
    public void onCheckoutClicked() {
        final List<EditorPartPresenter> openedEditors = new ArrayList<>();
        for (EditorPartPresenter partPresenter : editorAgent.getOpenedEditors().getValues().asIterable()) {
            openedEditors.add(partPresenter);
        }

        String name = selectedBranch.getDisplayName();
        String startingPoint = null;
        boolean remote = selectedBranch.isRemote();
        if (remote) {
            startingPoint = selectedBranch.getDisplayName();
        }
        final String projectId = project.getId();
        if (name == null) {
            return;
        }

        service.branchCheckout(projectId, name, startingPoint, remote, new AsyncRequestCallback<String>() {
            @Override
            protected void onSuccess(String result) {
                getBranches(projectId);
                refreshProject(openedEditors);
            }

            @Override
            protected void onFailure(Throwable exception) {
                final String errorMessage = (exception.getMessage() != null) ? exception.getMessage()
                                                                             : constant.branchCheckoutFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /**
     * Refresh project.
     *
     * @param openedEditors
     *         editors that corresponds to open files
     */
    private void refreshProject(final List<EditorPartPresenter> openedEditors) {
        resourceProvider.getActiveProject().refreshChildren(new AsyncCallback<Project>() {
            @Override
            public void onSuccess(Project result) {
                for (EditorPartPresenter partPresenter : openedEditors) {
                    final File file = partPresenter.getEditorInput().getFile();
                    refreshFile(file, partPresenter);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = (caught.getMessage() != null) ? caught.getMessage() : constant.refreshChildrenFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /**
     * Refresh file.
     *
     * @param file
     *         file to refresh
     * @param partPresenter
     *         editor that corresponds to the <code>file</code>.
     */
    private void refreshFile(final File file, final EditorPartPresenter partPresenter) {
        final Project project = resourceProvider.getActiveProject();
        project.findResourceByPath(file.getPath(), new AsyncCallback<Resource>() {
            @Override
            public void onFailure(Throwable caught) {
                eventBus.fireEvent(new FileEvent(file, FileEvent.FileOperation.CLOSE));
            }

            @Override
            public void onSuccess(final Resource result) {
                updateOpenedFile((File)result, partPresenter);
            }
        });
    }

    /**
     * Update content of the file.
     *
     * @param file
     *         file to update
     * @param partPresenter
     *         editor that corresponds to the <code>file</code>.
     */
    private void updateOpenedFile(final File file, final EditorPartPresenter partPresenter) {
        resourceProvider.getActiveProject().getContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                try {
                    EditorInput editorInput = partPresenter.getEditorInput();

                    editorInput.setFile(result);
                    partPresenter.init(editorInput);

                } catch (EditorInitException event) {
                    Log.error(BranchPresenter.class, "can not initializes the editor with the given input " + event);
                }
            }

            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = (caught.getMessage() != null) ? caught.getMessage() : constant.getContentFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
            }
        });
    }

    /**
     * Get the list of branches.
     *
     * @param projectId
     *         project id
     */
    private void getBranches(@NotNull String projectId) {
        service.branchList(projectId, LIST_ALL,
                           new AsyncRequestCallback<Array<Branch>>(dtoUnmarshallerFactory.newArrayUnmarshaller(Branch.class)) {
                               @Override
                               protected void onSuccess(Array<Branch> result) {
                                   view.setBranches(result);
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   final String errorMessage =
                                           (exception.getMessage() != null) ? exception.getMessage() : constant.branchesListFailed();
                                   Notification notification = new Notification(errorMessage, ERROR);
                                   notificationManager.showNotification(notification);
                               }
                           }
                          );
    }

    /** {@inheritDoc} */
    @Override
    public void onCreateClicked() {
        String name = Window.prompt(constant.branchTypeNew(), "");
        if (!name.isEmpty()) {
            final String projectId = project.getId();

            service.branchCreate(projectId, name, null,
                                 new AsyncRequestCallback<Branch>(dtoUnmarshallerFactory.newUnmarshaller(Branch.class)) {
                                     @Override
                                     protected void onSuccess(Branch result) {
                                         getBranches(projectId);
                                     }

                                     @Override
                                     protected void onFailure(Throwable exception) {
                                         final String errorMessage = (exception.getMessage() != null) ? exception.getMessage()
                                                                                                      : constant.branchCreateFailed();
                                         Notification notification = new Notification(errorMessage, ERROR);
                                         notificationManager.showNotification(notification);
                                     }
                                 }
                                );
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onBranchSelected(@NotNull Branch branch) {
        selectedBranch = branch;
        boolean enabled = !selectedBranch.isActive();
        view.setEnableCheckoutButton(enabled);
        view.setEnableDeleteButton(true);
        view.setEnableRenameButton(true);
    }
}