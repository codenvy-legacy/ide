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
package org.exoplatform.ide.codeassistant.framework.server.utils;

import java.io.InputStream;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.LoginException;
import javax.jcr.NoSuchWorkspaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.everrest.core.impl.provider.json.JsonException;
import org.everrest.core.impl.provider.json.JsonParser;
import org.everrest.core.impl.provider.json.JsonValue;
import org.everrest.core.impl.provider.json.ObjectBuilder;
import org.exoplatform.ide.discovery.RepositoryDiscoveryService;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 24, 2010 $
 * 
 */
public class GroovyScriptServiceUtil
{
   public static final String WEBDAV_CONTEXT = "/jcr/";

   public static final String GROOVY_CLASSPATH = ".groovyclasspath";

   static final String JAVA_SOURCE_ROOT_PREFIX = "/src/main/java";

   /**
    * Unmarshal classpath object in JSON format to Java bean {@link GroovyClassPath}.
    * 
    * @param stream
    * @return {@link GroovyClassPath}
    * @throws JsonException
    */
   public static GroovyClassPath json2ClassPath(InputStream stream) throws JsonException
   {
      // TODO check it works:
      JsonParser jsonParser = new JsonParser();
      jsonParser.parse(stream);
      JsonValue jsonValue = jsonParser.getJsonObject();
      GroovyClassPath classPath = ObjectBuilder.createObject(GroovyClassPath.class, jsonValue);
      return classPath;
   }

   /**
    * Parse JCR path to retrieve repository name, workspace name and absolute path in repository.
    * 
    * @param baseUri base URI
    * @param location file's location
    * @return array of {@link String}, where elements contain repository name, workspace name and the path to JCR node that
    *         contains file
    */
   @Deprecated
   public static String[] parseJcrLocation(String baseUri, String location)
   {
      baseUri += WEBDAV_CONTEXT;
      if (!location.startsWith(baseUri))
      {
         return null;
      }
      String[] elements = new String[3];
      location = location.substring(baseUri.length());
      elements[0] = location.substring(0, location.indexOf('/'));
      location = location.substring(location.indexOf('/') + 1);
      elements[1] = location.substring(0, location.indexOf('/'));
      elements[2] = location.substring(location.indexOf('/') + 1);
      return elements;
   }

   /**
    * @param repositoryService repository service
    * @return
    * @throws LoginException
    * @throws NoSuchWorkspaceException
    * @throws RepositoryException
    */
   public static Session getSession(RepositoryService repositoryService) throws LoginException,
      NoSuchWorkspaceException, RepositoryException
   {
      ManageableRepository repository = repositoryService.getCurrentRepository();
      return repository.login(RepositoryDiscoveryService.getEntryPoint());
   }

   /**
    * Find class path file's node by name step by step going upper in node hierarchy.
    * 
    * @param node node, in what child nodes to find class path file
    * @return {@link Node} found jcr node
    * @throws RepositoryException
    */
   public static Node findClassPathNode(Node node) throws RepositoryException
   {
      if (node == null)
         return null;
      // Get all child node that end with ".groovyclasspath"
      NodeIterator nodeIterator = node.getNodes("*" + GROOVY_CLASSPATH);
      while (nodeIterator.hasNext())
      {
         Node childNode = nodeIterator.nextNode();
         // The first found groovy class path file will be returned:
         if (GROOVY_CLASSPATH.equals(childNode.getName()))
            return childNode;
      }
      try
      {
         // Go upper to find class path file:
         Node parentNode = node.getParent();
         return findClassPathNode(parentNode);
      }
      catch (ItemNotFoundException e)
      {
         return null;
      }
      catch (AccessDeniedException e)
      {
         return null;
      }
   }

   /**
    * Get content of existed groovy class path file.
    * 
    * @param location script location
    * @return {@link InputStream} the content of proper groovy class path file
    */
   protected static InputStream getClassPathContent(String location, RepositoryService repositoryService)
   {
      location = (location.startsWith("/")) ? location.substring(1) : location;
      try
      {
         Session session = GroovyScriptServiceUtil.getSession(repositoryService);
         Node rootNode = session.getRootNode();
         Node scriptNode = rootNode.getNode(location);
         Node classpathNode = findClassPathNode(scriptNode.getParent());
         if (classpathNode != null)
         {
            return classpathNode.getNode("jcr:content").getProperty("jcr:data").getStream();
         }
      }
      catch (RepositoryException e)
      {
         return null;
      }
      return null;
   }

