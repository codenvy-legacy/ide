/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package org.exoplatform.ide.extension.samples.client.inviting.manage;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InvitedDeveloper implements Invite
{
   private String email;

   private String uuid;

   private Boolean activated;

   private Boolean valid;

   public InvitedDeveloper(String email, String uuid, Boolean activated, Boolean valid)
   {
      this.email = email;
      this.uuid = uuid;
      this.activated = activated;
      this.valid = valid;
   }

   @Override
   public void setEmail(String email)
   {
      this.email = email;
   }

   @Override
   public String getEmail()
   {
      return email;
   }

   @Override
   public void setUuid(String uuid)
   {
      this.uuid = uuid;
   }

   @Override
   public String getUuid()
   {
      return uuid;
   }

   @Override
   public void setActivated(Boolean activated)
   {
      this.activated = activated;
   }

   @Override
   public Boolean isActivated()
   {
      return activated;
   }

   @Override
   public void setValid(Boolean valid)
   {
      this.valid = valid;
   }

   @Override
   public Boolean isValid()
   {
      return valid;
   }

   @Override
   public String toString()
   {
      return "InvitedDeveloper{" +
         "email='" + email + '\'' +
         ", uuid='" + uuid + '\'' +
         ", activated=" + activated +
         ", valid=" + valid +
         '}';
   }
}
