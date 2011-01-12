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
package org.exoplatform.ide.upload;

import org.apache.commons.fileupload.FileItem;
import org.apache.tika.exception.TikaException;
import org.apache.tika.io.CountingInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.apache.tika.sax.SecureContentHandler;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.ide.zip.ZipUtils;
import org.exoplatform.services.jcr.RepositoryService;
import org.exoplatform.services.jcr.config.RepositoryConfigurationException;
import org.exoplatform.services.jcr.core.ManageableRepository;
import org.exoplatform.services.jcr.ext.app.SessionProviderService;
import org.exoplatform.services.jcr.ext.common.SessionProvider;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;

import javax.jcr.AccessDeniedException;
import javax.jcr.InvalidItemStateException;
import javax.jcr.ItemExistsException;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.nodetype.NoSuchNodeTypeException;
import javax.jcr.version.VersionException;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * Uses for storing files from local system to repository.
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/services/upload")
public class UploadService
{

   interface FormFields
   {

      public static final String FILE = "file";

      public static final String LOCATION = "location";

      public static final String MIME_TYPE = "mimeType";

      public static final String NODE_TYPE = "nodeType";

      public static final String JCR_CONTENT_NODE_TYPE = "jcrContentNodeType";

   }

   private static final String WEBDAV_CONTEXT = "jcr";

   static final String ERROR_OPEN = "<error>";

   static final String ERROR_CLOSE = "</error>";

   private static Log log = ExoLogger.getLogger(UploadService.class);

   private final RepositoryService repositoryService;

   private final SessionProviderService sessionProviderService;

   public UploadService(RepositoryService repositoryService,
      SessionProviderService sessionProviderService)
   {
      this.repositoryService = repositoryService;
      this.sessionProviderService = sessionProviderService;
   }

