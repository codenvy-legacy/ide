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

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.query.Query;
import javax.jcr.query.QueryResult;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant;
import org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistantException;
import org.exoplatform.ide.codeassistant.framework.server.api.ShortTypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.api.TypeInfo;
import org.exoplatform.ide.codeassistant.framework.server.extractors.GroovyClassNamesExtractor;
import org.exoplatform.ide.codeassistant.framework.server.impl.storage.ClassInfoStorage;
import org.exoplatform.ide.codeassistant.framework.server.impl.storage.DocStorage;
import org.exoplatform.ide.codeassistant.framework.server.utils.DependentResources;
import org.exoplatform.ide.codeassistant.framework.server.utils.GroovyScriptServiceUtil;
import org.exoplatform.ide.codeassistant.framework.server.utils.JcrUtils;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: CodeAssistantStorageImpl Feb 8, 2011 2:22:22 PM evgen $
 *
 */
public class CodeAssistantJcrImpl implements CodeAssistant
{
   private final RepositoryService repositoryService;

   private final ThreadLocalSessionProviderService sessionProviderService;

   private final String wsClassStorage;
   
   private final String wsDocStorage;

   /** Logger. */
   private static final Log LOG = ExoLogger.getLogger(CodeAssistantJcrImpl.class);

   public CodeAssistantJcrImpl(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService, ClassInfoStorage classInfoStrorage, DocStorage docStorage)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
      this.wsClassStorage = classInfoStrorage.getClassStorageWorkspace();
      this.wsDocStorage = docStorage.getDocStorageWorkspace();
   }

   /**
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#getClassByFQN(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public TypeInfo getClassByFQN(String fqn) throws CodeAssistantException
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
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#getClassByFQNFromProject(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public TypeInfo getClassByFQNFromProject(String fqn, String location) throws CodeAssistantException
   {
      try
      {
         if (location != null)
         {
            DependentResources dependentResources =
               GroovyScriptServiceUtil.getDependentResource(location, repositoryService);
            if (dependentResources != null)
            {
               TypeInfo classInfo =
                  new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassInfo(fqn,
                     dependentResources);
               if (classInfo == null)
                  throw new CodeAssistantException(404, "Class info for " + fqn + " not found");
               return classInfo;
            }

         }
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(500, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(500, e.getMessage());
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      return null;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#findFQNsByClassName(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> findFQNsByClassName(String className) throws CodeAssistantException
   {
      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:className='" + className + "'";
      try
      {
         Session session = JcrUtils.getSession(repositoryService, sessionProviderService, wsClassStorage);
         Query q = session.getWorkspace().getQueryManager().createQuery(sql, Query.SQL);
         QueryResult result = q.execute();
         NodeIterator nodes = result.getNodes();

         while (nodes.hasNext())
         {

            Node node = (Node)nodes.next();
            types.add(new ShortTypeInfo((int)node.getProperty("exoide:modifieres").getLong(), node.getProperty(
               "exoide:className").getString(), node.getProperty("exoide:fqn").getString(), node.getProperty(
               "exoide:type").getString()));
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

   /**
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#findFQNsByClassNameInProject(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> findFQNsByClassNameInProject(String className, String location)
      throws CodeAssistantException
   {
      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      try
      {
         if (location != null && !location.isEmpty())
         {
            DependentResources dependentResources =
               GroovyScriptServiceUtil.getDependentResource(location, repositoryService);
            if (dependentResources != null)
            {
               types =
                  new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassNames(className,
                     dependentResources);
            }
         }
         return types;
      }
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(400, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(400, e.getMessage());
      }
      catch (RepositoryException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
      catch (RepositoryConfigurationException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(404, e.getMessage());
      }
   }

   /**
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#findFQNsByPrefix(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> findFQNsByPrefix(String prefix, String where) throws CodeAssistantException
   {

      List<ShortTypeInfo> types = new ArrayList<ShortTypeInfo>();
      //by default search in className
      if (where == null || "".equals(where))
      {
         where = "className";
      }
      else if (!"className".equals(where) && !"fqn".equals(where))
      {
         throw new CodeAssistantException(400, "\"where\" parameter must be className or fqn");
      }
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:" + where + " LIKE '" + prefix + "%'";
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
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#findFQNsByPrefix(java.lang.String, java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> findFQNsByPrefixInProject(String prefix, String location)
      throws CodeAssistantException
   {
      List<ShortTypeInfo> groovyClass = null;
      try
      {
         if (location != null && !location.isEmpty())
         {

            DependentResources dependentResources =
               GroovyScriptServiceUtil.getDependentResource(location, repositoryService);
            if (dependentResources != null)
            {
               groovyClass =
                  new GroovyClassNamesExtractor(repositoryService, sessionProviderService).getClassNames(prefix,
                     dependentResources);

            }
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
      catch (MalformedURLException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(400, e.getMessage());
      }
      catch (URISyntaxException e)
      {
         if (LOG.isDebugEnabled())
            e.printStackTrace();
         throw new CodeAssistantException(400, e.getMessage());
      }

      return groovyClass;
   }

   /**
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#findByType(java.lang.String, java.lang.String)
    */
   @Override
   public ShortTypeInfo[] findByType(String type, String prefix) throws CodeAssistantException
   {
      String sql = "SELECT * FROM exoide:classDescription WHERE exoide:type = '" + type.toUpperCase() + "'";
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

         ShortTypeInfo[] types = new ShortTypeInfo[(int)nodes.getSize()];
         int i = 0;
         while (nodes.hasNext())
         {
            Node node = (Node)nodes.next();
            types[i++] =
               new ShortTypeInfo((int)0L, node.getProperty("exoide:className").getString(), node.getProperty(
                  "exoide:fqn").getString(), node.getProperty("exoide:type").getString());
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

   /**
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#getClassDoc(java.lang.String)
    */
   @Override
   public String getClassDoc(String fqn) throws CodeAssistantException
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
    * @throws CodeAssistantException 
    * @see org.exoplatform.ide.codeassistant.framework.server.api.CodeAssistant#findClassesByProject(java.lang.String, java.lang.String)
    */
   @Override
   public List<ShortTypeInfo> findClassesByProject(String baseUri, String fileRelPath) throws CodeAssistantException
   {
      List<ShortTypeInfo> classes = new ArrayList<ShortTypeInfo>();
      List<String> classNames = new ArrayList<String>();
      
      if (GroovyScriptServiceUtil.checkPathIntoTheProjectSourceFolder(fileRelPath))
      {
         try
         {
            Session session = GroovyScriptServiceUtil.getSession(repositoryService);
            
            Node rootNode = session.getRootNode();
   
            NodeIterator nodeIterator = null;
            
            // return classes only if file exists at location        
            if (rootNode.hasNode(GroovyScriptServiceUtil.getParentFolderPath(fileRelPath)))
            {
               nodeIterator = rootNode.getNode(GroovyScriptServiceUtil.getParentFolderPath(fileRelPath)).getNodes("*.java");
            }
            else
            {
               return classes;
            }
    
            if (nodeIterator != null)
            {
               while (nodeIterator.hasNext())
               {
                  Node childNode = nodeIterator.nextNode();
                  
                  // pass node of file with the same path as received location header
                  if (fileRelPath.endsWith(childNode.getPath()))
                     continue;
               
                  // pass classes with the same name in the same folder
                  if (classNames.contains(GroovyScriptServiceUtil.getClassNameOnFileName(childNode.getName())))
                     continue;
                  
                  classes.add(new ShortTypeInfo(
                     (int)0L, 
                     GroovyScriptServiceUtil.getClassNameOnFileName(childNode.getName()), 
                     GroovyScriptServiceUtil.getFQNByFilePath(childNode.getPath()), 
                     "CLASS"
                  ));
                  
                  classNames.add(GroovyScriptServiceUtil.getClassNameOnFileName(childNode.getName()));
               }
            }
         }
         catch (RepositoryException e)
         {
            if (LOG.isDebugEnabled())
               e.printStackTrace();
            //TODO:need fix status code
            throw new CodeAssistantException(HTTPStatus.NOT_FOUND, e.getMessage());
         }
      }
      
      return classes;
   }

}
