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
import org.exoplatform.ide.extension.java.shared.MavenResponse;
import org.exoplatform.ide.maven.InvocationRequestFactory;
import org.exoplatform.ide.maven.MavenTask;
import org.exoplatform.ide.maven.TaskService;
import org.exoplatform.ide.maven.TaskWatcher;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
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
   @Produces(MediaType.APPLICATION_JSON)
   public Response createApp(@QueryParam("workdir") FSLocation baseDir, @QueryParam("name") String name,
      @Context UriInfo uriInfo) throws Exception
   {
      // web applications (.war) only
      InvocationRequest request = ARCHETYPE_REQUEST_FACTORY.createRequest();
      File dir = new File(baseDir.getLocalPath(uriInfo));
      request.setBaseDirectory(dir);
      Properties properties = new Properties();
      properties.put("archetypeArtifactId", "maven-archetype-webapp");
      properties.put("groupId", name);
      properties.put("artifactId", name);
      request.setProperties(properties);
      MavenResponse mvn = execute(request);
      if (0 == mvn.getExitCode()) // If other than zero then build fails. 
      {
         File app = new File(dir, name);
         if (app.exists()) // Be sure application directory created after maven execution.
         {
            File ignore = new File(app, ".gitignore");
            FileWriter w = null;
            try
            {
               w = new FileWriter(ignore);
               w.write("target/");
               w.write('\n');
               w.flush();
            }
            finally
            {
               if (w != null)
                  w.close();
            }
         }
      }
      return createResponse(mvn);
   }

   @POST
   @Path("clean")
   @Produces(MediaType.APPLICATION_JSON)
   public Response clean(@QueryParam("workdir") FSLocation baseDir, @Context UriInfo uriInfo) throws Exception
   {
      InvocationRequest request = CLEAN_REQUEST_FACTORY.createRequest();
      request.setBaseDirectory(new File(baseDir.getLocalPath(uriInfo)));
      MavenResponse mvn = execute(request);
      return createResponse(mvn);
   }

   @POST
   @Path("package")
   @Produces(MediaType.APPLICATION_JSON)
   public Response pack(@QueryParam("workdir") FSLocation baseDir, @Context UriInfo uriInfo) throws Exception
   {
      InvocationRequest request = PACKAGE_REQUEST_FACTORY.createRequest();
      File dir = new File(baseDir.getLocalPath(uriInfo));
      request.setBaseDirectory(dir);
      MavenResponse mvn = execute(request);
      String[] files = new File(dir, "target").list(new FilenameFilter()
      {
         @Override
         public boolean accept(File dir, String name)
         {
            return name.endsWith(".war"); // Support only web applications at the moment.
         }
      });
      if (files.length > 0)
      {
         Map<String, String> result = new HashMap<String, String>(1);
         result.put("war", //
            baseDir.getURL() + "/target/" + files[0]);
         mvn.setResult(result);
      }
      return createResponse(mvn);
   }

   /*private Response execute(InvocationRequest request, long timeout ) throws Exception
   {
      return execute(request, new TaskWatcher(timeout));
   }*/

   private Response createResponse(MavenResponse mvn)
   {
      ResponseBuilder b = mvn.getExitCode() == 0 ? Response.ok() : Response.status(500);
      return b.entity(mvn).type(MediaType.APPLICATION_JSON).build();
   }

   private MavenResponse execute(InvocationRequest request) throws Exception
   {
      return execute(request, null);
   }

   private MavenResponse execute(InvocationRequest request, TaskWatcher watcher) throws Exception
   {
      MavenTask task = taskService.add(request, watcher);
      InvocationResult result;
      try
      {
         result = task.get(); // Block until task end.
      }
      finally
      {
         // Do not store task in pool since we are waiting until it ends and read output from it.
         taskService.remove(task.getId());
      }

      CommandLineException executionException = result.getExecutionException();
      if (executionException != null)
         throw executionException;

      // Send output of maven task to caller.
      int exitCode = result.getExitCode();
      return new MavenResponse(exitCode, task.getTaskLogger().getLogAsString(), null);
   }
}
