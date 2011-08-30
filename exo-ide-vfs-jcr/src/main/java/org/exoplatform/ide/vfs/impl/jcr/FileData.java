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
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.ItemType;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
class FileData extends ItemData
{
   class FileVersionIterator extends LazyIterator<FileData>
   {
      private javax.jcr.version.VersionIterator i;

      private boolean currentSeen;

      FileVersionIterator(javax.jcr.version.VersionIterator i)
      {
         this.i = i;
         fetchNext();
      }

      /**
       * @see org.exoplatform.ide.vfs.server.LazyIterator#fetchNext()
       */
      @Override
      protected void fetchNext()
      {
         next = null;
         while (next == null && i.hasNext())
         {
            Version version = i.nextVersion();
            try
            {
               next = (FileData)ItemData.fromNode(version.getNode("jcr:frozenNode"));
            }
            catch (RepositoryException e)
            {
               throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
            }
         }
         if (next == null && !currentSeen)
         {
            currentSeen = true;
            next = FileData.this;
         }
      }
   }

   class SingleVersionIterator extends LazyIterator<FileData>
   {
      private boolean currentSeen;

      /**
       * @see org.exoplatform.ide.vfs.server.LazyIterator#fetchNext()
       */
      @Override
      protected void fetchNext()
      {
         next = null;
         if (!currentSeen)
         {
            currentSeen = true;
            next = FileData.this;
         }
      }
   }

   static final String CURRENT_VERSION_ID = "current";

   FileData(Node node)
   {
      super(node, ItemType.FILE);
   }

   /**
    * @return id of version of current file
    * @throws VirtualFileSystemException if any errors occurs
    */
   String getVersionId() throws VirtualFileSystemException
   {
      return CURRENT_VERSION_ID;
   }

   /**
    * Get id of latest version.
    * 
    * @return latest version's id
    * @throws VirtualFileSystemException if any error occurs
    */
   String getCurrentVersionId() throws VirtualFileSystemException
   {
      return getId();
   }

