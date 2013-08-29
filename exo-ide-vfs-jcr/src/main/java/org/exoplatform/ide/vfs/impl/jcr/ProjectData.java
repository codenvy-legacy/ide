/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
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
