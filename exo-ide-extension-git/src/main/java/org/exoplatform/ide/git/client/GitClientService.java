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
package org.exoplatform.ide.git.client;

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.git.client.marshaller.DiffResponse;
import org.exoplatform.ide.git.client.marshaller.LogResponse;
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.shared.Branch;
import org.exoplatform.ide.git.shared.Remote;
import org.exoplatform.ide.git.shared.ResetRequest;
import org.exoplatform.ide.git.shared.Revision;
import org.exoplatform.ide.git.shared.DiffRequest.DiffType;

import java.util.List;

/**
 * Service contains methods for working with Git repository from client side.
 * Example usage, initialize Git repository: <br>
 * <code>
 * GitClientService.getInstance().init(workDir, bare, callback);
 * <code>
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 23, 2011 11:48:14 AM anya $
 *
 */
public abstract class GitClientService
{
   /**
    * Instance of {@link GitClientService}
    */
   private static GitClientService instance;

   /**
    * @return {@link GitClientService}
    */
   public static GitClientService getInstance()
   {
      return instance;
   }

   protected GitClientService()
   {
      instance = this;
   }

   /**
    * Add changes to Git index (temporary storage).
    * 
    * @param workDir location of Git repository working directory
    * @param update if <code>true</code> then never stage new files, but stage modified new contents of tracked files
    * and remove files from the index if the corresponding files in the working tree have been removed 
    * @param filePattern pattern of the files to be added, default is "." (all files are added)
    * @param callback callback
    */
   public abstract void add(String workDir, boolean update, String[] filePattern, AsyncRequestCallback<String> callback);

   /**
    * Fetch changes from remote repository to local one.
    * 
    * @param workDir location of Git repository working directory
    * @param remote remote repository's name
    * @param refspec  list of refspec to fetch.
    * <p>
    * Expected form is:
    * <ul>
    * <li>
    * refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from
    * remote repository will be fetched to 'refs/remotes/origin/featured'.</li>
    * <li>featured - remote branch name.</li>
    * </ul>
    * @param removeDeletedRefs if <code>true</code> then delete removed refs from local repository
    * @param callback callback
    */
   public abstract void fetch(String workDir, String remote, String[] refspec, boolean removeDeletedRefs,
      AsyncRequestCallback<String> callback);

   /**
    * Get the list of the branches.
    * For now, all branches cannot be returned at once, so
    * the parameter <code>remote</code> tells to get remote branches 
    * if <code>true</code> or local ones (if <code>false</code>).
    * 
    * @param workDir location of Git repository working directory
    * @param remote get remote branches
    * @param callback callback
    */
   public abstract void branchList(String workDir, boolean remote, AsyncRequestCallback<List<Branch>> callback);

   /**
    * Delete branch.
    * 
    * @param workDir location of Git repository working directory
    * @param name name of the branch to delete
    * @param force force if <code>true</code> delete branch {@link #name} even if it is not fully merged
    * @param callback callback
    */
   public abstract void branchDelete(String workDir, String name, boolean force, AsyncRequestCallback<String> callback);

   /**
    * Create new branch with pointed name.
    * 
    * @param workDir location of Git repository working directory
    * @param name new branch's name
    * @param startPoint name of a commit at which to start the new branch
    * @param callback callback
    */
   public abstract void branchCreate(String workDir, String name, String startPoint,
      AsyncRequestCallback<Branch> callback);

   /**
    * Checkout the branch with pointed name.
    * 
    * @param workDir location of Git repository working directory
    * @param name branch's name
    * @param startPoint if {@link #createNew} is <code>true</code> then the name of a commit at which to start the new branch
    * @param createNew if <code>true</code> then create a new branch
    * @param callback callback
    */
   public abstract void branchCheckout(String workDir, String name, String startPoint, boolean createNew,
      AsyncRequestCallback<String> callback);

   /**
    * Get the list of remote repositories for pointed by <code>workDir</code> parameter one.
    * 
    * @param workDir  location of Git repository working directory
    * @param remoteName remote repository's name
    * @param verbose If <code>true</code> show remote url and name otherwise show remote name
    * @param callback callback
    */
   public abstract void remoteList(String workDir, String remoteName, boolean verbose,
      AsyncRequestCallback<List<Remote>> callback);

   /**
    * Adds remote repository to the list of remote repositories.
    * 
    * @param workDir location of Git repository working directory
    * @param name remote repository's name
    * @param url remote repository's URL
    * @param callback callback
    */
   public abstract void remoteAdd(String workDir, String name, String url, AsyncRequestCallback<String> callback);

   /**
    * Deletes the pointed(by name) remote repository from the list of repositories.
    * 
    * @param workDir location of Git repository working directory
    * @param name remote repository name to delete
    * @param callback callback
    */
   public abstract void remoteDelete(String workDir, String name, AsyncRequestCallback<String> callback);

   /**
    * Remove files from the working tree and the index.
    * 
    * @param workDir  location of Git repository working directory
    * @param files files to remove
    * @param callback callback
    */
   public abstract void remove(String workDir, String[] files, AsyncRequestCallback<String> callback);

