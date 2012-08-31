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

import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.ItemType;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.ws.rs.core.MediaType;

class ProjectData extends FolderData
{
   ProjectData(Node node, String rootNodePath)
   {
      super(node, ItemType.PROJECT, rootNodePath);
   }

   final String getProjectType() throws VirtualFileSystemException
   {
      try
      {
         return node.getNode(".project").getProperty("vfs:projectType").getString();
      }
      catch (RepositoryException re)
      {
         throw new VirtualFileSystemException(re.getMessage(), re);
      }
   }

   /** @see org.exoplatform.ide.vfs.impl.jcr.FolderData#getMediaType() */
   @Override
   final MediaType getMediaType() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         String str = node.getNode(".project").getProperty("vfs:mimeType").getString();
         if (str.isEmpty())
         {
            return null;
         }
         return MediaType.valueOf(str);
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get mime type of folder " + getName() + ". Access denied. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get mime type of folder " + getName() + ". " + e.getMessage(), e);
      }
   }
}
