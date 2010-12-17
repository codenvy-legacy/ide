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

import org.exoplatform.ide.vfs.AccessControlEntry;
import org.exoplatform.ide.vfs.InputProperty;
import org.exoplatform.ide.vfs.OutputProperty;
import org.exoplatform.ide.vfs.PropertyFilter;
import org.exoplatform.ide.vfs.Type;
import org.exoplatform.ide.vfs.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.exceptions.ConstraintException;
import org.exoplatform.ide.vfs.exceptions.LockException;
import org.exoplatform.ide.vfs.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.exceptions.VirtualFileSystemRuntimeException;
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
import javax.jcr.nodetype.NodeType;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * Wrapper around node node to simplify interaction with JCR.
 * 
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
abstract class ItemData
{
   /**
    * Empty stream. Need it for value of property "jcr:data" of sub-node
    * "jcr:content" of "nt:file" nodes.
    */
   static InputStream EMPTY = new InputStream() {
      public int read()
      {
         return -1;
      }
   };

   static ItemData fromNode(Node node) throws RepositoryException
   {
      if (node.isNodeType("nt:file") && node.getNode("jcr:content").isNodeType("nt:resource"))
         return new DocumentData(node);
      if (node.isNodeType("nt:resource") && "jcr:content".equals(node.getName()))
         return new DocumentData(node.getParent());
      if (node.isNodeType("nt:frozenNode"))
         return new VersionData(node);
      return new FolderData(node);
   }

   /** Set of known JCR properties that should be skipped. */
   static final Set<String> SKIPPED_PROPERTIES = new HashSet<String>(Arrays.asList("jcr:primaryType", "jcr:created",
      "jcr:uuid", "jcr:baseVersion", "jcr:isCheckedOut", "jcr:predecessors", "jcr:versionHistory", "jcr:mixinTypes",
      "jcr:frozenMixinTypes", "jcr:frozenPrimaryType", "jcr:frozenUuid"));

   protected Node node;

   protected Type type;

   ItemData(Node node, Type type)
   {
      this.node = node;
      this.type = type;
   }

   /**
    * @return unified identifier of this item
    * @throws VirtualFileSystemException if any errors occurs
    */
   String getId() throws VirtualFileSystemException
   {
      return getPath();
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
    * @see Type
    */
   Type getType()
   {
      return type;
   }

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
    * @throws PermissionDeniedException if properties can't be retrieved cause
    *            to security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   List<OutputProperty> getProperties(PropertyFilter filter) throws PermissionDeniedException,
      VirtualFileSystemException
   {
      // TODO : property name mapping ?
      // jcr:blabla -> vfs:blabla
      try
      {
         List<OutputProperty> properties = new ArrayList<OutputProperty>();
         for (PropertyIterator i = node.getProperties(); i.hasNext();)
         {
            javax.jcr.Property jcrProperty = i.nextProperty();
            String name = jcrProperty.getName();
            if (!SKIPPED_PROPERTIES.contains(name) && filter.accept(name))
               properties.add(new OutputProperty(name, createProperty(jcrProperty)));
         }
         return properties;
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get properties of object " + getId()
            + ". Operation not permitted.");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get properties of object " + getId() + ". " + e.getMessage(), e);
      }
   }

