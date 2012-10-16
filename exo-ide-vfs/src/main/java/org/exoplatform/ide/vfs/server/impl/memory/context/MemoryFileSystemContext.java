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
import org.exoplatform.ide.vfs.shared.ItemType;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class MemoryFileSystemContext
{
   static final String ROOT_FOLDER_ID = ObjectIdGenerator.generateId();

   private final ConcurrentMap<String, MemoryItem> entries;
   private final MemoryFolder root;

   public MemoryFileSystemContext()
   {
      root = new MemoryFolder(ROOT_FOLDER_ID, "");
      entries = new ConcurrentHashMap<String, MemoryItem>();
      entries.put(root.getId(), root);
   }

   public MemoryFolder getRoot()
   {
      return root;
   }

   public void putItem(MemoryItem item) throws VirtualFileSystemException
   {
      if (ItemType.FILE == item.getType())
      {
         entries.put(item.getId(), item);
      }
      else
      {
         final Map<String, MemoryItem> flatten = new HashMap<String, MemoryItem>();
         item.accept(new MemoryItemVisitor()
         {
            @Override
            public void visit(MemoryItem i) throws VirtualFileSystemException
            {
               if (i.getType() == ItemType.FILE)
               {
                  flatten.put(i.getId(), i);
               }
               else
               {
                  for (MemoryItem ii : ((MemoryFolder)i).getChildren())
                  {
                     ii.accept(this);
                  }
                  flatten.put(i.getId(), i);
               }
            }
         });
         entries.putAll(flatten);
      }
   }

   public MemoryItem getItem(String id)
   {
      return entries.get(id);
   }

   public MemoryItem getItemByPath(String path)
   {
      if (path == null)
      {
         throw new IllegalArgumentException("Item path may not be null. ");
      }
      if ("/".equals(path))
      {
         return getRoot();
      }
      if (path.startsWith("/"))
      {
         path = path.substring(1);
      }
      MemoryItem item = getRoot();
      String[] split = path.split("/");
      for (int i = 0, length = split.length; item != null && i < length; i++)
      {
         String name = split[i];
         if (ItemType.FOLDER == item.getType() || ItemType.PROJECT == item.getType())
         {
            item = ((MemoryFolder)item).getChild(name);
         }
      }
      return item;
   }

   public void deleteItem(String id)
   {
      entries.remove(id);
   }

   public void deleteItems(Collection<String> ids)
   {
      entries.keySet().removeAll(ids);
   }
}
