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
package org.exoplatform.ide.extension.maven.server;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

/**
 * RESTful facade for {@link BuilderClient}
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/maven")
public class BuilderService {

    @PathParam("ws-name")
    private String                    wsName;

    @Inject
    private BuilderClient             builder;

    @Inject
    private VirtualFileSystemRegistry virtualFileSystemRegistry;

    /**
     * Start new build at remote build server. Job may be started immediately or add in queue. If WebSocket session identifier was provided
     * then status of job will be sent to client automatically when job will be finished. Otherwise client should check location given in
     * response header to current get status of job.
     * 
     * @param vfsId identifier of virtual file system
     * @param projectId identifier of project we want to send for build
     * @param useWebSocket if <code>true</code> - result status of build job will be sent via WebSocket
     * @param uriInfo context info about current request
     * @return response with status 202 if request is accepted. Client get location of resource that it should check to see the current
     *         status.
     * @throws BuilderException if request for new request was rejected by remote server
     * @throws IOException if any i/o errors occur
     * @throws VirtualFileSystemException if any error in VFS
     * @see BuilderClient#build(org.exoplatform.ide.vfs.server.VirtualFileSystem, String)
     */
    @GET
    @Path("build")
    public Response build(@QueryParam("projectid") String projectId, //
                          @QueryParam("vfsid") String vfsId, //
                          @QueryParam("name") String projectName, //
                          @QueryParam("type") String projectType, //
                          @Context UriInfo uriInfo) throws BuilderException, IOException, VirtualFileSystemException {
        VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(vfsId).newInstance(null, null);
        final String buildID = builder.build(vfs, projectId, projectName, projectType);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(wsName, buildID);
        return Response.status(202).location(location).entity(location.toString()).build();
    }


    /**
     * Start new build and deploy at remote build server. Job may be started immediately or add in queue. Client should check location given
     * in response header to current get status of job.
     * 
     * @param vfsId identifier of virtual file system
     * @param projectId identifier of project we want to send for build
     * @param uriInfo context info about current request
     * @return response with status 202 if request is accepted. Client get location of resource that it should check to see the current
     *         status.
     * @throws BuilderException if request for new request was rejected by remote server
     * @throws IOException if any i/o errors occur
     * @throws VirtualFileSystemException if any error in VFS
     * @see BuilderClient#build(org.exoplatform.ide.vfs.server.VirtualFileSystem, String)
     */
    @GET
    @Path("deploy")
    public Response deploy(@QueryParam("projectid") String projectId, //
                           @QueryParam("vfsid") String vfsId, //
                           @QueryParam("name") String projectName, //
                           @QueryParam("type") String projectType, //
                           @Context UriInfo uriInfo) throws BuilderException, IOException, VirtualFileSystemException {
        VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(vfsId).newInstance(null, null);
        // Item project = vfs.getItem(projectId, PropertyFilter.ALL_FILTER);
        // ContentStream pom = vfs.getContent(project.getPath() + "/pom.xml",null);

        final String buildID = builder.deploy(vfs, projectId, projectName, projectType);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(wsName, buildID);
        return Response.status(202).location(location).entity(location.toString()).build();
    }

    /**
     * Start new job to get list of dependencies of project. Job may be started immediately or add in queue. Client should check location
     * given in response header to current get status of job.
     * 
     * @param vfsId identifier of virtual file system
     * @param projectId identifier of project we want to send for getting list of dependencies
     * @param uriInfo context info about current request
     * @return response with status 202 if request is accepted. Client get location of resource that it should check to see the current
     *         status.
     * @throws BuilderException if request for new request was rejected by remote server
     * @throws IOException if any i/o errors occur
     * @throws VirtualFileSystemException if any error in VFS
     * @see BuilderClient#dependenciesList(org.exoplatform.ide.vfs.server.VirtualFileSystem, String)
     */
    @GET
    @Path("dependencies/list")
    public Response dependenciesList(@QueryParam("projectid") String projectId, //
                                     @QueryParam("vfsid") String vfsId, //
                                     @Context UriInfo uriInfo) throws BuilderException, IOException, VirtualFileSystemException {
        VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(vfsId).newInstance(null, null);
        final String buildID = builder.dependenciesList(vfs, projectId);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(wsName, buildID);
        return Response.status(202).location(location).entity(location.toString()).build();
    }

    /**
     * Start new job to get project dependencies in zip archive. Job may be started immediately or add in queue. Client should check
     * location given in response header to current get status of job.
     * 
     * @param vfsId identifier of virtual file system
     * @param projectId identifier of project we want to send for getting dependencies
     * @param classifier classifier to look for, e.g. : sources. May be <code>null</code>.
     * @param uriInfo context info about current request
     * @return response with status 202 if request is accepted. Client get location of resource that it should check to see the current
     *         status.
     * @throws BuilderException if request for new request was rejected by remote server
     * @throws IOException if any i/o errors occur
     * @throws VirtualFileSystemException if any error in VFS
     * @see BuilderClient#dependenciesCopy(org.exoplatform.ide.vfs.server.VirtualFileSystem, String, String)
     */
    @GET
    @Path("dependencies/copy")
    public Response dependenciesCopy(@QueryParam("projectid") String projectId, //
                                     @QueryParam("vfsid") String vfsId, //
                                     @QueryParam("classifier") String classifier, //
                                     @Context UriInfo uriInfo) throws BuilderException, IOException, VirtualFileSystemException {
        VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(vfsId).newInstance(null, null);
        final String buildID = builder.dependenciesCopy(vfs, projectId, classifier);
        final URI location = uriInfo.getBaseUriBuilder().path(getClass()).path(getClass(), "status").build(wsName, buildID);
        return Response.status(202).location(location).entity(location.toString()).build();
    }

    /**
     * Get result of previously launched job.
     * 
     * @param buildID ID of job
     * @return string that contains result of build in JSON format. Do nothing with such string just re-send result to client
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     */
    @GET
    @Path("result/{buildid}")
    public String result(@PathParam("buildid") String buildID) throws BuilderException, IOException {
        return builder.result(buildID);
    }

    /**
     * Check current status of previously launched job.
     * 
     * @param buildID ID of job
     * @return string that contains description of current status of build in JSON format. Do nothing with such string just re-send result
     *         to client
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     * @see BuilderClient#status(String)
     */
    @GET
    @Path("status/{buildid}")
    public String status(@PathParam("buildid") String buildID) throws BuilderException, IOException {
        return builder.status(buildID);
    }

    /**
     * Cancel previously launched job.
     * 
     * @param buildID ID of job
     * @param projectName name of project
     * @param projectType type of project
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     * @see BuilderClient#cancel(String)
     */
    @GET
    @Path("cancel/{buildid}")
    public void cancel(@PathParam("buildid") String buildID,
                       @QueryParam("projectName") String projectName,
                       @QueryParam("projectType") String projectType) throws BuilderException, IOException {
        builder.cancel(buildID, projectName, projectType);
    }

    /**
     * Get job log.
     * 
     * @param buildID ID of job
     * @return stream that contains job log
     * @throws IOException if any i/o errors occur
     * @throws BuilderException any other errors related to build server internal state or parameter of client request
     * @see BuilderClient#cancel(String)
     */
    @GET
    @Path("log/{buildid}")
    public InputStream log(@PathParam("buildid") String buildID) throws BuilderException, IOException {
        return builder.log(buildID);
    }

    /**
     * Check is URL for download artifact is valid. Artifact may be removed by timeout but GWT client not able to check it because to
     * cross-domain restriction for ajax requests.
     * 
     * @param url URL for checking
     * @return response with status 200 if URL valid
     * @throws IOException if any i/o errors occur
     * @throws BuilderException URL is not valid or any other errors related to build server internal state
     * @see BuilderClient#checkDownloadURL(String)
     */
    @GET
    @Path("check_download_url")
    public Response checkDownloadURL(@QueryParam("url") String url) throws BuilderException, IOException {
        builder.checkDownloadURL(url);
        return Response.ok().build();
    }
}
