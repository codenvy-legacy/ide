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
 * Request to add remote configuration {@link #name} for repository at
 * {@link #url}.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RemoteAddRequest extends GitRequest
{
   /** Remote name. */
   private String name;

   /** Repository url. */
   private String url;

   /**
    * List of tracked branches in remote repository. If not set then track all
    * branches.
    */
   private String[] branches;

   /**
    * @param name remote name
    * @param url repository url
    * @param branches list of tracked branches in remote repository. If not set
    *           then track all branches
    */
   public RemoteAddRequest(String name, String url, String[] branches)
   {
      this.name = name;
      this.url = url;
      this.branches = branches;
   }

   /**
    * @param name remote name
    * @param url repository url
    */
   public RemoteAddRequest(String name, String url)
   {
      this.name = name;
      this.url = url;
   }

   /**
    * "Empty" request for create remote configuration. Corresponding setters
    * used to setup required parameters.
    */
   public RemoteAddRequest()
   {
   }

   /**
    * @return remote name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name remote name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return repository url
    */
   public String getUrl()
   {
      return url;
   }

   /**
    * @param url repository url
    */
   public void setUrl(String url)
   {
      this.url = url;
   }

   /**
    * @return list of tracked branches in remote repository
    * @see #branches
    */
   public String[] getBranches()
   {
      return branches;
   }

   /**
    * @param branches list of tracked branches in remote repository
    * @see #branches
    */
   public void setBranches(String[] branches)
   {
      this.branches = branches;
   }
}
