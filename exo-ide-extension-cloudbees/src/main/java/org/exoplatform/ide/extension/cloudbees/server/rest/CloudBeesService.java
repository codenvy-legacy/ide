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
package org.exoplatform.ide.extension.cloudbees.server.rest;

import org.exoplatform.ide.FSLocation;
import org.exoplatform.ide.extension.cloudbees.server.CloudBees;

import java.io.File;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/cloudbees")
public class CloudBeesService
{
   @Inject
   private CloudBees cloudbees;

   public CloudBeesService()
   {
   }

   protected CloudBeesService(CloudBees cloudbees)
   {
      // Use this constructor when deploy CloudBeesService as singleton resource.
      this.cloudbees = cloudbees;
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws Exception
   {
      cloudbees.login(null, credentials.get("email"), credentials.get("password"));
   }

   @Path("logout")
   @POST
   public void logout()
   {
      cloudbees.logout();
   }

   @Path("domains")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<String> domains() throws Exception
   {
      return cloudbees.getDomains();
   }

   @Path("apps/war-deploy")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> warDeploy( //
      @QueryParam("appid") String appId, //
      @QueryParam("war") FSLocation warFile, //
      @QueryParam("message") String message, // Optional
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      return cloudbees.warDeploy(appId, warFile.getLocalPath(uriInfo), null, message);
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> applicationInfo( //
      @QueryParam("appid") String appId, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      return cloudbees.applicationInfo(appId, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/delete")
   @POST
   public void deleteApplication( //
      @QueryParam("appid") String appId, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      cloudbees.deleteApplication(appId, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }
}
