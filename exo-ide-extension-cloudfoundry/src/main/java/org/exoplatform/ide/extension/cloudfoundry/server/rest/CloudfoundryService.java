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
package org.exoplatform.ide.extension.cloudfoundry.server.rest;

import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.server.DebugMode;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudFoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.Instance;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/cloudfoundry")
public class CloudfoundryService
{
   @javax.inject.Inject
   private Cloudfoundry cloudfoundry;

   @javax.inject.Inject
   private VirtualFileSystemRegistry vfsRegistry;

   public CloudfoundryService()
   {
   }

   protected CloudfoundryService(Cloudfoundry cloudfoundry, VirtualFileSystemRegistry vfsRegistry)
   {
      // Use this constructor when deploy CloudfoundryService as singleton resource.
      this.cloudfoundry = cloudfoundry;
      this.vfsRegistry = vfsRegistry;
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws CloudfoundryException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      cloudfoundry.login(credentials.get("server"), credentials.get("email"), credentials.get("password"));
   }

   @Path("logout")
   @POST
   public void logout(@QueryParam("server") String server) throws IOException, CloudfoundryException,
      VirtualFileSystemException
   {
      cloudfoundry.logout(server);
   }

   @Path("info/system")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public SystemInfo systemInfo(@QueryParam("server") String server) throws CloudfoundryException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      return cloudfoundry.systemInfo(server);
   }

   @Path("info/frameworks")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<Framework> frameworks(@QueryParam("server") String server) throws CloudfoundryException,
      IOException, ParsingResponseException, VirtualFileSystemException
   {
      return cloudfoundry.systemInfo(server).getFrameworks().values();
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudFoundryApplication applicationInfo(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws CloudfoundryException, ParsingResponseException, VirtualFileSystemException, IOException
   {
      return cloudfoundry.applicationInfo(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
         : null, projectId);
   }

   @Path("apps/create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public CloudFoundryApplication createApplication(Map<String, String> params) throws CloudfoundryException,
      ParsingResponseException, VirtualFileSystemException, IOException
   {
      String debug = params.get("debug");
      DebugMode debugMode = null;
      if (debug != null)
      {
         debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
      }

      int instances;
      try
      {
         instances = Integer.parseInt(params.get("instances"));
      }
      catch (NumberFormatException e)
      {
         instances = 1;
      }

      int mem;
      try
      {
         mem = Integer.parseInt(params.get("memory"));
      }
      catch (NumberFormatException e)
      {
         mem = 0;
      }

      boolean noStart = Boolean.parseBoolean(params.get("nostart"));

      String vfsId = params.get("vfsid");
      VirtualFileSystem vfs = vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null;

      String warURLStr = params.get("war");
      URL warURL = warURLStr == null || warURLStr.isEmpty() ? null : new URL(warURLStr);

      return cloudfoundry.createApplication(params.get("server"), params.get("name"), params.get("type"),
         params.get("url"), instances, mem, noStart, params.get("runtime"), params.get("command"), debugMode, vfs,
         params.get("projectid"), warURL);
   }

   @Path("apps/start")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudFoundryApplication startApplication(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("debug") String debug,
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      DebugMode debugMode = null;
      if (debug != null)
      {
         debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
      }
      return cloudfoundry.startApplication(server, app, debugMode, vfsId != null ? vfsRegistry.getProvider(vfsId)
         .newInstance(null, null) : null, projectId);
   }

   @Path("apps/stop")
   @POST
   public void stopApplication(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.stopApplication(server, app,
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
   }

   @Path("apps/restart")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudFoundryApplication restartApplication(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("debug") String debug,
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      DebugMode debugMode = null;
      if (debug != null)
      {
         debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
      }
      return cloudfoundry.restartApplication(server, app,
         debugMode, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
         projectId);
   }

   @Path("apps/update")
   @POST
   public void updateApplication(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("war") URL war //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.updateApplication(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
         : null, projectId, war);
   }

   @Path("apps/files")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getFiles(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("path") String path, //
      @QueryParam("instance") String instance, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return cloudfoundry.getFiles(server, app, path, instance,
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
   }

   @Path("apps/logs")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String getLogs(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("instance") String instance, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return cloudfoundry.getLogs(server, app, instance,
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
   }

   @Path("apps/map")
   @POST
   public void mapUrl(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("url") String url //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.mapUrl(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
         projectId, url);
   }

   @Path("apps/unmap")
   @POST
   public void unmapUrl(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("url") String url //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.unmapUrl(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
         projectId, url);
   }

   @Path("apps/mem")
   @POST
   public void mem(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("mem") int mem //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.mem(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
         mem);
   }

   @Path("apps/instances")
   @POST
   public void instances(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("expr") String expression //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.instances(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
         projectId, expression);
   }

   @Path("apps/instances/info")
   @GET
   public Instance[] applicationInstances(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return cloudfoundry.applicationInstances(server, app,
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
   }

   @Path("apps/env/add")
   @POST
   public void environmentAdd(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("key") String key, //
      @QueryParam("val") String value //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.environmentAdd(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
         projectId, key, value);
   }

   @Path("apps/env/delete")
   @POST
   public void environmentDelete(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("key") String key //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.environmentDelete(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
         : null, projectId, key);
   }

   @Path("apps/delete")
   @POST
   public void deleteApplication(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId, //
      @QueryParam("delete-services") boolean deleteServices //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.deleteApplication(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
         : null, projectId, deleteServices);
   }

   @Path("apps/stats")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, CloudfoundryApplicationStatistics> applicationStats(
      @QueryParam("server") String server, //
      @QueryParam("name") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return cloudfoundry.applicationStats(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId)
         .newInstance(null, null) : null, projectId);
   }

   @Path("apps")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudFoundryApplication[] listApplications(@QueryParam("server") String server) throws IOException,
      ParsingResponseException, CloudfoundryException, VirtualFileSystemException
   {
      return cloudfoundry.listApplications(server);
   }

   @Path("services")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryServices services(@QueryParam("server") String server) throws IOException,
      ParsingResponseException, CloudfoundryException, VirtualFileSystemException
   {
      return cloudfoundry.services(server);
   }

   @Path("services/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public ProvisionedService createService(
      @QueryParam("server") String server, //
      @QueryParam("type") String service, //
      @QueryParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      return cloudfoundry.createService(server, service, name, app, vfsId != null ? vfsRegistry.getProvider(vfsId)
         .newInstance(null, null) : null, projectId);
   }

   @Path("services/delete/{name}")
   @POST
   public void deleteService(
      @QueryParam("server") String server, //
      @PathParam("name") String name //
   ) throws IOException, ParsingResponseException, CloudfoundryException, VirtualFileSystemException
   {
      cloudfoundry.deleteService(server, name);
   }

   @Path("services/bind/{name}")
   @POST
   public void bindService(
      @QueryParam("server") String server, //
      @PathParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.bindService(server, name, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
         : null, projectId);
   }

   @Path("services/unbind/{name}")
   @POST
   public void unbindService(
      @QueryParam("server") String server, //
      @PathParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.unbindService(server, name, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
         : null, projectId);
   }

   @Path("apps/validate-action")
   @POST
   public void validateAction(
      @QueryParam("server") String server, //
      @QueryParam("action") String action, //
      @QueryParam("name") String app, //
      @QueryParam("type") String framework, //
      @QueryParam("url") String url, //
      @DefaultValue("1") @QueryParam("instances") int instances, //
      @QueryParam("mem") int memory, //
      @QueryParam("nostart") boolean nostart, //
      @QueryParam("vfsid") String vfsId, //
      @QueryParam("projectid") String projectId //
   ) throws ParsingResponseException, CloudfoundryException, VirtualFileSystemException, IOException
   {
      cloudfoundry.validateAction(server, action, app, framework, url, instances, memory, nostart, vfsId != null
         ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
   }

   @Path("target")
   @POST
   public void target(@QueryParam("target") String target) throws IOException, CloudfoundryException,
      VirtualFileSystemException
   {
      cloudfoundry.setTarget(target);
   }

   @Path("target")
   @GET
   @Produces(MediaType.TEXT_PLAIN)
   public String target() throws IOException, CloudfoundryException, VirtualFileSystemException
   {
      return cloudfoundry.getTarget();
   }

   @Path("target/all")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<String> targets() throws IOException, CloudfoundryException, VirtualFileSystemException
   {
      return cloudfoundry.getTargets();
   }
}
