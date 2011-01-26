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
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.Type;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class FolderData extends ItemData
{
   private class ChildrenIterator extends LazyIterator<ItemData>
   {
      private NodeIterator i;

      public ChildrenIterator(NodeIterator i)
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
            Node c = i.nextNode();
            try
            {
               next = ItemData.fromNode(c);
            }
            catch (RepositoryException e)
            {
               throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
            }
         }
      }

   }

   FolderData(Node node)
   {
      super(node, Type.FOLDER);
   }

   LazyIterator<ItemData> getChildren() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         NodeIterator nodes = node.getNodes();
         return new ChildrenIterator(nodes);
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to content of folder " + getId() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   FileData createFile(String name, String nodeType, String contentNodeType, MediaType mediaType,
      InputStream content) throws InvalidArgumentException, PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         Node fileNode = node.addNode(name, nodeType);
         Node contentNode = fileNode.addNode("jcr:content", contentNodeType);
         contentNode.setProperty("jcr:mimeType", (mediaType.getType() + "/" + mediaType.getSubtype()));
         contentNode.setProperty("jcr:encoding", mediaType.getParameters().get("charset"));
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", content == null ? EMPTY : content);
         Session session = node.getSession();
         session.save();
         return (FileData)fromNode(fileNode);
      }
      catch (ItemExistsException e)
      {
         throw new InvalidArgumentException("Item with the name: " + name + " already exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable add file in folder " + getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   FolderData createFolder(String name, String nodeType) throws InvalidArgumentException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         Node folderNode = node.addNode(name, nodeType);
         Session session = node.getSession();
         session.save();
         return (FolderData)fromNode(folderNode);
      }
      catch (ItemExistsException e)
      {
         throw new InvalidArgumentException("Item with the name: " + name + " already exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable add new folder in folder " + getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }
}
