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
 *This class represent all permissions of item.<br>
 *Do not change <var>permissions</var> directly by <var>getPermissionsList()</var>,
 *use <var>addPermissions</var>,
 *<var>removePermission</var> methods instead.
 *<br>
 * Created by The eXo Platform SAS .
 * @author <a href="tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Oct 18, 2010 $
 *
 */
public class AccessControlList
{

   /**
    * The name of item owner 
    */
   private String owner;

   /**
    *The list of item permissions
    */
   private List<AccessControlEntry> permissions = new ArrayList<AccessControlEntry>();

   /**
    * Add new permission
    * @param access control entry
    */
   public void addPermission(AccessControlEntry ace)
   {
      permissions.add(ace);
   }

   /**
    * Get permission for specific identity
    * @param identity name
    * @return permissions for identity or <b>null</b> if identity not found  
    */
   public AccessControlEntry getPermisions(String identity)
   {
      for (AccessControlEntry e : permissions)
      {
         if (e.getIdentity().equals(identity))
         {
            return e;
         }
      }
      return null;
   }

   /**
    * Add specific permission for identity
    * @param identity name
    * @param permission
    */
   public void addPermission(String identity, Permissions permission)
   {
      for (AccessControlEntry e : permissions)
      {
         if (e.getIdentity().equals(identity))
         {
            e.addPermission(permission);
            return;
         }
      }

      permissions.add(new AccessControlEntry(identity, permission));
   }

   /**
    * Add list permissions for identity.
    * If identity not found, creates new access control entry
    * @param identity
    * @param permissions
    */
   public void addPermissions(String identity, List<Permissions> permissions)
   {
      for (AccessControlEntry e : this.permissions)
      {
         if (e.getIdentity().equals(identity))
         {
            e.addPermissionsList(permissions);
            return;
         }
      }

      this.permissions.add(new AccessControlEntry(identity, permissions));
   }

   /**
    * Remove specific permission for identity
    * @param identity
    * @param permission
    */
   public void removePermission(String identity, Permissions permission)
   {
      AccessControlEntry entry = null;
      for (AccessControlEntry e : permissions)
      {
         if (e.getIdentity().equals(identity))
         {
            e.removePermission(permission);
            entry = e;
            break;
         }
      }
      if (entry != null)
      {
         if (entry.getPermissionsList().size() == 0)
         {
            permissions.remove(entry);
         }
      }

   }

   /**
    * Remove all permission for identity
    * @param identity
    */
   public void removePermission(String identity)
   {
      for (int i = 0; permissions.size() > i; i++)
      {
         if (permissions.get(i).getIdentity().equals(identity))
         {
            permissions.remove(i);
            break;
         }
      }
   }

   /**
    * Remove access controls entry's with empty permission list.
    */
   public void removeEmptyPermissions()
   {
      List<AccessControlEntry> entyForRemove = new ArrayList<AccessControlEntry>();
      for (AccessControlEntry e : permissions)
      {
         if (e.getPermissionsList().size() == 0)
         {
            entyForRemove.add(e);
         }
      }

      for (AccessControlEntry e : entyForRemove)
      {
         permissions.remove(e);
      }
   }

   /**
    * @return the permissions list
    */
   public List<AccessControlEntry> getPermissionsList()
   {
      return permissions;
   }

   /**
    * Remove all permissions
    */
   public void clear()
   {
      permissions.clear();
   }

   /**
    * @return the owner
    */
   public String getOwner()
   {
      return owner;
   }

   /**
    * @param owner the owner to set
    */
   public void setOwner(String owner)
   {
      this.owner = owner;
   }

}
