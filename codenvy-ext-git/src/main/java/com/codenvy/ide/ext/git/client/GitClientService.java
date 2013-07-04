/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package com.codenvy.ide.ext.git.client;

import com.codenvy.ide.annotations.NotNull;
import com.codenvy.ide.annotations.Nullable;
import com.codenvy.ide.ext.git.shared.*;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.resources.model.Project;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.websocket.WebSocketException;
import com.codenvy.ide.websocket.rest.RequestCallback;
import com.google.gwt.http.client.RequestException;

/**
 * Service contains methods for working with Git repository from client side. Example usage, initialize Git repository: <br>
 * <code>
 * GitClientService.getInstance().init(workDir, bare, callback);
 * <code>
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 23, 2011 11:48:14 AM anya $
 */
public interface GitClientService {
    /**
     * Add changes to Git index (temporary storage).
     *
     * @param vfsId
     *         virtual file system id
     * @param project
     *         project (root of GIT repository)
     * @param update
     *         if <code>true</code> then never stage new files, but stage modified new contents of tracked files and remove files from
     *         the index if the corresponding files in the working tree have been removed
     * @param filePattern
     *         pattern of the files to be added, default is "." (all files are added)
     * @param callback
     *         callback
     * @throws RequestException
     */
    void add(@NotNull String vfsId, @NotNull Project project, boolean update, @Nullable JsonArray<String> filePattern,
             @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Add changes to Git index (temporary storage). Sends request over WebSocket.
     *
     * @param vfsId
     *         virtual file system id
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
    void addWS(@NotNull String vfsId, @NotNull Project project, boolean update, @Nullable JsonArray<String> filePattern,
               @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Fetch changes from remote repository to local one.
     *
     * @param vfsId
     *         virtual file system id
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
     * @throws RequestException
     */
    void fetch(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, JsonArray<String> refspec,
               boolean removeDeletedRefs, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Fetch changes from remote repository to local one (sends request over WebSocket).
     *
     * @param vfsId
     *         virtual file system id
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
    void fetchWS(@NotNull String vfsId, @NotNull Project project, @NotNull String remote, JsonArray<String> refspec,
                 boolean removeDeletedRefs, @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Get the list of the branches. For now, all branches cannot be returned at once, so the parameter <code>remote</code> tells to get
     * remote branches if <code>true</code> or local ones (if <code>false</code>).
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param mode
     *         get remote branches
     * @param callback
     *         callback
     */
    void branchList(@NotNull String vfsId, @NotNull String projectid, @Nullable String mode,
                    @NotNull AsyncRequestCallback<JsonArray<Branch>> callback) throws RequestException;

    /**
     * Delete branch.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         name of the branch to delete
     * @param force
     *         force if <code>true</code> delete branch {@link #name} even if it is not fully merged
     * @param callback
     *         callback
     */
    void branchDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, boolean force,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Checkout the branch with pointed name.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param oldName
     *         branch's current name
     * @param newName
     *         branch's new name
     * @param callback
     *         callback
     */
    void branchRename(@NotNull String vfsId, @NotNull String projectid, @NotNull String oldName, @NotNull String newName,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Create new branch with pointed name.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         new branch's name
     * @param startPoint
     *         name of a commit at which to start the new branch
     * @param callback
     *         callback
     */
    void branchCreate(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                      @NotNull AsyncRequestCallback<Branch> callback) throws RequestException;

    /**
     * Checkout the branch with pointed name.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         branch's name
     * @param startPoint
     *         if {@link #createNew} is <code>true</code> then the name of a commit at which to start the new branch
     * @param createNew
     *         if <code>true</code> then create a new branch
     * @param callback
     *         callback
     */
    void branchCheckout(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String startPoint,
                        boolean createNew, @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get the list of remote repositories for pointed by <code>workDir</code> parameter one.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param remoteName
     *         remote repository's name
     * @param verbose
     *         If <code>true</code> show remote url and name otherwise show remote name
     * @param callback
     *         callback
     */
    void remoteList(@NotNull String vfsId, @NotNull String projectid, @Nullable String remoteName, boolean verbose,
                    @NotNull AsyncRequestCallback<JsonArray<Remote>> callback) throws RequestException;

    /**
     * Adds remote repository to the list of remote repositories.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         remote repository's name
     * @param url
     *         remote repository's URL
     * @param callback
     *         callback
     */
    void remoteAdd(@NotNull String vfsId, @NotNull String projectid, @NotNull String name, @NotNull String url,
                   @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Deletes the pointed(by name) remote repository from the list of repositories.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param name
     *         remote repository name to delete
     * @param callback
     *         callback
     */
    void remoteDelete(@NotNull String vfsId, @NotNull String projectid, @NotNull String name,
                      @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Remove files from the working tree and the index.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param files
     *         files to remove
     * @param cached
     *         is for removal only from index
     * @param callback
     *         callback
     */
    void remove(@NotNull String vfsId, @NotNull String projectid, JsonArray<String> files, boolean cached,
                @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Reset current HEAD to the specified state. There two types of the reset: <br>
     * 1. Reset files in index - content of files is untouched. Typically it is useful to remove from index mistakenly added files.<br>
     * <code>git reset [paths]</code> is the opposite of <code>git add [paths]</code>. 2. Reset the current branch head to [commit] and
     * possibly updates the index (resetting it to the tree of [commit]) and the working tree depending on [mode].
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param commit
     *         commit to which current head should be reset
     * @param resetType
     *         type of the reset
     * @param callback
     *         callback
     */
    void reset(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit, @Nullable ResetRequest.ResetType resetType,
               @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Initializes new Git repository.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param projectName
     * @param bare
     *         to create bare repository or not
     * @param callback
     *         callback
     */
    void init(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
              @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Initializes new Git repository (over WebSocket).
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param projectName
     *         project name
     * @param bare
     *         to create bare repository or not
     * @param callback
     *         callback
     */
    void initWS(@NotNull String vfsId, @NotNull String projectid, @NotNull String projectName, boolean bare,
                @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Pull (fetch and merge) changes from remote repository to local one.
     *
     * @param vfsId
     *         virtual file system id
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
     * @throws RequestException
     */
    void pull(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
              @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Pull (fetch and merge) changes from remote repository to local one (sends request over WebSocket).
     *
     * @param vfsId
     *         virtual file system id
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
    void pullWS(@NotNull String vfsId, @NotNull Project project, @NotNull String refSpec, @NotNull String remote,
                @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Push changes from local repository to remote one.
     *
     * @param vfsId
     *         virtual file system id
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
     * @throws RequestException
     */
    void push(@NotNull String vfsId, @NotNull Project project, @NotNull JsonArray<String> refSpec, @NotNull String remote, boolean force,
              @NotNull AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Push changes from local repository to remote one (sends request over WebSocket).
     *
     * @param vfsId
     *         virtual file system id
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
    void pushWS(@NotNull String vfsId, @NotNull Project project, @NotNull JsonArray<String> refSpec, @NotNull String remote, boolean force,
                @NotNull RequestCallback<String> callback) throws WebSocketException;

    /**
     * Clones one remote repository to local one.
     *
     * @param vfsId
     *         virtual file system id
     * @param project
     *         project (root of GIT repository)
     * @param remoteUri
     *         the location of the remote repository
     * @param remoteName
     *         remote name instead of "origin"
     * @param callback
     *         callback
     * @throws RequestException
     */
    void cloneRepository(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                         @NotNull AsyncRequestCallback<RepoInfo> callback) throws RequestException;

    /**
     * Clones one remote repository to local one (over WebSocket).
     *
     * @param vfsId
     *         virtual file system id
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
    void cloneRepositoryWS(@NotNull String vfsId, @NotNull Project project, @NotNull String remoteUri, @NotNull String remoteName,
                           @NotNull RequestCallback<RepoInfo> callback) throws WebSocketException;

    /**
     * Performs commit changes from index to repository. The result of the commit is represented by {@link Revision}, which is returned by
     * callback in <code>onSuccess(Revision result)</code>.
     *
     * @param vfsId
     *         virtual file system id
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
     * @throws RequestException
     */
    void commit(@NotNull String vfsId, @NotNull Project project, @NotNull String message, boolean all, boolean amend,
                @NotNull AsyncRequestCallback<Revision> callback) throws RequestException;

    /**
     * Performs commit changes from index to repository. The result of the commit is represented by {@link Revision}, which is returned by
     * callback in <code>onSuccess(Revision result)</code>. Sends request over WebSocket.
     *
     * @param vfsId
     *         virtual file system id
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
    void commitWS(@NotNull String vfsId, @NotNull Project project, @NotNull String message, boolean all, boolean amend,
                  @NotNull RequestCallback<Revision> callback) throws WebSocketException;

    /**
     * Compare two commits, get the diff for pointed file(s) or for the whole project in text format.
     *
     * @param vfsId
     *         virtual file system id
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
     *         callback
     */
    void diff(@NotNull String vfsId, @NotNull String projectid, @NotNull JsonArray<String> fileFilter, @NotNull DiffRequest.DiffType type,
              boolean noRenames, int renameLimit, @NotNull String commitA, @NotNull String commitB,
              @NotNull AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Compare commit with index or working tree (depends on {@link #cached}), get the diff for pointed file(s) or for the whole project in
     * text format.
     *
     * @param vfsId
     *         virtual file system id
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
     *         callback
     */
    void diff(@NotNull String vfsId, @NotNull String projectid, @NotNull JsonArray<String> fileFilter, @NotNull DiffRequest.DiffType type,
              boolean noRenames, int renameLimit, @NotNull String commitA, boolean cached,
              @NotNull AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Get log of commits. The result is the list of {@link Revision}, which is returned by callback in
     * <code>onSuccess(Revision result)</code>.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param isTextFormat
     *         if <code>true</code> the loq response will be in text format
     * @param callback
     *         callback
     */
    void log(@NotNull String vfsId, @NotNull String projectid, boolean isTextFormat, @NotNull AsyncRequestCallback<LogResponse> callback)
            throws RequestException;

    /**
     * Merge the pointed commit with current HEAD.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param commit
     *         commit's reference to merge with
     * @param callback
     *         callback
     */
    void merge(@NotNull String vfsId, @NotNull String projectid, @NotNull String commit,
               @NotNull AsyncRequestCallback<MergeResult> callback) throws RequestException;

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
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param shortFormat
     *         to show in short format or not
     * @param callback
     *         callback
     */
    void statusText(@NotNull String vfsId, @NotNull String projectid, boolean shortFormat, @NotNull AsyncRequestCallback<String> callback)
            throws RequestException;

    /**
     * Gets the working tree status : list of untracked, changed not commited and changed not updated.
     *
     * @param vfsId
     *         virtual file system id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param callback
     *         callback
     */
    void status(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Status> callback) throws RequestException;

    /**
     * Get the Git ReadOnly Url for the pointed item's location.
     *
     * @param vfsId
     *         virtual file system's id
     * @param projectid
     *         project's id (root of GIT repository)
     * @param callback
     *         callback
     * @throws RequestException
     */
    void getGitReadOnlyUrl(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<StringBuilder> callback)
            throws RequestException;

    void getCommiters(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Commiters> callback)
            throws RequestException;

    void deleteRepository(@NotNull String vfsId, @NotNull String projectid, @NotNull AsyncRequestCallback<Void> callback)
            throws RequestException;
}