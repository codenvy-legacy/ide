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
package org.exoplatform.ide.git.server.rest;

import org.exoplatform.ide.git.client.GitWorkDirNotFoundException;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;

import javax.jcr.AccessDeniedException;
import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * This REST service is used for searching ".git" folder stored in JCR 
 * by pointing the href of the item, from which to start search and go upper in the hierarchy tree.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Mar 25, 2011 5:46:12 PM anya $
 *
 */

@Path("ide/git-repo")
public class GitRepoService
{  
   /**
    * WebDav context.
    */
   public static final String WEBDAV_CONTEXT = "/jcr/";

   /**
    * Name of the Git's work folder.
    */
   public static final String GIT = ".git";
   
   /**
    * Repository service.
    */
   protected RepositoryService repositoryService;
   
   /**
    * Session provider service. 
    */
   protected ThreadLocalSessionProviderService sessionProviderService;
   
   /**
    * @param repositoryService repository service
    * @param sessionProviderService session provider service
    */
   public GitRepoService(RepositoryService repositoryService, ThreadLocalSessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
   }
   
   @GET
   @Path("workdir")
   @Produces(MediaType.TEXT_PLAIN)
   public String getWorkDir(@Context UriInfo uriInfo, @HeaderParam("location") String location)
   {
      String baseUri = uriInfo.getBaseUri().toASCIIString();
      
      String[] jcrLocation = parseJcrLocation(baseUri, location);
      try
      {
         Session session = getSession(repositoryService, sessionProviderService, jcrLocation[0],
               jcrLocation[1] + "/" + jcrLocation[2]);
         Node rootNode = session.getRootNode();
         Node node = rootNode.getNode(jcrLocation[2]);
         //Find node, where ".git" is stored:
         Node gitNode = findGitNode(node);
         if (gitNode != null)
         {
            //Form the available href of the ".git" directory:
            String gitWorkDir = baseUri + WEBDAV_CONTEXT + jcrLocation[0] + "/" + jcrLocation[1];
            gitWorkDir += (gitNode.getPath().startsWith("/")) ? gitNode.getPath() : "/" + gitNode.getPath();
            return gitWorkDir;
         }
         else
         {
            throw new GitWorkDirNotFoundException("Git working directory not found.");
         }
      }
      catch (RepositoryException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 500));
      }
      catch (RepositoryConfigurationException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 500));
      }
      catch (GitWorkDirNotFoundException e)
      {
         throw new WebApplicationException(e, createErrorResponse(e, 404));
      }
   }
   
   /**
    * Find ".git" folder node location by name step by step going upper in node hierarchy.
    * 
    * @param node node, in what child nodes to find ".git" directory
    * @return {@link Node} found jcr node
    * @throws RepositoryException
    */
   public static Node findGitNode(Node node) throws RepositoryException
   {
      if (node == null)
         return null;
      //Get all child node that end with ".git"
      NodeIterator nodeIterator = node.getNodes("*" + GIT);
      while (nodeIterator.hasNext())
      {
         Node childNode = nodeIterator.nextNode();
         //The first found ".git" folder will be returned:
         if (GIT.equals(childNode.getName()))
            return childNode;
      }
      try
      {
         //Go upper to look for ".git" folder:   
         Node parentNode = node.getParent();
         return findGitNode(parentNode);
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
   
   /**
    * Create response to send with error message.
    * 
    * @param t thrown exception
    * @param status http status
    * @return {@link Response} response with error
    */
   protected Response createErrorResponse(Throwable t, int status)
   {
      return Response.status(status).entity(t.getMessage()).type("text/plain").build();
   }
}
