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
package org.exoplatform.ide.extension.aws.server.rest;

import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.server.Beanstalk;
import org.exoplatform.ide.extension.aws.shared.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.CreateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.CreateApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.CreateEnvironmentRequest;
import org.exoplatform.ide.extension.aws.shared.DeleteApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.SolutionStack;
import org.exoplatform.ide.extension.aws.shared.SolutionStackConfigurationOptionsRequest;
import org.exoplatform.ide.extension.aws.shared.UpdateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.UpdateApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.UpdateEnvironmentRequest;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: BeanstalkService.java Aug 23, 2012
 */
@Path("ide/aws/beanstalk")
public class BeanstalkService
{
   @javax.inject.Inject
   private VirtualFileSystemRegistry vfsRegistry;
   @javax.inject.Inject
   private Beanstalk beanstalk;

   public BeanstalkService()
   {
   }

   @Path("login")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void login(Map<String, String> credentials) throws AWSException
   {
      beanstalk.login(credentials.get("access_key"), credentials.get("secret_key"));
   }

   @Path("logout")
   @POST
   public void logout() throws AWSException
   {
      beanstalk.logout();
   }

   @Path("system/solution-stacks")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<SolutionStack> listAvailableSolutionStacks() throws AWSException
   {
      return beanstalk.listAvailableSolutionStacks();
   }

   @Path("system/solution-stacks/options")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<ConfigurationOptionInfo> listSolutionStackConfigurationOptions(
      SolutionStackConfigurationOptionsRequest params) throws AWSException
   {
      return beanstalk.listSolutionStackConfigurationOptions(params.getSolutionStackName());
   }

   @Path("apps/create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationInfo createApplication(@QueryParam("vfsid") String vfsId,
                                            @QueryParam("projectid") String projectId,
                                            CreateApplicationRequest params)
      throws AWSException, VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      String warURLStr = params.getWar();
      URL warURL = warURLStr == null || warURLStr.isEmpty() ? null : new URL(warURLStr);
      return beanstalk.createApplication(params.getApplicationName(), params.getDescription(), params.getS3Bucket(),
         params.getS3Key(), vfs, projectId, warURL);
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationInfo getApplicationInfo(@QueryParam("vfsid") String vfsId,
                                             @QueryParam("projectid") String projectId)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.getApplicationInfo(vfs, projectId);
   }

   @Path("apps/update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationInfo updateApplication(@QueryParam("vfsid") String vfsId,
                                            @QueryParam("projectid") String projectId,
                                            UpdateApplicationRequest params)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.updateApplication(vfs, projectId, params.getDescription());
   }

   @Path("apps/delete")
   @POST
   public void deleteApplication(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      beanstalk.deleteApplication(vfs, projectId);
   }

   @Path("apps")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<ApplicationInfo> listApplications() throws AWSException
   {
      return beanstalk.listApplications();
   }

   @Path("apps/versions/create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationVersionInfo createApplicationVersion(@QueryParam("vfsid") String vfsId,
                                                          @QueryParam("projectid") String projectId,
                                                          CreateApplicationVersionRequest params)
      throws AWSException, VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      String warURLStr = params.getWar();
      URL warURL = warURLStr == null || warURLStr.isEmpty() ? null : new URL(warURLStr);
      return beanstalk.createApplicationVersion(params.getS3Bucket(), params.getS3Key(),
         params.getVersionLabel(), params.getDescription(), vfs, projectId, warURL);
   }

   @Path("apps/versions/update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationVersionInfo updateApplicationVersion(@QueryParam("vfsid") String vfsId,
                                                          @QueryParam("projectid") String projectId,
                                                          UpdateApplicationVersionRequest params)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.updateApplicationVersion(params.getVersionLabel(), params.getDescription(), vfs, projectId);
   }

   @Path("apps/versions/delete")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void deleteApplicationVersion(@QueryParam("vfsid") String vfsId,
                                        @QueryParam("projectid") String projectId,
                                        DeleteApplicationVersionRequest params)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      beanstalk.deleteApplicationVersion(params.getVersionLabel(), params.isDeleteS3Bundle(), vfs, projectId);
   }

   @Path("apps/versions")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<ApplicationVersionInfo> listApplicationVersions(@QueryParam("vfsid") String vfsId,
                                                               @QueryParam("projectid") String projectId)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.listApplicationVersions(vfs, projectId);
   }

   @Path("environments/create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public EnvironmentInfo createApplicationEnvironment(@QueryParam("vfsid") String vfsId,
                                                       @QueryParam("projectid") String projectId,
                                                       CreateEnvironmentRequest params)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.createApplicationEnvironment(params.getEnvironmentName(), params.getSolutionStackName(),
         params.getTemplateName(), params.getVersionLabel(), params.getDescription(), vfs, projectId, params.getOptions());
   }

   @Path("environments/info/{id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public EnvironmentInfo getEnvironmentInfo(@PathParam("id") String id) throws AWSException
   {
      return beanstalk.getEnvironmentInfo(id);
   }

   @Path("environments/update/{id}")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public EnvironmentInfo updateEnvironment(@PathParam("id") String id, UpdateEnvironmentRequest params)
      throws AWSException
   {
      return beanstalk.updateEnvironment(id, params.getDescription(), params.getVersionLabel(),
         params.getTemplateName(), params.getOptions());
   }

   @Path("environments/stop/{id}")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public EnvironmentInfo stopEnvironment(@PathParam("id") String id) throws AWSException
   {
      return beanstalk.stopEnvironment(id);
   }

   @Path("environments")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<EnvironmentInfo> listApplicationEnvironments(@QueryParam("vfsid") String vfsId,
                                                            @QueryParam("projectid") String projectId)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.listApplicationEnvironments(vfs, projectId);
   }
}
