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
package org.exoplatform.ide.invite;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import java.util.concurrent.TimeUnit;

public class Invite
{
   /**
    * Time in millisecond how long invite will be valid.
    * Default 14 days.
    */
   private final static long INVITE_EXPIRATION_TIME = TimeUnit.DAYS.toMillis(14);

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      final int prime = 31;
      int result = 1;
      result = prime * result + (activated ? 1231 : 1237);
      result = prime * result + ((email == null) ? 0 : email.hashCode());
      result = prime * result + (int)(invitationTime ^ (invitationTime >>> 32));
      result = prime * result + ((password == null) ? 0 : password.hashCode());
      result = prime * result + ((uuid == null) ? 0 : uuid.hashCode());
      result = prime * result + (valid ? 1231 : 1237);
      return result;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
      {
         return true;
      }
      if (obj == null)
      {
         return false;
      }
      if (!(obj instanceof Invite))
      {
         return false;
      }
      Invite other = (Invite)obj;
      if (activated != other.activated)
      {
         return false;
      }
      if (email == null)
      {
         if (other.email != null)
         {
            return false;
         }
      }
      else if (!email.equals(other.email))
      {
         return false;
      }
      if (invitationTime != other.invitationTime)
      {
         return false;
      }
      if (password == null)
      {
         if (other.password != null)
         {
            return false;
         }
      }
      else if (!password.equals(other.password))
      {
         return false;
      }
      if (uuid == null)
      {
         if (other.uuid != null)
         {
            return false;
         }
      }
      else if (!uuid.equals(other.uuid))
      {
         return false;
      }
      if (valid != other.valid)
      {
         return false;
      }
      return true;
   }

   private String email;

   private String uuid;

   private String password;

   private boolean activated;

   private boolean valid;

   private long invitationTime;

   public void setEmail(String email)
   {
      this.email = email;
   }

   public String getEmail()
   {
      return email;
   }

   public void setUuid(String uuid)
   {
      this.uuid = uuid;
   }

   public String getUuid()
   {
      return uuid;
   }

   public void setActivated(boolean activated)
   {
      this.activated = activated;
   }

   public boolean isActivated()
   {
      return activated;
   }

   public void setInvitationTime(long invitationTime)
   {
      this.invitationTime = invitationTime;
   }

   public long getInvitationTime()
   {
      return invitationTime;
   }

   public void setPassword(String password)
   {
      this.password = password;
   }

   public String getPassword()
   {
      return password;
   }

   /**
    * @param valid
    *    the valid to set
    */
   public void setValid(boolean valid)
   {
      this.valid = valid;
   }

   public boolean isValid()
   {
      return valid;
   }

   @Override
   public String toString()
   {
      final StringBuffer sb = new StringBuffer();
      sb.append("Invite");
      sb.append("{email='").append(email).append('\'');
      sb.append(", uuid='").append(uuid).append('\'');
      sb.append(", password='").append(password).append('\'');
      sb.append(", activated=").append(activated);
      sb.append(", valid=").append(valid);
      sb.append(", invitationTime=").append(invitationTime);
      sb.append('}');
      return sb.toString();
   }

   public Element asElementAttributes(Document invitesDocument)
   {
      Element newInvite = invitesDocument.createElement(NameGenerator.generate("invite", 16));
      newInvite.setAttribute("email", email);
      newInvite.setAttribute("uuid", uuid);
      newInvite.setAttribute("password", password);
      newInvite.setAttribute("activated", Boolean.toString(activated));
      //valid field is not persisted,it is calculated on read
      newInvite.setAttribute("invitationTime", Long.toString(invitationTime));
      return newInvite;
   }

   public static Invite valueOf(NamedNodeMap attributes)
   {

      Invite invite = new Invite();
      invite.setEmail(attributes.getNamedItem("email").getNodeValue());
      invite.setUuid(attributes.getNamedItem("uuid").getNodeValue());
      invite.setPassword(attributes.getNamedItem("password").getNodeValue());
      invite.setActivated(Boolean.parseBoolean(attributes.getNamedItem("activated").getNodeValue()));

      //support of invitationTime in other places discontinued.
      if (attributes.getNamedItem("invitationTime") != null)
      {
         invite.setInvitationTime(Long.parseLong(attributes.getNamedItem("invitationTime").getNodeValue()));
      }
      else
      {
         invite.setInvitationTime(0);
      }

      invite.setValid(System.currentTimeMillis() <= (invite.getInvitationTime() + INVITE_EXPIRATION_TIME));

      return invite;
   }

}
