/*
 * Copyright (C) 2013 eXo Platform SAS.
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
