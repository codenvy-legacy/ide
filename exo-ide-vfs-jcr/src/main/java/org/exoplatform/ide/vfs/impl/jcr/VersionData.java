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
package org.exoplatform.ide.vfs.impl.jcr;

import org.exoplatform.ide.vfs.server.LazyIterator;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.InputStream;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
class VersionData extends DocumentData
{
   VersionData(Node node)
   {
      super(node);
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#getPath()
    */
   @Override
   String getPath() throws VirtualFileSystemException
   {
      try
      {
         return getCurrentVersion().getPath();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#getVersionId()
    */
   @Override
   String getVersionId() throws VirtualFileSystemException
   {
      try
      {
         return node.getParent().getName();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.DocumentData#getAllVersions()
    */
   @Override
   LazyIterator<DocumentData> getAllVersions() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         return getCurrentVersion().getAllVersions();
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get versions of document " + getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get versions of document " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.DocumentData#rename(java.lang.String,
    *      javax.ws.rs.core.MediaType, java.util.List)
    */
   @Override
   void rename(String newname, MediaType mediaType, List<String> lockTokens) throws LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      throw new NotSupportedException("Unable update not current version of document. ");
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.DocumentData#setContent(java.io.InputStream,
    *      javax.ws.rs.core.MediaType, java.util.List)
    */
   @Override
   void setContent(InputStream content, MediaType mediaType, List<String> lockTokens) throws LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      throw new NotSupportedException("Unable update not current version of document. ");
   }

   private DocumentData getCurrentVersion() throws RepositoryException
   {
      Version versionNode = (Version)node.getParent();
      String versionableUUID = versionNode.getContainingHistory().getVersionableUUID();
      Session session = node.getSession();
      return (DocumentData)ItemData.fromNode(session.getNodeByUUID(versionableUUID));
   }
}
