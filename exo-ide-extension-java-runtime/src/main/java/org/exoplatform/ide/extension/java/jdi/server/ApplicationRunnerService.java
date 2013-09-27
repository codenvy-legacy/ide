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
        app.setStopURL(uriInfo.getBaseUriBuilder().path(ApplicationRunnerService.this.getClass()).path(
                ApplicationRunnerService.this.getClass(), "stopApplication").queryParam("name", app.getName()).build(wsName).toString());
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
        app.setStopURL(uriInfo.getBaseUriBuilder().path(ApplicationRunnerService.this.getClass()).path(
                ApplicationRunnerService.this.getClass(), "stopApplication")
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
