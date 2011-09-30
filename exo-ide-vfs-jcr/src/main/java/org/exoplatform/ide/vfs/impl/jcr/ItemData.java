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
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.LockException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemRuntimeException;
import org.exoplatform.ide.vfs.shared.AccessControlEntry;
import org.exoplatform.ide.vfs.shared.BooleanProperty;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.NumberProperty;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.StringProperty;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.impl.core.value.StringValue;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.PropertyDefinition;
import javax.ws.rs.core.MediaType;

/**
 * Wrapper around node to simplify interaction with JCR.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
abstract class ItemData
{
   /**
    * Empty stream. Need it for value of property "jcr:data" of sub-node "jcr:content" of "nt:file" nodes.
    */
   static InputStream EMPTY = new InputStream()
   {
      public int read()
      {
         return -1;
      }
   };

   static ItemData fromNode(Node node) throws RepositoryException
   {
      if (node.isNodeType("nt:file") && node.getNode("jcr:content").isNodeType("nt:resource"))
         return new FileData(node);
      if (node.isNodeType("nt:resource") && "jcr:content".equals(node.getName()))
         return new FileData(node.getParent());
      if (node.isNodeType("nt:frozenNode"))
         return new VersionData(node);
      if (node.isNodeType("vfs:project"))
         return new ProjectData(node);
      return new FolderData(node);
   }

   /** Set of known JCR properties that should be skipped. */
   static final Set<String> SKIPPED_PROPERTIES = new HashSet<String>(Arrays.asList("jcr:primaryType", "jcr:created",
      "jcr:uuid", "jcr:baseVersion", "jcr:isCheckedOut", "jcr:predecessors", "jcr:versionHistory", "jcr:mixinTypes",
      "jcr:frozenMixinTypes", "jcr:frozenPrimaryType", "jcr:frozenUuid", "exo:permissions", "exo:owner"));

   Node node;
   final ItemType type;

   ItemData(Node node, ItemType type)
   {
      this.node = node;
      this.type = type;
   }

   /**
    * @return unified id of this item
    * @throws VirtualFileSystemException if any errors occurs
    */
   final String getId() throws VirtualFileSystemException
   {
      try
      {
         return ((ExtendedNode)node).getIdentifier();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @return name of this item
    * @throws VirtualFileSystemException if any errors occurs
    */
   String getName() throws VirtualFileSystemException
   {
      try
      {
         return node.getName();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @return type of this item
    * @see ItemType
    */
   final ItemType getType()
   {
      return type;
   }

   /**
    * Get media type of object.
    * 
    * @return type of object
    * @throws PermissionDeniedException if content type can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   abstract MediaType getMediaType() throws PermissionDeniedException, VirtualFileSystemException;

   /**
    * @return path of this item
    * @throws VirtualFileSystemException if any errors occurs
    */
   String getPath() throws VirtualFileSystemException
   {
      try
      {
         return node.getPath();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @return id of parent folder or null if current item is root folder
    * @throws VirtualFileSystemException if any errors occur
    */
   String getParentId() throws VirtualFileSystemException
   {
      try
      {
         if (type == ItemType.FOLDER && ((FolderData)this).isRootFolder())
         {
            return null;
         }
         return fromNode(node.getParent()).getId();
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @return creation date of this item in long format
    * @throws VirtualFileSystemException if any errors occurs
    */
   long getCreationDate() throws VirtualFileSystemException
   {
      try
      {
         return node.getProperty("jcr:created").getLong();
      }
      catch (PathNotFoundException e)
      {
         return -1;
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   /**
    * @return last modification date of this item
    * @throws VirtualFileSystemException if any errors occurs
    */
   long getLastModificationDate() throws VirtualFileSystemException
   {
      return getCreationDate();
   }

   /**
    * Get set of properties that acceptable by specified <code>filter</code>.
    * 
    * @param filter filter
    * @return properties
    * @throws PermissionDeniedException if properties can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @SuppressWarnings("rawtypes")
   final List<Property> getProperties(PropertyFilter filter) throws PermissionDeniedException,
      VirtualFileSystemException
   {
      return getProperties(this.node, filter);
   }

   /**
    * Get set of properties that acceptable by specified <code>filter</code>.
    * 
    * @param theNode the node
    * @param filter filter
    * @return properties
    * @throws PermissionDeniedException if properties can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   @SuppressWarnings("rawtypes")
   List<Property> getProperties(Node theNode, PropertyFilter filter) throws PermissionDeniedException,
      VirtualFileSystemException
   {
      if (filter == null)
      {
         throw new NullPointerException("PropertyFilter should not be null");
      }

      try
      {
         List<Property> properties = new ArrayList<Property>();
         for (PropertyIterator i = theNode.getProperties(); i.hasNext();)
         {
            javax.jcr.Property jcrProperty = i.nextProperty();
            String name = jcrProperty.getName();
            if (!SKIPPED_PROPERTIES.contains(name) && filter.accept(name))
            {
               properties.add(createProperty(jcrProperty));
            }
         }
         return properties;
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get properties of item " + getName()
            + ". Operation not permitted.");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get properties of item " + getName() + ". " + e.getMessage(), e);
      }
   }

   @SuppressWarnings("rawtypes")
   private Property createProperty(javax.jcr.Property property) throws RepositoryException
   {
      PropertyDefinition definition = property.getDefinition();
      boolean multiple = definition.isMultiple();
      switch (property.getType())
      {
         case PropertyType.DATE : {
            List<Double> v;
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               v = new ArrayList<Double>(jcrValues.length);
               for (int i = 0; i < jcrValues.length; i++)
               {
                  v.add((double)jcrValues[i].getLong());
               }
            }
            else
            {
               v = new ArrayList<Double>(1);
               v.add((double)property.getLong());
            }
            return new NumberProperty(property.getName(), v);
         }
         case PropertyType.DOUBLE : {
            List<Double> v;
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               v = new ArrayList<Double>(jcrValues.length);
               for (int i = 0; i < jcrValues.length; i++)
               {
                  v.add(jcrValues[i].getDouble());
               }
            }
            else
            {
               v = new ArrayList<Double>(1);
               v.add(property.getDouble());
            }
            return new NumberProperty(property.getName(), v);
         }
         case PropertyType.LONG : {
            List<Double> v;
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               v = new ArrayList<Double>(jcrValues.length);
               for (int i = 0; i < jcrValues.length; i++)
               {
                  v.add(jcrValues[i].getDouble());
               }
            }
            else
            {
               v = new ArrayList<Double>(1);
               v.add(property.getDouble());
            }
            return new NumberProperty(property.getName(), v);
         }
         case PropertyType.BOOLEAN : {
            List<Boolean> v;
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               v = new ArrayList<Boolean>(jcrValues.length);
               for (int i = 0; i < jcrValues.length; i++)
               {
                  v.add(jcrValues[i].getBoolean());
               }
            }
            else
            {
               v = new ArrayList<Boolean>(1);
               v.add(property.getBoolean());
            }
            return new BooleanProperty(property.getName(), v);
         }
         case PropertyType.STRING :
         case PropertyType.BINARY :
         case PropertyType.NAME :
         case PropertyType.PATH :
         case PropertyType.REFERENCE :
         default : {
            List<String> v;
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               v = new ArrayList<String>(jcrValues.length);
               for (int i = 0; i < jcrValues.length; i++)
               {
                  v.add(jcrValues[i].getString());
               }
            }
            else
            {
               v = new ArrayList<String>(1);
               v.add(property.getString());
            }
            return new StringProperty(property.getName(), v);
         }
      }
   }

   /**
    * Update properties.
    * 
    * @param properties set of properties that should be updated.
    * @param lockToken lock token. This lock token will be used if item is locked. Pass <code>null</code> if there is no
    *           lock token
    * @throws ConstraintException if any of following conditions are met:
    *            <ul>
    *            <li>at least one of updated properties is read-only</li>
    *            <li>value of any updated properties is not acceptable</li>
    *            </ul>
    * @throws LockException if item is locked and <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if properties can't be updated cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   final void updateProperties(List<ConvertibleProperty> properties, String lockToken) throws ConstraintException,
      LockException, PermissionDeniedException, VirtualFileSystemException
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

         for (ConvertibleProperty property : properties)
         {
            updateProperty(node, property);
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

   void updateProperty(Node theNode, ConvertibleProperty property) throws ConstraintException, LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      String name = property.getName();
      String[] value = property.valueToArray(String[].class);

      if (value == null)
      {
         value = new String[0];
      }

      try
      {
         if (value.length == 0)
         {
            theNode.setProperty(name, (Value)null);
         }
         else
         {
            {
               Value[] jcrValue = new Value[value.length];
               for (int i = 0; i < value.length; i++)
               {
                  jcrValue[i] = new StringValue(value[i]);
               }
               if (jcrValue.length > 1)
               {
                  try
                  {
                     theNode.setProperty(name, jcrValue);
                  }
                  catch (ValueFormatException e)
                  {
                     theNode.setProperty(name, jcrValue[0]);
                  }
               }
               else
               {
                  theNode.setProperty(name, jcrValue[0]);
               }
            }
         }
      }
      catch (ValueFormatException e)
      {
         throw new ConstraintException("Unable update property " + name + ". Specified value is not allowed. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable to update property " + name + ". Item is locked. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update property " + name + ". " + e.getMessage(), e);
      }
      catch (IOException e)
      {
         throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
      }
   }

   /**
    * Check is item locked or not.
    * 
    * @return always <code>false</code>. Will be overridden in FileData
    * @throws VirtualFileSystemException if any errors occurs
    */
   boolean isLocked() throws VirtualFileSystemException
   {
      return false;
   }

   /**
    * Delete item.
    * 
    * @param lockToken lock token. This lock token will be used if item is locked. Pass <code>null</code> if there is no
    *           lock
    * @throws LockException if item is locked and <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if item can't be removed cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void delete(String lockToken) throws LockException, PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         Session session = node.getSession();
         if (lockToken != null)
         {
            session.addLockToken(lockToken);
         }
         if (node.isLocked())
         {
            // ====== Workaround for disabling removing locked node. ======
            // JCR back-end does not prevent removing locked node (need lock parent node).
            Lock lock = node.getLock();
            if (lock.getLockToken() == null)
            {
               throw new LockException("Unable delete item " + getName() + ". Item is locked. ");
            }
         }
         node.remove();
         session.save();
         node = null;
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable delete item " + getName() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable delete item " + getName() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Get ACL applied to item.
    * 
    * @return ACL applied to item
    * @throws PermissionDeniedException if ACL can't be retrieved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   List<AccessControlEntry> getACL() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         if (!node.isNodeType("exo:privilegeable"))
         {
            return Collections.emptyList();
         }

         ExtendedNode extNode = (ExtendedNode)node;
         Map<String, Set<String>> tmp = new HashMap<String, Set<String>>();
         for (org.exoplatform.services.jcr.access.AccessControlEntry ace : extNode.getACL().getPermissionEntries())
         {
            String principal = ace.getIdentity();
            Set<String> permissions = tmp.get(principal);
            if (permissions == null)
            {
               permissions = new HashSet<String>();
               tmp.put(principal, permissions);
            }
            permissions.add(ace.getPermission());
         }

         List<AccessControlEntry> acl = new ArrayList<AccessControlEntry>(tmp.size());
         for (String principal : tmp.keySet())
         {
            AccessControlEntry ace = new AccessControlEntry();
            ace.setPrincipal(principal);
            Set<String> values = tmp.get(principal);
            if (values.size() == PermissionType.ALL.length)
            {
               ace.getPermissions().add(BasicPermissions.ALL.value());
            }
            else if (values.contains(PermissionType.READ) && values.contains(PermissionType.ADD_NODE))
            {
               ace.getPermissions().add(BasicPermissions.READ.value());
            }
            else if (values.contains(PermissionType.SET_PROPERTY) && values.contains(PermissionType.REMOVE))
            {
               ace.getPermissions().add(BasicPermissions.WRITE.value());
            }
            acl.add(ace);
         }
         return acl;
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get ACL of item " + getName() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get ACL of item " + getName() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Update ACL applied to item.
    * 
    * @param acl ACL
    * @param override if <code>true</code> then previous ACL replaced by specified. If <code>false</code> then specified
    *           ACL will be merged with existed
    * @param lockToken lock token. This lock token will be used if item is locked. Pass <code>null</code> if there is no
    *           lock token
    * @throws LockException if item is locked and <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if ACL can't be updated cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void updateACL(List<AccessControlEntry> acl, boolean override, String lockToken) throws LockException,
      PermissionDeniedException, VirtualFileSystemException
   {
      Map<String, Set<String>> tmp = new HashMap<String, Set<String>>();
      for (AccessControlEntry ace : acl)
      {
         String principal = ace.getPrincipal();
         Set<String> permissions = tmp.get(principal);
         if (permissions == null)
         {
            permissions = new HashSet<String>();
            tmp.put(principal, permissions);
         }
         for (String perm : ace.getPermissions())
         {
            if (BasicPermissions.READ.value().equals(perm))
            {
               permissions.add(PermissionType.READ);
            }
            else if (BasicPermissions.WRITE.value().equals(perm))
            {
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);
               permissions.add(PermissionType.ADD_NODE);
            }
            else if (BasicPermissions.ALL.value().equals(perm))
            {
               permissions.add(PermissionType.READ);
               permissions.add(PermissionType.ADD_NODE);
               permissions.add(PermissionType.SET_PROPERTY);
               permissions.add(PermissionType.REMOVE);
            }
         }
      }

      ExtendedNode extNode = (ExtendedNode)node;
      try
      {
         Session session = extNode.getSession();

         if (lockToken != null)
         {
            session.addLockToken(lockToken);
         }

         if (!extNode.isNodeType("exo:privilegeable"))
         {
            extNode.addMixin("exo:privilegeable");
         }

         Map<String, String[]> aces = new HashMap<String, String[]>(tmp.size());
         for (Map.Entry<String, Set<String>> e : tmp.entrySet())
         {
            aces.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));
         }

         if (override)
         {
            extNode.setPermissions(aces);
         }
         else
         {
            for (Map.Entry<String, String[]> e : aces.entrySet())
            {
               extNode.setPermission(e.getKey(), e.getValue());
            }
         }

         session.save();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable update ACL of item " + getName() + ". Item is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable update ACL of item " + getName() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update ACL of item " + getName() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Create copy of current item in specified folder.
    * 
    * @param folder parent
    * @return newly create copy
    * @throws ItemAlreadyExistException if destination folder already contains item with the same name as current
    * @throws PermissionDeniedException if new copy can't be created cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   ItemData copyTo(FolderData folder) throws ItemAlreadyExistException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         String itemPath = getPath();
         String destinationPath = folder.getPath();
         destinationPath += destinationPath.equals("/") ? getName() : ("/" + getName());
         Session session = node.getSession();
         session.getWorkspace().copy(itemPath, destinationPath);
         return fromNode((Node)session.getItem(destinationPath));
      }
      catch (ItemExistsException e)
      {
         throw new ItemAlreadyExistException("Destination folder already contains item with the same name. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable copy item " + getPath() + " to " + folder.getPath()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable copy item " + getPath() + " to " + folder.getPath() + ". "
            + e.getMessage(), e);
      }
   }

   /**
    * Move current item in specified folder.
    * 
    * @param folder new parent
    * @param lockToken lock token. This lock token will be used if this item is locked. Pass <code>null</code> if there
    *           is no lock token
    * @return id of moved object
    * @throws ItemAlreadyExistException if destination folder already contains item with the same name as current
    * @throws LockException if this item is locked and <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if item can't be moved cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   String moveTo(FolderData folder, String lockToken) throws LockException, PermissionDeniedException,
      VirtualFileSystemException
   {
      try
      {
         Session session = node.getSession();
         if (lockToken != null)
         {
            session.addLockToken(lockToken);
         }
         String itemPath = getPath();
         String destinationPath = folder.getPath();
         destinationPath += destinationPath.equals("/") ? getName() : ("/" + getName());
         session.getWorkspace().move(itemPath, destinationPath);
         node = (Node)session.getItem(destinationPath);
         return getId();
      }
      catch (ItemExistsException e)
      {
         throw new ItemAlreadyExistException("Destination folder already contains item with the same name. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable move item " + getPath() + " to " + folder.getPath()
            + ". Source item is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable move item " + getPath() + " to " + folder.getPath()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable move item " + getPath() + " to " + folder.getPath() + ". "
            + e.getMessage(), e);
      }
   }

   /**
    * Rename and(or) update content type of current object.
    * 
    * @param newname new name. May be <code>null</code> if name is unchangeable
    * @param mediaType new media type. May be <code>null</code> if content type is unchangeable
    * @param lockToken lock token. This lock token will be used if object is locked. Pass <code>null</code> if there is
    *           no lock token
    * @param addMixinTypes mixin types that must be added. Should be <code>null</code> if there is no additional mixins
    * @param removeMixinTypes mixin types that must be removed. Should be <code>null</code> if there is no mixins to
    *           remove
    * @return id of renamed object
    * @throws ConstraintException
    * @throws ItemAlreadyExistException if parent folder already contains object with the same name as specified
    * @throws LockException if object is locked and <code>lockToken</code> is <code>null</code> or does not matched
    * @throws PermissionDeniedException if object can't be renamed cause to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   abstract String rename(String newname, MediaType mediaType, String lockToken, String[] addMixinTypes,
      String[] removeMixinTypes) throws ConstraintException, ItemAlreadyExistException, LockException,
      PermissionDeniedException, VirtualFileSystemException;

   final Node getNode()
   {
      return node;
   }

   /**
    * @see java.lang.Object#hashCode()
    */
   @Override
   public int hashCode()
   {
      int hash = 8;
      hash = hash * 31 + type.hashCode();
      hash = hash * 31 + node.hashCode();
      return hash;
   }

   /**
    * @see java.lang.Object#equals(java.lang.Object)
    */
   @Override
   public boolean equals(Object obj)
   {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (obj == null || getClass() != obj.getClass())
         return false;
      ItemData other = (ItemData)obj;
      if (type != other.type)
         return false;
      if (!node.equals(other.node))
         return false;
      return true;
   }
}
