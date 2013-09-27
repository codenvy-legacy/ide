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
package org.exoplatform.ide.git.client.remote;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.output.event.OutputEvent;
import org.exoplatform.ide.client.framework.output.event.OutputMessage.Type;
import org.exoplatform.ide.git.client.GitClientService;
import org.exoplatform.ide.git.client.GitExtension;
import org.exoplatform.ide.git.client.GitPresenter;
import org.exoplatform.ide.git.client.fetch.FetchPresenter;
import org.exoplatform.ide.git.client.marshaller.BranchListUnmarshaller;
import org.exoplatform.ide.git.client.marshaller.RemoteListUnmarshaller;
import org.exoplatform.ide.git.client.pull.PullPresenter;
import org.exoplatform.ide.git.client.push.PushToRemotePresenter;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.BranchListRequest;
import org.exoplatform.ide.git.shared.Remote;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract presenter for displays, which has to show and work with branches ({@link FetchPresenter}, {@link PullPresenter},
 * {@link PushToRemotePresenter}) for not to duplicate same methods.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Apr 21, 2011 10:55:28 AM anya $
 */
public abstract class HasBranchesPresenter extends GitPresenter {
    /** The list of remote branches. */
    protected List<Branch> remoteBranches;

    /**
     * Get the list of remote repositories for local one. If remote repositories are found, then method {@link #onRemotesReceived(List)} is
     * called.
     */
    public void getRemotes(String projectId) {
        try {
            GitClientService.getInstance()
                            .remoteList(vfs.getId(), projectId, null, true,
                                        new AsyncRequestCallback<List<Remote>>(
                                                                               new RemoteListUnmarshaller(new ArrayList<Remote>())) {
                                            @Override
                                            protected void onSuccess(List<Remote> result) {
                                                if (result.size() == 0) {
                                                    Dialogs.getInstance().showError(GitExtension.MESSAGES.remoteListFailed());
                                                    return;
                                                }

                                                onRemotesReceived(result);
                                            }

                                            @Override
                                            protected void onFailure(Throwable exception) {
                                                String errorMessage =
                                                                      (exception.getMessage() != null) ? exception.getMessage()
                                                                          : GitExtension.MESSAGES
                                                                                                 .remoteListFailed();
                                                Dialogs.getInstance().showError(errorMessage);
                                            }
                                        });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.remoteListFailed();
            Dialogs.getInstance().showError(errorMessage);
        }
    }

    public abstract void onRemotesReceived(List<Remote> remotes);

    /**
     * Get the list of branches.
     * 
     * @param workDir Git repository work tree location
     * @param remoteMode is a remote mode
     */
    public void getBranches(String projectId, final String remoteMode) {
        try {
            GitClientService.getInstance()
                            .branchList(vfs.getId(), projectId, remoteMode,
                                        new AsyncRequestCallback<List<Branch>>(
                                                                               new BranchListUnmarshaller(new ArrayList<Branch>())) {

                                            @Override
                                            protected void onSuccess(List<Branch> result) {
                                                if (remoteMode.equals(BranchListRequest.LIST_REMOTE)) {
                                                    remoteBranches = result;
                                                    setRemoteBranches(remoteBranches);
                                                    return;
                                                }

                                                setLocalBranches(result);
                                            }

                                            @Override
                                            protected void onFailure(Throwable exception) {
                                                String errorMessage =
                                                                      (exception.getMessage() != null) ? exception.getMessage()
                                                                          : GitExtension.MESSAGES
                                                                                                 .branchesListFailed();
                                                IDE.fireEvent(new OutputEvent(errorMessage, Type.GIT));
                                            }
                                        });
        } catch (RequestException e) {
            String errorMessage = (e.getMessage() != null) ? e.getMessage() : GitExtension.MESSAGES.branchesListFailed();
            IDE.fireEvent(new OutputEvent(errorMessage, Type.ERROR));
        }
    }

    /**
     * Set remote branches to display the values.
     * 
     * @param branches remote branches
     */
    protected abstract void setRemoteBranches(List<Branch> branches);

    /**
     * Set local branches to display the values.
     * 
     * @param branches local branches
     */
    protected abstract void setLocalBranches(List<Branch> branches);

    /**
     * Set values of remote branches: filter remote branches due to selected remote repository.
     * 
     * @param remoteName
     */
    protected String[] getRemoteBranchesToDisplay(String remoteName) {
        List<String> branchesToDisplay = new ArrayList<String>();
        if (remoteBranches == null || remoteBranches.size() <= 0 || remoteName == null) {
            branchesToDisplay.add("master");
            return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
        }

        String compareString = "refs/remotes/" + remoteName + "/";
        for (Branch branch : remoteBranches) {
            if (branch.getName().startsWith(compareString)) {
                branchesToDisplay.add(branch.getName().replaceFirst(compareString, "refs/heads/"));
            }
        }
        if (branchesToDisplay.size() <= 0) {
            branchesToDisplay.add("master");
        }
        return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
    }

    /**
     * Set values of remote branches (show only simple name): filter remote branches due to selected remote repository.
     * 
     * @param remoteName
     */
    protected String[] getRemoteBranchesNamesToDisplay(String remoteName) {
        List<String> branchesToDisplay = new ArrayList<String>();
        if (remoteBranches == null || remoteBranches.size() <= 0 || remoteName == null) {
            branchesToDisplay.add("master");
            return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
        }
        String compareString = "refs/remotes/" + remoteName + "/";
        for (Branch branch : remoteBranches) {
            if (branch.getName().startsWith(compareString)) {
                branchesToDisplay.add(branch.getName().replaceFirst(compareString, ""));
            }
        }
        if (branchesToDisplay.size() <= 0) {
            branchesToDisplay.add("master");
        }
        return branchesToDisplay.toArray(new String[branchesToDisplay.size()]);
    }
}
