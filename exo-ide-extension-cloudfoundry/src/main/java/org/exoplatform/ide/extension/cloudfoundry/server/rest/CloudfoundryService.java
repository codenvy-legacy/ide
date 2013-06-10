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

import com.codenvy.ide.commons.server.ParsingResponseException;

import org.exoplatform.ide.extension.cloudfoundry.server.Cloudfoundry;
import org.exoplatform.ide.extension.cloudfoundry.server.CloudfoundryException;
import org.exoplatform.ide.extension.cloudfoundry.server.DebugMode;
import org.exoplatform.ide.extension.cloudfoundry.shared.*;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/cloudfoundry")
public class CloudfoundryService {
    private static final Log LOG = ExoLogger.getLogger(CloudfoundryService.class);

    @javax.inject.Inject
    private Cloudfoundry cloudfoundry;

    @javax.inject.Inject
    private VirtualFileSystemRegistry vfsRegistry;

    public CloudfoundryService() {
    }

    protected CloudfoundryService(Cloudfoundry cloudfoundry, VirtualFileSystemRegistry vfsRegistry) {
        // Use this constructor when deploy CloudfoundryService as singleton resource.
        this.cloudfoundry = cloudfoundry;
        this.vfsRegistry = vfsRegistry;
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials, @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider)
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, IOException {
        if (credentials == null)
            throw new IllegalArgumentException("You must set your target, email & password");
        cloudfoundry.login(credentials.get("server"), credentials.get("email"), credentials.get("password"), paasProvider);
    }

    @Path("logout")
    @POST
    public void logout(@QueryParam("server") String server, @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider)
            throws CredentialStoreException {
        cloudfoundry.logout(server, paasProvider);
    }

    @Path("info/system")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SystemInfo systemInfo(@QueryParam("server") String server, @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider)
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, IOException {
        return cloudfoundry.systemInfo(server, paasProvider);
    }

    @Path("info/frameworks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Framework> frameworks(@QueryParam("server") String server, @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider)
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, IOException {
        return cloudfoundry.systemInfo(server, paasProvider).getFrameworks().values();
    }

    @Path("apps/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CloudFoundryApplication applicationInfo(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                                                  )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return cloudfoundry.applicationInfo(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
                                                                       : null, projectId);
    }

    @Path("apps/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CloudFoundryApplication createApplication(Map<String, String> params)
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        String debug = params.get("debug");
        DebugMode debugMode = null;
        if (debug != null) {
            debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
        }

        int instances;
        try {
            instances = Integer.parseInt(params.get("instances"));
        } catch (NumberFormatException e) {
            instances = 1;
        }

        int mem;
        try {
            mem = Integer.parseInt(params.get("memory"));
        } catch (NumberFormatException e) {
            mem = 0;
        }

        boolean noStart = Boolean.parseBoolean(params.get("nostart"));

        String vfsId = params.get("vfsid");
        VirtualFileSystem vfs = vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null;

        String warURLStr = params.get("war");
        URL warURL = warURLStr == null || warURLStr.isEmpty() ? null : new URL(warURLStr);
        final String paasProvider = params.get("paasprovider");
        CloudFoundryApplication app =
                cloudfoundry.createApplication(params.get("server"), params.get("name"), params.get("type"),
                                               params.get("url"), instances, mem, noStart, params.get("runtime"), params.get("command"),
                                               debugMode, vfs,
                                               params.get("projectid"), warURL, paasProvider);

        String projectId = params.get("projectid");
        if (projectId != null) {
            Project proj = (Project)vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
            String paasName;
            if (paasProvider != null && paasProvider.equalsIgnoreCase("tier3webfabric")) {
                paasName = "Tier3 Web Fabric";
            } else {
                paasName = "CloudFoundry";
            }
            LOG.info("EVENT#application-created# PROJECT#" + proj.getName() + "# TYPE#" + proj.getProjectType()
                     + "# PAAS#" + paasName + "#");
        }
        return app;
    }

