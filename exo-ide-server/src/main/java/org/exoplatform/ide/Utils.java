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
package org.exoplatform.ide;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.Item;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ExtendedNode;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

/**
 * Utils class, that contains methods for creation of folders and files nodes in jcr.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Utils.java Apr 8, 2011 2:51:02 PM vereshchaka $
 * @deprecated not use JCR directly any more
 */
public class Utils
{
   public static final String DEFAULT_JCR_CONTENT_NODE_TYPE = "nt:resource";

   public static final String DEFAULT_FILE_NODE_TYPE = "nt:file";

   /**
    * Read value param from init params.
    * @param initParams
    * @param paramName
    * @return value param or null if value not found.
    */
   public static String readValueParam(InitParams initParams, String paramName)
   {
      if (initParams != null)
      {
         ValueParam vp = initParams.getValueParam(paramName);
         if (vp != null)
            return vp.getValue();
      }
      return null;
   }

   /**
    * Get session.
    * 
    * @param repoName - the repository name, e.g.: db1
    * @param repoPath - the repository path, e.g.: dev-monit/test.txt (need to get the name of workspace)
    * @return {@link Session}
    */
   public static Session getSession(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService, String repoName, String repoPath)
      throws RepositoryException, RepositoryConfigurationException
   {
      ManageableRepository repo = repositoryService.getRepository(repoName);
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      if (sp == null)
         throw new RepositoryException("SessionProvider is not properly set. Make the application calls"
            + "SessionProviderService.setSessionProvider(..) somewhere before ("
            + "for instance in Servlet Filter for WEB application)");

      if (repoPath.length() > 0 && repoPath.startsWith("/"))
      {
         repoPath = repoPath.substring(1);
      }
      String workspace = repoPath;
      if (repoPath.contains("/"))
      {
         workspace = repoPath.split("/")[0];
      }

      return sp.getSession(workspace, repo);
   }

   /**
    * Creates new file node.
    * 
    * @param session - the JCR session
    * @param parentPath - path to parent node
    * @param filePath - path to new file (from parent node)
    * @param data - file data as stream
    * @param mimeType - mime type of file
    * @param fileNodeType - file node type
    * @param contentNodeType - jcr:content node type
    */
   public static void putFile(Session session, String parentPath, String fileName, InputStream data, String mimeType,
      String fileNodeType, String contentNodeType) throws RepositoryException
   {
      Node file = createFile(session, parentPath, fileName, mimeType, fileNodeType, contentNodeType);
      file.getNode("jcr:content").setProperty("jcr:data", data);
   }

   /**
    * Creates new file node.
    * 
    * @param session - the JCR session
    * @param parentPath - path to parent node
    * @param filePath - path to new file (from parent node)
    * @param data - file data as String
    * @param mimeType - media type of file
    * @param fileNodeType - file node type
    * @param contentNodeType - jcr:content node type
    * @param secret - if <code>true</code> then make newly created file protected by ACL. Only owner of
    *           <code>session</code> has access to file
    */
   public static void putFile(Session session, String parentPath, String fileName, String data, String mimeType,
      String fileNodeType, String contentNodeType) throws RepositoryException
   {
      Node file = createFile(session, parentPath, fileName, mimeType, fileNodeType, contentNodeType);
      file.getNode("jcr:content").setProperty("jcr:data", data);
   }

   private static Node createFile(Session session, String parentPath, String fileName, String mimeType,
      String fileNodeType, String contentNodeType) throws RepositoryException
   {
      Node parent = parentPath != null ? session.getRootNode().getNode(parentPath) : session.getRootNode();
      ExtendedNode file = (ExtendedNode)parent.addNode(fileName, fileNodeType != null ? fileNodeType : "nt:file");
      Node content = file.addNode("jcr:content", contentNodeType != null ? contentNodeType : "nt:resource");
      content.setProperty("jcr:lastModified", Calendar.getInstance());
      content.setProperty("jcr:mimeType", mimeType);
      return file;
   }

   /**
    * Creates new folder node.
    * 
    * @param session - the session
    * @param parentPath - path to the parent node
    * @param folderPath - name the new folder node
    */
   public static void putFolder(Session session, String parentPath, String folderPath) throws RepositoryException
   {
      Node parent = parentPath != null ? session.getRootNode().getNode(parentPath) : session.getRootNode();
      if (parent.hasNode(folderPath))
      {
         return;
      }

      parent.addNode(folderPath, "nt:folder");
   }

   public static void ensureFoldersCreated(Session session, String parentPath, String folders)
      throws RepositoryException
   {
      Node node = parentPath != null ? session.getRootNode().getNode(parentPath) : session.getRootNode();

      String[] parts = folders.split("/");
      for (String part : parts)
      {
         if (node.hasNode(part))
         {
            node = node.getNode(part);
         }
         else
         {
            node = node.addNode(part, "nt:folder");
         }
      }
   }

   public static void putFolders(Session session, String folderPath) throws RepositoryException
   {
      if (!(folderPath.startsWith("/")))
         throw new IllegalArgumentException("Absolute folder path required. ");

      try
      {
         Item item = session.getItem(folderPath);
         if (!(item.isNode()))
            throw new IllegalArgumentException("Item " + folderPath + " exists and it is not node. ");
      }
      catch (PathNotFoundException e)
      {
         String[] pathSegments = folderPath.substring(1).split("/");
         Node folder = session.getRootNode();
         for (int i = 0; i < pathSegments.length; i++)
         {
            try
            {
               folder = folder.getNode(pathSegments[i]);
            }
            catch (PathNotFoundException e1)
            {
               folder = folder.addNode(pathSegments[i], "nt:folder");
            }
         }
      }
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
