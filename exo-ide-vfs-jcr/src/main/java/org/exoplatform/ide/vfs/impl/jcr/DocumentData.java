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
import org.exoplatform.ide.vfs.server.Type;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.version.Version;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
class DocumentData extends ItemData
{
   class DocumentVersionIterator extends LazyIterator<DocumentData>
   {
      private javax.jcr.version.VersionIterator i;

      private boolean currentSeen;

      DocumentVersionIterator(javax.jcr.version.VersionIterator i)
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
               next = (DocumentData)ItemData.fromNode(version.getNode("jcr:frozenNode"));
            }
            catch (RepositoryException e)
            {
               throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
            }
         }
         if (next == null && !currentSeen)
         {
            currentSeen = true;
            next = DocumentData.this;
         }
      }
   }

   class SingleVersionIterator extends LazyIterator<DocumentData>
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
            next = DocumentData.this;
         }
      }
   }

   static final String CURRENT_VERSION_ID = "current";

   DocumentData(Node node)
   {
      super(node, Type.DOCUMENT);
   }

   /**
    * @return identifier of version of current document
    * @throws VirtualFileSystemException if any errors occurs
    */
   String getVersionId() throws VirtualFileSystemException
   {
      return CURRENT_VERSION_ID;
   }

   /**
    * Get content of current document.
    * 
    * @return content
    * @throws PermissionDeniedException if content can't be retrieved cause to
    *            security restriction
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
         throw new PermissionDeniedException("Access denied to content of document " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get content of document " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Get media type of content.
    * 
    * @return type of content
    * @throws PermissionDeniedException if content type can't be retrieved cause
    *            to security restriction
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
         throw new PermissionDeniedException("Access denied to content of document " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get type of content of document " + getId() + ". "
            + e.getMessage(), e);
      }
   }

   /**
    * Get length of content.
    * 
    * @return length of content
    * @throws PermissionDeniedException if content length can't be retrieved
    *            cause to security restriction
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
         throw new PermissionDeniedException("Access denied to content of document " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get length of content of document " + getId() + ". "
            + e.getMessage(), e);
      }
   }

   /**
    * Get all versions of current document. If object has not any other versions
    * the iterator will contains only current document.
    * 
    * @return iterator over document's versions
    * @throws PermissionDeniedException if versions can't be retrieved cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   LazyIterator<DocumentData> getAllVersions() throws PermissionDeniedException, VirtualFileSystemException
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
            return new DocumentVersionIterator(i);
         }
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
    * Rename and(or) update content type of current document.
    * 
    * @param newname new name. May be <code>null</code> if name is unchangeable
    * @param mediaType new media type. May be <code>null</code> if content type
    *           is unchangeable
    * @param lockTokens lock tokens. This lock tokens will be used if document
    *           is locked. Pass <code>null</code> or empty list if there is no
    *           lock tokens
    * @throws ConstraintException if parent folder already contains document
    *            with the same name as specified or if <code>newname</code> is
    *            invalid
    * @throws LockException if document is locked and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if document can't be renamed cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void rename(String newname, MediaType mediaType, List<String> lockTokens) throws ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      if (newname == null && mediaType == null)
         return;
      if (newname.length() == 0)
         throw new ConstraintException("Empty name is not allowed. ");

      try
      {
         Session session = node.getSession();
         if (lockTokens != null && lockTokens.size() > 0)
         {
            for (String lt : lockTokens)
               session.addLockToken(lt);
         }
         if (newname != null)
         {
            String destinationPath = node.getParent().getPath() + "/" + newname;
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
         throw new ConstraintException("Document with the same name already exists. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable rename document " + getId() + ". Object is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable rename document " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable rename document " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Update content of document.
    * 
    * @param content new content
    * @param mediaType new content type
    * @param lockTokens lock tokens. This lock tokens will be used if document
    *           is locked. Pass <code>null</code> or empty list if there is no
    *           lock tokens
    * @throws LockException if document is locked and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if content can't be updated cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void setContent(InputStream content, MediaType mediaType, List<String> lockTokens) throws LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         Session session = node.getSession();
         if (lockTokens != null && lockTokens.size() > 0)
         {
            for (String lt : lockTokens)
               session.addLockToken(lt);
         }
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
         throw new LockException("Unable update content of document " + getId() + ". Object is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable update content of document " + getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update content of document " + getId() + ". " + e.getMessage(), e);
      }
   }
}
