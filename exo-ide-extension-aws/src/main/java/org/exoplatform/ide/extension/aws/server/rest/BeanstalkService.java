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
import org.exoplatform.ide.extension.aws.shared.ConfigurationOptionInfo;
import org.exoplatform.ide.extension.aws.shared.SolutionStack;
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
      @QueryParam("solution_stack") String solutionStackName) throws AWSException
   {
      return beanstalk.listSolutionStackConfigurationOptions(solutionStackName);
   }

   @Path("apps/create")
   @POST
   @Consumes(MediaType.APPLICATION_JSON)
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationInfo createApplication(Map<String, String> params)
      throws AWSException, VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(params.get("vfsid")).newInstance(null, null);
      String warURLStr = params.get("war");
      URL warURL = warURLStr == null || warURLStr.isEmpty() ? null : new URL(warURLStr);
      return beanstalk.createApplication(params.get("name"), params.get("description"), params.get("s3bucket"),
         params.get("s3key"), vfs, params.get("projectid"), warURL);
   }

   @Path("apps/info")
   @GET
   @Produces(MediaType.APPLICATION_JSON)
   public ApplicationInfo getApplicationInfo(@QueryParam("vfsid") String vfsId,
                                             @QueryParam("projectid") String projectId)
      throws AWSException, VirtualFileSystemException, IOException
   {
      VirtualFileSystem vfs = vfsRegistry.getProvider(vfsId).newInstance(null, null);
      return beanstalk.getApplicationInfo(vfs, projectId);
   }

   @Path("apps/delete")
   @POST
   public void deleteApplication(@QueryParam("vfsid") String vfsId,
                                 @QueryParam("projectid") String projectId)
      throws AWSException, VirtualFileSystemException, IOException
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
}
