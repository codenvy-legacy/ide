/**
 * Copyright (C) 2009 eXo Platform SAS.
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
 *
 */
package org.exoplatform.ideall.download;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.ThreadLocalSessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/services/downloadcontent")
public class DownloadContentService implements Const, ResourceContainer
{

   private static final String MODIFICATION_PATTERN = "EEE, dd MMM yyyy HH:mm:ss z";

   private static final String HEADER_CONTENT_DISPOSITION = "Content-Disposition";

   private static final String APPLICATION_OCTETSTREAM = "application/octet-stream";

   private static Log log = ExoLogger.getLogger(DownloadContentService.class);

   private RepositoryService repositoryService;

   private ThreadLocalSessionProviderService sessionProviderService;

   public DownloadContentService(RepositoryService repositoryService,
      ThreadLocalSessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
   }

   /**
    * Normalizes path.
    * 
    * @param repoPath repository path
    * @return normalized path.
    */
   protected String normalizePath(String repoPath)
   {
      if (repoPath.length() > 0 && repoPath.endsWith("/"))
      {
         return repoPath.substring(0, repoPath.length() - 1);
      }

      String[] pathElements = repoPath.split("/");
      StringBuffer escapedPath = new StringBuffer();
      for (String element : pathElements)
      {
         try
         {
            //WBT:358
//            if (element.contains(":"))
//            {
//               element = element.replaceAll(":", URLEncoder.encode(":", "UTF-8"));
//            }
            if (element.contains("["))
            {
               element = element.replaceAll("\\[", URLEncoder.encode("[", "UTF-8"));
            }
            if (element.contains("]"))
            {
               element = element.replaceAll("]", URLEncoder.encode("]", "UTF-8"));
            }
            if (element.contains("'"))
            {
               element = element.replaceAll("'", URLEncoder.encode("'", "UTF-8"));
            }
            if (element.contains("\""))
            {
               element = element.replaceAll("\"", URLEncoder.encode("\"", "UTF-8"));
            }
            escapedPath.append(element + "/");
         }
         catch (Exception e)
         {
            log.warn(e.getMessage());
         }
      }

      return escapedPath.toString().substring(0, escapedPath.length() - 1);
   }

   /**
    * Extracts path from repository path.
    * 
    * @param repoPath repository path
    * @return path
    */
   protected String path(String repoPath)
   {
      String path = repoPath.substring(workspaceName(repoPath).length());

      if (!"".equals(path))
      {
         return path;
      }

      return "/";
   }

   /**
    * Extracts workspace name from repository path.
    * 
    * @param repoPath repository path
    * @return workspace name
    */
   protected String workspaceName(String repoPath)
   {
      return repoPath.split("/")[0];
   }

   /**
    * Return JCR session for selected repository and workspace.
    * 
    * @param repoName
    * @param repoPath
    * @return
    * @throws RepositoryException
    * @throws RepositoryConfigurationException
    */
   private Session getSession(String repoName, String repoPath) throws RepositoryException,
      RepositoryConfigurationException
   {
      ManageableRepository repo = this.repositoryService.getRepository(repoName);
      SessionProvider sp = sessionProviderService.getSessionProvider(null);
      if (sp == null)
         throw new RepositoryException("SessionProvider is not properly set. Make the application calls"
            + "SessionProviderService.setSessionProvider(..) somewhere before ("
            + "for instance in Servlet Filter for WEB application)");

      return sp.getSession(workspaceName(repoPath), repo);
   }

   @GET
   @Path("/{fileName:.*}/")
   public Response download(@PathParam("fileName") String fileName, @QueryParam("repoPath") String repoPath)
   {
      if (repoPath.startsWith("/"))
      {
         repoPath = repoPath.substring(1);
      }
      repoPath = normalizePath(repoPath);

      String repoName = repoPath.substring(0, repoPath.indexOf("/"));

      repoPath = repoPath.substring(1);
      repoPath = repoPath.substring(repoPath.indexOf("/") + 1);

      try
      {
         Session session = getSession(repoName, repoPath);

         Node node = (Node)session.getItem(path(repoPath));

         if (NodeTypeUtil.isFile(node))
         {
            return getFile(node);
         }
         else
         {
            return getFolder(node, repoPath);
         }
      }
      catch (PathNotFoundException exc)
      {
         log.error("NoSuchWorkspaceException " + exc.getMessage(), exc);
         return Response.status(HTTPStatus.NOT_FOUND).entity(exc.getMessage()).build();
      }
      catch (Exception exc)
      {
         log.error(exc.getMessage(), exc);
         return Response.serverError().entity(exc.getMessage()).build();
      }
   }

   /**
    * Download resource as file.
    * 
    * @param node
    * @return
    * @throws RepositoryException
    */
   private Response getFile(Node node) throws RepositoryException, UnsupportedEncodingException, URISyntaxException
   {
      long contentLength = node.getNode(JCR_CONTENT).getProperty(JCR_DATA).getLength();
      String contentType = node.getNode(JCR_CONTENT).getProperty(JCR_MIMETYPE).getString();
      Calendar modified = node.getNode(JCR_CONTENT).getProperty(JCR_LASTMODIFIED).getDate();

      SimpleDateFormat dateFormat = new SimpleDateFormat(MODIFICATION_PATTERN, Locale.ENGLISH);
      dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
      String modifiedValue = dateFormat.format(modified.getTime());

      InputStream inputStream = node.getNode(JCR_CONTENT).getProperty(JCR_DATA).getStream();

      String contentDisposition = "attachment";

      return Response.ok().header(HttpHeaders.CONTENT_LENGTH, Long.toString(contentLength)).header(
         HttpHeaders.LAST_MODIFIED, modifiedValue).header(HEADER_CONTENT_DISPOSITION, contentDisposition).entity(
         inputStream).type(contentType).build();
   }

   /**
    * Download resource as zipped folder.
    * 
    * @param node
    * @param repoPath
    * @return
    * @throws RepositoryException
    */
   private Response getFolder(Node node, String repoPath) throws RepositoryException
   {
      String contentDisposition = "attachment";

      DirectoryContentEntity entity = new DirectoryContentEntity(node);

      return Response.ok().header(HEADER_CONTENT_DISPOSITION, contentDisposition).entity(entity).type(
         APPLICATION_OCTETSTREAM).build();
   }

}
