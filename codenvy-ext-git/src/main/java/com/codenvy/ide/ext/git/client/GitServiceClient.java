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
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.Commiters;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.GitUrlVendorInfo;
import com.codenvy.ide.ext.git.shared.LogResponse;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.ext.git.shared.RepoInfo;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.api.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Service contains methods for working with Git repository from client side. Example usage, initialize Git repository: <br>
 * <code>
 * GitServiceClient.getInstance().init(workDir, bare, callback);
 * <code>
 *
 * @author Ann Zhuleva
 */
public interface GitServiceClient {

    /**
     * Add changes to Git index (temporary storage). Sends request over WebSocket.
     *
     * @param project
     *         project (root of GIT repository)
     * @param update
     *         if <code>true</code> then never stage new files, but stage modified new contents of tracked files and remove files from
     *         the index if the corresponding files in the working tree have been removed
     * @param filePattern
     *         pattern of the files to be added, default is "." (all files are added)
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    void add(@NotNull Project project, boolean update, @Nullable List<String> filePattern,
             @NotNull RequestCallback<Void> callback) throws WebSocketException;

    /**
     * Fetch changes from remote repository to local one (sends request over WebSocket).
     *
     * @param project
     *         project root of GIT repository
     * @param remote
     *         remote repository's name
     * @param refspec
     *         list of refspec to fetch.
     *         <p/>
     *         Expected form is:
     *         <ul>
     *         <li>refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from remote repository will be fetched to
     *         'refs/remotes/origin/featured'.</li>
     *         <li>featured - remote branch name.</li>
     *         </ul>
     * @param removeDeletedRefs
     *         if <code>true</code> then delete removed refs from local repository
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    void fetch(@NotNull Project project, @NotNull String remote, List<String> refspec,
               boolean removeDeletedRefs, @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Get the list of the branches. For now, all branches cannot be returned at once, so the parameter <code>remote</code> tells to get
     * remote branches if <code>true</code> or local ones (if <code>false</code>).
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param mode
     *         get remote branches
     * @param callback
     */
    void branchList(@NotNull String projectid, @Nullable String mode,
                    @NotNull AsyncRequestCallback<Array<Branch>> callback);

    /**
     * Delete branch.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         name of the branch to delete
     * @param force
     *         force if <code>true</code> delete branch {@link #name} even if it is not fully merged
     * @param callback
     */
    void branchDelete(@NotNull String projectid, @NotNull String name, boolean force,
                      @NotNull AsyncRequestCallback<String> callback);

    /**
     * Checkout the branch with pointed name.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param oldName
     *         branch's current name
     * @param newName
     *         branch's new name
     * @param callback
     */
    void branchRename(@NotNull String projectid, @NotNull String oldName, @NotNull String newName,
                      @NotNull AsyncRequestCallback<String> callback);

    /**
     * Create new branch with pointed name.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         new branch's name
     * @param startPoint
     *         name of a commit at which to start the new branch
     * @param callback
     */
    void branchCreate(@NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                      @NotNull AsyncRequestCallback<Branch> callback);

    /**
     * Checkout the branch with pointed name.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         branch's name
     * @param startPoint
     *         if {@link #createNew} is <code>true</code> then the name of a commit at which to start the new branch
     * @param createNew
     *         if <code>true</code> then create a new branch
     * @param callback
     */
    void branchCheckout(@NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                        boolean createNew, @NotNull AsyncRequestCallback<String> callback);

    /**
     * Get the list of remote repositories for pointed by <code>workDir</code> parameter one.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param remoteName
     *         remote repository's name
     * @param verbose
     *         If <code>true</code> show remote url and name otherwise show remote name
     * @param callback
     */
    void remoteList(@NotNull String projectid, @Nullable String remoteName, boolean verbose,
                    @NotNull AsyncRequestCallback<Array<Remote>> callback);

    /**
     * Adds remote repository to the list of remote repositories.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         remote repository's name
     * @param url
     *         remote repository's URL
     * @param callback
     */
    void remoteAdd(@NotNull String projectid, @NotNull String name, @NotNull String url,
                   @NotNull AsyncRequestCallback<String> callback);

    /**
     * Deletes the pointed(by name) remote repository from the list of repositories.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         remote repository name to delete
     * @param callback
     */
    void remoteDelete(@NotNull String projectid, @NotNull String name,
                      @NotNull AsyncRequestCallback<String> callback);

    /**
     * Remove files from the working tree and the index.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param files
     *         files to remove
     * @param cached
     *         is for removal only from index
     * @param callback
     */
    void remove(@NotNull String projectid, List<String> files, boolean cached,
                @NotNull AsyncRequestCallback<String> callback);

    /**
     * Reset current HEAD to the specified state. There two types of the reset: <br>
     * 1. Reset files in index - content of files is untouched. Typically it is useful to remove from index mistakenly added files.<br>
     * <code>git reset [paths]</code> is the opposite of <code>git add [paths]</code>. 2. Reset the current branch head to [commit] and
     * possibly updates the index (resetting it to the tree of [commit]) and the working tree depending on [mode].
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param commit
     *         commit to which current head should be reset
     * @param resetType
     *         type of the reset
     * @param callback
     */
    void reset(@NotNull String projectid, @NotNull String commit, @Nullable ResetRequest.ResetType resetType,
               @NotNull AsyncRequestCallback<Void> callback);

    /**
     * Initializes new Git repository (over WebSocket).
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param projectName
     *         project name
     * @param bare
     *         to create bare repository or not
     * @param callback
     *         callback
     */
    void init(@NotNull String projectid, @NotNull String projectName, boolean bare,
              @NotNull RequestCallback<Void> callback) throws WebSocketException;

