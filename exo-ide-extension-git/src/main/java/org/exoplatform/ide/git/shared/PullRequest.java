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
 * Request to pull (fetch and merge) changes from remote repository to local
 * branch.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: PullRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class PullRequest extends GitRequest
{
   // TODO : docs
   private String refSpec;

   /**
    * Remote name. If <code>null</code> then 'origin' will be used.
    */
   private String remote;

   /**
    * Time (in seconds) to wait without data transfer occurring before aborting
    * fetching data from remote repository.
    */
   private int timeout;

   /**
    * @param timeout time (in seconds) to wait without data transfer occurring
    *           before aborting fetching data from remote repository
    */
   public PullRequest(String remote, String refSpec, int timeout)
   {
      this.remote = remote;
      this.refSpec = refSpec;
      this.timeout = timeout;
   }

   /**
    * "Empty" pull request. Corresponding setters used to setup required
    * parameters.
    */
   public PullRequest()
   {
   }

   /**
    * @return refspec to fetch
    * @see #refSpec
    */
   public String getRefSpec()
   {
      return refSpec;
   }

   /**
    * @param refSpec refspec to fetch
    * @see #refSpec
    */
   public void setRefSpec(String refSpec)
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
