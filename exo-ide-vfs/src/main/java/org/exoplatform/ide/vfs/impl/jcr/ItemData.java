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
import org.exoplatform.ide.vfs.OutputProperty;
import org.exoplatform.ide.vfs.PropertyFilter;
import org.exoplatform.ide.vfs.Type;
import org.exoplatform.ide.vfs.VirtualFileSystemInfo.BasicPermissions;
import org.exoplatform.ide.vfs.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.exceptions.LockException;
import org.exoplatform.ide.vfs.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.exceptions.VirtualFileSystemException;
import org.exoplatform.services.jcr.access.PermissionType;
import org.exoplatform.services.jcr.core.ExtendedNode;

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
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.lock.Lock;
import javax.jcr.nodetype.PropertyDefinition;

/**
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id$
 */
abstract class ItemData
{
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
      if (node.isNodeType("nt:frozenNode"))
         return new VersionData(node);
      return new FolderData(node);
   }

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

   String getId() throws VirtualFileSystemException
   {
      return getPath();
   }

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

   Type getType()
   {
      return type;
   }

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

   long getLastModificationDate() throws VirtualFileSystemException
   {
      return getCreationDate();
   }

   List<OutputProperty> getProperties(PropertyFilter filter) throws PermissionDeniedException,
      VirtualFileSystemException
   {
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
         throw new PermissionDeniedException("Operation not permitted.");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
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
      catch (UnsupportedRepositoryOperationException e)
      {
         throw new NotSupportedException("Locking is not supported. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable add place lock to object. " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable place lock to object " + getPath() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   void unlock(List<String> lockTokens) throws LockException, PermissionDeniedException, VirtualFileSystemException
   {
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
      catch (UnsupportedRepositoryOperationException e)
      {
         throw new NotSupportedException("Locking is not supported. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable remove lock from object. " + e.getMessage());
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable remove lock from object " + getPath()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

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

   //   Boolean getBoolean(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         return node.getProperty(name).getBoolean();
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   Boolean[] getBooleans(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] values = node.getProperty(name).getValues();
   //         Boolean[] res = new Boolean[values.length];
   //         for (int i = 0; i < values.length; i++)
   //            res[i] = values[i].getBoolean();
   //         return res;
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   Calendar getDate(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         return node.getProperty(name).getDate();
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   Calendar[] getDates(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] values = node.getProperty(name).getValues();
   //         Calendar[] res = new Calendar[values.length];
   //         for (int i = 0; i < values.length; i++)
   //            res[i] = values[i].getDate();
   //         return res;
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   Double getDouble(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         return node.getProperty(name).getDouble();
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   Double[] getDoubles(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] values = node.getProperty(name).getValues();
   //         Double[] res = new Double[values.length];
   //         for (int i = 0; i < values.length; i++)
   //            res[i] = values[i].getDouble();
   //         return res;
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   Long getLong(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         return node.getProperty(name).getLong();
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   Long[] getLongs(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] values = node.getProperty(name).getValues();
   //         Long[] res = new Long[values.length];
   //         for (int i = 0; i < values.length; i++)
   //            res[i] = values[i].getLong();
   //         return res;
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   String getString(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         return node.getProperty(name).getString();
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   String[] getStrings(String name) throws PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] values = node.getProperty(name).getValues();
   //         String[] res = new String[values.length];
   //         for (int i = 0; i < values.length; i++)
   //            res[i] = values[i].getString();
   //         return res;
   //      }
   //      catch (PathNotFoundException e)
   //      {
   //         return null;
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable get property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValue(String name, boolean value) throws LockException, PermissionDeniedException,
   //      VirtualFileSystemException
   //   {
   //      try
   //      {
   //         node.setProperty(name, value);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException re)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + re.getMessage(), re);
   //      }
   //   }
   //
   //   void setValues(String name, boolean[] values) throws LockException, PermissionDeniedException,
   //      VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] jcrValue = new Value[values.length];
   //         for (int i = 0; i < jcrValue.length; i++)
   //            jcrValue[i] = new BooleanValue(values[i]);
   //         node.setProperty(name, jcrValue);
   //      }
   //      catch (IOException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValue(String name, Calendar value) throws LockException, PermissionDeniedException,
   //      VirtualFileSystemException
   //   {
   //      try
   //      {
   //         node.setProperty(name, value);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValues(String name, Calendar[] values) throws LockException, PermissionDeniedException,
   //      VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] jcrValue = new Value[values.length];
   //         for (int i = 0; i < jcrValue.length; i++)
   //            jcrValue[i] = new DateValue(values[i]);
   //         node.setProperty(name, jcrValue);
   //      }
   //      catch (IOException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValue(String name, double value) throws LockException, PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         node.setProperty(name, value);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValues(String name, double[] values) throws LockException, PermissionDeniedException,
   //      VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] jcrValue = new Value[values.length];
   //         for (int i = 0; i < jcrValue.length; i++)
   //            jcrValue[i] = new DoubleValue(values[i]);
   //         node.setProperty(name, jcrValue);
   //      }
   //      catch (IOException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValue(String name, long value) throws LockException, PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         node.setProperty(name, value);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValues(String name, long[] values) throws LockException, PermissionDeniedException,
   //      VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] jcrValue = new Value[values.length];
   //         for (int i = 0; i < jcrValue.length; i++)
   //            jcrValue[i] = new LongValue(values[i]);
   //         node.setProperty(name, jcrValue);
   //      }
   //      catch (IOException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValue(String name, String value) throws LockException, PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         node.setProperty(name, value);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }
   //
   //   void setValues(String name, String[] strings) throws LockException, PermissionDeniedException,
   //      VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Value[] jcrValue = new Value[strings.length];
   //         for (int i = 0; i < jcrValue.length; i++)
   //            jcrValue[i] = new StringValue(strings[i]);
   //         node.setProperty(name, jcrValue);
   //      }
   //      catch (IOException e)
   //      {
   //         throw new VirtualFileSystemException("Failed set or update property " + name + ". " + e.getMessage(), e);
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Access denied to property " + name + " of object " + getPath());
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException("Unable set property " + name + ". " + e.getMessage(), e);
   //      }
   //   }

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
         throw new LockException("Unable delete object " + getPath() + ". Object is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable delete object " + getPath() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   //   @Deprecated
   //   void save() throws LockException, PermissionDeniedException, VirtualFileSystemException
   //   {
   //      try
   //      {
   //         Session session = node.getSession();
   //         session.save();
   //      }
   //      catch (AccessDeniedException e)
   //      {
   //         throw new PermissionDeniedException("Operation not permitted. " + e.getMessage());
   //      }
   //      catch (javax.jcr.lock.LockException e)
   //      {
   //         throw new LockException("Object " + getPath() + " is locked. ");
   //      }
   //      catch (RepositoryException e)
   //      {
   //         throw new VirtualFileSystemException(e.getMessage(), e);
   //      }
   //   }

   Node getNode()
   {
      return node;
   }

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
         throw new LockException("Unable update ACL of object " + getPath() + ". Object is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable update ACL of object " + getPath() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   List<AccessControlEntry> getACL() throws LockException, PermissionDeniedException, VirtualFileSystemException
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
         throw new PermissionDeniedException("Unable get ACL of object " + getPath() + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }

   String moveTo(FolderData folder, List<String> lockTokens) throws InvalidArgumentException, LockException,
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
         return destinationPath;
      }
      catch (ItemExistsException e)
      {
         throw new InvalidArgumentException("Destination folder already contains object with the same name. ");
      }
      catch (javax.jcr.lock.LockException e)
      {
         throw new LockException("Unable move object " + getPath() + " to " + folder.getPath()
            + ". Source object or destination folder is locked. ");
      }
      catch (AccessDeniedException e)
      {
         throw new PermissionDeniedException("Unable move object " + getPath() + " to " + folder.getPath()
            + ". Operation not permitted. ");
      }
      catch (RepositoryException e)
      {
         throw new VirtualFileSystemException(e.getMessage(), e);
      }
   }
}
