/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.server.impl;

import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.services.security.ConversationState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
abstract class MemoryItem
{
   private static String generateId()
   {
      return UUID.randomUUID().toString();
   }

   MemoryFolder parent;
   final ItemType type;
   final String id;
   String name;
   String mediaType;
   final Map<String, Set<String>> permissionsMap;
   final Map<String, List<String>> properties;

   final long creationDate;
   long lastModificationDate;

   MemoryItem(ItemType type, String name) throws VirtualFileSystemException
   {
      this(type, generateId(), name);
   }

   // For root folder only since don't want to have random ID for it!!!
   MemoryItem(ItemType type, String id, String name)
      throws VirtualFileSystemException
   {
      this.type = type;
      this.id = id;
      this.name = name;
      this.permissionsMap = new HashMap<String, Set<String>>();
      this.properties = new HashMap<String, List<String>>();
      this.creationDate = this.lastModificationDate = System.currentTimeMillis();
      if (parent != null)
      {
         parent.addChild(this);
      }
   }

   final MemoryFolder getParent()
   {
      return parent;
   }

   final void setParent(MemoryFolder parent)
   {
      this.parent = parent;
   }

   final ItemType getType()
   {
      return type;
   }

   final String getId()
   {
      return id;
   }

   final String getName()
   {
      return name;
   }

   final void setName(String name)
   {
      this.name = name;
      lastModificationDate = System.currentTimeMillis();
   }

   final String getMediaType()
   {
      return mediaType;
   }

   final void setMediaType(String mediaType)
   {
      this.mediaType = mediaType;
      lastModificationDate = System.currentTimeMillis();
   }

   final String getPath()
   {
      if (MemoryFileSystemContext.ROOT_FOLDER_ID.equals(id))
      {
         return "/";
      }

      MemoryFolder parent = this.parent;
      if (parent == null)
      {
         return null; // item is not root folder but not added in tree yet.
      }

      String name = this.name;
      LinkedList<String> pathSegments = new LinkedList<String>();
      pathSegments.add(name);

      while (parent != null)
      {
         pathSegments.addFirst(parent.getName());
         parent = parent.getParent();
      }

      StringBuilder path = new StringBuilder();
      path.append('/');
      for (String seg : pathSegments)
      {
         if (path.length() > 1)
         {
            path.append('/');
         }
         path.append(seg);
      }
      return path.toString();
   }

   final List<AccessControlEntry> getACL()
   {
      List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>();
      synchronized (permissionsMap)
      {
         for (Map.Entry<String, Set<String>> e : permissionsMap.entrySet())
         {
            acl.add(new AccessControlEntry(e.getKey(), new HashSet<String>(e.getValue())));
         }
      }
      return acl;
   }

   final void updateACL(List<AccessControlEntry> acl, boolean override)
   {
      synchronized (permissionsMap)
      {
         if (override)
         {
            permissionsMap.clear();
         }

         for (AccessControlEntry ace : acl)
         {
            String principal = ace.getPrincipal();
            Set<String> permissions = permissionsMap.get(principal);
            if (permissions == null)
            {
               permissions = new HashSet<String>();
               permissionsMap.put(principal, permissions);
            }
            permissions.addAll(ace.getPermissions());
         }
      }
      lastModificationDate = System.currentTimeMillis();
   }

   final List<Property> getProperties(PropertyFilter filter)
   {
      List<Property> result = new ArrayList<Property>();
      synchronized (properties)
      {
         for (Map.Entry<String, List<String>> e : properties.entrySet())
         {
            String name = e.getKey();
            if (filter.accept(name))
            {
               List<String> value = e.getValue();
               if (value != null)
               {
                  List<String> copy = new ArrayList<String>(value.size());
                  copy.addAll(value);
                  result.add(new Property(name, copy));
               }
               else
               {
                  result.add(new Property(name, (String)null));
               }
            }
         }
      }
      return result;
   }

   final void updateProperties(List<ConvertibleProperty> update)
   {
      synchronized (properties)
      {
         for (Property p : update)
         {
            String name = p.getName();
            List<String> value = p.getValue();
            if (value != null)
            {
               List<String> copy = new ArrayList<String>(value.size());
               copy.addAll(value);
               properties.put(name, copy);
            }
            else
            {
               properties.put(name, null);
            }
         }
      }
      lastModificationDate = System.currentTimeMillis();
   }

   final Map<String, List<String>> getProperties()
   {
      synchronized (properties)
      {
         Map<String, List<String>> copy = new HashMap<String, List<String>>(properties.size());
         for (Map.Entry<String, List<String>> e : properties.entrySet())
         {
            String name = e.getKey();
            List<String> value = e.getValue();
            if (value != null)
            {
               List<String> copyValue = new ArrayList<String>(value.size());
               copyValue.addAll(value);
               copy.put(name, copyValue);
            }
            else
            {
               copy.put(name, null);
            }
         }
         return copy;
      }
   }

   final void updateProperties(Map<String, List<String>> update)
   {
      synchronized (properties)
      {
         for (Map.Entry<String, List<String>> p : update.entrySet())
         {
            String name = p.getKey();
            List<String> value = p.getValue();
            if (value != null)
            {
               List<String> valueCopy = new ArrayList<String>(value.size());
               valueCopy.addAll(value);
               properties.put(name, valueCopy);
            }
            else
            {
               properties.put(name, null);
            }
         }
      }
      lastModificationDate = System.currentTimeMillis();
   }

   final long getCreationDate()
   {
      return creationDate;
   }

   final long getLastModificationDate()
   {
      return lastModificationDate;
   }

   final boolean checkPermissions(String... permissions)
   {
      final String user = getCurrentUser();
      synchronized (permissionsMap)
      {
         Set<String> userPermissions = permissionsMap.get(user);
         if (userPermissions != null && !userPermissions.contains(VirtualFileSystemInfo.BasicPermissions.ALL.value()))
         {
            for (String permission : permissions)
            {
               if (!userPermissions.contains(permission))
               {
                  return false;
               }
            }
         }
      }
      return true;
   }

   abstract MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException;

   final void accept(MemoryItemVisitor visitor) throws VirtualFileSystemException
   {
      visitor.visit(this);
   }

   boolean isLocked()
   {
      return false;
   }

   private String getCurrentUser()
   {
      ConversationState cs = ConversationState.getCurrent();
      if (cs != null)
      {
         return cs.getIdentity().getUserId();
      }
      return VirtualFileSystemInfo.ANONYMOUS_PRINCIPAL;
   }
}
