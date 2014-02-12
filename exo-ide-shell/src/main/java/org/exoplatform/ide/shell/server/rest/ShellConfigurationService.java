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
package org.exoplatform.ide.shell.server.rest;

import com.codenvy.commons.env.EnvironmentContext;

import org.exoplatform.ide.shell.conversationstate.ShellUser;
import org.exoplatform.ide.vfs.server.VirtualFileSystemFactory;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Configuration service for shell.
 * 
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShellConfigurationService.java Mar 6, 2012 4:46:36 PM azatsarynnyy $
 */
@Path("{ws-name}/shell/configuration")
public class ShellConfigurationService {
    private static Log LOG = ExoLogger.getLogger(ShellConfigurationService.class);
    
    @PathParam("ws-name")
    String wsName;

    @GET
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    public Map<String, Object> inializationParameters(@Context UriInfo uriInfo) {
        try {
            String vfsId = EnvironmentContext.getCurrent().getWorkspaceId();
            Map<String, Object> result = new HashMap<String, Object>();
            ConversationState curentState = ConversationState.getCurrent();
            if (curentState != null) {
                Identity identity = curentState.getIdentity();
                ShellUser user = new ShellUser(identity.getUserId(), identity.getRoles());
                if (LOG.isDebugEnabled())
                    LOG.info("Getting user identity: " + identity.getUserId());
                result.put("user", user);
                result.put("userSettings", "{}");
                LOG.info("EVENT#shell-launched#");
            }
            result.put("vfsId", vfsId);
            result.put("vfsBaseUrl", uriInfo.getBaseUriBuilder().path(VirtualFileSystemFactory.class).path("v2").build(wsName).toString());
            return result;
        } catch (Exception e) {
            throw new WebApplicationException(e);
        }
    }

}
