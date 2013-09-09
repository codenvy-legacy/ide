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
package org.exoplatform.ide.extension.jenkins.server.rest;

import org.exoplatform.ide.extension.jenkins.server.JenkinsClient;
import org.exoplatform.ide.extension.jenkins.server.JenkinsException;
import org.exoplatform.ide.extension.jenkins.shared.Job;
import org.exoplatform.ide.extension.jenkins.shared.JobStatus;
import org.exoplatform.ide.vfs.server.GitUrlResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/jenkins")
public class JenkinsService {
    @Inject
    private JenkinsClient jenkins;

    @Inject
    private VirtualFileSystemRegistry vfsRegistry;

    @Inject
    private GitUrlResolver gitUrlResolver;

    public JenkinsService() {
    }

    protected JenkinsService(JenkinsClient jenkins, VirtualFileSystemRegistry vfsRegistry, GitUrlResolver gitUrlResolver) {
        // Use this constructor when deploy JenkinsService as singleton resource.
        this.jenkins = jenkins;
        this.vfsRegistry = vfsRegistry;
        this.gitUrlResolver = gitUrlResolver;
    }

    @Path("job/create")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Job createJob( //
                          @QueryParam("name") String name, //
                          @QueryParam("user") String user, //
                          @QueryParam("email") String email, //
                          @QueryParam("projectid") String projectId, //
                          @QueryParam("vfsid") String vfsId, @Context UriInfo uriInfo) throws IOException, JenkinsException,
                                                                                              VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        String git = gitUrlResolver.resolve(uriInfo, vfs, projectId);
        jenkins.createJob(name, git, user, email, vfs, projectId);
        String buildUrl = uriInfo.getBaseUriBuilder().path(getClass(), "build").build(name).toString();
        String statusUrl = uriInfo.getBaseUriBuilder().path(getClass(), "jobStatus").build(name).toString();
        return new JobBean(name, buildUrl, statusUrl);
    }

    @Path("job/build")
    @POST
    public void build( //
                       @QueryParam("name") String jobName, //
                       @QueryParam("projectid") String projectId, //
                       @QueryParam("vfsid") String vfsId) throws IOException, JenkinsException, VirtualFileSystemException {
        VirtualFileSystem vfs = null;
        if (vfsId != null) {
            vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        }

        jenkins.build(jobName, vfs, projectId);
    }

    @Path("job/status")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public JobStatus jobStatus( //
                                @QueryParam("name") String jobName, //
                                @QueryParam("projectid") String projectId, //
                                @QueryParam("vfsid") String vfsId) throws IOException, JenkinsException, VirtualFileSystemException {
        return jenkins.jobStatus(jobName, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                 projectId);
    }

    @Path("job/console-output")
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public InputStream consoleOutput( //
                                      @QueryParam("name") String jobName, //
                                      @QueryParam("projectid") String projectId, //
                                      @QueryParam("vfsid") String vfsId) throws IOException, JenkinsException, VirtualFileSystemException {
        return jenkins.consoleOutput(jobName, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null,
                                     projectId);
    }

    @Path("job/delete")
    @POST
    public void deleteJob( //
                           @QueryParam("name") String jobName, //
                           @QueryParam("projectid") String projectId, //
                           @QueryParam("vfsid") String vfsId) throws IOException, JenkinsException, VirtualFileSystemException {
        jenkins.deleteJob(jobName, vfsId != null ? vfsRegistry.getProvider(vfsId).newInstance(null, null) : null, projectId);
    }
}