   private Object[] createProperty(javax.jcr.Property property) throws RepositoryException
   {
      PropertyDefinition definition = property.getDefinition();
      boolean multiple = definition.isMultiple();
      switch (property.getType())
      {
         case PropertyType.DATE : {
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               Long[] values = new Long[jcrValues.length];
               for (int i = 0; i < jcrValues.length; i++)
                  values[i] = jcrValues[i].getLong();
               return values;
            }
            return new Long[]{property.getLong()};
         }
         case PropertyType.DOUBLE : {
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               Double[] values = new Double[jcrValues.length];
               for (int i = 0; i < jcrValues.length; i++)
                  values[i] = jcrValues[i].getDouble();
               return values;
            }
            return new Double[]{property.getDouble()};
         }
         case PropertyType.LONG : {
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               Long[] values = new Long[jcrValues.length];
               for (int i = 0; i < jcrValues.length; i++)
                  values[i] = jcrValues[i].getLong();
               return values;
            }
            return new Long[]{property.getLong()};
         }
         case PropertyType.BOOLEAN : {
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               Boolean[] values = new Boolean[jcrValues.length];
               for (int i = 0; i < jcrValues.length; i++)
                  values[i] = jcrValues[i].getBoolean();
               return values;
            }
            return new Boolean[]{property.getBoolean()};
         }
         case PropertyType.STRING :
         case PropertyType.BINARY :
         case PropertyType.NAME :
         case PropertyType.PATH :
         case PropertyType.REFERENCE :
         default : {
            if (multiple)
            {
               Value[] jcrValues = property.getValues();
               String[] values = new String[jcrValues.length];
               for (int i = 0; i < jcrValues.length; i++)
                  values[i] = jcrValues[i].getString();
               return values;
            }
            return new String[]{property.getString()};
         }
      }
   }

   /**
    * Update properties.
    * 
    * @param properties set of properties that should be updated.
    * @param lockTokens lock tokens. This lock tokens will be used if object is
    *           locked. Pass <code>null</code> or empty list if there is no lock
    *           tokens
    * @throws ConstraintException if any of following conditions are met:
    *            <ul>
    *            <li>at least one of updated properties is read-only</li>
    *            <li>value of any updated properties is not acceptable</li>
    *            </ul>
    * @throws LockException if object is locked and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if properties can't be updated cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void updateProperties(List<InputProperty> properties, List<String> lockTokens) throws ConstraintException,
      LockException, PermissionDeniedException, VirtualFileSystemException
   {
      // TODO : property name mapping ?
      // vfs:blabla -> jcr:blabla
      try
      {
         Session session = node.getSession();
         if (lockTokens != null && lockTokens.size() > 0)
         {
            for (String lt : lockTokens)
               session.addLockToken(lt);
         }
         NodeType nodeType =
            node.isNodeType("nt:frozenNode") ? session.getWorkspace().getNodeTypeManager()
               .getNodeType(node.getProperty("jcr:frozenPrimaryType").getString()) : node.getPrimaryNodeType();
         PropertyDefinition[] propertyDefinitions = nodeType.getPropertyDefinitions();
         Map<String, PropertyDefinition> cache = new HashMap<String, PropertyDefinition>(propertyDefinitions.length);
         for (int i = 0; i < propertyDefinitions.length; i++)
            cache.put(propertyDefinitions[i].getName(), propertyDefinitions[i]);
         for (InputProperty property : properties)
         {
            String name = property.getName();
            String[] value = property.getValue();
            PropertyDefinition pd = cache.get(name);
            try
            {
               if (pd != null)
               {
                  if (pd.isProtected())
                     throw new ConstraintException("Property " + name + " is read-only. ");

                  if (value == null || value.length == 0)
                  {
                     if (pd.isMandatory())
                        throw new ConstraintException("Property " + name + " can't have null value. ");
                     removeProperty(name);
                  }
                  else
                  {
                     boolean multiple = pd.isMultiple();
                     if (multiple)
                        updateProperty(name, value);
                     else
                        updateProperty(name, value[0]);
                  }
               }
               else
               {
                  // Try to set property even there is no definition for that.
                  if (value == null || value.length == 0)
                     removeProperty(name);
                  else if (value.length == 1)
                     updateProperty(name, value[0]);
                  else
                     updateProperty(name, value);
               }
            }
            catch (IOException e)
            {
               throw new VirtualFileSystemRuntimeException(e.getMessage(), e);
            }
            catch (ValueFormatException e)
            {
               throw new ConstraintException("Unable update property " + name + ". Specified value is not allowed. ");
            }
         }
         session.save();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable to update properties of object " + getId() + ". Object is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable to update properties of object " + getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update properties of object " + getId() + ". " + e.getMessage(),
            e);
      }
   }

   private void updateProperty(String name, String value) throws RepositoryException, IOException
   {
      node.setProperty(name, new StringValue(value));
   }

   private void updateProperty(String name, String[] value) throws RepositoryException, IOException
   {
      Value[] jcrValue = new Value[value.length];
      for (int i = 0; i < value.length; i++)
         jcrValue[i] = new StringValue(value[i]);
      node.setProperty(name, jcrValue);
   }

   private void removeProperty(String name) throws RepositoryException
   {
      node.setProperty(name, (Value)null);
   }

   /**
    * Check is object locked or not.
    * 
    * @return <code>true</code> if object is locked and <code>false</code>
    *         otherwise
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
    * Place lock to current object.
    * 
    * @param isDeep if <code>true</code> this lock will apply to this object and
    *           all its descendants (if any). If <code>false</code> , it applies
    *           only to this object.
    * @return lock token
    * @throws LockException if object already locked
    * @throws PermissionDeniedException if object can't be locked cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   String lock(boolean isDeep) throws LockException, PermissionDeniedException, VirtualFileSystemException
   {
      if (isLocked())
         throw new LockException("Object already locked. ");
      try
      {
         if (node.canAddMixin("mix:lockable"))
         {
            Session session = node.getSession();
            node.addMixin("mix:lockable");
            session.save();
         }
         Lock lock = node.lock(isDeep, false);
         return lock.getLockToken();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable place lock to object " + getId() + ". " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable place lock to object " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable place lock to object " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Remove lock from object.
    * 
    * @param lockTokens set of lock tokens
    * @throws LockException if object is not locked or <code>lockTokens</code>
    *            is <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if lock can't be removed cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void unlock(List<String> lockTokens) throws LockException, PermissionDeniedException, VirtualFileSystemException
   {
      if (!isLocked())
         throw new LockException("Object is not locked. ");
      try
      {
         Session session = node.getSession();
         if (lockTokens != null && lockTokens.size() > 0)
         {
            for (String lt : lockTokens)
               session.addLockToken(lt);
         }
         node.unlock();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable remove lock from object " + getId() + ". " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable remove lock from object " + getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable remove lock from object " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Delete item.
    * 
    * @param lockTokens lock tokens. This lock tokens will be used if object is
    *           locked. Pass <code>null</code> or empty list if there is no lock
    *           tokens
    * @throws LockException if object is locked and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if object can't be removed cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void delete(List<String> lockTokens) throws LockException, PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         Session session = node.getSession();
         if (lockTokens != null && lockTokens.size() > 0)
         {
            for (String lt : lockTokens)
               session.addLockToken(lt);
         }
         node.remove();
         session.save();
         node = null;
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable delete object " + getId() + ". Object is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable delete object " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable delete object " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Get ACL applied to object.
    * 
    * @return ACL applied to object
    * @throws PermissionDeniedException if ACL can't be retrieved cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   List<AccessControlEntry> getACL() throws PermissionDeniedException, VirtualFileSystemException
   {
      try
      {
         if (!node.isNodeType("exo:privilegeable"))
            return Collections.emptyList();

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
               ace.getPermissions().add(BasicPermissions.ALL.value());
            else if (values.contains(PermissionType.READ) && values.contains(PermissionType.ADD_NODE))
               ace.getPermissions().add(BasicPermissions.READ.value());
            else if (values.contains(PermissionType.SET_PROPERTY) && values.contains(PermissionType.REMOVE))
               ace.getPermissions().add(BasicPermissions.WRITE.value());
            acl.add(ace);
         }
         return acl;
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable get ACL of object " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable get ACL of object " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Update ACL applied to object.
    * 
    * @param acl ACL
    * @param override if <code>true</code> then previous ACL replaced by
    *           specified. If <code>false</code> then specified ACL will be
    *           merged with existed
    * @param lockTokens lock tokens. This lock tokens will be used if object is
    *           locked. Pass <code>null</code> or empty list if there is no lock
    *           tokens
    * @throws LockException if object is locked and <code>lockTokens</code> is
    *            <code>null</code> or does not contains matched lock tokens
    * @throws PermissionDeniedException if ACL can't be updated cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   void updateACL(List<AccessControlEntry> acl, boolean override, List<String> lockTokens) throws LockException,
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

         if (lockTokens != null && lockTokens.size() > 0)
         {
            for (String lt : lockTokens)
               session.addLockToken(lt);
         }

         if (!extNode.isNodeType("exo:privilegeable"))
            extNode.addMixin("exo:privilegeable");

         Map<String, String[]> aces = new HashMap<String, String[]>(tmp.size());
         for (Map.Entry<String, Set<String>> e : tmp.entrySet())
            aces.put(e.getKey(), e.getValue().toArray(new String[e.getValue().size()]));

         if (override)
            extNode.setPermissions(aces);
         else
            for (Map.Entry<String, String[]> e : aces.entrySet())
               extNode.setPermission(e.getKey(), e.getValue());

         session.save();
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable update ACL of object " + getId() + ". Object is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable update ACL of object " + getId() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable update ACL of object " + getId() + ". " + e.getMessage(), e);
      }
   }

   /**
    * Create copy of current item in specified folder.
    * 
    * @param folder parent
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>folder</folder> is locked. Pass <code>null</code> or empty
    *           list if there is no lock tokens
    * @return newly create copy
    * @throws ConstraintException if destination folder already contains object
    *            with the same name as current
    * @throws LockException if <code>folder</folder> is locked and
    *            <code>lockTokens</code> is <code>null</code> or does not
    *            contains matched lock tokens
    * @throws PermissionDeniedException if new copy can't be created cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   ItemData copyTo(FolderData folder, List<String> lockTokens) throws ConstraintException, LockException,
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
         String objectPath = getPath();
         String destinationPath = folder.getPath();
         destinationPath += destinationPath.equals("/") ? getName() : ("/" + getName());
         session.getWorkspace().copy(objectPath, destinationPath);
         return fromNode((Node)session.getItem(destinationPath));
      }
      catch (ItemExistsException e)
      {
         throw new ConstraintException("Destination folder already contains object with the same name. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable copy object " + getId() + " to " + folder.getId()
            + ". Destination folder is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable copy object " + getId() + " to " + folder.getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable copy object " + getId() + " to " + folder.getId() + ". "
            + e.getMessage(), e);
      }
   }

   /**
    * Move current item in specified folder.
    * 
    * @param folder new parent
    * @param lockTokens lock tokens. This lock tokens will be used if
    *           <code>folder</folder> is locked. Pass <code>null</code> or empty
    *           list if there is no lock tokens
    * @return identifier moved object
    * @throws ConstraintException if destination folder already contains object
    *            with the same name as current
    * @throws LockException if <code>folder</folder> is locked and
    *            <code>lockTokens</code> is <code>null</code> or does not
    *            contains matched lock tokens
    * @throws PermissionDeniedException if object can't be moved cause to
    *            security restriction
    * @throws VirtualFileSystemException if any other errors occurs
    */
   String moveTo(FolderData folder, List<String> lockTokens) throws ConstraintException, LockException,
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
         String objectPath = getPath();
         String destinationPath = folder.getPath();
         destinationPath += destinationPath.equals("/") ? getName() : ("/" + getName());
         session.getWorkspace().move(objectPath, destinationPath);
         node = (Node)session.getItem(destinationPath);
         return getId();
      }
      catch (ItemExistsException e)
      {
         throw new ConstraintException("Destination folder already contains object with the same name. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable move object " + getId() + " to " + folder.getId()
            + ". Source object or destination folder is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable move object " + getId() + " to " + folder.getId()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException("Unable move object " + getId() + " to " + folder.getId() + ". "
            + e.getMessage(), e);
      }
   }

   Node getNode()
   {
      return node;
   }
}