   /**
    * Reset current HEAD to the specified state.
    * There two types of the reset: <br>
    * 1. Reset files in index -  content of files is untouched. Typically it is
    * useful to remove from index mistakenly added files.<br>
    * <code>git reset [paths]</code> is the opposite of <code>git add [paths]</code>.
    * 2. Reset the current branch head to [commit] and possibly 
    * updates the index (resetting it to the tree of [commit]) 
    * and the working tree depending on [mode].
    * 
    * @param workDir location of Git repository working directory
    * @param paths paths to reset
    * @param commit commit to which current head should be reset
    * @param resetType type of the reset
    * @param callback callback
    */
   public abstract void reset(String workDir, String[] paths, String commit, ResetRequest.ResetType resetType,
      AsyncRequestCallback<String> callback);

   /**
    * Initializes new Git repository.
    * 
    * @param workDir working directory of the new repository
    * @param bare to create bare repository or not
    * @param callback callback
    */
   public abstract void init(String workDir, boolean bare, AsyncRequestCallback<String> callback);

   /**
    * Pull(fetch and merge) changes from remote repository to local one.
    * 
    * @param workDir ocation of Git repository working directory
    * @param refSpec  list of refspec to fetch.
    * <p>
    * Expected form is:
    * <ul>
    * <li>
    * refs/heads/featured:refs/remotes/origin/featured - branch 'featured' from
    * remote repository will be fetched to 'refs/remotes/origin/featured'.</li>
    * <li>featured - remote branch name.</li>
    * </ul>
    * @param remote remote remote repository's name
    * @param callback callback
    */
   public abstract void pull(String workDir, String refSpec, String remote, AsyncRequestCallback<String> callback);

   /**
    * Push changes from local repository to remote one.
    * 
    * @param workDir location of Git repository working directory
    * @param refSpec list of refspec to push
    * @param remote remote repository name or url
    * @param force  push refuses to update a remote ref that is not 
    * an ancestor of the local ref used to overwrite it. If <code>true</code> disables the check. 
    * This can cause the remote repository to lose commits
    * @param callback callback
    */
   public abstract void push(String workDir, String[] refSpec, String remote, boolean force,
      AsyncRequestCallback<String> callback);

   /**
    * Clones one remote repository to local one.
    * 
    * @param workDir working directory of the new repository
    * @param remoteUri the location of the remote repository
    * @param remoteName remote name instead of "origin"
    * @param callback callback
    */
   public abstract void cloneRepository(String workDir, String remoteUri, String remoteName,
      AsyncRequestCallback<String> callback);

   /**
    * Performs commit changes from index to repository.
    * The result of the commit is represented by {@link Revision}, which is returned
    * by callback in <code>onSuccess(Revision result)</code>.
    * 
    * @param workDir location of Git repository working directory
    * @param message commit log message
    * @param all automatically stage files that have been modified and deleted
    * @param callback callback
    */
   public abstract void commit(String workDir, String message, boolean all, AsyncRequestCallback<Revision> callback);

   /**
    * Get the diff for pointed file(s) or for the whole project in text format.
    * 
    * @param workDir location of Git repository working directory
    * @param fileFilter files for which to show changes
    * @param type type of diff format
    * @param noRenames don't show renamed files
    * @param callback callback
    */
   public abstract void diff(String workDir, String[] fileFilter, DiffType type, boolean noRenames, AsyncRequestCallback<DiffResponse> callback);
   
   /**
    * Get log of commits.
    * The result is the list of {@link Revision},  which is returned
    * by callback in <code>onSuccess(Revision result)</code>.
    * 
    * @param workDir working directory of the Git repository
    * @param isTextFormat if <code>true</code> the loq response will be in text format
    * @param callback callback
    */
   public abstract void log(String workDir, boolean isTextFormat, AsyncRequestCallback<LogResponse> callback);

   /**
    * Gets the working tree status. The status of added, modified or deleted files is shown is written in {@link String}.
    * The format may be short or not.
    * Example of detailed format:<br>
    * <pre>
    * # Untracked files:
    * #
    * # file.html
    * # folder
    * </pre>
    * 
    * Example of short format:
    * <pre>
    * M  pom.xml
    * A  folder/test.html
    * D  123.txt
    * ?? folder/test.css
    * </pre>
    * 
    * @param workDir working directory of the Git repository
    * @param shortFormat to show in short format or not
    * @param fileFilter file filter to show status. It may be either list of file names to show status or name of directory to show all files under them.
    * @param callback callback
    */
   public abstract void statusText(String workDir, boolean shortFormat, String[] fileFilter,
      AsyncRequestCallback<StatusResponse> callback);

   /**
    * Gets the working tree status : list of untracked, changed not commited 
    * and changed not updated.
    * 
    * @param workDir working directory of the Git repository
    * @param callback callback
    */
   public abstract void status(String workDir, AsyncRequestCallback<StatusResponse> callback);

   /**
    * Get the Git work directory (where ".git" folder is located) 
    * for the pointed item's location.
    * 
    * @param href item's location
    * @param callback
    */
   public abstract void getWorkDir(String href, AsyncRequestCallback<WorkDirResponse> callback);

}
