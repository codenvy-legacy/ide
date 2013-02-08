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

   @Override
   public boolean equals(Object o)
   {
      if (this == o)
      {
         return true;
      }
      if (o == null || getClass() != o.getClass())
      {
         return false;
      }

      Invite invite = (Invite)o;

      if (activated != invite.activated)
      {
         return false;
      }
      if (invitationTime != invite.invitationTime)
      {
         return false;
      }
      if (valid != invite.valid)
      {
         return false;
      }
      if (!email.equals(invite.email))
      {
         return false;
      }
      if (from != null ? !from.equals(invite.from) : invite.from != null)
      {
         return false;
      }
      if (!password.equals(invite.password))
      {
         return false;
      }
      if (!uuid.equals(invite.uuid))
      {
         return false;
      }

      return true;
   }

   @Override
   public int hashCode()
   {
      int result = from != null ? from.hashCode() : 0;
      result = 31 * result + email.hashCode();
      result = 31 * result + uuid.hashCode();
      result = 31 * result + password.hashCode();
      result = 31 * result + (activated ? 1 : 0);
      result = 31 * result + (valid ? 1 : 0);
      result = 31 * result + (int)(invitationTime ^ (invitationTime >>> 32));
      return result;
   }

   private String from;

   private String email;

   private String uuid;

   private String password;

   private boolean activated;

   private boolean valid;

   private long invitationTime;

   public void setFrom(String from)
   {
      this.from = from;
   }

   public String getFrom()
   {
      return from;
   }

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
      return "Invite{" +
         "from='" + from + '\'' +
         ", email='" + email + '\'' +
         ", uuid='" + uuid + '\'' +
         ", password='" + password + '\'' +
         ", activated=" + activated +
         ", valid=" + valid +
         ", invitationTime=" + invitationTime +
         '}';
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
      newInvite.setAttribute("from", from);
      return newInvite;
   }

   public static Invite valueOf(NamedNodeMap attributes)
   {

      Invite invite = new Invite();
      invite.setEmail(attributes.getNamedItem("email").getNodeValue());
      invite.setUuid(attributes.getNamedItem("uuid").getNodeValue());
      invite.setPassword(attributes.getNamedItem("password").getNodeValue());
      invite.setActivated(Boolean.parseBoolean(attributes.getNamedItem("activated").getNodeValue()));

      if (attributes.getNamedItem("from") != null)
      {
         invite.setFrom(attributes.getNamedItem("from").getNodeValue());
      }

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
