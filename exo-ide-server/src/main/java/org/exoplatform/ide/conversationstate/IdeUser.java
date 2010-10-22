/*
 * Copyright (C) 2003-2010 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.conversationstate;

import java.util.Collection;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class IdeUser
{
   private String userId;
   
   private Collection<String> groups;
   
   private Collection<String> roles;
   
   public IdeUser()
   {
   }
   
   /**
    * @param userId the userId to set
    * @param groups the groups to set
    * @param roles the roles to set
    */
   public IdeUser(String userId, Collection<String> groups, Collection<String> roles)
   {
      this.userId = userId;
      this.groups = groups;
      this.roles = roles;
   }



   /**
    * @return the userId
    */
   public String getUserId()
   {
      return userId;
   }

   /**
    * @param userId the userId to set
    */
   public void setUserId(String userId)
   {
      this.userId = userId;
   }

   /**
    * @return the groups
    */
   public Collection<String> getGroups()
   {
      return groups;
   }

   /**
    * @param groups the groups to set
    */
   public void setGroups(Collection<String> groups)
   {
      this.groups = groups;
   }

   /**
    * @return the roles
    */
   public Collection<String> getRoles()
   {
      return roles;
   }

   /**
    * @param roles the roles to set
    */
   public void setRoles(Collection<String> roles)
   {
      this.roles = roles;
   }
   
   
}
