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
package com.codenvy.ide.ext.git.server;

import com.codenvy.ide.ext.git.shared.AddRequest;
import com.codenvy.ide.ext.git.shared.Branch;
import com.codenvy.ide.ext.git.shared.BranchCheckoutRequest;
import com.codenvy.ide.ext.git.shared.BranchCreateRequest;
import com.codenvy.ide.ext.git.shared.BranchDeleteRequest;
import com.codenvy.ide.ext.git.shared.BranchListRequest;
import com.codenvy.ide.ext.git.shared.CloneRequest;
import com.codenvy.ide.ext.git.shared.CommitRequest;
import com.codenvy.ide.ext.git.shared.DiffRequest;
import com.codenvy.ide.ext.git.shared.FetchRequest;
import com.codenvy.ide.ext.git.shared.GitUser;
import com.codenvy.ide.ext.git.shared.InitRequest;
import com.codenvy.ide.ext.git.shared.LogRequest;
import com.codenvy.ide.ext.git.shared.LsRemoteRequest;
import com.codenvy.ide.ext.git.shared.MergeRequest;
import com.codenvy.ide.ext.git.shared.MergeResult;
import com.codenvy.ide.ext.git.shared.MoveRequest;
import com.codenvy.ide.ext.git.shared.PullRequest;
import com.codenvy.ide.ext.git.shared.PushRequest;
import com.codenvy.ide.ext.git.shared.Remote;
import com.codenvy.ide.ext.git.shared.RemoteAddRequest;
import com.codenvy.ide.ext.git.shared.RemoteListRequest;
import com.codenvy.ide.ext.git.shared.RemoteReference;
import com.codenvy.ide.ext.git.shared.RemoteUpdateRequest;
import com.codenvy.ide.ext.git.shared.ResetRequest;
import com.codenvy.ide.ext.git.shared.Revision;
import com.codenvy.ide.ext.git.shared.RmRequest;
import com.codenvy.ide.ext.git.shared.Status;
import com.codenvy.ide.ext.git.shared.Tag;
import com.codenvy.ide.ext.git.shared.TagCreateRequest;
import com.codenvy.ide.ext.git.shared.TagDeleteRequest;
import com.codenvy.ide.ext.git.shared.TagListRequest;

import java.net.URISyntaxException;
import java.util.List;

