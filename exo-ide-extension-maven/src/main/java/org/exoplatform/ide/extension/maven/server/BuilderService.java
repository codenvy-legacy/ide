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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * RESTful facade for {@link BuilderClient}
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/maven")
public class BuilderService
{
   @Inject
   private BuilderClient builder;

   /**
    * Start new build at remote build server. Build may be started immediately or add in build queue. Client should
    * check location given in response header to current get status of build.
    *
    * @param gitURI Git location of project we want to build
    * @param uriInfo context info about current request
    * @return response with status 202 if request for build is accepted. Client get location of resource that it should
    *         check to see the current status of build.
    * @throws BuilderException if request for new build was rejected by remote build server
    * @throws IOException if any i/o errors occur
    * @see BuilderClient#build(String)
    */
   @GET
   @Path("build")
   public Response build(@QueryParam("gituri") String gitURI, @Context UriInfo uriInfo) throws BuilderException,
      IOException
   {
      final String buildID = builder.build(gitURI);
      final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(buildID);
      return Response.status(202).location(location).entity(location.toString()).build();
   }

   /**
    * Check current status of previously launched build.
    *
    * @param buildID ID of build
    * @return string that contains description of current status of build in JSON format. Do nothing with such string
    *         just re-send result to client
    * @throws IOException if any i/o errors occur
    * @throws BuilderException any other errors related to build server internal state or parameter of client request
    * @see BuilderClient#status(String)
    */
   @GET
   @Path("status/{buildid}")
   public String status(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.status(buildID);
   }

   /**
    * Cancel previously launched build.
    *
    * @param buildID ID of build
    * @throws IOException if any i/o errors occur
    * @throws BuilderException any other errors related to build server internal state or parameter of client request
    * @see BuilderClient#cancel(String)
    */
   @GET
   @Path("cancel/{buildid}")
   public void cancel(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      builder.cancel(buildID);
   }

   /**
    * Get build log.
    *
    * @param buildID ID of build
    * @return stream that contains build log
    * @throws IOException if any i/o errors occur
    * @throws BuilderException any other errors related to build server internal state or parameter of client request
    * @see BuilderClient#cancel(String)
    */
   @GET
   @Path("log/{buildid}")
   public InputStream log(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.log(buildID);
   }

   /**
    * Download result of build, typically result is *.war file. Note this method should not be called before successful
    * end of build. Client must check status with method {@link #status(String)} and if status is 'successful' call this
    * method to get result of build.
    *
    * @param buildID ID of build
    * @return binary stream that contains result of build. Only *.war file expected at the moment.
    * @throws IOException if any i/o errors occur
    * @throws BuilderException any other errors related to build server internal state or parameter of client request
    */
   @GET
   @Path("download/{buildid}")
   public InputStream download(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.download(buildID);
   }
}
