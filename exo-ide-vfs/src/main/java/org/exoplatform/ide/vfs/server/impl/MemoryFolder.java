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

import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class MemoryFolder extends MemoryItem
{
   final Map<String, MemoryItem> children;

   MemoryFolder(String name) throws VirtualFileSystemException
   {
      this(ItemType.FOLDER, name);
   }

   // for MemoryProject only!
   MemoryFolder(ItemType type, String name) throws VirtualFileSystemException
   {
      super(type, name);
      children = new LinkedHashMap<String, MemoryItem>();
   }

   // for root folder only!
   MemoryFolder(String id, String name) throws VirtualFileSystemException
   {
      super(ItemType.FOLDER, id, name);
      children = new LinkedHashMap<String, MemoryItem>();
   }

   List<MemoryItem> getChildren()
   {
      synchronized (children)
      {
         return new ArrayList<MemoryItem>(children.values());
      }
   }

   void addChild(MemoryItem child) throws VirtualFileSystemException
   {
      String newChildName = child.getName();
      synchronized (children)
      {
         if (children.get(newChildName) != null)
         {
            throw new ItemAlreadyExistException("Item with the name '" + newChildName + "' already exists. ");
         }
         children.put(newChildName, child);
      }
      child.setParent(this);
   }

   MemoryItem getChild(String name)
   {
      synchronized (children)
      {
         return children.get(name);
      }
   }

   MemoryItem removeChild(String name)
   {
      MemoryItem removed;
      synchronized (children)
      {
         removed = children.remove(name);
      }
      if (removed != null)
      {
         removed.setParent(null);
      }
      return removed;
   }

   @Override
   MemoryItem copy(MemoryFolder parent) throws VirtualFileSystemException
   {
      MemoryFolder copy = new MemoryFolder(name);
      for (MemoryItem i : getChildren())
      {
         i.copy(copy);
      }
      copy.updateProperties(getProperties());
      copy.updateACL(getACL(), true);
      parent.addChild(copy);
      return copy;
   }

   @Override
   public String toString()
   {
      return "MemoryFolder{" +
         "id='" + id + '\'' +
         ", path=" + getPath() +
         ", name='" + name + '\'' +
         ", type=" + type +
         ", parent=" + (parent == null ? null : parent.getId()) +
         ", mediaType='" + mediaType + '\'' +
         '}';
   }
}
