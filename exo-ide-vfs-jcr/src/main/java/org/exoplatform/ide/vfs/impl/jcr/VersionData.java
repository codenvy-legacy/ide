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

import org.exoplatform.ide.vfs.server.LazyIterator;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.InputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.version.Version;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: VersionData.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
final class VersionData extends FileData
{
   private FileData latest;

   VersionData(Node node, String rootNodePath) throws RepositoryException
   {
      super(node, rootNodePath);
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#getName()
    */
   @Override
   String getName() throws VirtualFileSystemException
   {
      try
      {
         return getLatestVersion().getName();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#getPath()
    */
   @Override
   String getPath() throws VirtualFileSystemException
   {
      try
      {
         return getLatestVersion().getPath();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.FileData#getVersionId()
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
    * @see org.exoplatform.ide.vfs.impl.jcr.FileData#getLastModificationDate()
    */
   @Override
   long getLastModificationDate() throws VirtualFileSystemException
   {
      // Version is read-only.
      return getCreationDate();
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.FileData#getAllVersions()
    */
   @Override
   LazyIterator<FileData> getAllVersions() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         return getLatestVersion().getAllVersions();
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get versions of file " + getName() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get versions of file " + getName() + ". " + e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#rename(java.lang.String, javax.ws.rs.core.MediaType,
    *      java.lang.String, java.lang.String[], java.lang.String[])
    */
   @Override
   String rename(String newName, MediaType mediaType, String lockToken, String[] addMixinTypes,
      String[] removeMixinTypes) throws ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      throw new NotSupportedException("Unable update not current version of file. ");
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.FileData#setContent(java.io.InputStream, javax.ws.rs.core.MediaType,
    *      java.lang.String)
    */
   @Override
   void setContent(InputStream content, MediaType mediaType, String lockToken) throws LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      throw new NotSupportedException("Unable update not current version of file. ");
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.FileData#getLatestVersionId()
    */
   String getLatestVersionId() throws VirtualFileSystemException
   {
      try
      {
         return getLatestVersion().getId();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   private FileData getLatestVersion() throws RepositoryException
   {
      if (latest == null)
      {
         Version versionNode = (Version)node.getParent();
         String versionableUUID = versionNode.getContainingHistory().getVersionableUUID();
         Session session = node.getSession();
         latest = (FileData)ItemData.fromNode(session.getNodeByUUID(versionableUUID), rootNodePath);
      }
      return latest;
   }
}
