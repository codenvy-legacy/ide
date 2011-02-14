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
package org.exoplatform.ide.build.maven;


import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.exoplatform.ide.build.BuildTask;
import org.exoplatform.ide.build.BuildWatcher;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

@Path("webapp")
public class MavenBuildService
{
   private MavenBuildTaskPool pool;
   // TODO : Make configurable.
   private int minPort = 10000;
   private int maxPort = 10020;

   public MavenBuildService(MavenBuildTaskPool pool)
   {
      this.pool = pool;
   }

   @POST
   @Path("build")
   public Response build( //
      @QueryParam("pomfile") File pomfile, //
      @Context UriInfo uriInfo //
   )
   {
      return invoke(pomfile, Collections.singletonList("package"), null, null, uriInfo);
   }

   @POST
   @Path("compile")
   public Response compile( //
      @QueryParam("pomfile") File pomfile, //
      @Context UriInfo uriInfo //
   )
   {
      return invoke(pomfile, Collections.singletonList("compile"), null, null, uriInfo);
   }

   @POST
   @Path("run")
   public Response run( //
      @QueryParam("pomfile") File pomfile, //
      @DefaultValue("600000") @QueryParam("timeout") long timeout, //
      @Context UriInfo uriInfo //
   )
   {
      Properties properties = new Properties();
      int port;
      int stopPort;
      try
      {
         port = Utils.findFreePort(minPort, maxPort);
         stopPort = Utils.findFreePort(port + 1, maxPort);
      }
      catch (IOException e)
      {
         // TODO : proper message about absence free ports
         throw new WebApplicationException(Response.serverError().entity(e.getMessage()).type(MediaType.TEXT_PLAIN)
            .build());
      }
   
      properties.put("jetty.port", Integer.toString(port));
      properties.put("jetty.stop.port", Integer.toString(stopPort));
      properties.put("jetty.stop.key", "JETTYSTOPKEY");
   
      BuildWatcher buildWatcher = null;
      if (timeout > 1)
         buildWatcher =
            new BuildWatcher(new JettyServerDestroyer(pool, createRequest(pomfile,
               Collections.singletonList("jetty:stop"), properties)), timeout);
   
      return invoke(pomfile, Collections.singletonList("jetty:run"), properties, buildWatcher, uriInfo);
   }

   @GET
   @Path("log/{taskId}")
   public Response log(@PathParam("taskId") String taskId) throws InterruptedException, ExecutionException
   {
      BuildTask<?> buildTask = pool.get(taskId);
      if (buildTask == null)
         throw new WebApplicationException(Response.serverError().entity("Invalid taskId: " + taskId)
            .type(MediaType.TEXT_PLAIN).build());
      // TODO : Add result of buildTask.isDone() in response.
      return Response.status(202).entity(buildTask.getLog()).type(MediaType.TEXT_PLAIN).build();
   }

   @POST
   @Path("kill/{taskId}")
   public void kill(@PathParam("taskId") String taskId)
   {
      BuildTask<?> buildTask = pool.get(taskId);
      if (buildTask == null)
         throw new WebApplicationException(Response.serverError().entity("Invalid taskId: " + taskId)
            .type(MediaType.TEXT_PLAIN).build());
      buildTask.cancel(true);
   }

   @POST
   @Path("remove/{taskId}")
   public void remove(@PathParam("taskId") String taskId)
   {
      BuildTask<?> buildTask = pool.remove(taskId);
      if (buildTask == null)
         throw new WebApplicationException(Response.serverError().entity("Invalid taskId: " + taskId)
            .type(MediaType.TEXT_PLAIN).build());
   }

   private Response invoke(File pomfile, List<String> goals, Properties properties, BuildWatcher buildWatcher,
      UriInfo uriInfo)
   {
      InvocationRequest request = createRequest(pomfile, goals, properties);
      String taskId = pool.add(request, buildWatcher);
      return Response.created(uriInfo.getBaseUriBuilder().path(getClass(), "log").build(taskId)).build();
   }

   private InvocationRequest createRequest(File pomfile, List<String> goals, Properties properties)
   {
      InvocationRequest request = new DefaultInvocationRequest();
      request.setPomFile(pomfile);
      request.setGoals(goals);
      request.setProperties(properties);
      return request;
   }
}
