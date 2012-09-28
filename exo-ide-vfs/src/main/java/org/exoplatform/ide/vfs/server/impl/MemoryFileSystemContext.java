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
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
class MemoryFileSystemContext
{
   static final String ROOT_FOLDER_NAME = "";
   static final String ROOT_FOLDER_ID = "abcdef01-234567890-abcd-ef0123456789";

   final Map<String, MemoryItem> byIDs;
   final MemoryFolder root;

   MemoryFileSystemContext() throws VirtualFileSystemException
   {
      root = new MemoryFolder(ROOT_FOLDER_ID, ROOT_FOLDER_NAME);
      byIDs = new ConcurrentHashMap<String, MemoryItem>();
      byIDs.put(root.getId(), root);
   }

   MemoryFolder getRoot()
   {
      return root;
   }

   MemoryFile createFile(String parentId, String name, String mediaType, InputStream content) throws VirtualFileSystemException
   {
      checkName(name);
      MemoryItem parent = getItem(parentId);
      if (!(ItemType.FOLDER == parent.getType() || ItemType.PROJECT == parent.getType()))
      {
         throw new InvalidArgumentException("Unable create file. Item specified as parent is not a folder or project. ");
      }
      byte[] bytes = null;
      try
      {
         if (content != null)
         {
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte[] buf = new byte[1024];
            int r;
            while ((r = content.read(buf)) != -1)
            {
               bout.write(buf, 0, r);
            }
            bytes = bout.toByteArray();
         }
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemException("Unable create file. " + e.getMessage(), e);
      }
      MemoryFile mFile = new MemoryFile(name, mediaType, bytes);
      ((MemoryFolder)parent).addChild(mFile);
      byIDs.put(mFile.getId(), mFile);
      return mFile;
   }

   MemoryFolder createFolder(String parentId, String name) throws VirtualFileSystemException
   {
      checkName(name);
      MemoryItem parent = getItem(parentId);
      if (!(ItemType.FOLDER == parent.getType() || ItemType.PROJECT == parent.getType()))
      {
         throw new InvalidArgumentException("Unable create folder. Item specified as parent is not a folder or project. ");
      }
      MemoryFolder mFolder = new MemoryFolder(name);
      ((MemoryFolder)parent).addChild(mFolder);
      byIDs.put(mFolder.getId(), mFolder);
      return mFolder;
   }

   MemoryFolder createProject(String parentId,
                              String name,
                              String mediaType,
                              String projectType,
                              List<ConvertibleProperty> properties) throws VirtualFileSystemException
   {
      checkName(name);
      MemoryItem parent = getItem(parentId);
      if (ItemType.PROJECT == parent.getType())
      {
         throw new ConstraintException("Unable create project. Item specified as parent is a project. "
            + "Project cannot contains another project.");
      }
      if (ItemType.FOLDER != parent.getType())
      {
         throw new InvalidArgumentException("Unable create project. Item specified as parent is not a folder. ");
      }
      MemoryProject mProject = new MemoryProject(name, mediaType, projectType);
      if (!(properties == null || properties.isEmpty()))
      {
         mProject.updateProperties(properties);
      }
      ((MemoryFolder)parent).addChild(mProject);
      byIDs.put(mProject.getId(), mProject);
      return mProject;
   }

   private void checkName(String name) throws InvalidArgumentException
   {
      if (name == null || name.trim().isEmpty())
      {
         throw new InvalidArgumentException("Item's name is not set. ");
      }
   }

   MemoryItem getItem(String id) throws VirtualFileSystemException
   {
      MemoryItem item = byIDs.get(id);
      if (item == null)
      {
         throw new ItemNotFoundException("Object '" + id + "' does not exists. ");
      }
      if (!item.checkPermissions(VirtualFileSystemInfo.BasicPermissions.READ.value()))
      {
         throw new PermissionDeniedException("Access denied to object " + id + ". ");
      }
      return item;
   }

