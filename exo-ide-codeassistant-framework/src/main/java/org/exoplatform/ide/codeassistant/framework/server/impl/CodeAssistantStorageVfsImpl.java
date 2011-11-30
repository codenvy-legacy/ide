/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.framework.server.impl;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.impl.storage.ClassInfoStorage;
import org.exoplatform.ide.codeassistant.framework.server.impl.storage.DocStorage;
import org.exoplatform.ide.vfs.server.PropertyFilter;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.NotSupportedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version ${Id}:  Nov 23, 2011 12:20:01 PM evgen $
 *
 */
public class CodeAssistantStorageVfsImpl implements CodeAssistantStorage
{

   private static final Logger LOG = LoggerFactory.getLogger(CodeAssistantStorageVfsImpl.class);

   private final String wsClassStorage;

   private final String wsDocStorage;

   private final VirtualFileSystemRegistry vfsRegistry;

   /**
    * 
    */
   public CodeAssistantStorageVfsImpl(VirtualFileSystemRegistry vfsRegistry, ClassInfoStorage classInfoStrorage,
      DocStorage docStorage)
   {
      this.vfsRegistry = vfsRegistry;
      wsClassStorage = classInfoStrorage.getClassStorageWorkspace();
      wsDocStorage = docStorage.getDocStorageWorkspace();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistantStorage#getClassByFQN(java.lang.String)
    */
   @Override
   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:fqn='" + fqn + "'";
      VirtualFileSystem vfs = getVfs(wsClassStorage);
      try
      {
         ItemList<Item> list = vfs.search(sql, -1, 0);
         if (!list.getItems().isEmpty())
         {
            JsonParser jsonParser = new JsonParser();
            jsonParser.parse(vfs.getContent(list.getItems().get(0).getId()).getStream());
            JsonValue jsonValue = jsonParser.getJsonObject();
            TypeInfo typeInfo = ObjectBuilder.createObject(TypeInfo.class, jsonValue);
            return typeInfo;
         }
         return null;
      }
      catch (NotSupportedException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(500, e.getMessage());
      }
      catch (InvalidArgumentException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(500, e.getMessage());
      }
      catch (VirtualFileSystemException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(500, e.getMessage());
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(500, e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistantStorage#findFQNsByClassName(java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByNamePrefix(String className) throws CodeAssistantException
   {
      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:className='" + className + "'";
      VirtualFileSystem vfs = getVfs(wsClassStorage);

      ItemList<Item> list;
      try
      {
         list = vfs.search(sql, -1, 0);
         for (Item i : list.getItems())
         {
            types.add(fromItem(vfs.getItem(i.getId(),
               PropertyFilter.valueOf("exoide:modifieres, exoide:className, exoide:fqn, exoide:type"))));
         }
         return types;
      }
      catch (NotSupportedException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (InvalidArgumentException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (VirtualFileSystemException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }

   }

   /**
    * @param i
    * @return
    */
   private ShortTypeInfo fromItem(Item i)
   {
      ShortTypeInfo info = new ShortTypeInfo();
      if (i.hasProperty("exoide:modifieres"))
      {
         info.setModifiers(((Double)i.getPropertyValue("exoide:modifieres")).intValue());
      }
      if (i.hasProperty("exoide:className"))
         info.setName((String)i.getPropertyValue("exoide:className"));
      if (i.hasProperty("exoide:fqn"))
         info.setQualifiedName((String)i.getPropertyValue("exoide:fqn"));
      if (i.hasProperty("exoide:type"))
         info.setType((String)i.getPropertyValue("exoide:type"));
      return info;
   }

  

   private VirtualFileSystem getVfs(String id) throws CodeAssistantException
   {
      try
      {
         return vfsRegistry.getProvider(id).newInstance(null);
      }
      catch (VirtualFileSystemException e)
      {
         throw new CodeAssistantException(500, e.getMessage());
      }
   }

   @Override
   public List<ShortTypeInfo> getTypesByFqnPrefix(String fqnPrefix) throws CodeAssistantException
   {
      return null;
   }

   @Override
   public List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException
   {
      return null;
   }

   @Override
   public List<ShortTypeInfo> getIntefaces(String prefix) throws CodeAssistantException
   {
      return null;
   }

   @Override
   public List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException
   {
      return null;
   }

   @Override
   public String getJavaDoc(String fqn) throws CodeAssistantException
   {
      return null;
   }

}
