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
package org.exoplatform.ide.groovy.util;

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonParserImpl;
import org.exoplatform.ws.frameworks.json.impl.ObjectBuilder;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

import java.io.InputStream;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Dec 24, 2010 $
 *
 */
public class GroovyScriptServiceUtil
{
   public static final String WEBDAV_CONTEXT = "/jcr/";

   /**
    * Get data about dependent sources by the given location
    * of the classpath file.
    * 
    * @param repositoryService repository service
    * @param sessionProviderService session provider service
    * @param baseUri base URI
    * @param classPathLocation location of the classpath file
    * @return {@link GroovyClassPath} groovy classpath data
    */
   public static GroovyClassPath getClassPath(RepositoryService repositoryService,
      SessionProviderService sessionProviderService, String baseUri, String classPathLocation)
   {
      String[] jcrLocation = parseJcrLocation(baseUri, classPathLocation);
      if (jcrLocation != null)
      {
         try
         {
            Session session =
               getSession(repositoryService, sessionProviderService, jcrLocation[0], jcrLocation[1] + "/"
                  + jcrLocation[2]);
            InputStream content = getFileContent(session, jcrLocation[2]);
            GroovyClassPath groovyClassPath = json2ClassPath(content);
            return groovyClassPath;
         }
         catch (RepositoryException e)
         {
            e.printStackTrace();
         }
         catch (RepositoryConfigurationException e)
         {
            e.printStackTrace();
         }
         catch (JsonException e)
         {
            e.printStackTrace();
         }
      }
      return null;
   }

   /**
    * Unmarshal classpath object in JSON format to Java bean {@link GroovyClassPath}.
    * 
    * @param stream 
    * @return {@link GroovyClassPath}
    * @throws JsonException
    */
   public static GroovyClassPath json2ClassPath(InputStream stream) throws JsonException
   {
      JsonParser jsonParser = new JsonParserImpl();
      JsonHandler jsonHandler = new JsonDefaultHandler();
      jsonParser.parse(stream, jsonHandler);
      JsonValue jsonValue = jsonHandler.getJsonObject();
      GroovyClassPath classPath = ObjectBuilder.createObject(GroovyClassPath.class, jsonValue);
      return classPath;
   }

   /**
    * Get content of file by its location in the repository.
    * 
    * @param session JCR session
    * @param repoPath path to file in repository
    * @return {@link InputStream} file's content
    * @throws RepositoryException
    */
   public static InputStream getFileContent(Session session, String repoPath) throws RepositoryException
   {
      Node rootNode = session.getRootNode();
      Node base = rootNode.getNode(repoPath);
      InputStream inputStream = base.getNode("jcr:content").getProperty("jcr:data").getStream();
      return inputStream;
   }

   /**
    * Parse JCR path to retrieve repository name, 
    * workspace name and absolute path in repository.
    * 
    * @param baseUri base URI
    * @param location file's location
    * @return array of {@link String}, where elements contain repository name, workspace name and 
    * the path to JCR node that contains file
    */
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
    * @param sessionProviderService session provider service
    * @param repoName repository's name
    * @param repoPath path to file in repository
    * @return {@link Session} created JCR session
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   public static Session getSession(RepositoryService repositoryService, SessionProviderService sessionProviderService,
      String repoName, String repoPath) throws RepositoryException, RepositoryConfigurationException
   {
      ManageableRepository repo = repositoryService.getRepository(repoName);
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      if (sp == null)
         throw new RepositoryException("SessionProvider is not properly set. Make the application calls"
            + "SessionProviderService.setSessionProvider(..) somewhere before ("
            + "for instance in Servlet Filter for WEB application)");

      String workspace = repoPath.split("/")[0];
      return sp.getSession(workspace, repo);
   }
}
