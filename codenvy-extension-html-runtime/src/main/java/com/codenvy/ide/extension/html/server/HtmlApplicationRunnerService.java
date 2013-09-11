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
package com.codenvy.ide.extension.html.server;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;

import org.exoplatform.ide.vfs.impl.fs.LocalFSMountStrategy;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Provide access to {@link ApplicationRunner} through HTTP.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: HtmlApplicationRunnerService.java Jun 26, 2013 1:14:54 PM azatsarynnyy $
 */
@Path("{ws-name}/html/runner")
public class HtmlApplicationRunnerService {

    @Inject
    private ApplicationRunner         runner;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Inject
    private LocalFSMountStrategy      fsMountStrategy;

    @Path("run")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInstance runApplication(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId) throws ApplicationRunnerException,
                                                                                        VirtualFileSystemException {
        VirtualFileSystem vfs = vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null;
        ApplicationInstance app = runner.runApplication(vfs, projectId, fsMountStrategy.getMountPath().getPath());
        return app;
    }

    @GET
    @Path("stop")
    public void stopApplication(@QueryParam("name") String name) throws ApplicationRunnerException {
        runner.stopApplication(name);
    }
}
