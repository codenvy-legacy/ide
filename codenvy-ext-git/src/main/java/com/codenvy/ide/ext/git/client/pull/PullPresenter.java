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
package com.codenvy.ide.ext.git.client.pull;

import com.codenvy.ide.api.editor.EditorAgent;
import com.codenvy.ide.api.editor.EditorInitException;
import com.codenvy.ide.api.editor.EditorInput;
import com.codenvy.ide.api.editor.EditorPartPresenter;
import com.codenvy.ide.api.notification.Notification;
import com.codenvy.ide.api.notification.NotificationManager;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.api.resources.model.File;
import com.codenvy.ide.api.resources.model.Resource;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.codenvy.ide.ext.git.client.GitServiceClient;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.DtoUnmarshallerFactory;
import com.codenvy.ide.util.loging.Log;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.codenvy.ide.api.notification.Notification.Type.ERROR;
import static com.codenvy.ide.api.notification.Notification.Type.INFO;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;

/**
 * Presenter pulling changes from remote repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 */
@Singleton
public class PullPresenter implements PullView.ActionDelegate {
    private       PullView                view;
    private       GitServiceClient        service;
    private       Project                 project;
    private       GitLocalizationConstant constant;
    private       EditorAgent             editorAgent;
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
    public PullPresenter(PullView view,
                         EditorAgent editorAgent,
                         GitServiceClient service,
                         ResourceProvider resourceProvider,
                         GitLocalizationConstant constant,
                         NotificationManager notificationManager,
                         DtoUnmarshallerFactory dtoUnmarshallerFactory) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.constant = constant;
        this.editorAgent = editorAgent;
        this.resourceProvider = resourceProvider;
        this.notificationManager = notificationManager;
        this.dtoUnmarshallerFactory = dtoUnmarshallerFactory;
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

        service.remoteList(projectId, null, true,
                           new AsyncRequestCallback<Array<Remote>>(dtoUnmarshallerFactory.newArrayUnmarshaller(Remote.class)) {
                               @Override
                               protected void onSuccess(Array<Remote> result) {
                                   getBranches(projectId, LIST_REMOTE);
                                   view.setRepositories(result);
                                   view.setEnablePullButton(!result.isEmpty());
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
                                           exception.getMessage() != null ? exception.getMessage()
                                                                          : constant.branchesListFailed();
                                   Notification notification = new Notification(errorMessage, ERROR);
                                   notificationManager.showNotification(notification);
                                   view.setEnablePullButton(false);
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
    public void onPullClicked() {
        String remoteName = view.getRepositoryName();
        final String remoteUrl = view.getRepositoryUrl();
        view.close();

        final List<EditorPartPresenter> openedEditors = new ArrayList<>();
        for (EditorPartPresenter partPresenter : editorAgent.getOpenedEditors().getValues().asIterable()) {
            openedEditors.add(partPresenter);
        }

        try {
            service.pull(project, getRefs(), remoteName, new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    Notification notification = new Notification(constant.pullSuccess(remoteUrl), INFO);
                    notificationManager.showNotification(notification);
                    refreshProject(openedEditors);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception, remoteUrl);
                }
            });
        } catch (WebSocketException e) {
            handleError(e, remoteUrl);
        }
    }
    /**
     * Refresh project.
     *
     * @param openedEditors
     *         editors that corresponds to open files
     */
    private void refreshProject(final List<EditorPartPresenter> openedEditors) {
        project.refreshChildren(new AsyncCallback<Project>() {
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
     *        editor that corresponds to the <code>file</code>.
     */
    private void refreshFile(final File file, final EditorPartPresenter partPresenter) {
        project.findResourceByPath(file.getPath(), new AsyncCallback<Resource>() {
            @Override
            public void onFailure(Throwable caught) {
                String errorMessage = (caught.getMessage() != null) ? caught.getMessage() : constant.findResourceFailed();
                Notification notification = new Notification(errorMessage, ERROR);
                notificationManager.showNotification(notification);
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
     *        editor that corresponds to the <code>file</code>.
     */
    private void updateOpenedFile(final File file, final EditorPartPresenter partPresenter) {
        project.getContent(file, new AsyncCallback<File>() {
            @Override
            public void onSuccess(File result) {
                try {
                    EditorInput editorInput = partPresenter.getEditorInput();

                    editorInput.setFile(result);
                    partPresenter.init(editorInput);

                } catch (EditorInitException event) {
                    Log.error(PullPresenter.class, "can not initializes the editor with the given input " + event);
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
     * @param t
     *         exception what happened
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
