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

import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MemoryFolder extends MemoryItem
{
   private final Map<String, MemoryItem> children;

   public MemoryFolder(String name)
   {
      this(ObjectIdGenerator.generateId(), name);
   }

   MemoryFolder(String id, String name)
   {
      super(ItemType.FOLDER, id, name);
      children = new LinkedHashMap<String, MemoryItem>();
   }

   public List<MemoryItem> getChildren()
   {
      return new ArrayList<MemoryItem>(children.values());
   }

   public void addChild(MemoryItem child) throws VirtualFileSystemException
   {
      String childName = child.getName();
      synchronized (children)
      {
         if (children.get(childName) != null)
         {
            throw new ItemAlreadyExistException("Item with the name '" + childName + "' already exists. ");
         }
         children.put(childName, child);
      }
      child.setParent(this);
   }

   public MemoryItem getChild(String name)
   {
      synchronized (children)
      {
         return children.get(name);
      }
   }

   public MemoryItem removeChild(String name)
   {
      synchronized (children)
      {
         MemoryItem removed = children.remove(name);
         if (removed != null)
         {
            removed.setParent(null);
         }
         return removed;
      }
   }

   public MemoryItem renameChild(String name, String newName) throws VirtualFileSystemException
   {
      synchronized (children)
      {
         MemoryItem child = children.get(name);
         if (child != null)
         {
            if (children.get(newName) != null)
            {
               throw new ItemAlreadyExistException("Item with the name '" + newName + "' already exists. ");
            }
            children.remove(name);
            child.setName(newName);
            children.put(newName, child);
            return child;
         }
         throw new ItemNotFoundException("Not found child item with the name '" + name);
      }
   }

   @Override
   public MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException
   {
      MemoryFolder copy = new MemoryFolder(ObjectIdGenerator.generateId(), name);
      for (MemoryItem i : getChildren())
      {
         i.copy(copy);
      }
      copy.updateProperties(getProperties(PropertyFilter.ALL_FILTER));
      copy.updateACL(getACL(), true);
      parent.addChild(copy);
      return copy;
   }

   public boolean isProject()
   {
      List<Property> properties;
      try
      {
         properties = getProperties(PropertyFilter.valueOf("vfs:mimeType"));
      }
      catch (InvalidArgumentException e)
      {
         // Should not happen.
         throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
      }
      if (properties.isEmpty())
      {
         return false;
      }
      List<String> values = properties.get(0).getValue();
      return !(values == null || values.isEmpty()) && Project.PROJECT_MIME_TYPE.equals(values.get(0));
   }

   public void setProjectType(String projectType) throws VirtualFileSystemException
   {
      updateProperties(Arrays.asList(new Property("vfs:projectType", projectType)));
      lastModificationDate = System.currentTimeMillis();
   }

   @Override
   public String toString()
   {
      return "MemoryFolder{" +
         "id='" + getId() + '\'' +
         ", path=" + getPath() +
         ", name='" + getName() + '\'' +
         ", type=" + getType() +
         ", isProject=" + isProject() +
         '}';
   }
}
