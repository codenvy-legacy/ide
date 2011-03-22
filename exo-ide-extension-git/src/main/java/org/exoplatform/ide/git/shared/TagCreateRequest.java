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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: TagCreateRequest.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class TagCreateRequest extends GitRequest
{
   private String name;
   private String revision;
   private String message;
   private boolean signed;
   private boolean forceUpdate;

   /**
    * @param name
    * @param revision
    * @param user
    * @param message
    * @param signed
    * @param forceUpdate
    */
   public TagCreateRequest(String name, String revision, GitUser user, String message, boolean signed,
      boolean forceUpdate)
   {
      this.name = name;
      this.revision = revision;
      this.message = message;
      this.signed = signed;
      this.forceUpdate = forceUpdate;
      setUser(user);
   }

   /**
    * @param name
    * @param revision
    * @param user
    * @param message
    */
   public TagCreateRequest(String name, String revision, GitUser user, String message)
   {
      this.name = name;
      this.revision = revision;
      this.message = message;
      setUser(user);
   }

   public TagCreateRequest()
   {
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   public String getRevision()
   {
      return revision;
   }

   public void setRevision(String revision)
   {
      this.revision = revision;
   }

   public String getMessage()
   {
      return message;
   }

   public void setMessage(String message)
   {
      this.message = message;
   }

   public boolean isSigned()
   {
      return signed;
   }

   public void setSigned(boolean signed)
   {
      this.signed = signed;
   }

   public boolean isForceUpdate()
   {
      return forceUpdate;
   }

   public void setForceUpdate(boolean forceUpdate)
   {
      this.forceUpdate = forceUpdate;
   }
}
