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

import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudbees.server.CloudBees;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;

import com.codenvy.commons.env.EnvironmentContext;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/cloudbees")
public class CloudBeesService {
    private static final Log LOG = ExoLogger.getLogger(CloudBeesService.class);

    @Inject
    private CloudBees cloudbees;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @QueryParam("vfsid")
    private String vfsId;

    @QueryParam("projectid")
    private String projectId;

    @QueryParam("appid")
    private String appId;

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws Exception {
        cloudbees.login(null, credentials.get("email"), credentials.get("password"));
    }

    @Path("logout")
    @POST
    public void logout() throws Exception {
        cloudbees.logout();
    }

    @Path("domains")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> domains() throws Exception {
        return cloudbees.getDomains();
    }

    @Path("apps/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> createApplication( //
                                                  @QueryParam("message") String message, // Optional
                                                  @QueryParam("war") URL war //
                                                ) throws Exception {
        Map<String, String> app = cloudbees
                .createApplication(appId, message, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId,
                                   war);
        if (projectId != null && vfsId != null) {
            VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
            Project proj = (Project)vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
            LOG.info("EVENT#application-created# WS#" + EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_NAME)
                     + "# USER#" + ConversationState.getCurrent().getIdentity().getUserId() + "# PROJECT#" + proj.getName() + "# TYPE#" + proj.getProjectType()
                     + "# PAAS#CloudBees#");
        }
        return app;
    }

    @Path("apps/update")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> updateApplication( //
                                                  @QueryParam("message") String message, // Optional
                                                  @QueryParam("war") URL war //
                                                ) throws Exception {
        return cloudbees.updateApplication(appId, message,
                                           vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId, war);
    }

    @Path("apps/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, String> applicationInfo() throws Exception {
        return cloudbees.applicationInfo(appId, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                         projectId);
    }

    @Path("apps/delete")
    @POST
    public void deleteApplication() throws Exception {
        cloudbees.deleteApplication(appId, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                    projectId);
    }

    @Path("apps/all")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<Map<String, String>> getAllApplications() throws Exception {
        return cloudbees.listApplications();
    }

   /*===== Account provisioning =====*/

    @Path("accounts")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CloudBeesAccount createAccount(CloudBeesAccount account) throws Exception {
        return cloudbees.createAccount(account);
    }

    @Path("accounts/{account}/users")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public CloudBeesUser addUserToAccount(@PathParam("account") String account,
                                          @QueryParam("existing_user") boolean existingUser,
                                          CloudBeesUser user) throws Exception {
        return cloudbees.createUser(account, user, existingUser);
    }
}
