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
import org.exoplatform.ide.extension.aws.shared.ec2.*;
import org.exoplatform.ide.security.paas.CredentialStoreException;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("{ws-name}/aws/ec2")
public class EC2Service {
    @Inject
    private EC2 ec2;

    public EC2Service() {
    }

    //

    @Path("login")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public void login(Map<String, String> credentials) throws AWSException, CredentialStoreException {
        ec2.login(credentials.get("access_key"), credentials.get("secret_key"));
    }

    @Path("logout")
    @POST
    public void logout() throws CredentialStoreException {
        ec2.logout();
    }

    //

    @Path("images")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public ImagesList listImages(@QueryParam("owner") String owner,
                                 @QueryParam("ispublic") boolean isPublic,
                                 @QueryParam("architecture") String architecture,
                                 @QueryParam("skipcount") int skipCount,
                                 @QueryParam("maxitems") int maxItems) throws AWSException, CredentialStoreException {
        Architecture arch = Architecture.fromValue(architecture);

        return ec2.listImages(owner, isPublic, arch, skipCount, maxItems);
    }

    @Path("key_pairs")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<KeyPairInfo> listKeyPairs() throws AWSException, CredentialStoreException {
        return ec2.listKeyPairs();
    }

    @Path("security_groups")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<SecurityGroupInfo> listSecurityGroups() throws AWSException, CredentialStoreException {
        return ec2.listSecurityGroups();
    }

    @Path("regions")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<RegionInfo> listRegions() throws AWSException, CredentialStoreException {
        return ec2.listRegions();
    }

    @Path("availability_zones")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> listAvailabilityZones() throws AWSException, CredentialStoreException {
        return ec2.listAvailabilityZones();
    }

    @Path("instances/run")
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> runInstance(RunInstanceRequest request) throws AWSException, CredentialStoreException {
        return ec2.runInstance(
                request.getImageId(),
                request.getInstanceType(),
                request.getNumberOfInstances(),
                request.getKeyName(),
                request.getSecurityGroupsIds(),
                request.getAvailabilityZone()
                              );
    }

    @Path("instances/start/{id}")
    @POST
    public void startInstance(@PathParam("id") String id) throws AWSException, CredentialStoreException {
        ec2.startInstance(id);
    }

    @Path("instances/stop/{id}")
    @POST
    public void stopInstance(@PathParam("id") String id, @QueryParam("force") Boolean force)
            throws AWSException, CredentialStoreException {
        ec2.stopInstance(id, force);
    }

    @Path("instances/reboot/{id}")
    @POST
    public void rebootInstance(@PathParam("id") String id) throws AWSException, CredentialStoreException {
        ec2.rebootInstance(id);
    }

    @Path("instances/terminate/{id}")
    @POST
    public void terminateInstance(@PathParam("id") String id) throws AWSException, CredentialStoreException {
        ec2.terminateInstance(id);
    }

    @Path("instances")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public List<InstanceInfo> getInstances() throws AWSException, CredentialStoreException {
        return ec2.getInstances();
    }
}
