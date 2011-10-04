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

import org.exoplatform.ide.extension.cloudbees.server.CloudBees;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.File;
import java.net.URL;
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
   
   @Inject
   private LocalPathResolver localPathResolver;

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @QueryParam("vfsId")
   private String vfsId;

   @QueryParam("projectid")
   private String projectId;

   @QueryParam("appid")
   private String appId;

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

   @Path("apps/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> createApplication( //
      @QueryParam("message") String message, // Optional
      @QueryParam("war") URL war, //
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return cloudbees.createApplication(appId, message, new File(localPathResolver.resolve(vfs, projectId)), war);
   }

   @Path("apps/update")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> updateApplication( //
      @QueryParam("message") String message, // Optional
      @QueryParam("war") URL war, //
      @Context UriInfo uriInfo //
   ) throws Exception
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return cloudbees.updateApplication(appId, message, new File(localPathResolver.resolve(vfs, projectId)), war);
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, String> applicationInfo() throws Exception
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      return cloudbees.applicationInfo(appId, new File(localPathResolver.resolve(vfs, projectId)));
   }

   @Path("apps/delete")
   @POST
   public void deleteApplication() throws Exception
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      if (vfs == null)
         throw new VirtualFileSystemException("Virtual file system not initialized");
      cloudbees.deleteApplication(appId, new File(localPathResolver.resolve(vfs, projectId)));
   }
   
   @Path("apps/all")   
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<Map<String,String>> getAllApplications() throws Exception
   {
      return cloudbees.listApplications();
   }
}
