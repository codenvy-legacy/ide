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
package org.exoplatform.ide.extension.maven.server;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

/**
 * RESTful facade for {@link BuilderClient}
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/maven")
public class BuilderService
{
   @Inject
   private BuilderClient builder;

   @Inject
   private VirtualFileSystemRegistry virtualFileSystemRegistry;

   /**
    * Start new build at remote build server. Job may be started immediately or add in queue. Client should check
    * location given in response header to current get status of job.
    *
    * @param vfsId
    *    identifier of virtual file system
    * @param projectId
    *    identifier of project we want to send for build
    * @param uriInfo
    *    context info about current request
    * @return response with status 202 if request is accepted. Client get location of resource that it should check to
    *         see the current status.
    * @throws BuilderException
    *    if request for new request was rejected by remote server
    * @throws IOException
    *    if any i/o errors occur
    * @throws VirtualFileSystemException
    *    if any error in VFS
    * @see BuilderClient#build(org.exoplatform.ide.vfs.server.VirtualFileSystem, String)
    */
   @GET
   @Path("build")
   public Response build(@QueryParam("projectid") String projectId, //
                         @QueryParam("vfsid") String vfsId, //
                         @Context UriInfo uriInfo) throws BuilderException, IOException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(vfsId).newInstance(null, null);
      final String buildID = builder.build(vfs, projectId);
      final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(buildID);
      return Response.status(202).location(location).entity(location.toString()).build();
   }

   /**
    * Start new job to get list of dependencies of project. Job may be started immediately or add in queue.
    * Client
    * should check location given in response header to current get status of job.
    *
    * @param vfsId
    *    identifier of virtual file system
    * @param projectId
    *    identifier of project we want to send for getting list of dependencies
    * @param uriInfo
    *    context info about current request
    * @return response with status 202 if request is accepted. Client get location of resource that it should check to
    *         see the current status.
    * @throws BuilderException
    *    if request for new request was rejected by remote server
    * @throws IOException
    *    if any i/o errors occur
    * @throws VirtualFileSystemException
    *    if any error in VFS
    * @see BuilderClient#dependenciesList(org.exoplatform.ide.vfs.server.VirtualFileSystem, String)
    */
   @GET
   @Path("dependencies/list")
   public Response dependenciesList(@QueryParam("projectid") String projectId, //
                                    @QueryParam("vfsid") String vfsId, //
                                    @Context UriInfo uriInfo) throws BuilderException, IOException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(vfsId).newInstance(null, null);
      final String buildID = builder.dependenciesList(vfs, projectId);
      final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(buildID);
      return Response.status(202).location(location).entity(location.toString()).build();
   }

   /**
    * Start new job to get project dependencies in zip archive. Job may be started immediately or add in queue.
    * Client should check location given in response header to current get status of job.
    *
    * @param vfsId
    *    identifier of virtual file system
    * @param projectId
    *    identifier of project we want to send for getting dependencies
    * @param classifier
    *    classifier to look for, e.g. : sources. May be <code>null</code>.
    * @param uriInfo
    *    context info about current request
    * @return response with status 202 if request is accepted. Client get location of resource that it should check to
    *         see the current status.
    * @throws BuilderException
    *    if request for new request was rejected by remote server
    * @throws IOException
    *    if any i/o errors occur
    * @throws VirtualFileSystemException
    *    if any error in VFS
    * @see BuilderClient#dependenciesCopy(org.exoplatform.ide.vfs.server.VirtualFileSystem, String, String)
    */
   @GET
   @Path("dependencies/copy")
   public Response dependenciesCopy(@QueryParam("projectid") String projectId, //
                                    @QueryParam("vfsid") String vfsId, //
                                    @QueryParam("classifier") String classifier, //
                                    @Context UriInfo uriInfo) throws BuilderException, IOException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = virtualFileSystemRegistry.getProvider(vfsId).newInstance(null, null);
      final String buildID = builder.dependenciesCopy(vfs, projectId, classifier);
      final URI location = uriInfo.getBaseUriBuilder().path(getClass(), "status").build(buildID);
      return Response.status(202).location(location).entity(location.toString()).build();
   }

   /**
    * Check current status of previously launched job.
    *
    * @param buildID
    *    ID of job
    * @return string that contains description of current status of build in JSON format. Do nothing with such string
    *         just re-send result to client
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    any other errors related to build server internal state or parameter of client request
    * @see BuilderClient#status(String)
    */
   @GET
   @Path("status/{buildid}")
   public String status(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.status(buildID);
   }

   /**
    * Cancel previously launched job.
    *
    * @param buildID
    *    ID of job
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    any other errors related to build server internal state or parameter of client request
    * @see BuilderClient#cancel(String)
    */
   @GET
   @Path("cancel/{buildid}")
   public void cancel(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      builder.cancel(buildID);
   }

   /**
    * Get job log.
    *
    * @param buildID
    *    ID of job
    * @return stream that contains job log
    * @throws IOException
    *    if any i/o errors occur
    * @throws BuilderException
    *    any other errors related to build server internal state or parameter of client request
    * @see BuilderClient#cancel(String)
    */
   @GET
   @Path("log/{buildid}")
   public InputStream log(@PathParam("buildid") String buildID) throws BuilderException, IOException
   {
      return builder.log(buildID);
   }
}
