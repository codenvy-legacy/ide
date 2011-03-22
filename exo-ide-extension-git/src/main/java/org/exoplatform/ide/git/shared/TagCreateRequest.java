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
 * Request to create new tag.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagCreateRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagCreateRequest extends GitRequest
{
   /**
    * Name of tag to create.
    */
   private String name;

   /**
    * Commit to make tag. If <code>null</code> then HEAD will be used.
    */
   private String commit;

   /**
    * Message for the tag.
    */
   private String message;

   /**
    * Force create tag. If tag with the same exists it will be replaced.
    */
   private boolean forceUpdate;

   /**
    * @param name name of tag to create
    * @param commit commit to make tag
    * @param user user to be assigned as tag creator
    * @param message message for the tag
    * @param forceUpdate force create tag operation
    */
   public TagCreateRequest(String name, String commit, GitUser user, String message, boolean forceUpdate)
   {
      this.name = name;
      this.commit = commit;
      this.message = message;
      this.forceUpdate = forceUpdate;
      setUser(user);
   }

   /**
    * @param name name of tag to create
    * @param commit commit to make tag
    * @param user user to be assigned as tag creator
    * @param message message for the tag
    */
   public TagCreateRequest(String name, String commit, GitUser user, String message)
   {
      this.name = name;
      this.commit = commit;
      this.message = message;
      setUser(user);
   }

   /**
    * "Empty" create tag request. Corresponding setters used to setup required
    * parameters.
    */
   public TagCreateRequest()
   {
   }

   /**
    * @return name of tag to create
    */
   public String getName()
   {
      return name;
   }

   /**
    * @param name name of tag to create
    */
   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return commit to make tag. If <code>null</code> then HEAD is used
    */
   public String getCommit()
   {
      return commit;
   }

   /**
    * @param commit commit to make tag. If <code>null</code> then HEAD is used
    */
   public void setCommit(String commit)
   {
      this.commit = commit;
   }

   /**
    * @return message for tag
    */
   public String getMessage()
   {
      return message;
   }

   /**
    * @param message message for tag
    */
   public void setMessage(String message)
   {
      this.message = message;
   }

   /**
    * @return force create tag operation
    * @see #forceUpdate
    */
   public boolean isForceUpdate()
   {
      return forceUpdate;
   }

   /**
    * @param forceUpdate if <code>true</code> force create tag operation
    * @see #forceUpdate
    */
   public void setForceUpdate(boolean forceUpdate)
   {
      this.forceUpdate = forceUpdate;
   }
}
