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
package org.exoplatform.ide.maven;

import org.apache.maven.shared.invoker.MavenInvocationException;
import org.codehaus.plexus.util.cli.CommandLineException;

import java.io.File;
import java.io.IOException;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * RESTful interface for BuildService.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("builder/maven")
public class Builder
{
   @Inject
   private BuildService tasks;

   @GET
   @Path("build")
   public Response build(@QueryParam("remoteuri") String remoteURI, @Context UriInfo uriInfo)
   {
      MavenBuildTask task = tasks.add(remoteURI);
      final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(task.getId());
      return Response
         .status(202)
         .location(location)
         .entity(location.toString())
         .type(MediaType.TEXT_PLAIN).build();
   }

   @GET
   @Path("status/{buildid}")
   public Response status(@PathParam("buildid") String buildID, @Context UriInfo uriInfo)
   {
      MavenBuildTask task = tasks.get(buildID);
      if (task != null)
      {
         if (task.isDone())
         {
            try
            {
               InvocationResultImpl result = task.getResult();
               if (0 == result.getExitCode())
               {
                  return Response
                     .status(200)
                     .entity("{\"status\":\"SUCCESSFUL\",\"downloadUrl\":\""
                        + uriInfo.getBaseUriBuilder().path(getClass(), "download").build(buildID).toString()
                        + "\"}")
                     .type(MediaType.APPLICATION_JSON).build();
               }
               else
               {
                  CommandLineException cle = result.getExecutionException();
                  if (cle != null)
                  {
                     return Response
                        .status(200)
                        .entity("{\"status\":\"FAILED\",\"error\":\"" + cle.getMessage() + "\"}")
                        .type(MediaType.APPLICATION_JSON).build();
                  }
                  return Response
                     .status(200)
                     .entity("{\"status\":\"FAILED\",\"exitCode\":" + result.getExitCode() + "}")
                     .type(MediaType.APPLICATION_JSON).build();
               }
            }
            catch (MavenInvocationException e)
            {
               throw new WebApplicationException(e);
            }
         }
         return Response
            .status(200)
            .entity("{\"status\":\"IN_PROGRESS\"}")
            .type(MediaType.APPLICATION_JSON).build();
      }
      // Incorrect task ID.
      throw new WebApplicationException(Response
         .status(404)
         .entity("Build " + buildID + " not found. ")
         .type(MediaType.TEXT_PLAIN).build());
   }

   @GET
   @Path("cancel/{buildid}")
   public void cancel(@PathParam("buildid") String buildID)
   {
      MavenBuildTask task = tasks.cancel(buildID);
      if (task == null)
      {
         // Incorrect task ID.
         throw new WebApplicationException(Response
            .status(404)
            .entity("Build " + buildID + " not found. ")
            .type(MediaType.TEXT_PLAIN).build());
      }
   }

   @GET
   @Path("log/{buildid}")
   public Response log(@PathParam("buildid") String buildID) throws IOException
   {
      MavenBuildTask task = tasks.get(buildID);
      if (task != null)
      {
         return Response.ok(task.getLogger().getLogReader(), MediaType.TEXT_PLAIN).build();
      }
      // Incorrect task ID.
      throw new WebApplicationException(Response
         .status(404)
         .entity("Build " + buildID + " not found. ")
         .type(MediaType.TEXT_PLAIN).build());
   }

   @GET
   @Path("download/{buildid}")
   public Response download(@PathParam("buildid") String buildID, @Context UriInfo uriInfo)
   {
      MavenBuildTask task = tasks.get(buildID);
      if (task != null)
      {
         if (task.isDone())
         {
            try
            {
               InvocationResultImpl result = task.getResult();
               if (0 == result.getExitCode())
               {
                  File artifact = result.getArtifacts()[0];
                  return Response.ok(artifact, "application/zip").build();
               }

               // Build is failed - nothing for download.
               throw new WebApplicationException(Response
                  .status(404)
                  .entity("Build failed. There is no artifact for download. ")
                  .type(MediaType.TEXT_PLAIN).build());
            }
            catch (MavenInvocationException e)
            {
               throw new WebApplicationException(e);
            }
         }
         // Sent location to check status method.
         final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(buildID);
         return Response
            .status(202)
            .location(location)
            .entity(location.toString())
            .type(MediaType.TEXT_PLAIN).build();
      }
      // Incorrect task ID.
      throw new WebApplicationException(Response
         .status(404)
         .entity("Build " + buildID + " not found. ")
         .type(MediaType.TEXT_PLAIN).build());
   }
}
