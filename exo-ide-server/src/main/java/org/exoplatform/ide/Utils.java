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

import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

import java.io.InputStream;
import java.util.Calendar;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;

/**
 * Utils class, that contains methods for creation of folders and files
 * nodes in jcr.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: Utils.java Apr 8, 2011 2:51:02 PM vereshchaka $
 *
 */
public class Utils
{
   public static final String DEFAULT_JCR_CONTENT_NODE_TYPE = "nt:resource";
   
   public static final String DEFAULT_FILE_NODE_TYPE = "nt:file";
   
   /**
    * Get session.
    * 
    * @param repoName - the repository name, e.g.: db1
    * @param repoPath - the repository path, e.g.: dev-monit/test.txt (need to get the name of workspace)
    * @return {@link Session}
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
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
    * @param session - the session
    * @param resourcePath - path to parent node.
    * @param filePath - path to new file (from parent node)
    * @param data - file's data
    * @param mimeType - mime type of file
    * @param fileNodeType - file node type
    * @param jcrContentNodeType - jcr:content node type
    * 
    * @throws PathNotFoundException
    * @throws RepositoryException
    */
   public static void putFile(Session session, String resourcePath, String filePath, InputStream data, String mimeType,
      String fileNodeType, String jcrContentNodeType)
      throws PathNotFoundException, RepositoryException
   {
      Node base;
      if (resourcePath != null)
      {
         base = session.getRootNode().getNode(resourcePath);
      }
      else
      {
         base = session.getRootNode();
      }
      
      if (fileNodeType == null)
      {
         fileNodeType = DEFAULT_FILE_NODE_TYPE;
      }
      
      if (jcrContentNodeType == null)
      {
         jcrContentNodeType = DEFAULT_JCR_CONTENT_NODE_TYPE;
      }
      
      base = base.addNode(filePath, fileNodeType);
      base = base.addNode("jcr:content", jcrContentNodeType);
      base.setProperty("jcr:data", data);
      base.setProperty("jcr:lastModified", Calendar.getInstance());
      base.setProperty("jcr:mimeType", mimeType);
   }

   /**
    * Creates new folder node.
    * 
    * @param session - the session 
    * @param parentFolderPath - path to the parent node
    * @param folderPath - path to the new folder node (from parent node)
    * 
    * @throws ItemExistsException
    * @throws PathNotFoundException
    * @throws NoSuchNodeTypeException
    * @throws LockException
    * @throws VersionException
    * @throws ConstraintViolationException
    * @throws RepositoryException
    */
   public static void putFolder(Session session, String parentFolderPath, String folderPath) throws ItemExistsException,
      PathNotFoundException, NoSuchNodeTypeException, LockException, VersionException, ConstraintViolationException,
      RepositoryException
   {
      Node base;
      if (parentFolderPath != null)
      {
         base = session.getRootNode().getNode(parentFolderPath);
      }
      else
      {
         base = session.getRootNode();
      }
   
      base.addNode(folderPath, "nt:folder");
   
   }
}
