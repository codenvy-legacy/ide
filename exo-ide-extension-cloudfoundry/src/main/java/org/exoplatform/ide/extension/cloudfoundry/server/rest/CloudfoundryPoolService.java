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
package org.exoplatform.ide.extension.cloudfoundry.server.rest;

import org.exoplatform.ide.extension.cloudfoundry.server.ext.CloudfoundryPool;
import org.exoplatform.ide.extension.cloudfoundry.server.ext.CloudfoundryServerConfiguration;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import java.security.Principal;

/**
 * RESTful front-end for CloudfoundryPool.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/cloudfoundry/pool")
public class CloudfoundryPoolService {
    @Inject
    private CloudfoundryPool pool;

    @Path("add")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void addConfiguration(@Context SecurityContext sctx, CloudfoundryServerConfiguration config) {
        checkPrivileges(sctx);
        pool.addConfiguration(config);
    }

    @Path("remove")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void removeConfiguration(@Context SecurityContext sctx, CloudfoundryServerConfiguration config) {
        checkPrivileges(sctx);
        pool.removeConfiguration(config);
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public CloudfoundryServerConfiguration[] getConfigurations(@Context SecurityContext sctx) {
        checkPrivileges(sctx);
        return pool.getConfigurations();
    }

    private void checkPrivileges(SecurityContext sctx) {
        Principal user = sctx.getUserPrincipal();
        if (user == null || !"cldadmin".equals(user.getName())) {
            throw new WebApplicationException(Response.status(403).entity("Operation not allowed.\n")
                                                      .type(MediaType.TEXT_PLAIN).build());
        }
    }
}
