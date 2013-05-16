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

import org.exoplatform.ide.shell.conversationstate.ShellUser;

import javax.annotation.security.RolesAllowed;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/shell/configuration")
public class DummyConfigurationService {
    @GET
    @Path("/init")
    @Produces(MediaType.APPLICATION_JSON)
    @RolesAllowed({"developer"})
    public Map<String, Object> inializationParameters(@Context UriInfo uriInfo, @Context HttpServletRequest request) {

        Map<String, Object> result = new HashMap<String, Object>();
        result.put("userSettings", "{}");
        ShellUser user =
                new ShellUser(request.getUserPrincipal().getName(), Collections.<String>emptyList());
        result.put("user", user);
        result.put("userSettings", "{}");
        return result;

    }

}
