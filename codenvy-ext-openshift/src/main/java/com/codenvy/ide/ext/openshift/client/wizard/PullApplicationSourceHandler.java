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
package com.codenvy.ide.ext.openshift.client.wizard;

import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.marshaller.BranchListUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RemoteListUnmarshaller;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.BranchListRequest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Handler which is executed after creating application on OpenShift to pull sources into local empty project.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class PullApplicationSourceHandler {

    private AsyncCallback<Boolean> callback;

    private Project project;

    private JsonArray<Remote> remotes;

    private JsonArray<Branch> remoteBranches;

    private JsonArray<Branch> localBranches;

    private String vfsId;

    private GitClientService gitService;

    /**
     * Create handler.
     *
     * @param vfsId
     * @param project
     * @param gitService
     * @param callback
     */
    public void pullApplicationSources(String vfsId, Project project, GitClientService gitService,
                                       AsyncCallback<Boolean> callback) {
        this.project = project;
        this.callback = callback;
        this.vfsId = vfsId;
        this.gitService = gitService;

        getRemotes(project.getId());
    }

    /**
     * Callback method that called when remotes are received.
     *
     * @param remotes
     *         list of remotes
     */
    public void onRemotesReceived(JsonArray<Remote> remotes) {
        this.remotes = remotes;
        getBranches(project.getId(), BranchListRequest.LIST_REMOTE);
    }

    /**
     * Sets list of local branches.
     *
     * @param branches
     *         list of branches
     */
    protected void setLocalBranches(JsonArray<Branch> branches) {
        localBranches = branches;
        getBranches(project.getId(), BranchListRequest.LIST_LOCAL);
    }

    /**
     * Sets list of remote branches.
     *
     * @param branches
     *         list of branches
     */
    protected void setRemoteBranches(JsonArray<Branch> branches) {
        remoteBranches = branches;
        pullSources();
    }

    /** Start pulling sources over websockets. */
    private void pullSources() {
        String remoteName = remotes.get(0).getName();
        final String remoteUrl = remotes.get(0).getUrl();

        String localBranch = localBranches != null && !localBranches.isEmpty() ? localBranches.get(0).getDisplayName() : "master";

        String remoteBranch = remoteBranches != null && !remoteBranches.isEmpty() ? remoteBranches.get(0).getDisplayName() : localBranch;

        String refs = (localBranch == null || localBranch.length() == 0) ? remoteBranch : "refs/heads/" + remoteBranch + ":"
                                                                                          + "refs/remotes/" + remoteName + "/";
        try {
            gitService.pullWS(vfsId, project, refs, remoteName, new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    onPullSuccess();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
        } catch (WebSocketException e) {
            pullSourcesREST(refs, remoteName, remoteUrl);
        }
    }

    /**
     * Starts pulling sources over Rest.
     *
     * @param refs
     *         remotes list
     * @param remoteName
     *         remotes name
     * @param remoteUrl
     *         remotes url
     */
    private void pullSourcesREST(String refs, String remoteName, final String remoteUrl) {
        try {
            gitService.pull(vfsId, project, refs, remoteName, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    onPullSuccess();
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception);
                }
            });
        } catch (RequestException e) {
            handleError(e);
        }
    }

    /** Performs action when pull of Git-repository is successfully completed. */
    private void onPullSuccess() {
        if (callback != null) {
            callback.onSuccess(true);
            callback = null;
        }
    }

    /** Performs action when pull of Git-repository is failed to complete. */
    private void handleError(Throwable e) {
        if (callback != null) {
            callback.onSuccess(false);
            callback = null;
        }
    }

    /**
     * Get list of branches based on local repository.
     *
     * @param projectId
     *         id of project from which branches will be gotten
     * @param remoteMode
     *         remote mode
     */
    public void getBranches(String projectId, final String remoteMode) {
        try {
            gitService.branchList(vfsId, projectId, remoteMode,
                                  new AsyncRequestCallback<JsonArray<Branch>>(
                                          new BranchListUnmarshaller(JsoArray.<Branch>create())) {

                                      @Override
                                      protected void onSuccess(JsonArray<Branch> result) {
                                          if (remoteMode.equals(BranchListRequest.LIST_REMOTE)) {
                                              remoteBranches = result;
                                              setRemoteBranches(remoteBranches);
                                              return;
                                          }

                                          setLocalBranches(result);
                                      }

                                      @Override
                                      protected void onFailure(Throwable exception) {
                                          handleError(exception);
                                      }
                                  });
        } catch (RequestException e) {
            handleError(e);
        }
    }

    /**
     * Get remotes based on local repository.
     *
     * @param projectId
     *         id of project from which remotes will be gotten
     */
    public void getRemotes(String projectId) {
        try {
            gitService.remoteList(vfsId, projectId, null, true,
                                  new AsyncRequestCallback<JsonArray<Remote>>(
                                          new RemoteListUnmarshaller(JsoArray.<Remote>create())) {
                                      @Override
                                      protected void onSuccess(JsonArray<Remote> result) {
                                          if (result.size() == 0) {
                                              return;
                                          }

                                          onRemotesReceived(result);
                                      }

                                      @Override
                                      protected void onFailure(Throwable exception) {
                                          handleError(exception);
                                      }
                                  });
        } catch (RequestException e) {
            handleError(e);
        }
    }

}
