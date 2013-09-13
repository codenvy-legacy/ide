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
