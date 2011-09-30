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
package org.exoplatform.ide.extension.jenkins.server.rest;

import org.exoplatform.ide.extension.jenkins.server.JenkinsClient;
import org.exoplatform.ide.extension.jenkins.server.JenkinsException;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.io.InputStream;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/jenkins")
public class JenkinsService
{
   @Inject
   private JenkinsClient jenkins;
   
   @Inject
   private VirtualFileSystemRegistry vfsRegistry;
   
   
   public JenkinsService()
   {
   }

   protected JenkinsService(JenkinsClient jenkins)
   {
      // Use this constructor when deploy JenkinsService as singleton resource.
      this.jenkins = jenkins;
   }

   @Path("job/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Job createJob( //
      @QueryParam("name") String name, //
      @QueryParam("git") String git, //
      @QueryParam("user") String user, //
      @QueryParam("email") String email, //
      @QueryParam("projectId") String projectId, //
      @QueryParam("vfsId") String vfsId,
      @Context UriInfo uriInfo
   ) throws IOException, JenkinsException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new WebApplicationException(Response.serverError().entity("Virtual file system not initialized").build());
      jenkins.createJob(name, git, user, email, projectId, vfs);
      String buildUrl = uriInfo.getBaseUriBuilder().path(getClass(), "build").build(name).toString();
      String statusUrl = uriInfo.getBaseUriBuilder().path(getClass(), "jobStatus").build(name).toString();
      return new Job(name, buildUrl, statusUrl);
   }

   @Path("job/build")
   @POST
   public void build( //
      @QueryParam("name") String jobName, //
      @QueryParam("projectId") String projectId, //
      @QueryParam("vfsId") String vfsId
   ) throws IOException, JenkinsException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new WebApplicationException(Response.serverError().entity("Virtual file system not initialized").build());
      jenkins.build(jobName, projectId, vfs);
   }

   @Path("job/status")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public JobStatus jobStatus( //
      @QueryParam("name") String jobName, //
      @QueryParam("projectId") String projectId, //
      @QueryParam("vfsId") String vfsId
   ) throws IOException, JenkinsException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new WebApplicationException(Response.serverError().entity("Virtual file system not initialized").build());
      return jenkins.jobStatus(jobName, projectId, vfs);
   }

   @Path("job/console-output")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public InputStream consoleOutput( //
      @QueryParam("name") String jobName, //
      @QueryParam("projectId") String projectId, //
      @QueryParam("vfsId") String vfsId
   ) throws IOException, JenkinsException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new WebApplicationException(Response.serverError().entity("Virtual file system not initialized").build());
      return jenkins.consoleOutput(jobName, projectId, vfs);
   }
}
