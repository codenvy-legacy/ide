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
import org.exoplatform.ide.extension.aws.shared.beanstalk.*;
import org.exoplatform.ide.security.paas.CredentialStoreException;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.VirtualFileSystemRegistry;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;
import org.exoplatform.ide.vfs.shared.Project;
import org.exoplatform.ide.vfs.shared.PropertyFilter;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vparfonov@exoplatform.com">Vitaly Parfonov</a>
 * @version $Id: BeanstalkService.java Aug 23, 2012
 */
@Path("{ws-name}/aws/beanstalk")
public class BeanstalkService {
    private static final Log LOG = ExoLogger.getLogger(BeanstalkService.class);

    @javax.inject.Inject
    private VirtualFileSystemRegistry vfsRegistry;
    @javax.inject.Inject
    private Beanstalk                 beanstalk;

    public BeanstalkService() {
    }

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws AWSException, CredentialStoreException {
        beanstalk.login(credentials.get("access_key"), credentials.get("secret_key"));
    }

    @Path("logout")
    @POST
    public void logout() throws CredentialStoreException {
        beanstalk.logout();
    }

    //

    @Path("system/solution-stacks")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SolutionStack> listAvailableSolutionStacks() throws AWSException, CredentialStoreException {
        return beanstalk.listAvailableSolutionStacks();
    }

    @Path("system/solution-stacks/options")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<ConfigurationOptionInfo> listSolutionStackConfigurationOptions(
            SolutionStackConfigurationOptionsRequest params) throws AWSException, CredentialStoreException {
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
            throws AWSException, CredentialStoreException, VirtualFileSystemException, IOException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        String warURLStr = params.getWar();
        URL warURL = warURLStr == null || warURLStr.isEmpty() ? null : new URL(warURLStr);
        ApplicationInfo app =
                beanstalk.createApplication(params.getApplicationName(), params.getDescription(), params.getS3Bucket(),
                                            params.getS3Key(), vfs, projectId, warURL);

        if (projectId != null) {
            Project proj = (Project)vfs.getItem(projectId, false, PropertyFilter.ALL_FILTER);
            LOG.info("EVENT#application-created# PROJECT#" + proj.getName() + "# TYPE#" + proj.getProjectType()
                     + "# PAAS#AWS:BeansTalk#");
        }
        return app;
    }

