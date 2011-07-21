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

import java.io.IOException;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/jenkins")
public class JenkinsService
{
   @javax.inject.Inject
   private JenkinsClient jenkins;

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
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public Job createJob(Map<String, String> job, @Context UriInfo uriInfo) throws IOException, JenkinsException
   {
      String name = job.get("name");
      jenkins.createJob( //
         name, //
         job.get("git"), //
         job.get("user"), //
         job.get("email") //
         );
      String buildUrl = uriInfo.getBaseUriBuilder().path(getClass(), "build").build(name).toString();
      String statusUrl = uriInfo.getBaseUriBuilder().path(getClass(), "jobStatus").build(name).toString();
      return new Job(name, buildUrl, statusUrl);
   }

   @Path("job/build/{name}")
   @POST
   public void build(@PathParam("name") String jobName) throws IOException, JenkinsException
   {
      jenkins.build(jobName);
   }

   @Path("job/status/{name}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public JobStatus jobStatus(@PathParam("name") String jobName) throws IOException, JenkinsException
   {
      return jenkins.jobStatus(jobName);
   }
}
