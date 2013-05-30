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
package org.exoplatform.ide.extension.appfog.server.rest;

import com.codenvy.ide.commons.server.ParsingResponseException;

import org.exoplatform.ide.extension.appfog.server.Appfog;
import org.exoplatform.ide.extension.appfog.server.AppfogException;
import org.exoplatform.ide.extension.appfog.server.DebugMode;
import org.exoplatform.ide.extension.appfog.shared.*;
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
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@Path("{ws-name}/appfog")
public class AppfogService {
    private static final Log LOG = ExoLogger.getLogger(AppfogService.class);

    @javax.inject.Inject
    private Appfog appfog;

    @javax.inject.Inject
    private VirtualFileSystemRegistry vfsRegistry;

    public AppfogService() {
    }

    protected AppfogService(Appfog appfog, VirtualFileSystemRegistry vfsRegistry) {
        // Use this constructor when deploy AppfogService as singleton resource.
        this.appfog = appfog;
        this.vfsRegistry = vfsRegistry;
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials)
            throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        appfog.login(credentials.get("server"), credentials.get("email"), credentials.get("password"));
    }

    @Path("logout")
    @POST
    public void logout(@QueryParam("server") String server) throws CredentialStoreException {
        appfog.logout(server);
    }

    @Path("info/system")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public SystemInfo systemInfo(@QueryParam("server") String server)
            throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        return appfog.systemInfo(server);
    }

    @Path("info/frameworks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<Framework> frameworks(@QueryParam("server") String server)
            throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        return appfog.systemInfo(server).getFrameworks().values();
    }

    @Path("apps/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AppfogApplication applicationInfo(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                                            )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return appfog.applicationInfo(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
                                                                 : null, projectId);
    }

    @Path("apps/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public AppfogApplication createApplication(Map<String, String> params)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
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

        String infraName = params.get("infra");

        //TODO to use enum type of infrastructures
        AppfogApplication app =
                appfog.createApplication(params.get("server"), params.get("name"), params.get("type"), params.get("url"),
                                         instances, mem, noStart, params.get("runtime"), params.get("command"), debugMode, vfs,
                                         params.get("projectid"), warURL, InfraType.fromValue(infraName));

        String projectId = params.get("projectid");
        if (projectId != null) {
            Project proj = (Project)vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
            LOG.info("EVENT#application-created# PROJECT#" + proj.getName() + "# TYPE#" + proj.getProjectType()
                     + "# PAAS#Appfog#");
        }
        return app;
    }

    @Path("apps/start")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public AppfogApplication startApplication(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("debug") String debug,
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                                             )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        DebugMode debugMode = null;
        if (debug != null) {
            debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
        }

        return appfog.startApplication(server, app, debugMode, vfsId != null ? vfsRegistry.getProvider(vfsId)
                                                                                          .newInstance(null, null) : null, projectId);
    }

    @Path("apps/stop")
    @POST
    public void stopApplication(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                               )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.stopApplication(server, app,
                               vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
    }

    @Path("apps/restart")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public AppfogApplication restartApplication(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("debug") String debug,
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                                               )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        DebugMode debugMode = null;
        if (debug != null) {
            debugMode = debug.isEmpty() ? new DebugMode() : new DebugMode(debug);
        }

        return appfog.restartApplication(server, app,
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
                                 )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.updateApplication(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
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
                          ) throws AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        return appfog.getFiles(server, app, path, instance,
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
                         ) throws AppfogException, CredentialStoreException, VirtualFileSystemException, IOException {
        return appfog.getLogs(server, app, instance,
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
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.mapUrl(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
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
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.unmapUrl(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
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
                   ) throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.mem(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
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
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.instances(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                         projectId, expression);
    }

    @Path("apps/instances/info")
    @GET
    public Instance[] applicationInstances(
            @QueryParam("server") String server, //
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId
                                          )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return appfog.applicationInstances(server, app,
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
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.environmentAdd(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
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
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.environmentDelete(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
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
                                 )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.deleteApplication(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
                                                            : null, projectId, deleteServices);
    }

    @Path("apps/stats")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, AppfogApplicationStatistics> applicationStats(
            @QueryParam("server") String server,
            @QueryParam("name") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId //
                                                                    )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return appfog.applicationStats(server, app, vfsId != null ? vfsRegistry.getProvider(vfsId)
                                                                               .newInstance(null, null) : null, projectId);
    }

    @Path("apps")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AppfogApplication[] listApplications(@QueryParam("server") String server)
            throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        return appfog.listApplications(server);
    }

    @Path("services")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public AppfogServices services(@QueryParam("server") String server)
            throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        return appfog.services(server);
    }

    @Path("services/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public AppfogProvisionedService createService(
            @QueryParam("server") String server, //
            @QueryParam("type") String service, //
            @QueryParam("name") String name, //
            @QueryParam("app") String app, //
            @QueryParam("vfsid") String vfsId, //
            @QueryParam("projectid") String projectId, //
            @QueryParam("infra") String infra
                                                 )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return appfog.createService(server, service, name, app, vfsId != null ? vfsRegistry.getProvider(vfsId)
                                                                                           .newInstance(null, null) : null, projectId,
                                    InfraType.fromValue(infra));
    }

    @Path("services/delete/{name}")
    @POST
    public void deleteService(
            @QueryParam("server") String server, //
            @PathParam("name") String name //
                             ) throws AppfogException, ParsingResponseException, CredentialStoreException, IOException {
        appfog.deleteService(server, name);
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
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.bindService(server, name, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
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
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.unbindService(server, name, app, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null)
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
                              )
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        appfog.validateAction(server, action, app, framework, url, instances, memory, nostart, vfsId != null
                                                                                               ? vfsRegistry.getProvider(vfsId)
                                                                                                            .newInstance(null, null) : null,
                              projectId);
    }

    @Path("target")
    @POST
    public void target(@QueryParam("target") String target)
            throws CredentialStoreException {
        appfog.setTarget(target);
    }

    @Path("target")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String target() throws CredentialStoreException {
        return appfog.getTarget();
    }

    @Path("target/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Collection<String> targets() throws CredentialStoreException {
        return appfog.getTargets();
    }

    @Path("infras")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public InfraDetail[] getInfras(@QueryParam("server") String server,
                                   @QueryParam("vfsid") String vfsId,
                                   @QueryParam("projectid") String projectId)
            throws AppfogException, ParsingResponseException, CredentialStoreException, VirtualFileSystemException, IOException {
        return appfog.getInfras(server, vfsId != null
                                        ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
    }
}
