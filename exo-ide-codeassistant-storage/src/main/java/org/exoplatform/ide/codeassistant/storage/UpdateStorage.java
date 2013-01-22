/*
 * Copyright (C) 2012 eXo Platform SAS.
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
package org.exoplatform.ide.codeassistant.storage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * REST interface for {@link UpdateStorageService}
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id:
 *
 */
@Path("storage")
public class UpdateStorage
{
   private static final Logger LOG = LoggerFactory.getLogger(UpdateStorage.class);

   @Inject
   private UpdateStorageService storageService;

   @Path("update/type")
   @POST
   @Consumes("application/json")
   public Response updateTypeDependecys(@Context UriInfo uriInfo, Dependencys dependencys) throws IOException
   {
      try
      {
         InputStream zip = doDownload(dependencys.getZipUrl());
         UpdateStorageTask task = storageService.updateTypeIndex(dependencys.getDependencies(), zip);
         final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(task.getId());
         return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
      }
      catch (MalformedURLException e)
      {
         return Response.status(404).entity(e.getMessage()).build();
      }
   }

   @Path("update/dock")
   @POST
   @Consumes("application/json")
   public Response updateJavaDock(@Context UriInfo uriInfo, Dependencys dependencys) throws IOException
   {
      try
      {
         InputStream zip = doDownload(dependencys.getZipUrl());
         UpdateStorageTask task = storageService.updateDockIndex(dependencys.getDependencies(), zip);
         final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(task.getId());
         return Response.status(202).location(location).entity(location.toString()).type(MediaType.TEXT_PLAIN).build();
      }
      catch (MalformedURLException e)
      {
         return Response.status(404).entity(e.getMessage()).build();
      }
   }

   @GET
   @Path("status/{buildid}")
   public Response status(@PathParam("buildid") String buildID, @Context UriInfo uriInfo)
   {
      UpdateStorageTask task = storageService.getTask(buildID);
      if (task != null)
      {
         if (task.isDone())
         {
            UpdateStorageResult result = task.getResult();
            if (task.getResult().getExitCode() == 0)
            {
               return Response
                  .status(200)
                  .entity(
                     "{\"status\":\"SUCCESSFUL\",\"downloadUrl\":\"\",\"time\":\""
                        + Long.toString(System.currentTimeMillis()) + "\"}").type(MediaType.APPLICATION_JSON).build();
            }
            else
            {
               return Response
                  .status(200)
                  .entity(
                     "{\"status\":\"FAILED\",\"exitCode\":" + result.getExitCode() + ",\"error\":\""
                        + result.getErroMessage() + "\"}").type(MediaType.APPLICATION_JSON).build();
            }
         }
         else
         {
            return Response.status(200).entity("{\"status\":\"IN_PROGRESS\"}").type(MediaType.APPLICATION_JSON).build();
         }
      }
      // Incorrect task ID.
      throw new WebApplicationException(Response.status(404).entity("Job " + buildID + " not found. ")
         .type(MediaType.TEXT_PLAIN).build());
   }

   private InputStream doDownload(String downloadURL) throws MalformedURLException, IOException
   {
      HttpURLConnection http = null;
      try
      {
         URL url = new URL(downloadURL);
         http = (HttpURLConnection)url.openConnection();
         http.setRequestMethod("GET");
         int responseCode = http.getResponseCode();
         if (responseCode != 200)
         {
            throw new IOException("Can't download zipped dependencys");
         }
         // Connection closed automatically when input stream closed.
         // If IOException or BuilderException occurs then connection closed immediately.
         return new HttpStream(http);
      }
      catch (MalformedURLException e)
      {
         throw e;
      }
      catch (IOException ioe)
      {
         if (http != null)
         {
            http.disconnect();
         }
         throw ioe;
      }

   }

   /** Stream that automatically close HTTP connection when all data ends. */
   private static class HttpStream extends FilterInputStream
   {
      private final HttpURLConnection http;

      private boolean closed;

      private HttpStream(HttpURLConnection http) throws IOException
      {
         super(http.getInputStream());
         this.http = http;
      }

      @Override
      public int read() throws IOException
      {
         int r = super.read();
         if (r == -1)
         {
            close();
         }
         return r;
      }

      @Override
      public int read(byte[] b) throws IOException
      {
         int r = super.read(b);
         if (r == -1)
         {
            close();
         }
         return r;
      }

      @Override
      public int read(byte[] b, int off, int len) throws IOException
      {
         int r = super.read(b, off, len);
         if (r == -1)
         {
            close();
         }
         return r;
      }

      @Override
      public void close() throws IOException
      {
         if (closed)
         {
            return;
         }
         try
         {
            super.close();
         }
         finally
         {
            http.disconnect();
            closed = true;
         }
      }
   }
}
