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
package org.exoplatform.ide.vfs.server.impl.memory.context;

import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.AccessControlEntryImpl;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfoImpl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class MemoryItem
{
   private final ItemType type;
   private final String id;
   private final Map<String, Set<String>> permissionsMap;
   private final Map<String, List<String>> properties;
   private final long creationDate;

   private MemoryFolder parent;
   String name;
   long lastModificationDate;

   MemoryItem(ItemType type, String id, String name)
   {
      this.type = type;
      this.id = id;
      this.name = name;
      this.permissionsMap = new HashMap<String, Set<String>>();
      permissionsMap.put(VirtualFileSystemInfo.ANY_PRINCIPAL,
         new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
      this.properties = new HashMap<String, List<String>>();
      this.creationDate = this.lastModificationDate = System.currentTimeMillis();
   }

   public final MemoryFolder getParent()
   {
      return parent;
   }

   final void setParent(MemoryFolder parent)
   {
      this.parent = parent;
   }

   public final ItemType getType()
   {
      return type;
   }

   public final String getId()
   {
      return id;
   }

   public final String getName()
   {
      return name;
   }

   final void setName(String newName) throws VirtualFileSystemException
   {
      this.name = newName;
      lastModificationDate = System.currentTimeMillis();
   }

   public final String getMediaType() throws VirtualFileSystemException
   {
      List<Property> properties = getProperties(PropertyFilter.valueOf("vfs:mimeType"));
      if (properties.size() > 0)
      {
         List<String> values = properties.get(0).getValue();
         if (!(values == null || values.isEmpty()))
         {
            return values.get(0);
         }
      }
      return null;
   }

   public final void setMediaType(String mediaType)
   {
      updateProperties(Arrays.<Property>asList(new PropertyImpl("vfs:mimeType", mediaType)));
      lastModificationDate = System.currentTimeMillis();
   }

   public final String getPath()
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

   public final List<AccessControlEntry> getACL()
   {
      List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>();
      synchronized (permissionsMap)
      {
         for (Map.Entry<String, Set<String>> e : permissionsMap.entrySet())
         {
            acl.add(new AccessControlEntryImpl(e.getKey(), new HashSet<String>(e.getValue())));
         }
      }
      return acl;
   }

   public final void updateACL(List<AccessControlEntry> acl, boolean override)
   {
      Map<String, Set<String>> update = new HashMap<String, Set<String>>(acl.size());
      for (AccessControlEntry ace : acl)
      {
         String principal = ace.getPrincipal();
         Set<String> permissions = update.get(principal);
         if (permissions == null)
         {
            permissions = new HashSet<String>();
            update.put(principal, permissions);
         }
         permissions.addAll(ace.getPermissions());
      }

      if (override && update.isEmpty())
      {
         update.put(VirtualFileSystemInfo.ANY_PRINCIPAL,
            new HashSet<String>(Arrays.asList(VirtualFileSystemInfoImpl.BasicPermissions.ALL.value())));
      }

      synchronized (permissionsMap)
      {
         if (override)
         {
            permissionsMap.clear();
         }
         permissionsMap.putAll(update);
      }
      lastModificationDate = System.currentTimeMillis();
   }

   public Map<String, Set<String>> getPermissions()
   {
      synchronized (permissionsMap)
      {
         Map<String, Set<String>> copy = new HashMap<String, Set<String>>(permissionsMap.size());
         for (Map.Entry<String, Set<String>> e : permissionsMap.entrySet())
         {
            copy.put(e.getKey(), new HashSet<String>(e.getValue()));
         }
         return copy;
      }
   }

   public final List<Property> getProperties(PropertyFilter filter)
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
                  result.add(new PropertyImpl(name, copy));
               }
               else
               {
                  result.add(new PropertyImpl(name, (String)null));
               }
            }
         }
      }
      return result;
   }

   public final void updateProperties(List<Property> update)
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

   public final long getCreationDate()
   {
      return creationDate;
   }

   public final long getLastModificationDate()
   {
      return lastModificationDate;
   }

   public abstract MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException;

   public final void accept(MemoryItemVisitor visitor) throws VirtualFileSystemException
   {
      visitor.visit(this);
   }
}
