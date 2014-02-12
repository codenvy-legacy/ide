/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.ide.commons.IDEWorkspace;
import com.codenvy.ide.commons.IdeUser;
import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Profile;
import com.codenvy.organization.model.User;
import com.codenvy.organization.model.Workspace;

import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 23, 2011 evgen $
 */
@Path("{ws-name}/configuration")
public class IDEConfigurationService {

    private static Log             LOG = ExoLogger.getLogger(IDEConfigurationService.class);

    private final UserManager      userManager;

    private final WorkspaceManager workspaceManager;

    public IDEConfigurationService() throws OrganizationServiceException {
        userManager = new UserManager();
        workspaceManager = new WorkspaceManager();
    }

    @PathParam("ws-name")
    private String wsName;

    /**
     * periodic request to prevent session expiration TODO: need find better solutions
     */
    @GET
    @Path("ping")
    public void ping()
    {
    }

    @GET
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> inializationParameters(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        try {
            String vfsId = EnvironmentContext.getCurrent().getWorkspaceId();
            Map<String, Object> result = new HashMap<String, Object>();
            ConversationState curentState = ConversationState.getCurrent();
            Identity identity = curentState.getIdentity();
            String userId = identity.getUserId();
            
            String firstName = null;
            String lastName = null;
            
            if (!"__anonim".equals(userId) && userManager.userExists(userId)) {
                User user = userManager.getUserByAlias(userId);
                Profile profile = user.getProfile();
                
                firstName = profile.getAttribute("firstName");
                lastName = profile.getAttribute("lastName");
            }
            
            boolean temporary = false;
            List<IDEWorkspace> workspaces = new ArrayList<IDEWorkspace>();
            try {
                for (Workspace workspace : userManager.getUserWorkspaces(userId)) {
                    workspaces.add(new IDEWorkspace(uriInfo.getBaseUriBuilder().replacePath(null).path("ide").path(workspace.getName()).build().toString(),
                                                    workspace.getName(), workspace.getId(), workspace.isTemporary()));
                }
                temporary = userManager.getUserByAlias(userId).isTemporary();
            } catch (OrganizationServiceException ignore) {
                // ignore
            }
            
            IdeUser user = new IdeUser(userId, firstName, lastName, identity.getRoles(), request.getSession().getId(), workspaces, temporary);
            LOG.debug(user.toString());
            result.put("user", user);
            result.put("userSettings", Collections.emptyMap());
            result.put("vfsId", vfsId);
            result.put("currentWorkspace",
                       new IDEWorkspace(uriInfo.getBaseUriBuilder().replacePath(null).path("ide").path(wsName)
                                               .build().toString(), wsName, vfsId, workspaceManager.getWorkspaceByName(wsName)
                                                                                                   .isTemporary()));
            result.put("vfsBaseUrl", uriInfo.getBaseUriBuilder().path(VirtualFileSystemFactory.class).path("v2").build(wsName).toString());
            return result;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"developer"})
    public String getConfiguration() {
        return "{}"; // TODO: small hack add for supporting previous version of IDE. In 1.2 changed structure of user settings
    }

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @RolesAllowed({"developer"})
    public void setConfiguration(String body) throws IOException {
        // not impl yet
    }


}
