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
