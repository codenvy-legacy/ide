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
package org.exoplatform.ide.extension.python.server;

import org.exoplatform.ide.extension.python.shared.ApplicationInstance;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;

/**
 * Provide access to {@link ApplicationRunner} through HTTP.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/python/runner")
public class ApplicationRunnerService {
    @Inject
    private ApplicationRunner         runner;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;
    
    @PathParam("ws-name")
    String wsName;

    @Path("run")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInstance runApplication(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId,
                                              @Context UriInfo uriInfo)
                                                                       throws ApplicationRunnerException, VirtualFileSystemException {
        ApplicationInstance app =
                                  runner.runApplication(vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                                        projectId);
        app.setStopURL(uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "stopApplication").queryParam("name", app.getName())
                              .build(wsName).toString());
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
}