   /**
    * Get dependent resources of the script from classpath file.
    * 
    * @param scriptLocation location of script, which uses classpath file
    * @return {@link DependentResources} dependent resources
    */
   public static DependentResources getDependentResource(String scriptLocation, RepositoryService repositoryService)
   {
      // Get content of groovy class path file:
      InputStream classPathFileContent = getClassPathContent(scriptLocation, repositoryService);

      if (classPathFileContent != null)
      {
         try
         {
            // Unmarshal content in JSON format to Java object:
            GroovyClassPath groovyClassPath = GroovyScriptServiceUtil.json2ClassPath(classPathFileContent);
            if (groovyClassPath != null)
            {
               // Get current repository name, if not null or default repository's name.
               String repositoryName =
                  (repositoryService.getCurrentRepository() != null) ? repositoryService.getCurrentRepository()
                     .getConfiguration().getName() : repositoryService.getDefaultRepository().getConfiguration()
                     .getName();
               return new DependentResources(repositoryName, groovyClassPath);
            }
         }
         catch (JsonException e)
         {
            return null;
         }
         catch (RepositoryException e)
         {
            return null;
         }
         catch (RepositoryConfigurationException e)
         {
            return null;
         }
      }
      return null;
   }

   /**
    * Return word until first point like "ClassName" on file name "ClassName.java"
    * 
    * @param fileName
    * @return
    */
   public static String getClassNameOnFileName(String fileName)
   {
      if (fileName != null)
         return fileName.substring(0, fileName.indexOf("."));

      return null;
   }

   /**
    * Return possible FQN like "org.exoplatform.example.ClassName" on file path "/org/exoplatform/example/ClassName.java"
    * 
    * @param fileName
    * @return
    */
   public static String getFQNByFilePath(String filePath)
   {
      if (filePath != null)
      {
         String fqn = filePath;

         // remove "[...]" from path like "[3]" from path "org/exoplatform[3]/example/ClassName.java"
         fqn = fqn.replaceAll("\\[.*\\]", "");

         // looking for java source folder root like "/src/main/java" to remove unnecessary path prefix like
         // "/My Project/src/main/java" in path "/My Project/src/main/java/com/example/"
         if (fqn.matches(".*" + JAVA_SOURCE_ROOT_PREFIX + ".*"))
         {
            fqn = fqn.replaceAll(".*" + JAVA_SOURCE_ROOT_PREFIX, "");
         }

         // remove file extension from path like ".java" from path "org/exoplatform/example/ClassName.java"
         if (fqn.matches(".*[.][^/]*$"))
            fqn = fqn.substring(0, fqn.lastIndexOf("."));

         // remove symbol "/" at the start of string
         if (fqn.indexOf("/") == 0)
            fqn = fqn.substring(1);

         // replace "/" on "."
         fqn = fqn.replaceAll("/", ".");

         return fqn;
      }

      return null;
   }

   /**
    * Return possible file parent folder path like "org/exoplatform/example" on file path
    * "/org/exoplatform/example/ClassName.java", or return file path as it if there is no any "/" in file path.
    * 
    * @param fileRelPath
    * @return
    */
   public static String getParentFolderPath(String fileRelPath)
   {
      if (fileRelPath != null)
      {
         if (fileRelPath.indexOf("/") != -1)
         {
            String parentFolderPath = fileRelPath.substring(0, fileRelPath.lastIndexOf("/"));

            // remove started "/"
            if (parentFolderPath.indexOf("/") == 0)
            {
               parentFolderPath = parentFolderPath.substring(1);
            }

            return parentFolderPath;
         }
         else
         {
            return fileRelPath;
         }

      }
      return null;
   }

   /**
    * Looking for java source folder root like "/src/main/java" as part of location.
    * 
    * @param location
    * @return
    */
   public static boolean checkPathIntoTheProjectSourceFolder(String location)
   {
      return location.matches(".*" + JAVA_SOURCE_ROOT_PREFIX + ".*");
   }

}