/**
 * Connection to Git repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitConnection.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public interface GitConnection {
    /**
     * Add content of working tree to Git index. This action prepares content to next commit.
     * 
     * @param request add request
     * @throws GitException if any error occurs when add files to the index
     * @throw IllegalArgumentException if {@link AddRequest#getFilepattern()} returns <code>null</code> or empty array
     * @see AddRequest
     */
    void add(AddRequest request) throws GitException;

    /**
     * Checkout a branch to the working tree.
     * 
     * @param request checkout request
     * @throws GitException if any error occurs when checkout
     * @throw IllegalArgumentException if one of the following conditions are met:
     *        <ul>
     *        <li>{@link BranchCheckoutRequest#getName()} provide invalid branch name</li>
     *        <li>creation of branch requested ( {@link BranchCheckoutRequest#isCreateNew()} returns <code>true</code> ) but branch with the
     *        same name already exists</li>
     *        </ul>
     * @see BranchCheckoutRequest
     */
    void branchCheckout(BranchCheckoutRequest request) throws GitException;

    /**
     * Create new branch.
     * 
     * @param request create branch request
     * @return newly created branch
     * @throws GitException if any error occurs when create branch
     * @throw IllegalArgumentException if one of the following conditions are met:
     *        <ul>
     *        <li>{@link BranchCreateRequest#getName()} provide invalid branch name</li>
     *        <li>branch with the same name already exists</li>
     *        </ul>
     * @see BranchCreateRequest
     */
    Branch branchCreate(BranchCreateRequest request) throws GitException;

    /**
     * Delete branch.
     * 
     * @param request delete branch request
     * @throws GitException if any error occurs when delete branch
     * @throw IllegalArgumentException if one of the following conditions are met:
     *        <ul>
     *        <li>branch is not merged and {@link BranchDeleteRequest#isForce()} returns <code>false</code></li>
     *        <li>deleted branch is currently checkedout in working tree</li>
     *        </ul>
     * @see BranchDeleteRequest
     */
    void branchDelete(BranchDeleteRequest request) throws GitException;

    /**
     * Rename branch.
     * 
     * @param oldName current name of branch
     * @param newName new name of branch
     * @throws GitException if any error occurs when delete branch
     */
    void branchRename(String oldName, String newName) throws GitException;

    /**
     * List branches.
     * 
     * @param request list branches request
     * @return list of branch
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if {@link BranchListRequest#getListMode()} returns not <code>null</code> or 'a' or 'r'
     * @see BranchListRequest
     */
    List<Branch> branchList(BranchListRequest request) throws GitException;

    /**
     * Clone repository.
     * 
     * @param request clone request
     * @return connection to cloned repository
     * @throws URISyntaxException if {@link CloneRequest#getRemoteUri()} return invalid value
     * @throws GitException if any other error occurs
     * @see CloneRequest
     */
    GitConnection clone(CloneRequest request) throws URISyntaxException, GitException;

    /**
     * Commit current state of index in new commit.
     * 
     * @param request commit request
     * @return new commit
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if {@link CommitRequest#getMessage()} returns <code>null</code>
     * @see CommitRequest
     */
    Revision commit(CommitRequest request) throws GitException;

    /**
     * Show diff between commits.
     * 
     * @param request diff request
     * @return diff page. Diff info can be serialized to stream by using method {@link DiffPage#writeTo(java.io.OutputStream)}
     * @throws GitException if any error occurs
     * @see DiffPage
     * @see DiffRequest
     */
    DiffPage diff(DiffRequest request) throws GitException;

    /**
     * Fetch data from remote repository.
     * 
     * @param request fetch request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if remote specified by {@link FetchRequest#getRemote()} is invalid
     * @see FetchRequest
     */
    void fetch(FetchRequest request) throws GitException;

    /**
     * Initialize new Git repository.
     * 
     * @param request init request
     * @return connection to newly created repository
     * @throws GitException if any error occurs
     * @see InitRequest
     */
    GitConnection init(InitRequest request) throws GitException;

    /**
     * Get commit logs.
     * 
     * @param request log request
     * @return log page. Logs can be serialized to stream by using method {@link DiffPage#writeTo(java.io.OutputStream)}
     * @throws GitException if any error occurs
     * @see LogRequest
     */
    LogPage log(LogRequest request) throws GitException;

    /**
     * List references in a remote repository.
     *
     * @param request ls-remote request
     * @return list references in a remote repository.
     * @throws GitException if any error occurs
     * @see LsRemoteRequest
     */
    List<RemoteReference> lsRemote(LsRemoteRequest request) throws GitException;

    /**
     * Merge commits.
     * 
     * @param request merge request
     * @return result of merge
     * @throws IllegalArgumentException if {@link MergeRequest#getCommit()} returns invalid value, e.g. there is no specified commit
     * @throws GitException if any error occurs
     * @see MergeRequest
     */
    MergeResult merge(MergeRequest request) throws GitException;

    /**
     * Move or rename file or directory.
     * 
     * @param request move request
     * @throws IllegalArgumentException if {@link MoveRequest#getSource()} or {@link MoveRequest#getTarget()} returns invalid value, e.g.
     *             there is not specified source or specified target already exists
     * @throws GitException if any error occurs
     * @see MoveRequest
     */
    void mv(MoveRequest request) throws GitException;

    /**
     * Pull (fetch and merge at once) changes from remote repository to local branch.
     * 
     * @param request pull request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if remote configuration is invalid
     * @see PullRequest
     */
    void pull(PullRequest request) throws GitException;

    /**
     * Send changes from local repository to remote one.
     * 
     * @param request push request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if remote configuration is invalid
     * @see PushRequest
     */
    void push(PushRequest request) throws GitException;

    /**
     * Add new remote configuration.
     * 
     * @param request add remote configuration request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if remote (see {@link RemoteAddRequest#getName()}) already exists or any updated parameter (e.g.
     *             URLs) invalid
     * @see RemoteAddRequest
     */
    void remoteAdd(RemoteAddRequest request) throws GitException;

    /**
     * Remove the remote named <code>name</code>. All remote tracking branches and configuration settings for the remote are removed.
     * 
     * @param name remote configuration to remove
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if remote <code>name</code> not found
     */
    void remoteDelete(String name) throws GitException;

    /**
     * Show remotes.
     * 
     * @param request remote list request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if remote <code>name</code> not found
     * @see RemoteListRequest
     */
    List<Remote> remoteList(RemoteListRequest request) throws GitException;

    /**
     * Update remote configuration.
     * 
     * @param request update remote configuration request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if remote configuration (see {@link RemoteUpdateRequest#getName()}) not found or any updated
     *             parameter (e.g. URLs) invalid
     * @see RemoteUpdateRequest
     */
    void remoteUpdate(RemoteUpdateRequest request) throws GitException;

    /**
     * Reset current HEAD to the specified state.
     * 
     * @param request reset request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if reset type or commit is invalid
     * @see ResetRequest
     * @see ResetRequest#getCommit()
     * @see ResetRequest#getType()
     */
    void reset(ResetRequest request) throws GitException;

    /**
     * Remove files.
     * 
     * @param request remove request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException {@link RmRequest#getFiles()} returns <code>null</code> or empty array
     * @see RmRequest
     */
    void rm(RmRequest request) throws GitException;

    /**
     * Get status of working tree.
     * 
     * @param shortFormat shortFormat
     * @return status.
     * @throws GitException if any error occurs
     */
    Status status(boolean shortFormat) throws GitException;

    /**
     * Create new tag.
     * 
     * @param request tag create request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if tag name ( {@link TagCreateRequest#getName()} ) is invalid or <code>null</code>
     * @see TagCreateRequest
     */
    Tag tagCreate(TagCreateRequest request) throws GitException;

    /**
     * @param request delete tag request
     * @throws GitException if any error occurs
     * @throws IllegalArgumentException if there is tag with specified name (see {@link TagDeleteRequest#getName()})
     * @see TagDeleteRequest
     */
    void tagDelete(TagDeleteRequest request) throws GitException;

    /**
     * Get list of available tags.
     * 
     * @param request tag list request
     * @return list of tags matched to request, see {@link TagListRequest#getPattern()}
     * @throws GitException if any error occurs
     * @see TagListRequest
     */
    List<Tag> tagList(TagListRequest request) throws GitException;

    /** @return user associated with this connection */
    GitUser getUser();

    /**
     * Gel list of commiters in current repository.
     * 
     * @return
     * @throws GitException
     */
    List<GitUser> getCommiters() throws GitException;

    /** Close connection, release associated resources. */
    void close();
}