   @POST
   @Consumes("multipart/*")
   @Path("/folder")
   public Response uploadFolder(Iterator<FileItem> iterator, @Context UriInfo uriInfo)
   {
      HashMap<String, FileItem> requestItems = new HashMap<String, FileItem>();
      while (iterator.hasNext())
      {
         FileItem item = iterator.next();
         String fieldName = item.getFieldName();
         requestItems.put(fieldName, item);
      }

      if (requestItems.get(FormFields.FILE) == null)
      {
         return Response.serverError().entity(ERROR_OPEN + "Can't find input file" + ERROR_CLOSE)
            .type(MediaType.TEXT_HTML).build();
      }

      FileItem fileItem = requestItems.get(FormFields.FILE);

      try
      {
         InputStream inStream = fileItem.getInputStream();

         checkForZipBomb(inStream);
         String location = requestItems.get(FormFields.LOCATION).getString();

         location = URLDecoder.decode(location, "UTF-8");

         String prefix = uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, "/").build().toString();

         if (!location.startsWith(prefix))
         {
            return Response.serverError().entity(ERROR_OPEN + "Invalid path, where to upload file" + ERROR_CLOSE)
               .type(MediaType.TEXT_HTML).build();
         }

         location = location.substring(prefix.length());

         final String repositoryName = location.substring(0, location.indexOf("/"));
         final String repoPath = location.substring(location.indexOf("/") + 1);

         Session session = null;
         session = getSession(repositoryName, repoPath);

         ZipUtils.unzip(session, fileItem.getInputStream(), getResourcePath(repoPath));
      }
      catch (IOException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (IllegalArgumentException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (SAXException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (TikaException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (AccessDeniedException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (ItemExistsException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (ConstraintViolationException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (InvalidItemStateException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (VersionException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (LockException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (NoSuchNodeTypeException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (RepositoryException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }
      catch (RepositoryConfigurationException e)
      {
         if (log.isDebugEnabled())
            e.printStackTrace();
         return Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build();
      }

      return Response.ok().type(MediaType.TEXT_HTML).build();

   }

   @POST
   @Consumes("multipart/*")
   public Response post(Iterator<FileItem> iterator, @Context UriInfo uriInfo)
   {
      HashMap<String, FileItem> requestItems = new HashMap<String, FileItem>();
      while (iterator.hasNext())
      {
         FileItem item = iterator.next();
         String fieldName = item.getFieldName();
         requestItems.put(fieldName, item);
      }

      if (requestItems.get(FormFields.FILE) == null)
      {
         return Response.serverError().entity(ERROR_OPEN + "Can't find input file" + ERROR_CLOSE)
            .type(MediaType.TEXT_HTML).build();
      }

      try
      {
         FileItem fileItem = requestItems.get(FormFields.FILE);
         InputStream inputStream = fileItem.getInputStream();

         String location = requestItems.get(FormFields.LOCATION).getString();

         location = URLDecoder.decode(location, "UTF-8");

         String prefix = uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, "/").build().toString();

         if (!location.startsWith(prefix))
         {
            return Response.serverError().entity(ERROR_OPEN + "Invalid path, where to upload file" + ERROR_CLOSE)
               .type(MediaType.TEXT_HTML).build();
         }

         location = location.substring(prefix.length());

         final String repositoryName = location.substring(0, location.indexOf("/"));
         final String repoPath = location.substring(location.indexOf("/") + 1);
         final String mimeType = requestItems.get(FormFields.MIME_TYPE).getString();

         String nodeType = null;
         if (requestItems.get(FormFields.NODE_TYPE) != null)
         {
            nodeType = requestItems.get(FormFields.NODE_TYPE).getString();
            if ("".equals(nodeType))
            {
               nodeType = null;
            }
         }

         String jcrContentNodeType = null;
         if (requestItems.get(FormFields.JCR_CONTENT_NODE_TYPE) != null)
         {
            jcrContentNodeType = requestItems.get(FormFields.JCR_CONTENT_NODE_TYPE).getString();
            if ("".equals(jcrContentNodeType))
            {
               jcrContentNodeType = null;
            }
         }

         Session session = null;
         session = getSession(repositoryName, repoPath);
         ZipUtils.putFile(session, getResourcePath(repoPath), getFileName(repoPath), inputStream, mimeType, nodeType,
            jcrContentNodeType);
         session.save();

         return Response.status(HTTPStatus.CREATED).type(MediaType.TEXT_HTML).build();

      }
      catch (PathNotFoundException e)
      {
         log.error(e.getMessage(), e);
         return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_HTML).build();
      }
      catch (RepositoryException e)
      {
         log.error(e.getMessage(), e);
         return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_HTML).build();
      }
      catch (RepositoryConfigurationException e)
      {
         log.error(e.getMessage(), e);
         return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_HTML).build();
      }
      catch (UnsupportedEncodingException e)
      {
         log.error(e.getMessage(), e);
         return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_HTML).build();
      }
      catch (IOException e)
      {
         log.error(e.getMessage(), e);
         return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_HTML).build();
      }

   }

   /**
    * Parse input stream and check for zip bomb.
    * 
    * @param inputStream - file input stream
    * @throws IOException
    * @throws SAXException
    * @throws TikaException
    */
   private void checkForZipBomb(InputStream inputStream) throws IOException, SAXException, TikaException
   {
      InputStream is = inputStream;
      CountingInputStream count = new CountingInputStream(is);
      ContentHandler handler = new BodyContentHandler();
      SecureContentHandler secure = new SecureContentHandler(handler, count);
      Metadata metadata = new Metadata();
      AutoDetectParser parser = new AutoDetectParser();
      parser.parse(count, secure, metadata);

      if (count != null)
      {
         count.close();
      }
      if (is != null)
      {
         is.close();
      }
   }

   /**
    * Get session.
    * 
    * @param repoName - the repository name.
    * @param repoPath - the repository path
    * @return {@link Session}
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
    * Get resource path from repository path.
    * 
    * Resource path - path to the parent folder of uploaded file
    * without workspace name.
    * 
    * Returns resource path without "/" at the begin.
    * 
    * @param repoPath - repository path 
    * @return the resource path. 
    * If file will be uploaded to root folder, return <code>null<code>
    */
   private String getResourcePath(String repoPath)
   {
      if (repoPath.startsWith("/"))
         repoPath = repoPath.substring(1);
      
      //crop workspace name
      String resourcePath = repoPath.substring(repoPath.indexOf("/") + 1);
      //crop file name
      if (resourcePath.contains("/"))
         resourcePath = resourcePath.substring(0, resourcePath.lastIndexOf("/"));
      else
         resourcePath = null;
      
      return resourcePath;
   }
   
   /**
    * Get file name from file repository path.
    * 
    * @param repoPath - repository path for file.
    * @return the name of uploaded file
    */
   private String getFileName(String repoPath)
   {
      return repoPath.substring(repoPath.lastIndexOf("/") + 1);
   }

}
