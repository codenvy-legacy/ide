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
package com.codenvy.ide.ext.git.client.pull;

import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.dto.DtoFactory;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringUnmarshaller;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;

/**
 * Presenter pulling changes from remote repository.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 20, 2011 4:20:24 PM anya $
 */
@Singleton
public class PullPresenter implements PullView.ActionDelegate {
    private PullView                view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private GitLocalizationConstant constant;
    private NotificationManager     notificationManager;
    private Project                 project;
    private DtoFactory              dtoFactory;

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
    public PullPresenter(PullView view, GitClientService service, ResourceProvider resourceProvider,
                         GitLocalizationConstant constant, NotificationManager notificationManager, DtoFactory dtoFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.constant = constant;
        this.notificationManager = notificationManager;
        this.dtoFactory = dtoFactory;
    }

    /** Show dialog. */
    public void showDialog() {
        project = resourceProvider.getActiveProject();
        getRemotes();
    }

    /**
     * Get the list of remote repositories for local one. If remote repositories are found, then get the list of branches (remote and
     * local).
     */
    private void getRemotes() {
        final String projectId = project.getId();
        view.setEnablePullButton(true);

        try {
            service.remoteList(resourceProvider.getVfsInfo().getId(), projectId, null, true,
                               new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                   @Override
                                   protected void onSuccess(String result) {
                                       Array<Remote> remotes = dtoFactory.createListDtoFromJson(result, Remote.class);
                                       getBranches(projectId, LIST_REMOTE);
                                       view.setRepositories(remotes);
                                       view.setEnablePullButton(!remotes.isEmpty());
                                       view.showDialog();
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       String errorMessage =
                                                             exception.getMessage() != null ? exception.getMessage()
                                                                 : constant.remoteListFailed();
                                       Window.alert(errorMessage);
                                       view.setEnablePullButton(false);
                                   }
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteListFailed();
            Window.alert(errorMessage);
            view.setEnablePullButton(false);
        }
    }

    /**
     * Get the list of branches.
     * 
     * @param projectId Git repository work tree location
     * @param remoteMode is a remote mode
     */
    private void getBranches(@NotNull final String projectId, @NotNull final String remoteMode) {
        try {
            service.branchList(resourceProvider.getVfsInfo().getId(), projectId, remoteMode,
                               new AsyncRequestCallback<String>(new StringUnmarshaller()) {
                                   @Override
                                   protected void onSuccess(String result) {
                                       Array<Branch> branches = dtoFactory.createListDtoFromJson(result, Branch.class);
                                       if (LIST_REMOTE.equals(remoteMode)) {
                                           view.setRemoteBranches(getRemoteBranchesToDisplay(view.getRepositoryName(), branches));
                                           getBranches(projectId, LIST_LOCAL);
                                       } else {
                                           view.setLocalBranches(getLocalBranchesToDisplay(branches));
                                           for (Branch branch : branches.asIterable()) {
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
                                                             exception.getMessage() != null ? exception.getMessage()
                                                                 : constant.branchesListFailed();
                                       Notification notification = new Notification(errorMessage, ERROR);
                                       notificationManager.showNotification(notification);
                                       view.setEnablePullButton(false);
                                   }
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.branchesListFailed();
            Notification notification = new Notification(errorMessage, ERROR);
            notificationManager.showNotification(notification);
            view.setEnablePullButton(false);
        }
    }

    /**
     * Set values of remote branches: filter remote branches due to selected remote repository.
     * 
     * @param remoteName remote name
     * @param remoteBranches remote branches
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
     * @param localBranches local branches
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
    public void onPullClicked() {
        String remoteName = view.getRepositoryName();
        final String remoteUrl = view.getRepositoryUrl();

        try {
            service.pullWS(resourceProvider.getVfsInfo().getId(), project, getRefs(), remoteName, new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            Notification notification = new Notification(constant.pullSuccess(remoteUrl), INFO);
                            notificationManager.showNotification(notification);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(PullPresenter.class, "can not get project " + project.getName());
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception, remoteUrl);
                }
            });
        } catch (WebSocketException e) {
            doPullREST(remoteUrl, remoteName);
        }
        view.close();
    }

    /**
     * Perform pull from pointed by user remote repository, from pointed remote branch to local one. Local branch may not be pointed. Sends
     * request over HTTP.
     */
    private void doPullREST(@NotNull final String remoteUrl, @NotNull String remoteName) {
        try {
            service.pull(resourceProvider.getVfsInfo().getId(), project, getRefs(), remoteName, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    resourceProvider.getProject(project.getName(), new AsyncCallback<Project>() {
                        @Override
                        public void onSuccess(Project result) {
                            Notification notification = new Notification(constant.pullSuccess(remoteUrl), INFO);
                            notificationManager.showNotification(notification);
                        }

                        @Override
                        public void onFailure(Throwable caught) {
                            Log.error(PullPresenter.class, "can not get project " + project.getName());
                        }
                    });
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception, remoteUrl);
                }
            });
        } catch (RequestException e) {
            handleError(e, remoteUrl);
        }
    }

    /** @return list of refs to fetch */
    @NotNull
    private String getRefs() {
        String remoteName = view.getRepositoryName();
        String localBranch = view.getLocalBranch();
        String remoteBranch = view.getRemoteBranch();

        return localBranch.isEmpty() ? remoteBranch
            : "refs/heads/" + localBranch + ":" + "refs/remotes/" + remoteName + "/" + remoteBranch;
    }

    /**
     * Handler some action whether some exception happened.
     * 
     * @param t exception what happened
     */
    private void handleError(@NotNull Throwable t, @NotNull String remoteUrl) {
        String errorMessage = (t.getMessage() != null) ? t.getMessage() : constant.pullFail(remoteUrl);
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
    public void onRemoteBranchChanged() {
        view.selectLocalBranch(view.getRemoteBranch());
    }

}
