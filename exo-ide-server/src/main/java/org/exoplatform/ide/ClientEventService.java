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

package org.exoplatform.ide;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Handle rest service calls to show events to console.
 *
 * @author <a href="mailto:vzhukovskii@codenvy.com">Vladyslav Zhukovskii</a>
 * @version $Id: 30.08.13 vlad $
 */
@Path("{ws-name}/client-event")
public class ClientEventService {
    private static final Log LOG = ExoLogger.getLogger(ClientEventService.class);

    @PathParam("ws-name")
    private String wsName;

    @GET
    @Path("autocomplete")
    public void autocompleteEvent(@QueryParam("project") String project, @QueryParam("type") String type) {
        if (project == null || project.isEmpty() || type == null || type.isEmpty()) {
            throw new WebApplicationException(
                    Response.status(Response.Status.BAD_REQUEST).entity("Project name and project type should not be null or empty.")
                            .build());
        }

        LOG.info("EVENT#user-code-complete# PROJECT#" + project + "# TYPE#" + type + "#");
    }
}
