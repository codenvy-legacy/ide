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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.net.URL;
import java.util.Map;

/**
 * Provide access to {@link ApplicationRunner} through HTTP.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/java/runner")
public class ApplicationRunnerService {
    @Inject
    private ApplicationRunner runner;
    
    @PathParam("ws-name")
    String wsName;

    @Path("run")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInstance runApplication(@QueryParam("war") URL war,
                                              @Context UriInfo uriInfo,
                                              Map<String, String> params)
            throws ApplicationRunnerException {
        if (params == null)
            throw new IllegalArgumentException("Body ot this response can not be null or empty");
        ApplicationInstance app = runner.runApplication(war, params);
        app.setStopURL(uriInfo.getBaseUriBuilder().path(ApplicationRunnerService.this.getClass(), "stopApplication")
                              .queryParam("name", app.getName()).build(wsName).toString());
        return app;
    }

    @Path("debug")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInstance debugApplication(@QueryParam("war") URL war,
                                                @QueryParam("suspend") boolean suspend,
                                                @Context UriInfo uriInfo,
                                                Map<String, String> params) throws ApplicationRunnerException {
        ApplicationInstance app = runner.debugApplication(war, suspend, params);
        app.setStopURL(uriInfo.getBaseUriBuilder().path(ApplicationRunnerService.this.getClass(), "stopApplication")
                              .queryParam("name", app.getName()).build(wsName).toString());
        return app;
    }

    @GET
    @Path("logs")
    @Produces(MediaType.TEXT_PLAIN)
    public String getLogs(@QueryParam("name") String name) throws ApplicationRunnerException {
        return runner.getLogs(name);
    }

    @GET
    @Path("stop")
    public void stopApplication(@QueryParam("name") String name) throws ApplicationRunnerException {
        runner.stopApplication(name);
    }

    @GET
    @Path("prolong")
    public void prolongExpirationTime(@QueryParam("name") String name,
                                      @QueryParam("time") long time) throws ApplicationRunnerException {
        runner.prolongExpirationTime(name, time);
    }

    @Path("update")
    @GET
    public void updateApplication(@QueryParam("name") String name,
                                  @QueryParam("war") URL war) throws ApplicationRunnerException {
        runner.updateApplication(name, war);
    }
}
