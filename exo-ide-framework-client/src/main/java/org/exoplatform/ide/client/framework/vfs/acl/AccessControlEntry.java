/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.framework.vfs.acl;

import java.util.ArrayList;
import java.util.List;

/**
 *This class represents one identity with this permissions;
 *Do not change <var>permissionsList</var> directly by <var>getPermissionsList()</var>,
 *use <code>addPermission()</code>, <code>removePermission()</code> instead 
 *<br>
 * Created by The eXo Platform SAS .
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 18, 2010 $
 *
 */
public class AccessControlEntry
{
   
   /**
    *The name of identity(its user name, group or role) 
    */
   private String identity;

   /**
    * The list of allowed permissions
    */
   private List<Permissions> permissionsList = new ArrayList<Permissions>();

   /**
    * @param identity
    */
   public AccessControlEntry(String identity)
   {
      this.identity = identity;
   }

   public AccessControlEntry(String identity, Permissions permission)
   {
      this.identity = identity;
      this.permissionsList.add(permission);
   }
   
   public AccessControlEntry(String identity, List<Permissions> permissions)
   {
      this.identity = identity;
      this.permissionsList.addAll(permissions);
   }
   
   /**
    * @return list of permission
    */
   public List<Permissions> getPermissionsList()
   {
      return permissionsList;
   }

   /**
    * Add specific permission 
    * 
    * @param permission
    */
   public void addPermission(Permissions permission)
   {

      if (!permissionsList.contains(permission))
         permissionsList.add(permission);
   }

   /**
    * Remove specific permissions 
    * @param permissions
    */
   public void removePermission(Permissions permissions)
   {
      if (permissionsList.contains(permissions))
      {
         permissionsList.remove(permissions);
      }
   }

   /**
    * @return the identity
    */
   public String getIdentity()
   {
      return identity;
   }

   
   /**
    * @param identity the identity to set
    */
   public void setIdentity(String identity)
   {
      this.identity = identity;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if(!(obj instanceof AccessControlEntry))
         return false;
      
      AccessControlEntry entry = (AccessControlEntry) obj;
      
      return this.identity.equals(entry.getIdentity());
   }

   /**
    * Add all permissions
    * @param permissions list
    */
   public void addPermissionsList(List<Permissions> permissions)
   {
      for(Permissions p : permissions)
      {
         addPermission(p);
      }
   }
}
