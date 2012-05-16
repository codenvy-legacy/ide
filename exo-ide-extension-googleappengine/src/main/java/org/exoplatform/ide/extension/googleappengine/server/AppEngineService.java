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
package org.exoplatform.ide.extension.googleappengine.server;

import com.google.appengine.tools.admin.CronEntry;
import com.google.apphosting.utils.config.BackendsXml;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;

import java.io.Reader;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/appengine")
public class AppEngineService
{
   @Inject
   private AppEngineClient client;

   @Inject
   private VirtualFileSystemRegistry vfsRegistry;

   @POST
   @Path("backend/configure")
   @Consumes(MediaType.APPLICATION_JSON)
   public void configureBackend(@QueryParam("vfsid") String vfsId,
                                @QueryParam("projectid") String projectId,
                                @QueryParam("backend_name") String backendName,
                                Map<String, String> credentials) throws Exception
   {
      client.configureBackend(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         backendName,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("cron/info")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public List<CronEntry> cronInfo(@QueryParam("vfsid") String vfsId,
                                   @QueryParam("projectid") String projectId,
                                   Map<String, String> credentials) throws Exception
   {
      return client.cronInfo(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backend/delete")
   @Consumes(MediaType.APPLICATION_JSON)
   public void deleteBackend(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             @QueryParam("backend_name") String backendName,
                             Map<String, String> credentials) throws Exception
   {
      client.deleteBackend(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         backendName,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("resource_limits")
   @Consumes(MediaType.APPLICATION_JSON)
   public Map<String, Long> getResourceLimits(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId,
                                              Map<String, String> credentials) throws Exception
   {
      return client.getResourceLimits(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backends/list")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public List<BackendsXml.Entry> listBackends(@QueryParam("vfsid") String vfsId,
                                               @QueryParam("projectid") String projectId,
                                               Map<String, String> credentials) throws Exception
   {
      return client.listBackends(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("logs")
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.TEXT_PLAIN)
   public Reader requestLogs(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             @QueryParam("num_days") int numDays,
                             @QueryParam("log_severity") String logSeverity,
                             Map<String, String> credentials) throws Exception
   {
      return client.requestLogs(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         numDays,
         logSeverity,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("rollback")
   @Consumes(MediaType.APPLICATION_JSON)
   public void rollback(@QueryParam("vfsid") String vfsId,
                        @QueryParam("projectid") String projectId,
                        Map<String, String> credentials) throws Exception
   {
      client.rollback(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backend/rollback")
   @Consumes(MediaType.APPLICATION_JSON)
   public void rollbackBackend(@QueryParam("vfsid") String vfsId,
                               @QueryParam("projectid") String projectId,
                               @QueryParam("backend_name") String backendName,
                               Map<String, String> credentials) throws Exception
   {
      client.rollbackBackend(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         backendName,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backends/rollback")
   @Consumes(MediaType.APPLICATION_JSON)
   public void rollbackAllBackends(@QueryParam("vfsid") String vfsId,
                                   @QueryParam("projectid") String projectId,
                                   Map<String, String> credentials) throws Exception
   {
      client.rollbackAllBackends(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backend/set_state")
   @Consumes(MediaType.APPLICATION_JSON)
   public void setBackendState(@QueryParam("vfsid") String vfsId,
                               @QueryParam("projectid") String projectId,
                               @QueryParam("backend_name") String backendName,
                               @QueryParam("backend_state") String backendState,
                               Map<String, String> credentials) throws Exception
   {
      client.setBackendState(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         backendName,
         backendState,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("update")
   @Consumes(MediaType.APPLICATION_JSON)
   public void update(@QueryParam("vfsid") String vfsId,
                      @QueryParam("projectid") String projectId,
                      @QueryParam("bin") URL bin,
                      Map<String, String> credentials) throws Exception
   {
      client.update(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         bin,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backends/update_all")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateAllBackends(@QueryParam("vfsid") String vfsId,
                                 @QueryParam("projectid") String projectId,
                                 Map<String, String> credentials) throws Exception
   {
      client.updateAllBackends(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backend/update")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateBackend(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             @QueryParam("backend_name") String backendName,
                             Map<String, String> credentials) throws Exception
   {
      client.updateBackend(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         backendName,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("backends/update")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateBackends(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             @QueryParam("backends_name") List<String> backendNames,
                             Map<String, String> credentials) throws Exception
   {
      client.updateBackends(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         backendNames,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("cron/update")
   @Produces(MediaType.APPLICATION_JSON)
   public void updateCron(@QueryParam("vfsid") String vfsId,
                          @QueryParam("projectid") String projectId,
                          Map<String, String> credentials) throws Exception
   {
      client.updateCron(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("dos/update")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateDos(@QueryParam("vfsid") String vfsId,
                         @QueryParam("projectid") String projectId,
                         Map<String, String> credentials) throws Exception
   {
      client.updateDos(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("indexes/update")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateIndexes(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             Map<String, String> credentials) throws Exception
   {
      client.updateIndexes(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("pagespeed/update")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updatePagespeed(@QueryParam("vfsid") String vfsId,
                               @QueryParam("projectid") String projectId,
                               Map<String, String> credentials) throws Exception
   {
      client.updatePagespeed(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("queues/update")
   @Consumes(MediaType.APPLICATION_JSON)
   public void updateQueues(@QueryParam("vfsid") String vfsId,
                            @QueryParam("projectid") String projectId,
                            Map<String, String> credentials) throws Exception
   {
      client.updateQueues(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }

   @POST
   @Path("vacuum_indexes")
   @Consumes(MediaType.APPLICATION_JSON)
   public void vacuumIndexes(@QueryParam("vfsid") String vfsId,
                             @QueryParam("projectid") String projectId,
                             Map<String, String> credentials) throws Exception
   {
      client.vacuumIndexes(
         vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null) : null,
         projectId,
         credentials.get("email"),
         credentials.get("password")
      );
   }
}
