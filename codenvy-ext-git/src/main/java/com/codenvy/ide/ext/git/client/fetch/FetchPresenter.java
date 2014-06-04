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
package com.codenvy.ide.ext.git.client.fetch;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;

/**
 * Presenter for fetching changes from remote repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class FetchPresenter implements FetchView.ActionDelegate {
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       FetchView               view;
    private       GitServiceClient        service;
    private       ResourceProvider        resourceProvider;
    private       GitLocalizationConstant constant;
    private       Project                 project;
    private       NotificationManager     notificationManager;

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
    public FetchPresenter(FetchView view, GitServiceClient service, ResourceProvider resourceProvider,
                          GitLocalizationConstant constant, NotificationManager notificationManager,
                          DtoUnmarshallerFactory dtoUnmarshallerFactory) {
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
        view.setRemoveDeleteRefs(false);
        view.setFetchAllBranches(true);
        getRemotes();
    }

    /**
     * Get the list of remote repositories for local one. If remote repositories are found, then get the list of branches (remote and
     * local).
     */
    private void getRemotes() {
        final String projectId = project.getId();
        service.remoteList(projectId, null, true,
                           new AsyncRequestCallback<Array<Remote>>(dtoUnmarshallerFactory.newArrayUnmarshaller(Remote.class)) {
                               @Override
                               protected void onSuccess(Array<Remote> result) {
                                   view.setRepositories(result);
                                   getBranches(projectId, LIST_REMOTE);
                                   view.setEnableFetchButton(!result.isEmpty());
                                   view.showDialog();
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   String errorMessage =
                                           exception.getMessage() != null ? exception.getMessage() : constant.remoteListFailed();
                                   Window.alert(errorMessage);
                                   view.setEnableFetchButton(false);
                               }
                           });
    }

    /**
     * Get the list of branches.
     *
     * @param projectId
     *         Git repository work tree location
     * @param remoteMode
     *         is a remote mode
     */
    private void getBranches(@NotNull final String projectId, @NotNull final String remoteMode) {
        service.branchList(projectId, remoteMode,
                           new AsyncRequestCallback<Array<Branch>>(dtoUnmarshallerFactory.newArrayUnmarshaller(Branch.class)) {
                               @Override
                               protected void onSuccess(Array<Branch> result) {
                                   if (LIST_REMOTE.equals(remoteMode)) {
                                       view.setRemoteBranches(getRemoteBranchesToDisplay(view.getRepositoryName(), result));
                                       getBranches(projectId, LIST_LOCAL);
                                   } else {
                                       view.setLocalBranches(getLocalBranchesToDisplay(result));
                                       for (Branch branch : result.asIterable()) {
                                           if (branch.isActive()) {
                                               view.selectRemoteBranch(branch.getDisplayName());
                                               break;
                                           }
                                       }
                                   }
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   String errorMessage =
                                           exception.getMessage() != null ? exception.getMessage() : constant.branchesListFailed();
                                   Notification notification = new Notification(errorMessage, ERROR);
                                   notificationManager.showNotification(notification);
                                   view.setEnableFetchButton(false);
                               }
                           });
    }

    /**
     * Set values of remote branches: filter remote branches due to selected remote repository.
     *
     * @param remoteName
     *         remote name
     * @param remoteBranches
     *         remote branches
     */
    @NotNull
    private Array<String> getRemoteBranchesToDisplay(@NotNull String remoteName, @NotNull Array<Branch> remoteBranches) {
        Array<String> branches = Collections.createArray();

        if (remoteBranches.isEmpty()) {
            branches.add("master");
            return branches;
        }

        String compareString = "refs/remotes/" + remoteName + "/";
        for (int i = 0; i < remoteBranches.size(); i++) {
            Branch branch = remoteBranches.get(i);
            String branchName = branch.getName();
            if (branchName.startsWith(compareString)) {
                branches.add(branchName.replaceFirst(compareString, ""));
            }
        }

        if (branches.isEmpty()) {
            branches.add("master");
        }
        return branches;
    }

    /**
     * Set values of local branches.
     *
     * @param localBranches
     *         local branches
     */
    @NotNull
    private Array<String> getLocalBranchesToDisplay(@NotNull Array<Branch> localBranches) {
        Array<String> branches = Collections.createArray();

        if (localBranches.isEmpty()) {
            branches.add("master");
            return branches;
        }

        for (Branch branch : localBranches.asIterable()) {
            branches.add(branch.getDisplayName());
        }

        return branches;
    }

    /** {@inheritDoc} */
    @Override
    public void onFetchClicked() {
        final String remoteUrl = view.getRepositoryUrl();
        String remoteName = view.getRepositoryName();
        boolean removeDeletedRefs = view.isRemoveDeletedRefs();

        try {
            service.fetch(project, remoteName, getRefs(), removeDeletedRefs,
                          new RequestCallback<String>() {
                              @Override
                              protected void onSuccess(String result) {
                                  Notification notification = new Notification(constant.fetchSuccess(remoteUrl), INFO);
                                  notificationManager.showNotification(notification);
                              }

                              @Override
                              protected void onFailure(Throwable exception) {
                                  handleError(exception, remoteUrl);
                              }
                          }
                         );
        } catch (WebSocketException e) {
            handleError(e, remoteUrl);
        }
        view.close();
    }

    /** @return list of refs to fetch */
    @NotNull
    private List<String> getRefs() {
        if (view.isFetchAllBranches()) {
            return new ArrayList<String>();
        }

        String localBranch = view.getLocalBranch();
        String remoteBranch = view.getRemoteBranch();
        String remoteName = view.getRepositoryName();
        String refs = localBranch.isEmpty() ? remoteBranch
                                            : "refs/heads/" + localBranch + ":" + "refs/remotes/" + remoteName + "/" + remoteBranch;
        return new ArrayList<String>(Arrays.asList(refs));
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param t
     *         exception what happened
     */
    private void handleError(@NotNull Throwable t, @NotNull String remoteUrl) {
        String errorMessage = (t.getMessage() != null) ? t.getMessage() : constant.fetchFail(remoteUrl);
        Notification notification = new Notification(errorMessage, ERROR);
        notificationManager.showNotification(notification);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onValueChanged() {
        boolean isFetchAll = view.isFetchAllBranches();
        view.setEnableLocalBranchField(!isFetchAll);
        view.setEnableRemoteBranchField(!isFetchAll);
    }

    /** {@inheritDoc} */
    @Override
    public void onRemoteBranchChanged() {
        view.selectLocalBranch(view.getRemoteBranch());
    }
}