   MemoryItem getItemByPath(String path) throws VirtualFileSystemException
   {
      if (path == null)
      {
         throw new IllegalArgumentException("Item path may not be null. ");
      }
      if ("/".equals(path))
      {
         return root;
      }
      MemoryItem item = root;
      String[] split = path.split("/");
      for (int i = 1, length = split.length; item != null && i < length; i++)
      {
         String name = split[i];
         if (ItemType.FOLDER == item.getType() || ItemType.PROJECT == item.getType())
         {
            item = ((MemoryFolder)item).getChild(name);
         }
      }
      if (item == null)
      {
         throw new ItemNotFoundException("Object '" + path + "' does not exists. ");
      }
      if (!item.checkPermissions(VirtualFileSystemInfo.BasicPermissions.READ.value()))
      {
         throw new PermissionDeniedException("Access denied to object " + path + ". ");
      }
      return item;
   }

   MemoryItem deleteItem(String id) throws VirtualFileSystemException
   {
      MemoryItem item = getItem(id);
      if (ROOT_FOLDER_ID.equals(item.getId()))
      {
         throw new VirtualFileSystemException("Unable delete root folder. ");
      }
      if (!item.checkPermissions(VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
      {
         throw new PermissionDeniedException("Unable delete item " + item.getName() + ". Operation not permitted. ");
      }
      MemoryFolder parent = item.getParent();
      if (ItemType.FILE == item.getType())
      {
         parent.removeChild(item.getName());
         byIDs.remove(item.getId());
      }
      else
      {
         final List<MemoryItem> toDelete = new ArrayList<MemoryItem>();
         item.accept(new MemoryItemVisitor()
         {
            @Override
            public void visit(MemoryItem i) throws VirtualFileSystemException
            {
               if (i.getType() == ItemType.FILE)
               {
                  toDelete.add(i);
               }
               else
               {
                  for (MemoryItem ii : ((MemoryFolder)i).getChildren())
                  {
                     ii.accept(this);
                  }
                  toDelete.add(i);
               }
            }
         });

         for (MemoryItem mi : toDelete)
         {
            if (!mi.checkPermissions(VirtualFileSystemInfo.BasicPermissions.WRITE.value()))
            {
               throw new PermissionDeniedException("Unable delete item " + mi.getPath() + ". Operation not permitted. ");
            }
         }

         // remove tree
         parent.removeChild(item.getName());
         byIDs.values().removeAll(toDelete);

      }
      return item;
   }

   MemoryItem copy(String id, String parentId) throws VirtualFileSystemException
   {
      MemoryItem object = getItem(id);
      MemoryItem parent = getItem(parentId);
      if (id.equals(parentId))
      {
         throw new IllegalArgumentException("Item cannot be copied to itself. ");
      }
      if (!(ItemType.PROJECT == parent.getType() || ItemType.FOLDER == parent.getType()))
      {
         throw new InvalidArgumentException("Unable copy. Item specified as parent is not a folder or project. ");
      }
      if (ItemType.PROJECT == parent.getType() && ItemType.PROJECT == object.getType())
      {
         throw new ConstraintException(
            "Unable copy. Item specified as parent is a project. Project cannot contains another project.");
      }
      MemoryItem copy = object.copy((MemoryFolder)parent);
      if (ItemType.PROJECT == copy.getType())
      {
         byIDs.put(copy.getId(), copy);
      }
      else
      {
         // Make new tree be accessible by ID.
         copy.accept(new MemoryItemVisitor()
         {
            @Override
            public void visit(MemoryItem i) throws VirtualFileSystemException
            {
               if (i.getType() == ItemType.FILE)
               {
                  byIDs.put(i.getId(), i);
               }
               else
               {
                  for (MemoryItem ii : ((MemoryFolder)i).getChildren())
                  {
                     ii.accept(this);
                  }
                  byIDs.put(i.getId(), i);
               }
            }
         });
         byIDs.put(copy.getId(), copy);
      }
      return copy;
   }
}
