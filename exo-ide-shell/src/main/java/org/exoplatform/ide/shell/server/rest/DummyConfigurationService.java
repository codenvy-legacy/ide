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
