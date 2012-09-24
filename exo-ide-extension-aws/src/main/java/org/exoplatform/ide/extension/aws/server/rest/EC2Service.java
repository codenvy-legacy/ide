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
package org.exoplatform.ide.extension.aws.server.rest;

import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.server.ec2.EC2;
import org.exoplatform.ide.extension.aws.shared.ec2.Architecture;
import org.exoplatform.ide.extension.aws.shared.ec2.InstanceStatusInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.ImagesList;
import org.exoplatform.ide.extension.aws.shared.ec2.KeyPairInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.RebootInstanceRequest;
import org.exoplatform.ide.extension.aws.shared.ec2.RegionInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.RunInstanceRequest;
import org.exoplatform.ide.extension.aws.shared.ec2.SecurityGroupInfo;
import org.exoplatform.ide.extension.aws.shared.ec2.StartInstanceRequest;
import org.exoplatform.ide.extension.aws.shared.ec2.StatusRequest;
import org.exoplatform.ide.extension.aws.shared.ec2.StopInstanceRequest;
import org.exoplatform.ide.extension.aws.shared.ec2.TerminateInstanceRequest;
import java.util.List;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("ide/aws/ec2")
public class EC2Service
{
   @Inject
   private EC2 ec2;

   public EC2Service()
   {
   }

   @Path("images")
   @GET
   public ImagesList listImages(@QueryParam("owner") String owner,
                                @QueryParam("ispublic") boolean isPublic,
                                @QueryParam("architecture") String architecture,
                                @QueryParam("skipcount") int skipCount,
                                @QueryParam("maxitems") int maxItems) throws AWSException
   {
      Architecture arch = Architecture.fromValue(architecture);

      return ec2.listImages(owner, isPublic, arch, skipCount, maxItems);
   }

   @Path("key_pairs")
   @GET
   public List<KeyPairInfo> listKeyPairs() throws AWSException
   {
      return ec2.listKeyPairs();
   }

   @Path("security_groups")
   @GET
   public List<SecurityGroupInfo> listSecurityGroups() throws AWSException
   {
      return ec2.listSecurityGroups();
   }

   @Path("regions")
   @GET
   public List<RegionInfo> listRegions() throws AWSException
   {
      return ec2.listRegions();
   }

   @Path("availability_zones")
   @GET
   public List<String> listAvailabilityZones() throws AWSException
   {
      return ec2.listAvailabilityZones();
   }

   @Path("instance/run")
   @POST
   public List<String> runInstance(RunInstanceRequest request) throws AWSException
   {
       return ec2.runInstance(
          request.getImageId(),
          request.getInstanceType(),
          request.getNumberOfInstances(),
          request.getKeyName(),
          request.getSecurityGroupsIds(),
          request.getAvailabilityZone()
       );
   }

   @Path("instance/start")
   @POST
   public void startInstance(StartInstanceRequest request) throws AWSException
   {
      ec2.startInstance(request.getInstanceIds());
   }

   @Path("instance/stop")
   @POST
   public void stopInstance(StopInstanceRequest request) throws AWSException
   {
      ec2.stopInstance(request.getForce(), request.getInstanceIds());
   }

   @Path("instance/reboot")
   @POST
   public void rebootInstance(RebootInstanceRequest request) throws AWSException
   {
      ec2.rebootInstance(request.getInstanceIds());
   }

   @Path("instance/terminate")
   @POST
   public void terminateInstance(TerminateInstanceRequest request) throws AWSException
   {
      ec2.terminateInstance(request.getInstanceIds());
   }

   @Path("instance/status")
   @GET
   public List<InstanceStatusInfo> getStatus(StatusRequest request) throws AWSException
   {
      return ec2.getStatus(
         request.getMaxResult(),
         request.getIncludeAllInstances(),
         request.getNextToken());
   }
//
//   @Path("instance/list")
//   @GET
//   public void getInstances() throws AWSException
//   {
//      //TODO get list of instances for this owner and return to client
//   }
}
