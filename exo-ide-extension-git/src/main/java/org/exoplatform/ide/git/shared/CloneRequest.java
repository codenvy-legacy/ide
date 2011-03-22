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
package org.exoplatform.ide.git.shared;


/**
 * Clone repository to {@link #workingDir}.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: CloneRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class CloneRequest extends GitRequest
{
   /**
    * URI of repository to be cloned.
    */
   private String remoteUri;

   /**
    * List of refspec to fetch in cloned repository.
    * <p>
    * Expected form is "refs/heads/featured:refs/remotes/origin/featured".
    * Branch 'featured' from remote repository will be cloned to
    * 'remotes/origin/featured'.
    */
   private String[] branchesToFetch;

   /**
    * Work directory for cloning.
    */
   private String workingDir;

   /**
    * Remote name. If <code>null</code> then 'origin' will be used.
    */
   private String remoteName;

   /**
    * Time (in seconds) to wait without data transfer occurring before aborting
    * fetching data from remote repository.
    */
   private int timeout;

   /**
    * @param remoteUri URI of repository to be cloned
    * @param branchesToFetch list of remote branches to fetch in cloned
    *           repository
    * @param workingDir work directory for cloning
    * @param remoteName remote name
    * @param user git user
    * @param timeout time (in seconds) to wait without data transfer occurring
    *           before aborting fetching data from remote repository
    */
   public CloneRequest(String remoteUri, String[] branchesToFetch, String workingDir, String remoteName, GitUser user,
      int timeout)
   {
      this.remoteUri = remoteUri;
      this.branchesToFetch = branchesToFetch;
      this.workingDir = workingDir;
      this.remoteName = remoteName;
      this.timeout = timeout;
      setUser(user);
   }

   /**
    * @param remoteUri URI of repository to be cloned
    * @param workingDir work directory for cloning
    * @param user git user
    */
   public CloneRequest(String remoteUri, String workingDir, GitUser user)
   {
      this.remoteUri = remoteUri;
      this.workingDir = workingDir;
      setUser(user);
   }

   /**
    * "Empty" request to clone repository. Corresponding setters used to setup
    * required behavior.
    */
   public CloneRequest()
   {
   }

   /**
    * @return URI of repository to be cloned
    */
   public String getRemoteUri()
   {
      return remoteUri;
   }

   /**
    * @param remoteUri URI of repository to be cloned
    */
   public void setRemoteUri(String remoteUri)
   {
      this.remoteUri = remoteUri;
   }

   /**
    * @return list of remote branches to fetch in cloned repository
    */
   public String[] getBranchesToFetch()
   {
      return branchesToFetch;
   }

   /**
    * @param branchesToFetch list of remote branches to fetch in cloned
    *           repository
    */
   public void setBranchesToFetch(String[] branchesToFetch)
   {
      this.branchesToFetch = branchesToFetch;
   }

   /**
    * @return work directory for cloning
    */
   public String getWorkingDir()
   {
      return workingDir;
   }

   /**
    * @param workingDir work directory for cloning
    */
   public void setWorkingDir(String workingDir)
   {
      this.workingDir = workingDir;
   }

   /**
    * @return remote name. If <code>null</code> then 'origin' will be used
    */
   public String getRemoteName()
   {
      return remoteName;
   }

   /**
    * @param remoteName remote name. If <code>null</code> then 'origin' will be
    *           used
    */
   public void setRemoteName(String remoteName)
   {
      this.remoteName = remoteName;
   }

   /**
    * @return time (in seconds) to wait without data transfer occurring before
    *         aborting fetching data from remote repository. If 0 then default
    *         timeout may be used. This is implementation specific
    */
   public int getTimeout()
   {
      return timeout;
   }

   /**
    * @param timeout time (in seconds) to wait without data transfer occurring
    *           before aborting fetching data from remote repository. If 0 then
    *           default timeout may be used. This is implementation specific
    */
   public void setTimeout(int timeout)
   {
      this.timeout = timeout;
   }
}