    @Path("apps/start")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CloudFoundryApplication startApplication(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("debug") String debug,
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId, //
            @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider //
                                                   )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        DebugMode debugMode = null;
        if (debug != null) {
            debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
        }
        return cloudfoundry.startApplication(server, app, debugMode, vfsId != null ? vfsRegistry.getProvider(vfsId)
                                                                                                .newInstance(null, null) : null, projectId,
                                                                                                paasProvider);
    }

    @Path("apps/stop")
    @POST
    public void stopApplication(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId, //
            @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider //
                               )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        cloudfoundry.stopApplication(server, app,
                                     vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId, paasProvider);
    }

    @Path("apps/restart")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public CloudFoundryApplication restartApplication(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("debug") String debug,
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId, //
            @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider //
                                                     )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        DebugMode debugMode = null;
        if (debug != null) {
            debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
        }
        return cloudfoundry.restartApplication(server, app,
                                               debugMode, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                               projectId, paasProvider);
    }

    @Path("apps/update")
    @POST
    public void updateApplication(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId, //
            @QueryParam("war") URL war //
                                 )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                          ) throws CloudfoundryException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                         ) throws CloudfoundryException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                      )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                        )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                   )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                         )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        cloudfoundry.instances(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                               projectId, expression);
    }

    @Path("apps/instances/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Instance[] applicationInstances(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                                          )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                              )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                                 )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
            @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider, //
            @QueryParam("delete-services") boolean deleteServices //
                                 )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        cloudfoundry.deleteApplication(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
                                                                  : null, projectId, paasProvider, deleteServices);
    }

    @Path("apps/stats")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, CloudfoundryApplicationStatistics> applicationStats(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                                                                          )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return cloudfoundry.applicationStats(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId)
                                                                                     .newInstance(null, null) : null, projectId);
    }

    @Path("apps")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CloudFoundryApplication[] listApplications(@QueryParam("server") String server, @QueryParam("paasprovider") String paasProvider)
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, IOException {
        return cloudfoundry.listApplications(server, paasProvider);
    }

    @Path("services")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CloudfoundryServices services(@QueryParam("server") String server, @QueryParam("paasprovider") String paasProvider)
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, IOException {
        return cloudfoundry.services(server, paasProvider);
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
                                           )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return cloudfoundry.createService(server, service, name, app, vfsId != null ? vfsRegistry.getProvider(vfsId)
                                                                                                 .newInstance(null, null) : null,
                                          projectId);
    }

    @Path("services/delete/{name}")
    @POST
    public void deleteService(
            @QueryParam("server") String server, //
            @PathParam("name") String name, //
            @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider
                             ) throws CloudfoundryException, ParsingResponseException, CredentialStoreException, IOException {
        cloudfoundry.deleteService(server, name, paasProvider);
    }

    @Path("services/bind/{name}")
    @POST
    public void bindService(
            @QueryParam("server") String server, //
            @PathParam("name") String name, //
            @QueryParam("app") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                           )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
                             )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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
            @QueryParam("projectid") String projectId, //
            @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider
                              )
            throws CloudfoundryException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        cloudfoundry.validateAction(server, action, app, framework, url, instances, memory, nostart, vfsId != null
                                                                                                     ? vfsRegistry.getProvider(vfsId)
                                                                                                                  .newInstance(null, null)
                                                                                                     : null, projectId, paasProvider);
    }

    @Path("target")
    @POST
    public void target(@QueryParam("target") String target, @QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider)
            throws CredentialStoreException {
        cloudfoundry.setTarget(target, paasProvider);
    }

    @Path("target")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String target(@QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider) throws CredentialStoreException {
        return cloudfoundry.getTarget(paasProvider);
    }

    @Path("target/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<String> targets(@QueryParam("paasprovider") @DefaultValue("cloudfoundry") String paasProvider)
            throws CredentialStoreException {
        return cloudfoundry.getTargets(paasProvider);
    }
}
