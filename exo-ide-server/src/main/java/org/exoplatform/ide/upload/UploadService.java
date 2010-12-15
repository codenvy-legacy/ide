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
import org.exoplatform.commons.utils.MimeTypeResolver;
import org.exoplatform.services.jcr.webdav.WebDavService;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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

   private static final String WEBDAV_CONTEXT = "ide-vfs-webdav";
   
   static final String ERROR_OPEN = "<error>";
   
   static final String ERROR_CLOSE = "</error>";
   
   private static Log log = ExoLogger.getLogger(UploadService.class);

   private WebDavService webDavService;

   public UploadService(WebDavService webDavService)
   {
      this.webDavService = webDavService;
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
         return Response.serverError().entity(ERROR_OPEN + "Can't find input file" + ERROR_CLOSE).type(
            MediaType.TEXT_HTML).build();
      }

      try
      {
         FileItem fileItem = requestItems.get(FormFields.FILE);
         
         InputStream inStream = fileItem.getInputStream();
         
         checkForZipBomb(inStream);
         
         String location = requestItems.get(FormFields.LOCATION).getString();
         
         location = URLDecoder.decode(location, "UTF-8");

         String prefix = uriInfo.getBaseUriBuilder().segment(WEBDAV_CONTEXT, "/").build().toString();

         if (!location.startsWith(prefix))
         {
            return Response.serverError().entity(ERROR_OPEN + "Invalid path, where to upload file" + ERROR_CLOSE).type(
               MediaType.TEXT_HTML).build();
         }

         location = location.substring(prefix.length());

         String repositoryName = location.substring(0, location.indexOf("/"));
         String repoPath = location.substring(location.indexOf("/") + 1, location.lastIndexOf("/"));
         
         Response unzipResponse = unzip(fileItem.getInputStream(), repositoryName, repoPath);
        
         return Response.fromResponse(unzipResponse).type(MediaType.TEXT_HTML).build();
         
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
      catch (SAXException e)
      {
         log.error(e.getMessage(), e);
         return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_HTML).build();
      }
      catch (TikaException e)
      {
         log.error(e.getMessage(), e);
         return Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_HTML).build();
      }

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
         return Response.serverError().entity(ERROR_OPEN + "Can't find input file" + ERROR_CLOSE).type(
            MediaType.TEXT_HTML).build();
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
            return Response.serverError().entity(ERROR_OPEN + "Invalid path, where to upload file" + ERROR_CLOSE).type(
               MediaType.TEXT_HTML).build();
         }

         location = location.substring(prefix.length());

         String repositoryName = location.substring(0, location.indexOf("/"));
         String repoPath = location.substring(location.indexOf("/") + 1);
         String mimeType = requestItems.get(FormFields.MIME_TYPE).getString();
         
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

         MediaType mediaType = new MediaType(mimeType.split("/")[0], mimeType.split("/")[1]);

         Response response = webDavService.put(repositoryName, repoPath, null, null, nodeType, jcrContentNodeType, null, mediaType,
            inputStream);
         return Response.fromResponse(response).type(MediaType.TEXT_HTML).build();
         
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
   
   private Response unzip(InputStream inputStream, String repositoryName, String repoPath)
   {
      InputStream is = inputStream;
      byte[] buf = new byte[1024];

      try
      {
         ZipInputStream zin = new ZipInputStream(is);

         ZipEntry zipentry;

         zipentry = zin.getNextEntry();
         while (zipentry != null)
         {
            //for each entry to be extracted
            String entryName = zipentry.getName();
            if (zipentry.isDirectory())
            {
               Response response = webDavService.mkcol(repositoryName, repoPath + "/" + entryName, null, null, null, null);
               
               if (response.getStatus() != 201)
               {
                  return Response.fromResponse(response).type(MediaType.TEXT_HTML).build();
               }
            }
            else
            {
               int bytesRead;

               ByteArrayOutputStream outS = new ByteArrayOutputStream();

               while ((bytesRead = zin.read(buf, 0, 1024)) > -1)
               {
                  outS.write(buf, 0, bytesRead);
               }

               ByteArrayInputStream byteInputStream = new ByteArrayInputStream(outS.toByteArray());

               outS.close();
               
               MimeTypeResolver resolver = new MimeTypeResolver();
               String mimeType = resolver.getMimeType(entryName);
               
               MediaType mediaType = new MediaType(mimeType.split("/")[0], mimeType.split("/")[1]);

               Response response = webDavService.put(repositoryName, repoPath + "/" + entryName, null, null, null,
                     null, null, mediaType, byteInputStream);

               if (response.getStatus() != 201)
               {
                  return Response.fromResponse(response).type(MediaType.TEXT_HTML).build();
               }
            }
            zin.closeEntry();
            zipentry = zin.getNextEntry();

         }//while

         zin.close();
      }
      catch (IOException e)
      {
         log.error(e.getMessage(), e);
         Response.serverError().entity("Can't unzip folder").type(MediaType.TEXT_HTML).build();
      }

      return Response.ok().type(MediaType.TEXT_HTML).build();

   }

}