    @Path("apps/info")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ApplicationInfo getApplicationInfo(@QueryParam("vfsid") String vfsId,
                                              @QueryParam("projectid") String projectId)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
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
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.updateApplication(vfs, projectId, params.getDescription());
    }

    @Path("apps/delete")
    @POST
    public void deleteApplication(@QueryParam("vfsid") String vfsId, @QueryParam("projectid") String projectId)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        beanstalk.deleteApplication(vfs, projectId);
    }

    @Path("apps/events")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EventsList listApplicationEvents(@QueryParam("vfsid") String vfsId,
                                            @QueryParam("projectid") String projectId,
                                            ListEventsRequest params)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.listApplicationEvents(params.getVersionLabel(), params.getTemplateName(),
                                               params.getEnvironmentId(), params.getSeverity(), params.getStartTime(), params.getEndTime(),
                                               params.getMaxRecords(), params.getNextToken(), vfs, projectId);
    }

    @Path("apps")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicationInfo> listApplications() throws AWSException, CredentialStoreException {
        return beanstalk.listApplications();
    }

    //

    @Path("apps/template/create")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Configuration createConfigurationTemplate(@QueryParam("vfsid") String vfsId,
                                                     @QueryParam("projectid") String projectId,
                                                     CreateConfigurationTemplateRequest params)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.createConfigurationTemplate(params.getTemplateName(), params.getSolutionStackName(),
                                                     params.getSourceApplicationName(), params.getSourceTemplateName(),
                                                     params.getEnvironmentId(),
                                                     params.getDescription(), params.getOptions(), vfs, projectId);
    }

    @Path("apps/template")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Configuration getConfigurationTemplate(@QueryParam("vfsid") String vfsId,
                                                  @QueryParam("projectid") String projectId,
                                                  ConfigurationRequest params)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.getConfigurationTemplate(params.getTemplateName(), vfs, projectId);
    }

    @Path("apps/template/update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Configuration updateConfigurationTemplate(@QueryParam("vfsid") String vfsId,
                                                     @QueryParam("projectid") String projectId,
                                                     UpdateConfigurationTemplateRequest params)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.updateConfigurationTemplate(params.getTemplateName(), params.getDescription(), vfs, projectId);
    }

    @Path("apps/template/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteConfigurationTemplate(@QueryParam("vfsid") String vfsId,
                                            @QueryParam("projectid") String projectId,
                                            DeleteConfigurationTemplateRequest params)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
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
            throws AWSException, CredentialStoreException, VirtualFileSystemException, IOException {
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
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.updateApplicationVersion(params.getVersionLabel(), params.getDescription(), vfs, projectId);
    }

    @Path("apps/versions/delete")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void deleteApplicationVersion(@QueryParam("vfsid") String vfsId,
                                         @QueryParam("projectid") String projectId,
                                         DeleteApplicationVersionRequest params)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        beanstalk.deleteApplicationVersion(params.getVersionLabel(), params.isDeleteS3Bundle(), vfs, projectId);
    }

    @Path("apps/versions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<ApplicationVersionInfo> listApplicationVersions(@QueryParam("vfsid") String vfsId,
                                                                @QueryParam("projectid") String projectId)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
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
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.createApplicationEnvironment(params.getEnvironmentName(), params.getSolutionStackName(),
                                                      params.getTemplateName(), params.getVersionLabel(), params.getDescription(), vfs,
                                                      projectId, params.getOptions());
    }

    @Path("environments/info/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EnvironmentInfo getEnvironmentInfo(@PathParam("id") String id) throws AWSException, CredentialStoreException {
        return beanstalk.getEnvironmentInfo(id);
    }

    @Path("environments/update/{id}")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public EnvironmentInfo updateEnvironment(@PathParam("id") String id, UpdateEnvironmentRequest params)
            throws AWSException, CredentialStoreException {
        return beanstalk.updateEnvironment(id, params.getDescription(), params.getVersionLabel(),
                                           params.getTemplateName(), params.getOptions());
    }

    @Path("environments/rebuild/{id}")
    @GET
    public void rebuildEnvironment(@PathParam("id") String id) throws AWSException, CredentialStoreException {
        beanstalk.rebuildEnvironment(id);
    }

    @Path("environments/stop/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public EnvironmentInfo stopEnvironment(@PathParam("id") String id) throws AWSException, CredentialStoreException {
        return beanstalk.stopEnvironment(id);
    }

    @Path("environments/configuration")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public List<Configuration> getEnvironmentConfigurations(@QueryParam("vfsid") String vfsId,
                                                            @QueryParam("projectid") String projectId,
                                                            ConfigurationRequest params)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.getEnvironmentConfigurations(params.getEnvironmentName(), vfs, projectId);
    }

    @Path("environments")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<EnvironmentInfo> listApplicationEnvironments(@QueryParam("vfsid") String vfsId,
                                                             @QueryParam("projectid") String projectId)
            throws AWSException, CredentialStoreException, VirtualFileSystemException {
        VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
        return beanstalk.listApplicationEnvironments(vfs, projectId);
    }

    //

    @Path("server/restart/{id}")
    @GET
    public void restartApplicationServer(@PathParam("id") String id) throws AWSException, CredentialStoreException {
        beanstalk.restartApplicationServer(id);
    }

    @Path("environments/logs/{id}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<InstanceLog> getEnvironmentLogs(@PathParam("id") String environmentId)
            throws AWSException, CredentialStoreException {
        return beanstalk.getEnvironmentLogs(environmentId);
    }
}
