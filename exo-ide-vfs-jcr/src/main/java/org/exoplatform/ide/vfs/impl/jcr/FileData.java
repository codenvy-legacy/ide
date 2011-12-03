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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;
import javax.jcr.version.Version;
import javax.jcr.version.VersionException;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
class FileData extends ItemData
{
   private static class FileVersionIterator extends LazyIterator<FileData>
   {
      private final javax.jcr.version.VersionIterator i;
      private final String rootNodePath;
      private FileData latest;

      FileVersionIterator(javax.jcr.version.VersionIterator i, FileData latest, String rootNodePath)
      {
         i.next(); // skip jcr:rootVersion
         this.i = i;
         this.rootNodePath = rootNodePath;
         this.latest = latest;
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
               next = (FileData)ItemData.fromNode(version.getNode("jcr:frozenNode"), rootNodePath);
            }
            catch (RepositoryException e)
            {
               throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
            }
         }
         if (next == null && latest != null)
         {
            next = latest;
            latest = null;
         }
      }
   }

   private static class SingleVersionIterator extends LazyIterator<FileData>
   {
      public SingleVersionIterator(FileData file)
      {
         next = file;
      }

      /**
       * @see org.exoplatform.ide.vfs.server.LazyIterator#fetchNext()
       */
      @Override
      protected void fetchNext()
      {
         next = null;
      }
   }

   private static final String CURRENT_VERSION_ID = "0";

   FileData(Node node, String rootNodePath) throws RepositoryException
   {
      super(node, ItemType.FILE, rootNodePath);
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
   String getLatestVersionId() throws VirtualFileSystemException
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
         throw new PermissionDeniedException("Access denied to content of file " + getName() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get content of file " + getName() + ". " + e.getMessage(), e);
      }
   }

   MediaType getMediaType() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         String str = node.getProperty("jcr:content/jcr:mimeType").getString();
         if (str.isEmpty())
         {
            return null;
         }
         try
         {
            String encoding = node.getProperty("jcr:content/jcr:encoding").getString();
            str += (";charset=" + encoding);
         }
         catch (PathNotFoundException e)
         {
         }
         return MediaType.valueOf(str);
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get mime type of file " + getName() + ". Access denied. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get mime type of file " + getName() + ". " + e.getMessage(), e);
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
         throw new PermissionDeniedException("Access denied to content of file " + getName() + ". ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get length of content of file " + getName() + ". "
            + e.getMessage(), e);
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
            return new SingleVersionIterator(this);
         }
         else
         {
            return new FileVersionIterator(node.getVersionHistory().getAllVersions(), this, rootNodePath);
         }
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

   FileData getVersion(String versionId) throws InvalidArgumentException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         if (getVersionId().equals(versionId))
         {
            return this;
         }
         if (!(node.isNodeType("mix:versionable")))
         {
            // If not file versionable then any version ID is not acceptable.
            throw new InvalidArgumentException("Version " + versionId + " does not exist. ");
         }
         try
         {
            return (FileData)fromNode(node.getVersionHistory().getVersion(versionId).getNode("jcr:frozenNode"),
               rootNodePath);
         }
         catch (VersionException e)
         {
            throw new InvalidArgumentException("Version " + versionId + " does not exist. ");
         }
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Access denied to version " + versionId + " of file " + getName() + ". ");
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
      {
         throw new LockException("File already locked. ");
      }
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
         throw new LockException("Unable place lock to file " + getName() + ". " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable place lock to file " + getName() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable place lock to file " + getName() + ". " + e.getMessage(), e);
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
      {
         throw new LockException("File is not locked. ");
      }
      try
      {
         Session session = node.getSession();
         if (lockToken != null)
         {
            session.addLockToken(lockToken);
         }
         node.unlock();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable remove lock from file " + getName() + ". " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable remove lock from file " + getName()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable remove lock from file " + getName() + ". " + e.getMessage(), e);
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
    * @see org.exoplatform.ide.vfs.impl.jcr.ItemData#rename(java.lang.String, javax.ws.rs.core.MediaType,
    *      java.lang.String, java.lang.String[], java.lang.String[])
    */
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
         if (lockToken != null)
         {
            session.addLockToken(lockToken);
         }
         if (newname != null && newname.length() > 0)
         {
            Node parent = node.getParent();
            String destinationPath = (parent.getDepth() == 0 ? "/" : (parent.getPath() + "/")) + newname;
            session.move(node.getPath(), destinationPath);
            node = (Node)session.getItem(destinationPath);
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

         if (mediaType != null)
         {
            Node contentNode = node.getNode("jcr:content");
            contentNode.setProperty("jcr:mimeType", (mediaType.getType() + "/" + mediaType.getSubtype()));
            contentNode.setProperty("jcr:encoding", mediaType.getParameters().get("charset"));
         }

         session.save();
         return getId();
      }
      catch (ItemExistsException e)
      {
         throw new ItemAlreadyExistException("File with the same name already exists. ");
      }
      catch (ConstraintViolationException e)
      {
         throw new ConstraintException(e.getMessage(), e);
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable rename file " + getName() + ". File is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable rename file " + getName() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable rename file " + getName() + ". " + e.getMessage(), e);
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
         {
            session.addLockToken(lockToken);
         }

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
         throw new LockException("Unable update content of file " + getName() + ". File is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable update content of file " + getName()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update content of file " + getName() + ". " + e.getMessage(), e);
      }
   }

   @SuppressWarnings("rawtypes")
   @Override
   final List<Property> getProperties(Node theNode, PropertyFilter filter) throws PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         List<Property> properties = super.getProperties(theNode, filter);
         // Get properties from jcr:content node. In fact always skip 'known' properties
         // for node type 'nt:resource' such as: 'jcr:encoding', 'jcr:mimeType', 'jcr:data',
         // 'jcr:lastModified'. But if type of jcr:content node is extension of 'nt:resource'
         // it may contains other properties so need retrieve them.
         properties.addAll(super.getProperties(theNode.getNode("jcr:content"), filter));
         return properties;
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
      }
   }

   @Override
   final void updateProperties(List<ConvertibleProperty> properties, String[] addMixinTypes, String[] removeMixinTypes,
      String lockToken) throws ConstraintException, LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      if (properties == null || properties.size() == 0)
      {
         return;
      }

      try
      {
         Session session = node.getSession();
         if (lockToken != null)
         {
            session.addLockToken(lockToken);
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
         Map<String, PropertyDefinition> ntFilePropertyDefinitions = null;
         Map<String, PropertyDefinition> ntResourcePropertyDefinitions = null;
         Node contentNode = node.getNode("jcr:content");
         for (ConvertibleProperty property : properties)
         {
            // Since properties from nt:file and nt:resource are merged
            // in method getProperties(Node, PropertyFilter) here we need to determine
            // which node should be updated.
            String propertyName = property.getName();
            if (node.hasProperty(propertyName))
            {
               // If nt:file node already contains property simple try to update it.
               updateProperty(node, property);
            }
            else if (contentNode.hasProperty(propertyName))
            {
               // If nt:resource (jcr:content) node already contains property simple try to update it.
               updateProperty(contentNode, property);
            }
            else
            {
               if (ntFilePropertyDefinitions == null)
               {
                  // Read property definitions for nt:file or its extension.
                  ntFilePropertyDefinitions = buildPropertyDefinitionsMap(node);
               }
               if (ntFilePropertyDefinitions.get(propertyName) != null)
               {
                  // If property definition found in nt:file then update nt:file.
                  updateProperty(node, property);
                  continue;
               }
               if (ntResourcePropertyDefinitions == null)
               {
                  // Read property definitions for nt:resource (jcr:content) or its extension.
                  ntResourcePropertyDefinitions = buildPropertyDefinitionsMap(contentNode);
               }
               if (ntResourcePropertyDefinitions.get(propertyName) != null)
               {
                  // If property definition found in nt:resource (jcr:content) then update jcr:content.
                  updateProperty(contentNode, property);
                  continue;
               }
               try
               {
                  // If definition not found then try to update nt:file first.
                  // Node type may contain '*' property definition. 
                  updateProperty(node, property);
               }
               catch (ConstraintException e)
               {
                  // If property is not acceptable for nt:file try to update nt:resource. 
                  updateProperty(contentNode, property);
               }
            }
         }
         session.save();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable to update properties of item " + getName() + ". Item is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable to update properties of item " + getName()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update properties of item " + getName() + ". " + e.getMessage(),
            e);
      }
   }

   private Map<String, PropertyDefinition> buildPropertyDefinitionsMap(Node theNode) throws RepositoryException
   {
      Map<String, PropertyDefinition> map = new HashMap<String, PropertyDefinition>();
      PropertyDefinition[] ppropertyDefinitions = theNode.getPrimaryNodeType().getPropertyDefinitions();
      for (int i = 0; i < ppropertyDefinitions.length; i++)
      {
         map.put(ppropertyDefinitions[i].getName(), ppropertyDefinitions[i]);
      }
      NodeType[] mixinNodeTypes = theNode.getMixinNodeTypes();
      for (int i = 0; i < mixinNodeTypes.length; i++)
      {
         PropertyDefinition[] mpropertyDefinitions = mixinNodeTypes[i].getPropertyDefinitions();
         for (int j = 0; j < mpropertyDefinitions.length; j++)
         {
            map.put(mpropertyDefinitions[j].getName(), mpropertyDefinitions[j]);
         }
      }
      return map;
   }
}
