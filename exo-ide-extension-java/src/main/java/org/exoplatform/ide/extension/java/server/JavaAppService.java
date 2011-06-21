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
package org.exoplatform.ide.extension.java.server;

import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.codehaus.plexus.util.cli.CommandLineException;
import org.exoplatform.ide.FSLocation;
import org.exoplatform.ide.extension.maven.InvocationRequestFactory;
import org.exoplatform.ide.extension.maven.MavenTask;
import org.exoplatform.ide.extension.maven.TaskService;
import org.exoplatform.ide.extension.maven.TaskWatcher;

import java.io.File;
import java.util.Arrays;
import java.util.Properties;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/application/java")
public class JavaAppService
{
   private static InvocationRequestFactory ARCHETYPE_REQUEST_FACTORY = new InvocationRequestFactory(
      Arrays.asList("archetype:generate"));

   private static InvocationRequestFactory CLEAN_REQUEST_FACTORY = new InvocationRequestFactory(Arrays.asList("clean"));

   private static InvocationRequestFactory PACKAGE_REQUEST_FACTORY = new InvocationRequestFactory(Arrays.asList(
      "clean", "package"));

   private final TaskService taskService;

   public JavaAppService(TaskService taskService)
   {
      this.taskService = taskService;
   }

   @POST
   @Path("create")
   @Produces(MediaType.TEXT_PLAIN)
   public Response createApp(@QueryParam("workdir") FSLocation baseDir, @QueryParam("name") String name,
      @Context UriInfo uriInfo) throws Exception
   {
      InvocationRequest request = ARCHETYPE_REQUEST_FACTORY.createRequest();
      request.setBaseDirectory(new File(baseDir.getLocalPath(uriInfo)));
      Properties properties = new Properties();
      properties.put("archetypeArtifactId", "maven-archetype-webapp");
      properties.put("groupId", name);
      properties.put("artifactId", name);
      request.setProperties(properties);
      return execute(request);
   }

   @POST
   @Path("clean")
   @Produces(MediaType.TEXT_PLAIN)
   public Response clean(@QueryParam("workdir") FSLocation baseDir, @Context UriInfo uriInfo) throws Exception
   {
      InvocationRequest request = CLEAN_REQUEST_FACTORY.createRequest();
      request.setBaseDirectory(new File(baseDir.getLocalPath(uriInfo)));
      return execute(request);
   }

   @POST
   @Path("package")
   @Produces(MediaType.TEXT_PLAIN)
   public Response pack(@QueryParam("workdir") FSLocation baseDir, @Context UriInfo uriInfo) throws Exception
   {
      InvocationRequest request = PACKAGE_REQUEST_FACTORY.createRequest();
      request.setBaseDirectory(new File(baseDir.getLocalPath(uriInfo)));
      return execute(request);
   }

   /*private Response execute(InvocationRequest request, long timeout ) throws Exception
   {
      return execute(request, new TaskWatcher(timeout));
   }*/

   private Response execute(InvocationRequest request) throws Exception
   {
      return execute(request, null);
   }

   private Response execute(InvocationRequest request, TaskWatcher watcher) throws Exception
   {
      MavenTask task = taskService.add(request, watcher);
      InvocationResult result = task.get(); // Block until task end.

      CommandLineException executionException = result.getExecutionException();
      if (executionException != null)
         throw executionException;

      // Send output of maven task to caller;
      int exitCode = result.getExitCode();
      Response response;
      if (exitCode == 0)
         response = Response.ok().entity(task.getTaskLogger().getLogReader()).type(MediaType.TEXT_PLAIN).build();
      else
         response =
            Response.status(500).entity(task.getTaskLogger().getLogReader()).type(MediaType.TEXT_PLAIN)
               .header("Maven-Exit-Code", exitCode).build();

      return response;
   }
}
