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
package com.codenvy.ide.ext.git.client.push;

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
 * Presenter for pushing changes to remote repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class PushToRemotePresenter implements PushToRemoteView.ActionDelegate {
    private final DtoUnmarshallerFactory  dtoUnmarshallerFactory;
    private       PushToRemoteView        view;
    private       GitServiceClient        service;
    private       ResourceProvider        resourceProvider;
    private       GitLocalizationConstant constant;
    private       NotificationManager     notificationManager;
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
    public PushToRemotePresenter(PushToRemoteView view, GitServiceClient service, ResourceProvider resourceProvider,
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
        getRemotes();
    }

    /**
     * Get the list of remote repositories for local one.
     * If remote repositories are found, then get the list of branches (remote and local).
     */
    private void getRemotes() {
        final String projectId = project.getId();

        service.remoteList(projectId, null, true,
                           new AsyncRequestCallback<Array<Remote>>(dtoUnmarshallerFactory.newArrayUnmarshaller(Remote.class)) {
                               @Override
                               protected void onSuccess(Array<Remote> result) {
                                   getBranches(projectId, LIST_LOCAL);
                                   view.setEnablePushButton(!result.isEmpty());
                                   view.setRepositories(result);
                                   view.showDialog();
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   final String errorMessage =
                                           exception.getMessage() != null ? exception.getMessage() : constant.remoteListFailed();
                                   Window.alert(errorMessage);
                                   view.setEnablePushButton(false);
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
                                   if(!LIST_REMOTE.equals(remoteMode)) {
                                       Array<String> localBranches = getLocalBranchesToDisplay(result);
                                       view.setLocalBranches(localBranches);

                                       for (Branch branch : result.asIterable()) {
                                            if (branch.isActive()) {
                                                view.selectLocalBranch(branch.getDisplayName());
                                                break;
                                            }
                                        }
                                       getBranches(projectId, LIST_REMOTE);
                                    } else {
                                       Array<String> remoteBranches = getRemoteBranchesToDisplay(view.getRepository(), result);
                                       // Need to add the current local branch in the list of remote branches
                                       // to be able to push changes to the remote branch  with same name
                                       String currentBranch = view.getLocalBranch();
                                           if (!remoteBranches.contains(currentBranch)) {
                                               remoteBranches.add(currentBranch);
                                           }
                                       view.setRemoteBranches(remoteBranches);
                                    }
                               }

                               @Override
                               protected void onFailure(Throwable exception) {
                                   String errorMessage =
                                           exception.getMessage() != null ? exception.getMessage()
                                                                          : constant.branchesListFailed();
                                   Notification notification = new Notification(errorMessage, ERROR);
                                   notificationManager.showNotification(notification);
                                   view.setEnablePushButton(false);
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
            if (branch.getName().startsWith(compareString)) {
                branches.add(branch.getName().replaceFirst(compareString, ""));
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
    public void onPushClicked() {
        final String repository = view.getRepository();

        try {
            service.push(project, getRefs(), repository, false, new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    Notification notification = new Notification(constant.pushSuccess(repository), INFO);
                    notificationManager.showNotification(notification);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                    if (repository.startsWith("https://")) {
                        Notification notification = new Notification(constant.useSshProtocol(), ERROR);
                        notificationManager.showNotification(notification);
                    }
                }
            });
        } catch (WebSocketException e) {
            handleError(e);
            if (repository.startsWith("https://")) {
                Notification notification = new Notification(constant.useSshProtocol(), ERROR);
                notificationManager.showNotification(notification);
            }
        }
        view.close();
    }

    /** @return list of refs to push */
    @NotNull
    private List<String> getRefs() {
        String localBranch = "refs/heads/" + view.getLocalBranch();
        String remoteBranch = "refs/heads/" + view.getRemoteBranch();
        return new ArrayList<String>(Arrays.asList(localBranch + ":" + remoteBranch));
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param t
     *         exception what happened
     */
    private void handleError(@NotNull Throwable t) {
        String errorMessage = t.getMessage() != null ? t.getMessage() : constant.pushFail();
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
    public void onLocalBranchChanged() {
        view.selectRemoteBranch(view.getLocalBranch());
    }
}
