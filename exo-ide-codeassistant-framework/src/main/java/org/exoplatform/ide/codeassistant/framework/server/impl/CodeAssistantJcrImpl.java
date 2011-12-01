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
import org.exoplatform.ide.codeassistant.framework.server.impl.storage.ClassInfoStorage;
import org.exoplatform.ide.codeassistant.framework.server.impl.storage.DocStorage;
import org.exoplatform.ide.codeassistant.framework.server.utils.JcrUtils;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantException;
import org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage;
import org.exoplatform.ide.codeassistant.jvm.JavaType;
import org.exoplatform.ide.codeassistant.jvm.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.jvm.TypeInfo;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistantStorageImpl Feb 8, 2011 2:22:22 PM evgen $
 *
 */
public class CodeAssistantJcrImpl implements CodeAssistantStorage
{
   private final RepositoryService repositoryService;

   private final ThreadLocalSessionProviderService sessionProviderService;

   private final String wsClassStorage;

   private final String wsDocStorage;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(CodeAssistantJcrImpl.class);

   public CodeAssistantJcrImpl(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService, ClassInfoStorage classInfoStrorage,
      DocStorage docStorage)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      this.wsClassStorage = classInfoStrorage.getClassStorageWorkspace();
      this.wsDocStorage = docStorage.getDocStorageWorkspace();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#getClassByFQN(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public TypeInfo getTypeByFqn(String fqn) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:fqn='" + fqn + "'";
      try
      {
         Session session =
            org.exoplatform.ide.codeassistant.framework.server.utils.JcrUtils.getSession(repositoryService,
               sessionProviderService, wsClassStorage);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();
         //TODO
         if (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            JsonParser jsonParser = new JsonParser();
            jsonParser.parse(node.getProperty("jcr:data").getStream());
            JsonValue jsonValue = jsonParser.getJsonObject();
            TypeInfo typeInfo = ObjectBuilder.createObject(TypeInfo.class, jsonValue);
            return typeInfo;
         }
         return null;

      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (JsonException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(500, e.getMessage());
      }

   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByPrefix(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByNamePrefix(String prefix) throws CodeAssistantException
   {

      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:className LIKE '" + prefix + "%'";
      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsClassStorage);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();

         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            types.add(new ShortTypeInfo((int)0L, node.getProperty("exoide:className").getString(), node.getProperty(
               "exoide:fqn").getString(), node.getProperty("exoide:type").getString()));
         }

      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }

      return types;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findFQNsByPrefix(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> getTypesByFqnPrefix(String prefix) throws CodeAssistantException
   {

      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:fqn LIKE '" + prefix + "%'";
      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsClassStorage);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();

         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            types.add(new ShortTypeInfo((int)0L, node.getProperty("exoide:className").getString(), node.getProperty(
               "exoide:fqn").getString(), node.getProperty("exoide:type").getString()));
         }

      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }

      return types;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#findByType(java.lang.String, java.lang.String)
    */

   private List<ShortTypeInfo> findByType(JavaType type, String prefix) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:type = '" + type.toString() + "'";
      if (prefix != null && !prefix.isEmpty())
      {
         sql += " AND exoide:className LIKE '" + prefix + "%'";
      }

      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsClassStorage);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();

         List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>((int)nodes.getSize());
         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            types.add(new ShortTypeInfo((int)0L, node.getProperty("exoide:className").getString(), node.getProperty(
               "exoide:fqn").getString(), node.getProperty("exoide:type").getString()));
         }
         return types;
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
   }

   public List<ShortTypeInfo> getAnnotations(String prefix) throws CodeAssistantException
   {
      return findByType(JavaType.ANNOTATION, prefix);

   };

   @Override
   public List<ShortTypeInfo> getClasses(String prefix) throws CodeAssistantException
   {
      return findByType(JavaType.CLASS, prefix);
   }

   @Override
   public List<ShortTypeInfo> getIntefaces(String prefix) throws CodeAssistantException
   {
      return findByType(JavaType.INTERFACE, prefix);
   }

   /**
    * @see org.exoplatform.ide.codeassistant.api.CodeAssistant#getClassDoc(java.lang.String)
    */
   @Override
   public String getClassJavaDoc(String fqn) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:javaDoc WHERE exoide:fqn='" + fqn + "'";
      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsDocStorage);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();

         String doc = new String();
         if (nodes.getSize() == 0)
            throw new CodeAssistantException(404, "Not found");
         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            doc = node.getProperty("jcr:data").getString();
         }
         doc = doc.replaceAll("[ ]+\\*", "");
         // need to return valid HTML
         return doc;
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         //TODO:need fix status code
         throw new CodeAssistantException(404, e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.codeassistant.jvm.CodeAssistantStorage#getMemberJavaDoc(java.lang.String)
    */
   @Override
   public String getMemberJavaDoc(String fqn) throws CodeAssistantException
   {
      return getClassJavaDoc(fqn);
   }
}
