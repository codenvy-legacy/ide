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
import org.exoplatform.ide.git.client.marshaller.StatusResponse;
import org.exoplatform.ide.git.client.marshaller.WorkDirResponse;
import org.exoplatform.ide.git.shared.Revision;

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
    * Initializes new Git repository.
    * 
    * @param workDir working directory of the new repository
    * @param bare to create bare repository or not
    * @param callback callback
    */
   public abstract void init(String workDir, boolean bare, AsyncRequestCallback<String> callback);
   
   /**
    * Clones one remote repository to local one.
    * 
    * @param workDir working directory of the new repository
    * @param remoteUri the location of the remote repository
    * @param remoteName remote name instead of "origin"
    * @param callback callback
    */
   public abstract void cloneRepository(String workDir, String remoteUri, String remoteName, AsyncRequestCallback<String> callback);
   
   /**
    * Performs commit changes from index to repository.
    * The result of the commit is represented by {@link Revision}, which is returned
    * by callback in <code>onSuccess(Revision result)</code>.
    * 
    * @param workDir location of Git repository working directory
    * @param message commit log message
    * @param callback callback
    */
   public abstract void commit(String workDir, String message, AsyncRequestCallback<Revision> callback);
   
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
   public abstract void status(String workDir, boolean shortFormat, String[] fileFilter, AsyncRequestCallback<StatusResponse> callback);

   /**
    * Get the Git work directory (where ".git" folder is located) 
    * for the pointed item's location.
    * 
    * @param href item's location
    * @param callback
    */
   public abstract void getWorkDir(String href,  AsyncRequestCallback<WorkDirResponse> callback);

}
