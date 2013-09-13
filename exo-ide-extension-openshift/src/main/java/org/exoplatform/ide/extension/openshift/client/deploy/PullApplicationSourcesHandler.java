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
package org.exoplatform.ide.extension.openshift.client.deploy;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.job.JobManager;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.remote.HasBranchesPresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.util.List;

/**
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Guluy</a>
 * @version $
 */
public class PullApplicationSourcesHandler extends HasBranchesPresenter {

    private PullCompleteCallback callback;

    private ProjectModel         project;

    private List<Remote>         remotes;

    private List<Branch>         remoteBranches;

    private List<Branch>         localBranches;

    public void pullApplicationSources(VirtualFileSystemInfo vfsInfo, ProjectModel project, PullCompleteCallback callback) {
        this.project = project;
        this.callback = callback;
        vfs = vfsInfo;

        getRemotes(project.getId());
    }

    @Override
    public void onRemotesReceived(List<Remote> remotes) {
        this.remotes = remotes;
        getBranches(project.getId(), BranchListRequest.LIST_REMOTE);
    }

    @Override
    protected void setLocalBranches(List<Branch> branches) {
        localBranches = branches;
        getBranches(project.getId(), BranchListRequest.LIST_LOCAL);
    }

    @Override
    protected void setRemoteBranches(List<Branch> branches) {
        remoteBranches = branches;
        pullSources();
    }

    private void pullSources() {
        String remoteName = remotes.get(0).getName();
        final String remoteUrl = remotes.get(0).getUrl();

        String localBranch = localBranches != null && !localBranches.isEmpty() ? localBranches.get(0).getDisplayName() : "master";

        String remoteBranch = remoteBranches != null && !remoteBranches.isEmpty() ? remoteBranches.get(0).getDisplayName() : localBranch;

        String refs = (localBranch == null || localBranch.length() == 0) ? remoteBranch : "refs/heads/" + remoteBranch + ":"
                                                                                          + "refs/remotes/" + remoteName + "/"
                                                                                          + remoteBranch;

        JobManager.get().showJobSeparated();

        try {
            GitClientService.getInstance().pullWS(vfs.getId(), project, refs, remoteName, new RequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    onPullSuccess(remoteUrl);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    handleError(exception, remoteUrl);
                }
            });
        } catch (WebSocketException e) {
            pullSourcesREST(refs, remoteName, remoteUrl);
        }
    }

    private void pullSourcesREST(String refs, String remoteName, final String remoteUrl) {
        try {
            GitClientService.getInstance().pull(vfs.getId(), project, refs, remoteName, new AsyncRequestCallback<String>() {
                @Override
                protected void onSuccess(String result) {
                    onPullSuccess(remoteUrl);
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

    /**
     * Performs action when pull of Git-repository is successfully completed.
     * 
     * @param remoteUrl URL of Git-repository
     */
    private void onPullSuccess(String remoteUrl) {
        IDE.fireEvent(new OutputEvent(GitExtension.MESSAGES.pullSuccess(remoteUrl), Type.GIT));
        IDE.fireEvent(new RefreshBrowserEvent());

        if (callback != null) {
            callback.onPullComplete(true);
            callback = null;
        }
    }

    private void handleError(Throwable e, String remoteGitUrl) {
        e.printStackTrace();

        if (callback != null) {
            callback.onPullComplete(false);
            callback = null;
        }
    }

}
