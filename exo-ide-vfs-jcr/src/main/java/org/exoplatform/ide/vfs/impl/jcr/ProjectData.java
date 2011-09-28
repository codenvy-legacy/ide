/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Project;

import java.util.List;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;

class ProjectData extends FolderData
{
   ProjectData(Node node)
   {
      super(node);
   }

   final String getProjectType() throws ConstraintException, VirtualFileSystemException
   {
      try
      {
         return node.getProperty("vfs:projectType").getString();
      }
      catch (PathNotFoundException e)
      {
         //throw new VirtualFileSystemException("Mandatory property 'vfs:projectType' not found.");
         return null;
      }
      catch (RepositoryException re)
      {
         throw new VirtualFileSystemException(re.getMessage(), re);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.FolderData#createFolder(java.lang.String, java.lang.String,
    *      java.lang.String[], java.util.List)
    */
   @Override
   FolderData createFolder(String name, String nodeType, String[] mixinTypes, List<ConvertibleProperty> properties)
      throws InvalidArgumentException, ConstraintException, PermissionDeniedException, VirtualFileSystemException
   {
      if (properties != null && properties.size() > 0)
      {
         for (ConvertibleProperty property : properties)
         {
            if ("vfs:mimeType".equals(property.getName()))
            {
               List<String> value = property.getValue();
               if (value != null && value.size() > 0 && Project.PROJECT_MIME_TYPE.equalsIgnoreCase(value.get(0)))
               {
                  throw new ConstraintException("Can't create new project inside project. ");
               }
            }
         }
      }
      return super.createFolder(name, nodeType, mixinTypes, properties);
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#copyTo(org.exoplatform.ide.vfs.impl.jcr.FolderData)
    */
   @Override
   ItemData copyTo(FolderData folder) throws ConstraintException, PermissionDeniedException, VirtualFileSystemException
   {
      if (folder instanceof ProjectData)
         throw new ConstraintException("Unable copy. Item specified as parent is a project. ");
      return super.copyTo(folder);
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#moveTo(org.exoplatform.ide.vfs.impl.jcr.FolderData,
    *      java.lang.String)
    */
   @Override
   String moveTo(FolderData folder, String lockToken) throws ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      if (folder instanceof ProjectData)
         throw new ConstraintException("Unable move. Item specified as parent is a project. ");
      return super.moveTo(folder, lockToken);
   }
}
