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
 * Request to update tracked repositories.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class RemoteUpdateRequest extends GitRequest
{
   /** Remote name. */
   private String name;

   /**
    * Updates for list of currently tracked branches.
    * 
    * @see #addBranches
    */
   private String[] branches;

   /**
    * If <code>true</code> {@link #branches} instead of replacing the list of
    * currently tracked branches, added to that list.
    */
   private boolean addBranches;

   /** Remote URLs to be added. */
   private String[] addUrl;

   /** Remote URLs to be removed. */
   private String[] removeUrl;

   /** Remote push URLs to be added. */
   private String[] addPushUrl;

   /** Remote push URLs to be removed. */
   private String[] removePushUrl;

   /**
    * @param name name of remote
    * @param branches updates for list of currently tracked branches
    * @param addBranches if <code>true</code> then <code>branches</code> instead
    *           of replacing the list of currently tracked branches, added to
    *           that list
    * @param addUrl remote URLs to be added
    * @param removeUrl remote URLs to be removed
    * @param addPushUrl remote push URLs to be added
    * @param removePushUrl remote push URLs to be removed
    */
   public RemoteUpdateRequest(String name, String[] branches, boolean addBranches, String[] addUrl, String[] removeUrl,
      String[] addPushUrl, String[] removePushUrl)
   {
      this.name = name;
      this.branches = branches;
      this.addBranches = addBranches;
      this.addUrl = addUrl;
      this.removeUrl = removeUrl;
      this.addPushUrl = addPushUrl;
      this.removePushUrl = removePushUrl;
   }

   /**
    * "Empty" request for update remote. Corresponding setters used to setup
    * required parameters.
    */
   public RemoteUpdateRequest()
   {
   }

   /**
    * @return remote name
    * @see #name
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name remote name
    * @see #name
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return list tracked branches
    * @see #branches
    */
   public String[] getBranches()
   {
      return branches;
   }

   /**
    * @param branches list tracked branches
    * @see #branches
    */
   public void setBranches(String[] branches)
   {
      this.branches = branches;
   }

   /**
    * @return if <code>true</code> then {@link #branches} instead of replacing
    *         the list of currently tracked branches, added to that list
    * @see #addBranches
    */
   public boolean isAddBranches()
   {
      return addBranches;
   }

   /**
    * @param addBranches if <code>true</code> then {@link #branches} instead of
    *           replacing the list of currently tracked branches, added to that
    *           list
    * @see #addBranches
    */
   public void setAddBranches(boolean addBranches)
   {
      this.addBranches = addBranches;
   }

   /**
    * @return remote URLs to be added
    */
   public String[] getAddUrl()
   {
      return addUrl;
   }

   public void setAddUrl(String[] addUrl)
   {
      this.addUrl = addUrl;
   }

   /**
    * @return remote URLs to be removed
    */
   public String[] getRemoveUrl()
   {
      return removeUrl;
   }

   /**
    * @param removeUrl URLs to be removed
    */
   public void setRemoveUrl(String[] removeUrl)
   {
      this.removeUrl = removeUrl;
   }

   /**
    * @return remote push URLs to be added
    */
   public String[] getAddPushUrl()
   {
      return addPushUrl;
   }

   /**
    * @param addPushUrl push URLs to be added
    */
   public void setAddPushUrl(String[] addPushUrl)
   {
      this.addPushUrl = addPushUrl;
   }

   /**
    * @return remote push URLs to be removed
    */
   public String[] getRemovePushUrl()
   {
      return removePushUrl;
   }

   /**
    * @param removePushUrl remote push URLs to be removed
    */
   public void setRemovePushUrl(String[] removePushUrl)
   {
      this.removePushUrl = removePushUrl;
   }
}
