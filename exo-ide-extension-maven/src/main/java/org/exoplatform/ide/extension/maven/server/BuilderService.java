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
package org.exoplatform.ide.extension.maven.server;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/maven")
public class BuilderService
{
   @Inject
   private BuilderClient builder;

   @GET
   @Path("build")
   public Response build(@QueryParam("remoteuri") String remoteURI) throws BuilderException, IOException
   {
      final URI checkStatusURI = builder.build(remoteURI);
      return Response.status(202).location(checkStatusURI).entity(checkStatusURI.toString()).build();
   }

   @GET
   @Path("status/{buildid}")
   public String status(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.status(buildID);
   }

   @GET
   @Path("cancel/{buildid}")
   public void cancel(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      builder.cancel(buildID);
   }

   @GET
   @Path("log/{buildid}")
   public InputStream log(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.log(buildID);
   }

   @GET
   @Path("download/{buildid}")
   public InputStream download(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.download(buildID);
   }
}
