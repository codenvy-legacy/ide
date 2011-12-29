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

import org.exoplatform.ide.vfs.server.ConvertibleProperty;
import org.exoplatform.ide.vfs.server.LazyIterator;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Property;

import java.io.InputStream;
import java.util.Calendar;
import java.util.List;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class FolderData extends ItemData
{
   private static class ChildrenIterator extends LazyIterator<ItemData>
   {
      private final NodeIterator i;
      private final String rootNodePath;

      public ChildrenIterator(NodeIterator i, String rootNodePath)
      {
         this.i = i;
         this.rootNodePath = rootNodePath;
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
            try
            {
               next = ItemData.fromNode(i.nextNode(), rootNodePath);
            }
            catch (RepositoryException e)
            {
               throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
            }
         }
      }
   }

   FolderData(Node node, String rootNodePath) throws RepositoryException
   {
      super(node, ItemType.FOLDER, rootNodePath);
   }

   final boolean isRootFolder() throws VirtualFileSystemException
   {
      try
      {
         return node.getDepth() == 0;
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#getMediaType()
    */
   @Override
   MediaType getMediaType() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         String str = node.getProperty("vfs:mimeType").getString();
         if (str.isEmpty())
         {
            return null;
         }
         return MediaType.valueOf(str);
      }
      catch (PathNotFoundException e)
      {
         return null;
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

   final LazyIterator<ItemData> getChildren() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         NodeIterator nodes = node.getNodes();
         return new ChildrenIterator(nodes, rootNodePath);
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to content of folder " + getName() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   final boolean hasChild(String name) throws VirtualFileSystemException
   {
      try
      {
         return node.hasNode(name);
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   final ItemData getChild(String name) throws VirtualFileSystemException
   {
      try
      {
         return fromNode(node.getNode(name), rootNodePath);
      }
      catch (PathNotFoundException e)
      {
         return null;
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to content of folder " + getName() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   FileData createFile(String name,
                       String nodeType,
                       String contentNodeType,
                       MediaType mediaType,
                       String[] mixinTypes,
                       List<ConvertibleProperty> properties,
                       InputStream content) throws InvalidArgumentException,
      ItemAlreadyExistException, PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         Node fileNode = node.addNode(name, nodeType);
         Node contentNode = fileNode.addNode("jcr:content", contentNodeType);
         contentNode.setProperty("jcr:mimeType", (mediaType.getType() + "/" + mediaType.getSubtype()));
         contentNode.setProperty("jcr:encoding", mediaType.getParameters().get("charset"));
         contentNode.setProperty("jcr:lastModified", Calendar.getInstance());
         contentNode.setProperty("jcr:data", content == null ? EMPTY : content);

         if (mixinTypes != null)
         {
            for (int i = 0; i < mixinTypes.length; i++)
            {
               if (fileNode.canAddMixin(mixinTypes[i]))
               {
                  fileNode.addMixin(mixinTypes[i]);
               }
            }
         }

         if (properties != null && properties.size() > 0)
         {
            for (ConvertibleProperty property : properties)
            {
               updateProperty(fileNode, property);
            }
         }

         Session session = node.getSession();
         session.save();
         return (FileData)fromNode(fileNode, rootNodePath);
      }
      catch (ItemExistsException e)
      {
         throw new ItemAlreadyExistException("Item with the name: " + name + " already exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable add file in folder " + getPath() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * Create folder node.
    * 
    * @param name name of folder
    * @param nodeType primary node type name
    * @param mixinTypes mixin types that must be added. Should be <code>null</code> if there is no additional mixins
    * @param properties set of properties that should be added to newly created node. Should be <code>null</code> if
    *           there is no properties
    * @return newly created folder item
    * @throws ItemAlreadyExistException if folder already has item with name <code>name</code>
    * @throws ConstraintException if any of following conditions are met:
    *            <ul>
    *            <li>at least one of updated properties is read-only</li>
    *            <li>value of any updated properties is not acceptable</li>
    *            </ul>
    * @throws PermissionDeniedException if properties can't be updated cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   FolderData createFolder(String name, String nodeType, String[] mixinTypes, List<ConvertibleProperty> properties)
      throws InvalidArgumentException, ConstraintException, ItemAlreadyExistException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         if (node.hasNode(name))
         {
            throw new ItemAlreadyExistException("Folder '" + name + "' already exists. ");
         }

         Node folderNode;
         if (name.indexOf('/') > 0)
         {
            String[] nameSegments = name.split("/");
            Node current = node;
            for (int i = 0; i < nameSegments.length; i++)
            {
               try
               {
                  current = current.getNode(nameSegments[i]);
               }
               catch (PathNotFoundException e1)
               {
                  current = current.addNode(nameSegments[i], nodeType);
               }
            }
            folderNode = current;
         }
         else
         {
            folderNode = node.addNode(name, nodeType);
         }

         if (mixinTypes != null)
         {
            for (int i = 0; i < mixinTypes.length; i++)
            {
               if (folderNode.canAddMixin(mixinTypes[i]))
               {
                  folderNode.addMixin(mixinTypes[i]);
               }
            }
         }

         if (properties != null && properties.size() > 0)
         {
            for (ConvertibleProperty property : properties)
            {
               updateProperty(folderNode, property);
            }
         }

         Session session = node.getSession();
         session.save();
         return (FolderData)fromNode(folderNode, rootNodePath);
      }
      catch (ItemExistsException e)
      {
         throw new ItemAlreadyExistException("Folder '" + name + "' already exists. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable add new folder in folder " + getPath()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#rename(java.lang.String, javax.ws.rs.core.MediaType,
    *      java.lang.String, java.lang.String[], java.lang.String[])
    */
   @Override
   String rename(String newname, MediaType mediaType, String lockToken, String[] addMixinTypes,
      String[] removeMixinTypes) throws ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      if ((newname == null || newname.length() == 0) && mediaType == null)
      {
         return getId();
      }

      try
      {
         Session session = node.getSession();
         if (newname != null && newname.length() > 0)
         {
            Node parent = node.getParent();
            String destinationPath = (parent.getDepth() == 0 ? "/" : (parent.getPath() + "/")) + newname;
            session.move(node.getPath(), destinationPath);
            node = (Node)session.getItem(destinationPath);
         }

         if (removeMixinTypes != null && removeMixinTypes.length > 0)
         {
            for (int i = 0; i < removeMixinTypes.length; i++)
            {
               node.removeMixin(removeMixinTypes[i]);
            }
         }

         if (addMixinTypes != null)
         {
            for (int i = 0; i < addMixinTypes.length; i++)
            {
               if (node.canAddMixin(addMixinTypes[i]))
               {
                  node.addMixin(addMixinTypes[i]);
               }
            }
         }

         if (mediaType != null/* && node.isNodeType("vfs:project")*/)
         {
            updateProperty(node,
               new ConvertibleProperty("vfs:mimeType", (mediaType.getType() + "/" + mediaType.getSubtype())));
         }

         session.save();
         return getId();
      }
      catch (ItemExistsException e)
      {
         throw new ItemAlreadyExistException("Folder with the same name already exists. ");
      }
      catch (ConstraintViolationException e)
      {
         throw new ConstraintException(e.getMessage(), e);
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable rename folder " + getName() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable rename folder " + getName() + ". " + e.getMessage(), e);
      }
   }

   @Override
   final void updateProperty(Node theNode, ConvertibleProperty property) throws ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         super.updateProperty(theNode.isNodeType("vfs:project") ? theNode.getNode(".project") : theNode, property);
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#getProperties(javax.jcr.Node,
    *      org.exoplatform.ide.vfs.server.PropertyFilter)
    */
   @Override
   @SuppressWarnings("rawtypes")
   final List<Property> getProperties(Node theNode, PropertyFilter filter) throws PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         return super.getProperties(theNode.isNodeType("vfs:project") ? theNode.getNode(".project") : theNode, filter);
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
      }
   }
}
