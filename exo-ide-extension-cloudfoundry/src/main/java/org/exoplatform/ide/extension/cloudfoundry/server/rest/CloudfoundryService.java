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

import org.exoplatform.ide.FSLocation;
import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.server.ParsingResponseException;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplication;
import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;

import java.io.File;
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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/cloudfoundry")
public class CloudfoundryService
{
   @javax.inject.Inject
   private Cloudfoundry cloudfoundry;

   public CloudfoundryService()
   {
   }

   protected CloudfoundryService(Cloudfoundry cloudfoundry)
   {
      // Use this constructor when deploy CloudfoundryService as singleton resource.
      this.cloudfoundry = cloudfoundry;
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws CloudfoundryException, IOException,
      ParsingResponseException
   {
      cloudfoundry.login(credentials.get("email"), credentials.get("password"));
   }

   @Path("logout")
   @POST
   public void logout()
   {
      cloudfoundry.logout();
   }

   @Path("info/system")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public SystemInfo systemInfo() throws CloudfoundryException, IOException, ParsingResponseException
   {
      return cloudfoundry.systemInfo();
   }

   @Path("info/frameworks")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Collection<Framework> frameworks()
   {
      return Cloudfoundry.FRAMEWORKS.values();
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication applicationInfo( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws CloudfoundryException, IOException, ParsingResponseException
   {
      return cloudfoundry.applicationInfo(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication createApplication( //
      @QueryParam("name") String app, //
      @QueryParam("type") String framework, //
      @QueryParam("url") String url, //
      @DefaultValue("1") @QueryParam("instances") int instances, //
      @QueryParam("mem") int memory, //
      @QueryParam("nostart") boolean nostart, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("war") URL war, //
      @Context UriInfo uriInfo //
   ) throws CloudfoundryException, IOException, ParsingResponseException
   {
      return cloudfoundry.createApplication(app, framework, url, instances, memory, nostart, workDir != null
         ? new File(workDir.getLocalPath(uriInfo)) : null, war);
   }

   @Path("apps/start")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication startApplication( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return cloudfoundry.startApplication(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/stop")
   @POST
   public void stopApplication( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.stopApplication(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/restart")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication restartApplication( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return cloudfoundry.restartApplication(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   /*@Path("apps/rename")
   @POST
   public void renameApplication( //
      @QueryParam("name") String app, //
      @QueryParam("newname") String newname, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.renameApplication(app, newname, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }*/

   @Path("apps/update")
   @POST
   public void updateApplication( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("war") URL war, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.updateApplication(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, war);
   }

   @Path("apps/map")
   @POST
   public void mapUrl( //
      @QueryParam("name") String app, //
      @QueryParam("url") String url, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.mapUrl(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, url);
   }

   @Path("apps/unmap")
   @POST
   public void unmapUrl( //
      @QueryParam("name") String app, //
      @QueryParam("url") String url, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.unmapUrl(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, url);
   }

   @Path("apps/mem")
   @POST
   public void mem( //
      @QueryParam("name") String app, //
      @QueryParam("mem") int mem, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.mem(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, mem);
   }

   @Path("apps/instances")
   @POST
   public void instances( //
      @QueryParam("name") String app, //
      @QueryParam("expr") String expression, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.instances(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, expression);
   }

   @Path("apps/env/add")
   @POST
   public void environmentAdd( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("key") String key, //
      @QueryParam("val") String value, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.environmentAdd(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, key, value);
   }

   @Path("apps/env/delete")
   @POST
   public void environmentDelete( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("key") String key, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.environmentDelete(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null, key);
   }

   @Path("apps/delete")
   @POST
   public void deleteApplication( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @QueryParam("delete-services") boolean deleteServices, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.deleteApplication(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null,
         deleteServices);
   }

   @Path("apps/stats")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public Map<String, CloudfoundryApplicationStatistics> applicationStats( //
      @QueryParam("name") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return cloudfoundry.applicationStats(app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryApplication[] listApplications() throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      return cloudfoundry.listApplications();
   }

   @Path("services")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public CloudfoundryServices services() throws IOException, ParsingResponseException, CloudfoundryException
   {
      return cloudfoundry.services();
   }

   @Path("services/create")
   @POST
   @Produces(MediaType.APPLICATION_JSON)
   public ProvisionedService createService( //
      @QueryParam("type") String service, //
      @QueryParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo //
   ) throws IOException, ParsingResponseException, CloudfoundryException
   {
      return cloudfoundry.createService(service, name, app, workDir != null ? new File(workDir.getLocalPath(uriInfo))
         : null);
   }

   @Path("services/delete/{name}")
   @POST
   public void deleteService(@PathParam("name") String name) throws IOException, ParsingResponseException,
      CloudfoundryException
   {
      cloudfoundry.deleteService(name);
   }

   @Path("services/bind/{name}")
   @POST
   public void bindService( //
      @PathParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.bindService(name, app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("services/unbind/{name}")
   @POST
   public void unbindService( //
      @PathParam("name") String name, //
      @QueryParam("app") String app, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.unbindService(name, app, workDir != null ? new File(workDir.getLocalPath(uriInfo)) : null);
   }

   @Path("apps/validate-action")
   @POST
   public void validateAction( //
      @QueryParam("action") String action, //
      @QueryParam("name") String app, //
      @QueryParam("type") String framework, //
      @QueryParam("url") String url, //
      @DefaultValue("1") @QueryParam("instances") int instances, //
      @QueryParam("mem") int memory, //
      @QueryParam("nostart") boolean nostart, //
      @QueryParam("workdir") FSLocation workDir, //
      @Context UriInfo uriInfo) throws IOException, ParsingResponseException, CloudfoundryException
   {
      cloudfoundry.validateAction(action, app, framework, url, instances, memory, nostart, workDir != null ? new File(
         workDir.getLocalPath(uriInfo)) : null);
   }
}
