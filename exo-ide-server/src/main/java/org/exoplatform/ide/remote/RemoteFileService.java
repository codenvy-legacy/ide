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
package org.exoplatform.ide.remote;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.everrest.http.client.HTTPConnection;
import org.everrest.http.client.HTTPResponse;
import org.everrest.http.client.ModuleException;
import org.everrest.http.client.NVPair;
import org.everrest.http.client.ProtocolNotSuppException;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

@Path("/ide/remotefile")
public class RemoteFileService
{

   private static Log log = ExoLogger.getLogger(RemoteFileService.class);

   protected static final int DEFAULT_CONNECTION_TIMEOUT_MS = 10000;

   /**
    * Loads content of the specified resource and sends it to client. 
    * 
    * @param fileURL
    * @return
    * @throws RemoteFileServiceException
    */
   @GET
   @Path("/content")
   @Produces("*/*")
   public Response getRemoteFileContent(@QueryParam("url") String fileURL) throws RemoteFileServiceException
   {
      try
      {
         URL url = new URL(fileURL);
         HTTPConnection conn = new HTTPConnection(url);
         conn.setTimeout(DEFAULT_CONNECTION_TIMEOUT_MS);

         NVPair[] headerPairs = new NVPair[1];

         NVPair hostHeader = new NVPair(HttpHeaders.HOST, url.getHost());
         headerPairs[0] = hostHeader;
         conn.setAllowUserInteraction(false);

         HTTPResponse httpResponse = conn.Get(url.getFile(), (NVPair[])null, headerPairs);

         ResponseBuilder responseBuilder = Response.status(httpResponse.getStatusCode());

         byte[] data = httpResponse.getData();
         responseBuilder.header(HttpHeaders.CONTENT_LENGTH, data.length);

         String contentType = httpResponse.getHeader(HttpHeaders.CONTENT_TYPE);
         responseBuilder.header(HttpHeaders.CONTENT_TYPE, contentType);

         return responseBuilder.entity(data).build();
      }
      catch (MalformedURLException e)
      {
         if (log.isDebugEnabled())
         {
            log.debug(e.getMessage(), e);
         }
         throw new RemoteFileServiceException(404, "Invalid URL.");
      }
      catch (ProtocolNotSuppException e)
      {
         if (log.isDebugEnabled())
         {
            log.debug(e.getMessage(), e);
         }
         throw new RemoteFileServiceException(500, "Protocol not supported.");
      }
      catch (IOException e)
      {
         if (log.isDebugEnabled())
         {
            log.debug(e.getMessage(), e);
         }
         throw new RemoteFileServiceException(500, "Transfering file error.");
      }
      catch (ModuleException e)
      {
         if (log.isDebugEnabled())
         {
            log.debug(e.getMessage(), e);
         }
         throw new RemoteFileServiceException(500, "Transfering file error.");
      }
   }

}
