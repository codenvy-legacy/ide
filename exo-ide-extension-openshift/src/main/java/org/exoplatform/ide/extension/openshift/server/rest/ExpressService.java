/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.extension.openshift.server.rest;

import org.exoplatform.ide.extension.openshift.server.Express;
import org.exoplatform.ide.extension.openshift.server.ExpressException;
import org.exoplatform.ide.extension.openshift.server.ParsingResponseException;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/openshift/express")
public class ExpressService
{
   @Inject
   private Express express;
   @Inject
   private LocalPathResolver localPathResolver;
   @Inject
   private VirtualFileSystemRegistry vfsRegistry;
   @QueryParam("vfsid")
   private String vfsId;
   @QueryParam("projectid")
   private String projectId;
   @QueryParam("name")
   private String appName;

   @POST
   @Path("login")
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws ExpressException, IOException, VirtualFileSystemException
   {
      express.login(credentials.get("rhlogin"), credentials.get("password"));
   }

   @POST
   @Path("logout")
   public void logout() throws IOException, VirtualFileSystemException
   {
      express.logout();
   }

   @POST
   @Path("domain/create")
   public void createDomain(@QueryParam("namespace") String namespace, @QueryParam("alter") boolean alter)
      throws ExpressException, IOException, VirtualFileSystemException
   {
      express.createDomain(namespace, alter);
   }

   @POST
   @Path("apps/create")
   @Produces(MediaType.APPLICATION_JSON)
   public AppInfo createApplication(@QueryParam("type") String type) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      return express.createApplication(appName, type,
         (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);
   }

   @GET
   @Path("apps/type")
   @Produces(MediaType.APPLICATION_JSON)
   public Set<String> applicationTypes()
   {
      return Express.APP_TYPES;
   }

   @GET
   @Path("apps/info")
   @Produces(MediaType.APPLICATION_JSON)
   public AppInfo applicationInfo() throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      return express.applicationInfo(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId))
         : null);
   }

   @POST
   @Path("apps/destroy")
   public void destroyApplication() throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null);
      express.destroyApplication(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId))
         : null);
   }

   @GET
   @Path("user/info")
   @Produces(MediaType.APPLICATION_JSON)
   public RHUserInfo userInfo(@QueryParam("appsinfo") boolean appsInfo) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      return express.userInfo(appsInfo);
   }
}
