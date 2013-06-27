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
            String vfsId = (String)EnvironmentContext.getCurrent().getVariable(EnvironmentContext.WORKSPACE_ID);
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
