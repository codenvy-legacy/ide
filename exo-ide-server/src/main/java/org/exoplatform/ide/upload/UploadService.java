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
package org.exoplatform.ide.upload;

import org.apache.commons.fileupload.FileItem;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.InvalidArgumentException;
import org.exoplatform.ide.vfs.server.exceptions.ItemAlreadyExistException;
import org.exoplatform.ide.vfs.server.exceptions.ItemNotFoundException;
import org.exoplatform.ide.vfs.server.exceptions.PermissionDeniedException;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Iterator;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
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

@Path("/ide/upload")
public class UploadService
{

   interface FormFields
   {

      public static final String FILE = "file";

      public static final String LOCATION = "location";
      
      public static final String PARENT_ID = "parentId";
      
      public static final String NAME = "name";

      public static final String MIME_TYPE = "mimeType";

      public static final String VFS_ID = "vfsId";
      
      /**
       * Operation with file: update or create
       */
      public static final String ACTION_UPDATE = "update";
      
      /**
       * The id of existing file, which will be overrided with
       * new uploaded file.
       */
      public static final String FILE_ID = "fileId";

   }
   
   private final VirtualFileSystemRegistry vfsRegistry;

   private static Log log = ExoLogger.getLogger(UploadService.class);

   public UploadService(VirtualFileSystemRegistry vfsRegistry)
   {
      this.vfsRegistry = vfsRegistry;
   }

   @POST
   @Consumes("multipart/*")
   @Produces(MediaType.TEXT_HTML)
   public Response uploadFile(Iterator<FileItem> iterator, @Context UriInfo uriInfo) 
   {
      HashMap<String, FileItem> requestItems = getRequestItems(iterator);

      if (requestItems.get(FormFields.FILE) == null)
      {
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML)
            .entity("Can't find input file").build());
      }
      
      final String vfsId = requestItems.get(FormFields.VFS_ID).getString();
      VirtualFileSystem vfs = null;
      try
      {
         vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      }
      catch (VirtualFileSystemException e1)
      {
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML)
            .entity(e1.getMessage()).build());
      }
      
      if (vfs == null)
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML)
            .entity("Virtual file system not initialized").build());

      try
      {
         FileItem fileItem = requestItems.get(FormFields.FILE);
         InputStream inputStream = fileItem.getInputStream();
         
         final String mimeType = requestItems.get(FormFields.MIME_TYPE).getString();
         final String parentId = requestItems.get(FormFields.PARENT_ID).getString();
         
         final String name = requestItems.get(FormFields.NAME).getString();
         
         if ("update".equals(requestItems.get(FormFields.ACTION_UPDATE).getString()))
         {
            //update existing file
            final String fileId = requestItems.get(FormFields.FILE_ID).getString();
            vfs.updateContent(fileId, MediaType.valueOf(mimeType), inputStream, null);
         }
         else
         {
            //create new file
            vfs.createFile(parentId, name, MediaType.valueOf(mimeType), inputStream);
         }
         
         return Response.status(201).type(MediaType.TEXT_HTML).build();

      }
      catch (IOException e)
      {
         log.error(e.getMessage(), e);
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build());
      }
      catch (ItemNotFoundException e)
      {
         log.error(e.getMessage(), e);
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build());
      }
      catch (InvalidArgumentException e)
      {
         log.error(e.getMessage(), e);
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build());
      }
      catch (ItemAlreadyExistException e)
      {
         log.error(e.getMessage(), e);
         //add to message special code, that client will be able to detect,
         //that file already exists and show ask dialog.
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML)
            .entity("{itemalreadyexists}" + e.getMessage())
            .build());
      }
      catch (PermissionDeniedException e)
      {
         log.error(e.getMessage(), e);
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build());
      }
      catch (IllegalArgumentException e)
      {
         log.error(e.getMessage(), e);
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build());
      }
      catch (VirtualFileSystemException e)
      {
         log.error(e.getMessage(), e);
         throw new WebApplicationException(Response.serverError().type(MediaType.TEXT_HTML).entity(e.getMessage()).build());
      }

   }

   /**
    * Get the map of form fields request items.
    * 
    * @param iterator - file item iterator
    * @return {@link HashMap}
    */
   private HashMap<String, FileItem> getRequestItems(Iterator<FileItem> iterator)
   {
      HashMap<String, FileItem> requestItems = new HashMap<String, FileItem>();
      while (iterator.hasNext())
      {
         FileItem item = iterator.next();
         String fieldName = item.getFieldName();
         requestItems.put(fieldName, item);
      }
      return requestItems;
   }
   
}
