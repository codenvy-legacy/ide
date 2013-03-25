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

import org.exoplatform.ide.commons.ParsingResponseException;
import org.exoplatform.ide.extension.openshift.server.Express;
import org.exoplatform.ide.extension.openshift.server.ExpressException;
import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;
import org.exoplatform.ide.extension.ssh.server.SshKeyStoreException;
import org.exoplatform.ide.vfs.server.LocalPathResolver;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.Property;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.ide.vfs.shared.PropertyImpl;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
   private static final Log LOG = ExoLogger.getLogger(ExpressService.class);

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
   public void login(Map<String, String> credentials) throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
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
      throws ExpressException, IOException, ParsingResponseException, VirtualFileSystemException, SshKeyStoreException
   {
      express.createDomain(namespace, alter);
   }

   @POST
   @Path("apps/create")
   @Produces(MediaType.APPLICATION_JSON)
   public AppInfo createApplication(@QueryParam("type") String type) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      AppInfo application =
         express.createApplication(appName, type,
            (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId)) : null);

      if (projectId != null)
      {
         Project proj = (Project)vfs.getItem(projectId, PropertyFilter.ALL_FILTER);
         LOG.info("EVENT#application-created# PROJECT#" + proj.getName() + "# TYPE#" + proj.getProjectType()
            + "# PAAS#OpenShift#");
      }
      return application;
   }

   @GET
   @Path("apps/type")
   @Produces(MediaType.APPLICATION_JSON)
   public Set<String> applicationTypes() throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      return express.frameworks();
   }

   @GET
   @Path("apps/info")
   @Produces(MediaType.APPLICATION_JSON)
   public AppInfo applicationInfo() throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return express.applicationInfo(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId))
         : null);
   }

   @POST
   @Path("apps/destroy")
   public void destroyApplication() throws ExpressException, IOException, ParsingResponseException,
      VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      express.destroyApplication(appName, (projectId != null) ? new File(localPathResolver.resolve(vfs, projectId))
         : null);

      if (projectId != null)
      {
         // Update VFS properties. Need it to uniform client.
         Property p = new PropertyImpl("openshift-express-application", Collections.<String>emptyList());
         List<Property> properties = new ArrayList<Property>(1);
         properties.add(p);
         vfs.updateItem(projectId, properties, null);
      }
   }

   @GET
   @Path("user/info")
   @Produces(MediaType.APPLICATION_JSON)
   public RHUserInfo userInfo(@QueryParam("appsinfo") boolean appsInfo) throws ExpressException, IOException,
      ParsingResponseException, VirtualFileSystemException
   {
      return express.userInfo(appsInfo);
   }

   @POST
   @Path("apps/stop")
   public void stopApplication(@QueryParam("appname") String appName)
      throws IOException, ExpressException, VirtualFileSystemException
   {
      express.stopApplication(appName);
   }

   @POST
   @Path("apps/start")
   public void startApplication(@QueryParam("appname") String appName)
      throws IOException, ExpressException, VirtualFileSystemException
   {
      express.startApplication(appName);
   }

   @POST
   @Path("apps/restart")
   public void restartApplication(@QueryParam("appname") String appName)
      throws IOException, ExpressException, VirtualFileSystemException
   {
      express.restartApplication(appName);
   }

   @GET
   @Path("apps/health")
   public String getApplicationHealth(@QueryParam("appname") String appName)
      throws IOException, ExpressException, VirtualFileSystemException
   {
      return express.getApplicationHealth(appName);
   }
}
