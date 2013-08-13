/*
 * Copyright (C) 2013 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.git.client.push;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.api.parts.ConsolePart;
import com.codenvy.ide.api.resources.ResourceProvider;
import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitLocalizationConstant;
import com.codenvy.ide.ext.git.client.marshaller.BranchListUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RemoteListUnmarshaller;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_LOCAL;
import static com.codenvy.ide.ext.git.shared.BranchListRequest.LIST_REMOTE;

/**
 * Presenter for pushing changes to remote repository.
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 4, 2011 9:53:07 AM anya $
 */
@Singleton
public class PushToRemotePresenter implements PushToRemoteView.ActionDelegate {
    private PushToRemoteView        view;
    private GitClientService        service;
    private ResourceProvider        resourceProvider;
    private ConsolePart             console;
    private GitLocalizationConstant constant;
    private Project                 project;

    /**
     * Create presenter.
     *
     * @param view
     * @param service
     * @param resourceProvider
     * @param console
     * @param constant
     */
    @Inject
    public PushToRemotePresenter(PushToRemoteView view, GitClientService service, ResourceProvider resourceProvider, ConsolePart console,
                                 GitLocalizationConstant constant) {
        this.view = view;
        this.view.setDelegate(this);
        this.service = service;
        this.resourceProvider = resourceProvider;
        this.console = console;
        this.constant = constant;
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
        RemoteListUnmarshaller unmarshaller = new RemoteListUnmarshaller(JsonCollections.<Remote>createArray());
        final String projectId = project.getId();

        try {
            service.remoteList(resourceProvider.getVfsId(), projectId, null, true,
                               new AsyncRequestCallback<JsonArray<Remote>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Remote> result) {
                                       getBranches(projectId, LIST_REMOTE);
                                       getBranches(projectId, LIST_LOCAL);
                                       view.setEnablePushButton(!result.isEmpty());
                                       view.setRepositories(result);
                                       view.showDialog();
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       String errorMessage =
                                               exception.getMessage() != null ? exception.getMessage() : constant.remoteListFailed();
                                       Window.alert(errorMessage);
                                   }
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.remoteListFailed();
            Window.alert(errorMessage);
        }
    }

    /**
     * Get the list of branches.
     *
     * @param projectId
     *         Git repository work tree location
     * @param remoteMode
     *         is a remote mode
     */
    private void getBranches(@NotNull String projectId, @NotNull final String remoteMode) {
        BranchListUnmarshaller unmarshaller = new BranchListUnmarshaller(JsonCollections.<Branch>createArray());
        try {
            service.branchList(resourceProvider.getVfsId(), projectId, remoteMode,
                               new AsyncRequestCallback<JsonArray<Branch>>(unmarshaller) {
                                   @Override
                                   protected void onSuccess(JsonArray<Branch> result) {
                                       if (LIST_REMOTE.equals(remoteMode)) {
                                           view.setRemoteBranches(getRemoteBranchesToDisplay(remoteMode, result));
                                       } else {
                                           view.setLocalBranches(getLocalBranchesToDisplay(result));
                                       }
                                   }

                                   @Override
                                   protected void onFailure(Throwable exception) {
                                       String errorMessage =
                                               exception.getMessage() != null ? exception.getMessage() : constant.branchesListFailed();
                                       console.print(errorMessage);
                                       view.setEnablePushButton(false);
                                   }
                               });
        } catch (RequestException e) {
            String errorMessage = e.getMessage() != null ? e.getMessage() : constant.branchesListFailed();
            console.print(errorMessage);
            view.setEnablePushButton(false);
        }
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
    private JsonArray<String> getRemoteBranchesToDisplay(@NotNull String remoteName, @NotNull JsonArray<Branch> remoteBranches) {
        JsonArray<String> branches = JsonCollections.createArray();

        if (remoteBranches.isEmpty()) {
            branches.add("master");
            return branches;
        }

        String compareString = "refs/remotes/" + remoteName + "/";
        for (int i = 0; i < remoteBranches.size(); i++) {
            Branch branch = remoteBranches.get(i);
            if (branch.getName().startsWith(compareString)) {
                branches.add(branch.getName().replaceFirst(compareString, "refs/heads/"));
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
    private JsonArray<String> getLocalBranchesToDisplay(@NotNull JsonArray<Branch> localBranches) {
        JsonArray<String> branches = JsonCollections.createArray();

        if (localBranches.isEmpty()) {
            branches.add("master");
            return branches;
        }

        for (int i = 0; i < localBranches.size(); i++) {
            Branch branch = localBranches.get(i);
            branches.add(branch.getName());
        }

        return branches;
    }

    /** {@inheritDoc} */
    @Override
    public void onPushClicked() {
        final String repository = view.getRepository();

        try {
            service.pushWS(resourceProvider.getVfsId(), project, getRefs(), repository, false, new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    console.print(constant.pushSuccess(repository));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                    if (repository.startsWith("https://")) {
                        console.print(constant.useSshProtocol());
                    }
                }
            });
        } catch (WebSocketException e) {
            doPushREST(repository);
        }
        view.close();
    }

    /** Push changes to remote repository (sends request over HTTP). */
    private void doPushREST(@NotNull final String repository) {
        try {
            service.push(resourceProvider.getVfsId(), project, getRefs(), repository, false, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    console.print(constant.pushSuccess(repository));
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                    if (repository.startsWith("https://")) {
                        console.print(constant.useSshProtocol());
                    }
                }
            });
        } catch (RequestException e) {
            handleError(e);
        }
    }

    /** @return list of refs to push */
    @NotNull
    private JsonArray<String> getRefs() {
        String localBranch = view.getLocalBranch();
        String remoteBranch = view.getRemoteBranch();
        JsoArray<String> array = JsoArray.create();
        array.add(localBranch + ":" + remoteBranch);
        return array;
    }

    /**
     * Handler some action whether some exception happened.
     *
     * @param t
     *         exception what happened
     */
    private void handleError(@NotNull Throwable t) {
        String errorMessage = t.getMessage() != null ? t.getMessage() : constant.pushFail();
        console.print(errorMessage);
    }

    /** {@inheritDoc} */
    @Override
    public void onCancelClicked() {
        view.close();
    }
}