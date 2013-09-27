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
package org.exoplatform.ide.git.client;

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.websocket.WebSocketException;
import org.exoplatform.ide.client.framework.websocket.rest.RequestCallback;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.Commiters;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;
import org.exoplatform.ide.git.shared.MergeResult;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.RepoInfo;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.Status;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ProjectModel;

import java.util.List;

/**
 * Service contains methods for working with Git repository from client side. Example usage, initialize Git repository: <br>
 * <code>
 * GitClientService.getInstance().init(workDir, bare, callback);
 * <code>
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Mar 23, 2011 11:48:14 AM anya $
 */
public abstract class GitClientService {
    /** Instance of {@link GitClientService} */
    private static GitClientService instance;

    /** @return {@link GitClientService} */
    public static GitClientService getInstance() {
        return instance;
    }

    protected GitClientService() {
        instance = this;
    }

    /**
     * Add changes to Git index (temporary storage).
     * 
     * @param vfsId virtual file system id
     * @param project project (root of GIT repository)
     * @param update if <code>true</code> then never stage new files, but stage modified new contents of tracked files and remove files from
     *            the index if the corresponding files in the working tree have been removed
     * @param filePattern pattern of the files to be added, default is "." (all files are added)
     * @param callback callback
     * @throws RequestException
     */
    public abstract void add(String vfsId,
                             ProjectModel project,
                             boolean update,
                             String[] filePattern,
                             AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Add changes to Git index (temporary storage). Sends request over WebSocket.
     * 
     * @param vfsId virtual file system id
     * @param project project (root of GIT repository)
     * @param update if <code>true</code> then never stage new files, but stage modified new contents of tracked files and remove files from
     *            the index if the corresponding files in the working tree have been removed
     * @param filePattern pattern of the files to be added, default is "." (all files are added)
     * @param callback callback
     * @throws WebSocketException
     */
    public abstract void addWS(String vfsId,
                               ProjectModel project,
                               boolean update,
                               String[] filePattern,
                               RequestCallback<String> callback) throws WebSocketException;

    /**
     * Fetch changes from remote repository to local one.
     * 
     * @param vfsId virtual file system id
     * @param project project root of GIT repository
     * @param remote remote repository's name
     * @param refspec list of refspec to fetch.
     *            <p/>
     *            Expected form is:
     *            <ul>
     *            <li>refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from remote repository will be fetched to
     *            'refs/remotes/origin/featured'.</li>
     *            <li>featured - remote branch name.</li>
     *            </ul>
     * @param removeDeletedRefs if <code>true</code> then delete removed refs from local repository
     * @param callback callback
     * @throws RequestException
     */
    public abstract void fetch(String vfsId,
                               ProjectModel project,
                               String remote,
                               String[] refspec,
                               boolean removeDeletedRefs,
                               AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Fetch changes from remote repository to local one (sends request over WebSocket).
     * 
     * @param vfsId virtual file system id
     * @param project project root of GIT repository
     * @param remote remote repository's name
     * @param refspec list of refspec to fetch.
     *            <p/>
     *            Expected form is:
     *            <ul>
     *            <li>refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from remote repository will be fetched to
     *            'refs/remotes/origin/featured'.</li>
     *            <li>featured - remote branch name.</li>
     *            </ul>
     * @param removeDeletedRefs if <code>true</code> then delete removed refs from local repository
     * @param callback callback
     * @throws WebSocketException
     */
    public abstract void fetchWS(String vfsId,
                                 ProjectModel project,
                                 String remote,
                                 String[] refspec,
                                 boolean removeDeletedRefs,
                                 RequestCallback<String> callback) throws WebSocketException;

    /**
     * Get the list of the branches. For now, all branches cannot be returned at once, so the parameter <code>remote</code> tells to get
     * remote branches if <code>true</code> or local ones (if <code>false</code>).
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param mode get remote branches
     * @param callback callback
     */
    public abstract void branchList(String vfsId,
                                    String projectid,
                                    String mode,
                                    AsyncRequestCallback<List<Branch>> callback) throws RequestException;

    /**
     * Delete branch.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param name name of the branch to delete
     * @param force force if <code>true</code> delete branch {@link #name} even if it is not fully merged
     * @param callback callback
     */
    public abstract void branchDelete(String vfsId,
                                      String projectid,
                                      String name,
                                      boolean force,
                                      AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Checkout the branch with pointed name.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param oldName branch's current name
     * @param newName branch's new name
     * @param callback callback
     */
    public abstract void branchRename(String vfsId,
                                      String projectid,
                                      String oldName,
                                      String newName,
                                      AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Create new branch with pointed name.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param name new branch's name
     * @param startPoint name of a commit at which to start the new branch
     * @param callback callback
     */
    public abstract void branchCreate(String vfsId,
                                      String projectid,
                                      String name,
                                      String startPoint,
                                      AsyncRequestCallback<Branch> callback) throws RequestException;

    /**
     * Checkout the branch with pointed name.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param name branch's name
     * @param startPoint if {@link #createNew} is <code>true</code> then the name of a commit at which to start the new branch
     * @param createNew if <code>true</code> then create a new branch
     * @param callback callback
     */
    public abstract void branchCheckout(String vfsId,
                                        String projectid,
                                        String name,
                                        String startPoint,
                                        boolean createNew,
                                        AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Get the list of remote repositories for pointed by <code>workDir</code> parameter one.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param remoteName remote repository's name
     * @param verbose If <code>true</code> show remote url and name otherwise show remote name
     * @param callback callback
     */
    public abstract void remoteList(String vfsId,
                                    String projectid,
                                    String remoteName,
                                    boolean verbose,
                                    AsyncRequestCallback<List<Remote>> callback) throws RequestException;

    /**
     * Adds remote repository to the list of remote repositories.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param name remote repository's name
     * @param url remote repository's URL
     * @param callback callback
     */
    public abstract void remoteAdd(String vfsId,
                                   String projectid,
                                   String name,
                                   String url,
                                   AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Deletes the pointed(by name) remote repository from the list of repositories.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param name remote repository name to delete
     * @param callback callback
     */
    public abstract void remoteDelete(String vfsId,
                                      String projectid,
                                      String name,
                                      AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Remove files from the working tree and the index.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param files files to remove
     * @param cached is for removal only from index
     * @param callback callback
     */
    public abstract void remove(String vfsId,
                                String projectid,
                                String[] files,
                                Boolean cached,
                                AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Reset current HEAD to the specified state. There two types of the reset: <br>
     * 1. Reset files in index - content of files is untouched. Typically it is useful to remove from index mistakenly added files.<br>
     * <code>git reset [paths]</code> is the opposite of <code>git add [paths]</code>. 2. Reset the current branch head to [commit] and
     * possibly updates the index (resetting it to the tree of [commit]) and the working tree depending on [mode].
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param commit commit to which current head should be reset
     * @param resetType type of the reset
     * @param callback callback
     */
    public abstract void reset(String vfsId,
                               String projectid,
                               String commit,
                               ResetRequest.ResetType resetType,
                               AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Initializes new Git repository.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param projectName
     * @param bare to create bare repository or not
     * @param callback callback
     */
    public abstract void init(String vfsId,
                              String projectid,
                              String projectName,
                              boolean bare,
                              AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Initializes new Git repository (over WebSocket).
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param projectName
     * @param bare to create bare repository or not
     * @param callback callback
     */
    public abstract void initWS(String vfsId,
                                String projectid,
                                String projectName,
                                boolean bare,
                                RequestCallback<String> callback) throws WebSocketException;

    /**
     * Pull (fetch and merge) changes from remote repository to local one.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param refSpec list of refspec to fetch.
     *            <p/>
     *            Expected form is:
     *            <ul>
     *            <li>refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from remote repository will be fetched to
     *            'refs/remotes/origin/featured'.</li>
     *            <li>featured - remote branch name.</li>
     *            </ul>
     * @param remote remote remote repository's name
     * @param callback callback
     * @throws RequestException
     */
    public abstract void pull(String vfsId,
                              ProjectModel project,
                              String refSpec,
                              String remote,
                              AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Pull (fetch and merge) changes from remote repository to local one (sends request over WebSocket).
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param refSpec list of refspec to fetch.
     *            <p/>
     *            Expected form is:
     *            <ul>
     *            <li>refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from remote repository will be fetched to
     *            'refs/remotes/origin/featured'.</li>
     *            <li>featured - remote branch name.</li>
     *            </ul>
     * @param remote remote remote repository's name
     * @param callback callback
     * @throws WebSocketException
     */
    public abstract void pullWS(String vfsId,
                                ProjectModel project,
                                String refSpec,
                                String remote,
                                RequestCallback<String> callback) throws WebSocketException;

    /**
     * Push changes from local repository to remote one.
     * 
     * @param vfsId virtual file system id
     * @param projectid projectid to GIT repository
     * @param refSpec list of refspec to push
     * @param remote remote repository name or url
     * @param force push refuses to update a remote ref that is not an ancestor of the local ref used to overwrite it. If <code>true</code>
     *            disables the check. This can cause the remote repository to lose commits
     * @param callback callback
     * @throws RequestException
     */
    public abstract void push(String vfsId,
                              ProjectModel project,
                              String[] refSpec,
                              String remote,
                              boolean force,
                              AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Push changes from local repository to remote one (sends request over WebSocket).
     * 
     * @param vfsId virtual file system id
     * @param projectid projectid to GIT repository
     * @param refSpec list of refspec to push
     * @param remote remote repository name or url
     * @param force push refuses to update a remote ref that is not an ancestor of the local ref used to overwrite it. If <code>true</code>
     *            disables the check. This can cause the remote repository to lose commits
     * @param callback callback
     * @throws WebSocketException
     */
    public abstract void pushWS(String vfsId,
                                ProjectModel project,
                                String[] refSpec,
                                String remote,
                                boolean force,
                                RequestCallback<String> callback) throws WebSocketException;

    /**
     * Clones one remote repository to local one.
     * 
     * @param vfsId virtual file system id
     * @param project project (root of GIT repository)
     * @param remoteUri the location of the remote repository
     * @param remoteName remote name instead of "origin"
     * @param callback callback
     * @throws RequestException
     */
    public abstract void cloneRepository(String vfsId,
                                         FolderModel project,
                                         String remoteUri,
                                         String remoteName,
                                         AsyncRequestCallback<RepoInfo> callback) throws RequestException;

    /**
     * Clones one remote repository to local one (over WebSocket).
     * 
     * @param vfsId virtual file system id
     * @param project project (root of GIT repository)
     * @param remoteUri the location of the remote repository
     * @param remoteName remote name instead of "origin"
     * @param callback callback
     * @throws WebSocketException
     */
    public abstract void cloneRepositoryWS(String vfsId,
                                           FolderModel project,
                                           String remoteUri,
                                           String remoteName,
                                           RequestCallback<RepoInfo> callback) throws WebSocketException;

    /**
     * Performs commit changes from index to repository. The result of the commit is represented by {@link Revision}, which is returned by
     * callback in <code>onSuccess(Revision result)</code>.
     * 
     * @param vfsId virtual file system id
     * @param project project (root of GIT repository)
     * @param message commit log message
     * @param all automatically stage files that have been modified and deleted
     * @param amend indicates that previous commit must be overwritten
     * @param callback callback
     * @throws RequestException
     */
    public abstract void commit(String vfsId,
                                ProjectModel project,
                                String message,
                                boolean all,
                                boolean amend,
                                AsyncRequestCallback<Revision> callback) throws RequestException;

    /**
     * Performs commit changes from index to repository. The result of the commit is represented by {@link Revision}, which is returned by
     * callback in <code>onSuccess(Revision result)</code>. Sends request over WebSocket.
     * 
     * @param vfsId virtual file system id
     * @param project project (root of GIT repository)
     * @param message commit log message
     * @param all automatically stage files that have been modified and deleted
     * @param amend indicates that previous commit must be overwritten
     * @param callback callback
     * @throws WebSocketException
     */
    public abstract void commitWS(String vfsId,
                                  ProjectModel project,
                                  String message,
                                  boolean all,
                                  boolean amend,
                                  RequestCallback<Revision> callback) throws WebSocketException;

    /**
     * Compare two commits, get the diff for pointed file(s) or for the whole project in text format.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param fileFilter files for which to show changes
     * @param type type of diff format
     * @param noRenames don't show renamed files
     * @param renameLimit the limit of shown renamed files
     * @param commitA first commit to compare
     * @param commitB second commit to be compared
     * @param callback callback
     */
    public abstract void diff(String vfsId,
                              String projectid,
                              String[] fileFilter,
                              DiffType type,
                              boolean noRenames,
                              int renameLimit,
                              String commitA,
                              String commitB,
                              AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Compare commit with index or working tree (depends on {@link #cached}), get the diff for pointed file(s) or for the whole project in
     * text format.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param fileFilter files for which to show changes
     * @param type type of diff format
     * @param noRenames don't show renamed files
     * @param renameLimit the limit of shown renamed files
     * @param commitA commit to compare
     * @param cached if <code>true</code> then compare commit with index, if <code>false</code>, then compare with working tree.
     * @param callback callback
     */
    public abstract void diff(String vfsId,
                              String projectid,
                              String[] fileFilter,
                              DiffType type,
                              boolean noRenames,
                              int renameLimit,
                              String commitA,
                              boolean cached,
                              AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    /**
     * Get log of commits. The result is the list of {@link Revision}, which is returned by callback in
     * <code>onSuccess(Revision result)</code>.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param isTextFormat if <code>true</code> the loq response will be in text format
     * @param callback callback
     */
    public abstract void log(String vfsId,
                             String projectid,
                             boolean isTextFormat,
                             AsyncRequestCallback<LogResponse> callback) throws RequestException;

    /**
     * Merge the pointed commit with current HEAD.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param commit commit's reference to merge with
     * @param callback callback
     */
    public abstract void merge(String vfsId,
                               String projectid,
                               String commit,
                               AsyncRequestCallback<MergeResult> callback) throws RequestException;

    /**
     * Gets the working tree status. The status of added, modified or deleted files is shown is written in {@link String}. The format may be
     * short or not. Example of detailed format:<br>
     * <p/>
     * 
     * <pre>
     * # Untracked files:
     * #
     * # file.html
     * # folder
     * </pre>
     * <p/>
     * Example of short format:
     * <p/>
     * 
     * <pre>
     * M  pom.xml
     * A  folder/test.html
     * D  123.txt
     * ?? folder/test.css
     * </pre>
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param shortFormat to show in short format or not
     * @param callback callback
     */
    public abstract void statusText(String vfsId,
                                    String projectid,
                                    boolean shortFormat,
                                    AsyncRequestCallback<String> callback) throws RequestException;

    /**
     * Gets the working tree status : list of untracked, changed not commited and changed not updated.
     * 
     * @param vfsId virtual file system id
     * @param projectid project's id (root of GIT repository)
     * @param callback callback
     */
    public abstract void status(String vfsId,
                                String projectid,
                                AsyncRequestCallback<Status> callback) throws RequestException;

    /**
     * Get the Git ReadOnly Url for the pointed item's location.
     * 
     * @param vfsId virtual file system's id
     * @param projectid project's id (root of GIT repository)
     * @param callback
     * @throws RequestException
     */
    public abstract void getGitReadOnlyUrl(String vfsId,
                                           String projectid,
                                           AsyncRequestCallback<StringBuilder> callback) throws RequestException;

    public abstract void getCommiters(String vfsId,
                                      String projectid,
                                      AsyncRequestCallback<Commiters> callback) throws RequestException;

    public abstract void deleteRepository(String vfsId, String projectid, AsyncRequestCallback<Void> callback) throws RequestException;

}