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
 * Fetch data from remote repository.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: FetchRequest.java 22817 2011-03-22 09:17:52Z andrew00x $
 */
public class FetchRequest extends GitRequest
{
   /**
    * List of refspec to fetch.
    * <p>
    * Expected form is "refs/heads/featured:refs/remotes/origin/featured".
    * Branch 'featured' from remote repository will be fetched to
    * 'remotes/origin/featured'.
    */
   private String[] refSpec;

   /**
    * Remote name. If <code>null</code> then 'origin' will be used.
    */
   private String remote;

   /**
    * Remove refs in local branch if they are removed in remote branch.
    */
   private boolean removeDeletedRefs;

   /**
    * Time (in seconds) to wait without data transfer occurring before aborting
    * fetching data from remote repository.
    */
   private int timeout;

   /**
    * @param refSpec list of refspec to fetch
    * @param remote remote name. If <code>null</code> then 'origin' will be used
    * @param removeDeletedRefs remove or not refs in local branch if they are
    *           removed in remote branch
    * @param timeout time (in seconds) to wait without data transfer occurring
    *           before aborting fetching data from remote repository
    */
   public FetchRequest(String[] refSpec, String remote, boolean removeDeletedRefs, int timeout)
   {
      this.refSpec = refSpec;
      this.remote = remote;
      this.removeDeletedRefs = removeDeletedRefs;
      this.timeout = timeout;
   }

   /**
    * "Empty" fetch request. Corresponding setters used to setup required
    * parameters.
    */
   public FetchRequest()
   {
   }

   /**
    * @return list of refspec to fetch
    */
   public String[] getRefSpec()
   {
      return refSpec;
   }

   /**
    * @param refSpec list of refspec to fetch
    */
   public void setRefSpec(String[] refSpec)
   {
      this.refSpec = refSpec;
   }

   /**
    * @return remote name. If <code>null</code> then 'origin' will be used
    */
   public String getRemote()
   {
      return remote;
   }

   /**
    * @param remote remote name
    */
   public void setRemote(String remote)
   {
      this.remote = remote;
   }

   /**
    * @return <code>true</code> if local refs must be deleted if they deleted in
    *         remote repository and <code>false</code> otherwise
    */
   public boolean isRemoveDeletedRefs()
   {
      return removeDeletedRefs;
   }

   /**
    * @param removeDeletedRefs <code>true</code> if local refs must be deleted
    *           if they deleted in remote repository and <code>false</code>
    *           otherwise
    */
   public void setRemoveDeletedRefs(boolean removeDeletedRefs)
   {
      this.removeDeletedRefs = removeDeletedRefs;
   }

   /**
    * @return time (in seconds) to wait without data transfer occurring before
    *         aborting fetching data from remote repository
    */
   public int getTimeout()
   {
      return timeout;
   }

   /**
    * @param timeout time (in seconds) to wait without data transfer occurring
    *           before aborting fetching data from remote repository
    */
   public void setTimeout(int timeout)
   {
      this.timeout = timeout;
   }
}
