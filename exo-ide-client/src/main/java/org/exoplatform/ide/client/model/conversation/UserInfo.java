/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ide.client.model.conversation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UserInfo
{
   
   public static final String DEFAULT_USER_NAME = "DefaultUser";

   private String name;
   
   private List<String> groups;
   
   private List<String> roles;

   public UserInfo()
   {
   }

   public UserInfo(String name)
   {
      this.name = name;
   }
   
   

   public UserInfo(String name, List<String> groups, List<String> roles)
   {
      this.name = name;
      this.groups = groups;
      this.roles = roles;
   }

   public String getName()
   {
      return name;
   }

   public void setName(String name)
   {
      this.name = name;
   }

   /**
    * @return the groups
    */
   public List<String> getGroups()
   {
      if (groups == null)
         groups = new ArrayList<String>();
      return groups;
   }

   /**
    * @param groups the groups to set
    */
   public void setGroups(List<String> groups)
   {
      this.groups = groups;
   }

   /**
    * @return the roles
    */
   public List<String> getRoles()
   {
      if (roles == null) 
         roles = new ArrayList<String>();
      return roles;
   }

   /**
    * @param roles the roles to set
    */
   public void setRoles(List<String> roles)
   {
      this.roles = roles;
   }
   
   

}