    /**
     * Pull (fetch and merge) changes from remote repository to local one (sends request over WebSocket).
     *
     * @param project
     *         project's id (root of GIT repository)
     * @param refSpec
     *         list of refspec to fetch.
     *         <p/>
     *         Expected form is:
     *         <ul>
     *         <li>refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from remote repository will be fetched to
     *         'refs/remotes/origin/featured'.</li>
     *         <li>featured - remote branch name.</li>
     *         </ul>
     * @param remote
     *         remote remote repository's name
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    void pull(@NotNull Project project, @NotNull String refSpec, @NotNull String remote,
              @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Push changes from local repository to remote one (sends request over WebSocket).
     *
     * @param project
     *         projectid to GIT repository
     * @param refSpec
     *         list of refspec to push
     * @param remote
     *         remote repository name or url
     * @param force
     *         push refuses to update a remote ref that is not an ancestor of the local ref used to overwrite it. If <code>true</code>
     *         disables the check. This can cause the remote repository to lose commits
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    void push(@NotNull Project project, @NotNull List<String> refSpec, @NotNull String remote, boolean force,
              @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Clones one remote repository to local one (over WebSocket).
     *
     * @param project
     *         project (root of GIT repository)
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    void cloneRepository(@NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                         @NotNull RequestCallback<RepoInfo> callback) throws WebSocketException;

    /**
     * Performs commit changes from index to repository. The result of the commit is represented by {@link Revision}, which is returned by
     * callback in <code>onSuccess(Revision result)</code>. Sends request over WebSocket.
     *
     * @param project
     *         project (root of GIT repository)
     * @param message
     *         commit log message
     * @param all
     *         automatically stage files that have been modified and deleted
     * @param amend
     *         indicates that previous commit must be overwritten
     * @param callback
     *         callback
     * @throws WebSocketException
     */
    void commit(@NotNull Project project, @NotNull String message, boolean all, boolean amend,
                @NotNull AsyncRequestCallback<Revision> callback) ;

    /**
     * Compare two commits, get the diff for pointed file(s) or for the whole project in text format.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param fileFilter
     *         files for which to show changes
     * @param type
     *         type of diff format
     * @param noRenames
     *         don't show renamed files
     * @param renameLimit
     *         the limit of shown renamed files
     * @param commitA
     *         first commit to compare
     * @param commitB
     *         second commit to be compared
     * @param callback
     */
    void diff(@NotNull String projectid, @NotNull List<String> fileFilter, @NotNull DiffRequest.DiffType type,
              boolean noRenames, int renameLimit, @NotNull String commitA, @NotNull String commitB,
              @NotNull AsyncRequestCallback<String> callback);

    /**
     * Compare commit with index or working tree (depends on {@link #cached}), get the diff for pointed file(s) or for the whole project in
     * text format.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param fileFilter
     *         files for which to show changes
     * @param type
     *         type of diff format
     * @param noRenames
     *         don't show renamed files
     * @param renameLimit
     *         the limit of shown renamed files
     * @param commitA
     *         commit to compare
     * @param cached
     *         if <code>true</code> then compare commit with index, if <code>false</code>, then compare with working tree.
     * @param callback
     */
    void diff(@NotNull String projectid, @NotNull List<String> fileFilter, @NotNull DiffRequest.DiffType type,
              boolean noRenames, int renameLimit, @NotNull String commitA, boolean cached, @NotNull AsyncRequestCallback<String> callback);

    /**
     * Get log of commits. The result is the list of {@link Revision}, which is returned by callback in
     * <code>onSuccess(Revision result)</code>.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param isTextFormat
     *         if <code>true</code> the loq response will be in text format
     * @param callback
     */
    void log(@NotNull String projectid, boolean isTextFormat, @NotNull AsyncRequestCallback<LogResponse> callback);

    /**
     * Merge the pointed commit with current HEAD.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param commit
     *         commit's reference to merge with
     * @param callback
     */
    void merge(@NotNull String projectid, @NotNull String commit,
               @NotNull AsyncRequestCallback<MergeResult> callback);

    /**
     * Gets the working tree status. The status of added, modified or deleted files is shown is written in {@link String}. The format may
     * be
     * short or not. Example of detailed format:<br>
     * <p/>
     * <p/>
     * <pre>
     * # Untracked files:
     * #
     * # file.html
     * # folder
     * </pre>
     * <p/>
     * Example of short format:
     * <p/>
     * <p/>
     * <pre>
     * M  pom.xml
     * A  folder/test.html
     * D  123.txt
     * ?? folder/test.css
     * </pre>
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param shortFormat
     *         to show in short format or not
     * @param callback
     */
    void statusText(@NotNull String projectid, boolean shortFormat, @NotNull AsyncRequestCallback<String> callback);

    /**
     * Gets the working tree status : list of untracked, changed not commited and changed not updated.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param callback
     */
    void status(@NotNull String projectid, @NotNull AsyncRequestCallback<Status> callback);

    /**
     * Get the Git ReadOnly Url for the pointed item's location.
     *
     * @param projectid
     *         project's id (root of GIT repository)
     * @param callback
     */
    void getGitReadOnlyUrl(@NotNull String projectid, @NotNull AsyncRequestCallback<String> callback);

    void getCommitters(@NotNull String projectid, @NotNull AsyncRequestCallback<Commiters> callback);

    void deleteRepository(@NotNull String projectid, @NotNull AsyncRequestCallback<Void> callback);

    void getUrlVendorInfo(@NotNull String vcsUrl, @NotNull AsyncRequestCallback<GitUrlVendorInfo> callback);
}