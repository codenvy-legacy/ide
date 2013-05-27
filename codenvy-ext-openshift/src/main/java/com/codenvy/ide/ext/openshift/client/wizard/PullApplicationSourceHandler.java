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
package com.codenvy.ide.ext.openshift.client.wizard;

import com.codenvy.ide.ext.git.client.GitClientService;
import com.codenvy.ide.ext.git.client.GitExtension;
import com.codenvy.ide.ext.git.client.marshaller.BranchListUnmarshaller;
import com.codenvy.ide.ext.git.client.marshaller.RemoteListUnmarshaller;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.BranchListRequest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.js.JsoArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.resources.model.VirtualFileSystemInfo;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;

import java.util.ArrayList;
import java.util.List;

/**
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

    public void pullApplicationSources(String vfsId, Project project, GitClientService gitService,
                                       AsyncCallback<Boolean> callback) {
        this.project = project;
        this.callback = callback;
        this.vfsId = vfsId;
        this.gitService = gitService;

        getRemotes(project.getId());
    }

    public void onRemotesReceived(JsonArray<Remote> remotes) {
        this.remotes = remotes;
        getBranches(project.getId(), BranchListRequest.LIST_REMOTE);
    }

    protected void setLocalBranches(JsonArray<Branch> branches) {
        localBranches = branches;
        getBranches(project.getId(), BranchListRequest.LIST_LOCAL);
    }

    protected void setRemoteBranches(JsonArray<Branch> branches) {
        remoteBranches = branches;
        pullSources();
    }

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

    /**
     * Performs action when pull of Git-repository is successfully completed.
     *
     */
    private void onPullSuccess() {
        if (callback != null) {
            callback.onSuccess(true);
            callback = null;
        }
    }

    private void handleError(Throwable e) {
        if (callback != null) {
            callback.onSuccess(false);
            callback = null;
        }
    }

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

                                      }
                                  });
        } catch (RequestException e) {

        }
    }

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
                                      }
                                  });
        } catch (RequestException e) {

        }
    }

}
