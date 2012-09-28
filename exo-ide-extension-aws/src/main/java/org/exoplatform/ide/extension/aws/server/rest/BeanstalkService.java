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
import org.exoplatform.ide.extension.aws.server.beanstalk.Beanstalk;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ApplicationVersionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ConfigurationTemplateInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateConfigurationTemplateRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.CreateEnvironmentRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.DeleteApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.DeleteConfigurationTemplateRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EnvironmentInfo;
import org.exoplatform.ide.extension.aws.shared.beanstalk.EventsList;
import org.exoplatform.ide.extension.aws.shared.beanstalk.InstanceLog;
import org.exoplatform.ide.extension.aws.shared.beanstalk.ListEventsRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStack;
import org.exoplatform.ide.extension.aws.shared.beanstalk.SolutionStackConfigurationOptionsRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateApplicationRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateApplicationVersionRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateConfigurationTemplateRequest;
import org.exoplatform.ide.extension.aws.shared.beanstalk.UpdateEnvironmentRequest;
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

   //

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

   //

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

   @Path("apps/events")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public EventsList listApplicationEvents(@QueryParam("vfsid") String vfsId,
                                           @QueryParam("projectid") String projectId,
                                           ListEventsRequest params) throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.listApplicationEvents(params.getVersionLabel(), params.getTemplateName(),
         params.getEnvironmentId(), params.getSeverity(), params.getStartTime(), params.getEndTime(),
         params.getMaxRecords(), params.getNextToken(), vfs, projectId);
   }

   @Path("apps")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<ApplicationInfo> listApplications() throws AWSException
   {
      return beanstalk.listApplications();
   }

   //

   @Path("apps/template/create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public ConfigurationTemplateInfo createConfigurationTemplate(@QueryParam("vfsid") String vfsId,
                                                                @QueryParam("projectid") String projectId,
                                                                CreateConfigurationTemplateRequest params)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.createConfigurationTemplate(params.getTemplateName(), params.getSolutionStackName(),
         params.getSourceApplicationName(), params.getSourceTemplateName(), params.getEnvironmentId(),
         params.getDescription(), params.getOptions(), vfs, projectId);
   }

   @Path("apps/template/update")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public ConfigurationTemplateInfo updateConfigurationTemplate(@QueryParam("vfsid") String vfsId,
                                                                @QueryParam("projectid") String projectId,
                                                                UpdateConfigurationTemplateRequest params)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.updateConfigurationTemplate(params.getTemplateName(), params.getDescription(), vfs, projectId);
   }

   @Path("apps/template/delete")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   public void deleteConfigurationTemplate(@QueryParam("vfsid") String vfsId,
                                           @QueryParam("projectid") String projectId,
                                           DeleteConfigurationTemplateRequest params)
      throws AWSException, VirtualFileSystemException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      beanstalk.deleteConfigurationTemplate(params.getTemplateName(), vfs, projectId);
   }

   //

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

   //

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

   @Path("environments/rebuild/{id}")
   @GET
   public void rebuildEnvironment(@PathParam("id") String id) throws AWSException
   {
      beanstalk.rebuildEnvironment(id);
   }

   @Path("environments/stop/{id}")
   @GET
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

   //

   @Path("server/restart/{id}")
   @GET
   public void restartApplicationServer(@PathParam("id") String id) throws AWSException
   {
      beanstalk.restartApplicationServer(id);
   }

   @Path("environments/logs/{id}")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public List<InstanceLog> getEnvironmentLogs(@PathParam("id") String environmentId) throws AWSException
   {
      return beanstalk.getEnvironmentLogs(environmentId);
   }
}