   /**
    * Get content of current file.
    * 
    * @return content
    * @throws PermissionDeniedException if content can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   InputStream getContent() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         return node.getProperty("jcr:content/jcr:data").getStream();
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to content of file " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get content of file " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Get media type of content.
    * 
    * @return type of content
    * @throws PermissionDeniedException if content type can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   String getContenType() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         String mimeType = node.getProperty("jcr:content/jcr:mimeType").getString();
         try
         {
            String encoding = node.getProperty("jcr:content/jcr:encoding").getString();
            // TODO : default charset ?
            mimeType += (";charset=" + encoding);
         }
         catch (PathNotFoundException e)
         {
         }
         return mimeType;
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to content of file " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get type of content of file " + getId() + ". " + e.getMessage(),
            e);
      }
   }

   /**
    * Get length of content.
    * 
    * @return length of content
    * @throws PermissionDeniedException if content length can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   long getContenLength() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         return node.getProperty("jcr:content/jcr:data").getLength();
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to content of file " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(
            "Unable get length of content of file " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Get all versions of current file. If file has not any other versions the iterator will contains only current file.
    * 
    * @return iterator over file's versions
    * @throws PermissionDeniedException if versions can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   LazyIterator<FileData> getAllVersions() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         if (!(node.isNodeType("mix:versionable")))
         {
            return new SingleVersionIterator();
         }
         else
         {
            javax.jcr.version.VersionIterator i = node.getVersionHistory().getAllVersions();
            i.next(); // skip jcr:rootVersion
            return new FileVersionIterator(i);
         }
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get versions of file " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get versions of file " + getId() + ". " + e.getMessage(), e);
      }
   }

   FileData getVersion(String versionId) throws InvalidArgumentException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         if (getVersionId().equals(versionId))
            return this;
         // If not file versionable then any version ID is not acceptable.
         if (!(node.isNodeType("mix:versionable")))
            throw new InvalidArgumentException("Version " + versionId + " does not exist. ");
         try
         {
            return (FileData)fromNode(node.getVersionHistory().getVersion(versionId).getNode("jcr:frozenNode"));
         }
         catch (VersionException e)
         {
            throw new InvalidArgumentException("Version " + versionId + " does not exist. ");
         }
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to version " + versionId + " of file " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#getLastModificationDate()
    */
   @Override
   long getLastModificationDate() throws VirtualFileSystemException
   {
      try
      {
         return node.getProperty("jcr:content/jcr:lastModified").getLong();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * Place lock to current file.
    * 
    * @return lock token
    * @throws LockException if file already locked
    * @throws PermissionDeniedException if file can't be locked cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   String lock() throws LockException, PermissionDeniedException, VirtualFileSystemException
   {
      if (isLocked())
         throw new LockException("File already locked. ");
      try
      {
         if (node.canAddMixin("mix:lockable"))
         {
            Session session = node.getSession();
            node.addMixin("mix:lockable");
            session.save();
         }
         Lock lock = node.lock(true, false);
         return lock.getLockToken();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable place lock to file " + getId() + ". " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable place lock to file " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable place lock to file " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Remove lock from file.
    * 
    * @param lockToken lock token
    * @throws LockException if file is not locked or <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if lock can't be removed cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void unlock(String lockToken) throws LockException, PermissionDeniedException, VirtualFileSystemException
   {
      if (!isLocked())
         throw new LockException("File is not locked. ");
      try
      {
         Session session = node.getSession();
         if (lockToken != null)
            session.addLockToken(lockToken);
         node.unlock();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable remove lock from file " + getId() + ". " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable remove lock from file " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable remove lock from file " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Check is file locked or not.
    * 
    * @return <code>true</code> if file is locked and <code>false</code> otherwise
    * @throws VirtualFileSystemException if any errors occurs
    */
   boolean isLocked() throws VirtualFileSystemException
   {
      try
      {
         return node.isLocked();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * Rename and(or) update content type of current file.
    * 
    * @param newname new name. May be <code>null</code> if name is unchangeable
    * @param mediaType new media type. May be <code>null</code> if content type is unchangeable
    * @param lockToken lock token. This lock token will be used if file is locked. Pass <code>null</code> if there is no
    *           lock token
    * @throws ConstraintException if parent folder already contains file with the same name as specified or if
    *            <code>newname</code> is invalid
    * @throws LockException if file is locked and <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if file can't be renamed cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void rename(String newname, MediaType mediaType, String lockToken) throws ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      if ((newname == null || newname.length() == 0) && mediaType == null)
         return;

      try
      {
         Session session = node.getSession();
         if (lockToken != null)
            session.addLockToken(lockToken);
         if (newname != null && newname.length() > 0)
         {
            Node parent = node.getParent();
            String destinationPath = (parent.getDepth() == 0 ? "/" : (parent.getPath() + "/")) + newname;
            session.move(node.getPath(), destinationPath);
            node = (Node)session.getItem(destinationPath);
         }
         if (mediaType != null)
         {
            Node contentNode = node.getNode("jcr:content");
            contentNode.setProperty("jcr:mimeType", (mediaType.getType() + "/" + mediaType.getSubtype()));
            contentNode.setProperty("jcr:encoding", mediaType.getParameters().get("charset"));
         }
         session.save();
      }
      catch (ItemExistsException e)
      {
         throw new ConstraintException("File with the same name already exists. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable rename file " + getId() + ". File is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable rename file " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable rename file " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Update content of file. Previous state of JCR node saved in version history.
    * 
    * @param content new content. If <code>content</code> then content of file will be removed.
    * @param mediaType new content type
    * @param lockToken lock token. This lock token will be used if file is locked. Pass <code>null</code> if there is no
    *           lock token
    * @throws LockException if file is locked and <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if content can't be updated cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void setContent(InputStream content, MediaType mediaType, String lockToken) throws LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         Session session = node.getSession();
         if (lockToken != null)
            session.addLockToken(lockToken);

         if (!node.isNodeType("mix:versionable"))
         {
            node.addMixin("mix:versionable");
            session.save();
         }
         node.checkin();
         node.checkout();

         Node contentNode = node.getNode("jcr:content");
         if (content != null)
         {
            contentNode.setProperty("jcr:data", content);
            contentNode.setProperty("jcr:mimeType", (mediaType.getType() + "/" + mediaType.getSubtype()));
            contentNode.setProperty("jcr:encoding", mediaType.getParameters().get("charset"));
            contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         }
         else
         {
            contentNode.setProperty("jcr:data", EMPTY);
            contentNode.setProperty("jcr:mimeType", "");
            contentNode.setProperty("jcr:encoding", (Value)null);
            contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         }
         session.save();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable update content of file " + getId() + ". File is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable update content of file " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update content of file " + getId() + ". " + e.getMessage(), e);
      }
   }
}
