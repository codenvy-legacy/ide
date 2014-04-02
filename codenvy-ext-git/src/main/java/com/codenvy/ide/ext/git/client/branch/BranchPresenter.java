/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.ext.git.client.branch;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_ALL;

/**
 * Presenter for displaying and work with branches.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class BranchPresenter implements BranchView.ActionDelegate {
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       BranchView              view;
    private       GitServiceClient        service;
    private       ResourceProvider        resourceProvider;
    private       GitLocalizationConstant constant;
    private       NotificationManager     notificationManager;
    private       Branch                  selectedBranch;
    private       Project                 project;

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
    public BranchPresenter(BranchView view, GitServiceClient service, ResourceProvider resourceProvider, GitLocalizationConstant constant,
                           NotificationManager notificationManager, DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
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
            service.branchRename(projectId, currentBranchName, name,
                                 new AsyncRequestCallback<String>() {
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
        boolean needToDelete = Window.confirm(constant.branchDeleteAsk(name));
        if (needToDelete) {
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
    }

    /** {@inheritDoc} */
    @Override
    public void onCheckoutClicked() {
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

        service.branchCheckout(projectId, name, startingPoint, remote,
                               new AsyncRequestCallback<String>() {
                                   @Override
                                   protected void onSuccess(String result) {
                                       resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                                           @Override
                                           public void onSuccess(Project result) {
                                               getBranches(projectId);
                                           }

                                           @Override
                                           public void onFailure(Throwable caught) {
                                               Log.error(BranchPresenter.class, "can not get project " + project.getName());
                                           }
                                       });
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
                           });
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
                                 });
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