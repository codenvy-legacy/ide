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
package org.exoplatform.ide.extension.aws.server.ec2;

import com.amazonaws.AmazonClientException;
import com.amazonaws.services.ec2.AmazonEC2;
import com.amazonaws.services.ec2.model.*;

import org.exoplatform.ide.extension.aws.server.AWSClient;
import org.exoplatform.ide.extension.aws.server.AWSException;
import org.exoplatform.ide.extension.aws.shared.ec2.*;
import org.exoplatform.ide.extension.aws.shared.ec2.KeyPairInfo;
import org.exoplatform.ide.security.paas.CredentialStore;
import org.exoplatform.ide.security.paas.CredentialStoreException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EC2 extends AWSClient {
    public EC2(CredentialStore credentialStore) {
        super(credentialStore);
    }

    /**
     * Get available AMIs.
     *
     * @param owner
     *         owner of AMI. May be specified ID of amazon user or 'self' to get own images. If this parameter is not
     *         specified 'amazon' assumed
     * @param isPublic
     *         if <code>true</code> get public images and private otherwise
     * @param architecture
     *         image architecture i386|x86_64. If not specified get images of both architectures
     * @param skipCount
     *         how may items skip in original result. Must be equals or greater then 0
     * @param maxItems
     *         how may items include result. If -1 then no limit of max images in result set
     * @return list of available images
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     * @throws IllegalArgumentException
     *         if skipCount parameter is negative or greater then total number of images
     */
    public ImagesList listImages(String owner,
                                 boolean isPublic,
                                 Architecture architecture,
                                 int skipCount,
                                 int maxItems) throws AWSException, CredentialStoreException {
        try {
            return listImages(getEC2Client(), owner, isPublic, architecture, skipCount, maxItems);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private ImagesList listImages(AmazonEC2 ec2Client,
                                  String owner,
                                  boolean isPublic,
                                  Architecture architecture,
                                  int skipCount,
                                  int maxItems) throws AWSException {
        if (skipCount < 0) {
            throw new IllegalArgumentException("'skipCount' parameter may not be negative. ");
        }
        if (owner == null) {
            owner = "amazon";
        }
        DescribeImagesRequest request = new DescribeImagesRequest()
                .withOwners(owner)
                .withFilters(new Filter().withName("is-public").withValues(Boolean.toString(isPublic)),
                             new Filter().withName("image-type").withValues("machine"));
        if (architecture != null) {
            request.withFilters(new Filter().withName("architecture").withValues(architecture.toString()));
        }
        List<Image> ec2ImagesList = ec2Client.describeImages(request).getImages();
        final int totalNumber = ec2ImagesList.size();
        Iterator<Image> ec2ImagesIterator = ec2ImagesList.iterator();
        try {
            if (skipCount > 0) {
                int skip = skipCount;
                while (skip-- > 0) {
                    ec2ImagesIterator.next();
                }
            }
        } catch (NoSuchElementException nse) {
            throw new IllegalArgumentException("'skipCount' parameter: '"
                                               + skipCount + "' is greater then total number of images. ");
        }

        List<ImageInfo> images = new ArrayList<ImageInfo>();
        for (int count = 0; ec2ImagesIterator.hasNext() && (maxItems < 0 || count < maxItems); count++) {
            Image ec2Image = ec2ImagesIterator.next();
            images.add(new ImageInfoImpl.Builder()
                               .amiId(ec2Image.getImageId())
                               .manifest(ec2Image.getImageLocation())
                               .state(ec2Image.getState())
                               .ownerId(ec2Image.getOwnerId())
                               .ownerAlias(ec2Image.getImageOwnerAlias())
                               .tags(ec2Image.getTags()).build());
        }

        ImagesList imagesList = new ImagesListImpl();
        imagesList.setImages(images);
        imagesList.setTotal(totalNumber);
        imagesList.setHasMore(ec2ImagesIterator.hasNext());
        if (ec2ImagesIterator.hasNext()) {
            imagesList.setNextSkip(skipCount + images.size());
            imagesList.setMaxItems(maxItems);
        }
        return imagesList;
    }

    //

    /**
     * Return description of all key pairs available for current authorized user.
     * EC2 key pairs used to launch and access EC2 instances.
     *
     * @return list with objects containing key name and key fingerprint
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<KeyPairInfo> listKeyPairs() throws AWSException, CredentialStoreException {
        try {
            return listKeyPairs(getEC2Client());
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<KeyPairInfo> listKeyPairs(AmazonEC2 ec2Client) throws AWSException {
        List<com.amazonaws.services.ec2.model.KeyPairInfo> ec2KeyPair = ec2Client.describeKeyPairs().getKeyPairs();
        List<KeyPairInfo> keyPair = new ArrayList<KeyPairInfo>(ec2KeyPair.size());
        for (com.amazonaws.services.ec2.model.KeyPairInfo ec2KeyPairInfo : ec2KeyPair) {
            keyPair.add(new KeyPairInfoImpl(ec2KeyPairInfo.getKeyName(), ec2KeyPairInfo.getKeyFingerprint()));
        }
        return keyPair;
    }

    //

    /**
     * Return description about security groups available for current authorized user.
     *
     * @return list with objects containing description about AWS security group
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<SecurityGroupInfo> listSecurityGroups() throws AWSException, CredentialStoreException {
        try {
            return listSecurityGroups(getEC2Client());
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<SecurityGroupInfo> listSecurityGroups(AmazonEC2 ec2Client) throws AWSException {
        List<SecurityGroup> ec2SecurityGroups = ec2Client.describeSecurityGroups().getSecurityGroups();
        List<SecurityGroupInfo> securityGroup = new ArrayList<SecurityGroupInfo>(ec2SecurityGroups.size());
        for (SecurityGroup ec2SecurityGroup : ec2SecurityGroups) {
            securityGroup.add(new SecurityGroupInfoImpl(ec2SecurityGroup.getGroupId(), ec2SecurityGroup.getGroupName(),
                                                        ec2SecurityGroup.getOwnerId(), ec2SecurityGroup.getDescription()));
        }
        return securityGroup;
    }

    //

    /**
     * Return description about regions zones that are currently available to the account.
     *
     * @return list of described EC2 regions available for account
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<RegionInfo> listRegions() throws AWSException, CredentialStoreException {
        try {
            return listRegions(getEC2Client());
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<RegionInfo> listRegions(AmazonEC2 ec2Client) throws AWSException {
        List<Region> ec2Regions = ec2Client.describeRegions().getRegions();
        List<RegionInfo> regions = new ArrayList<RegionInfo>(ec2Regions.size());
        for (Region region : ec2Regions) {
            regions.add(new RegionInfoImpl(region.getRegionName(), region.getEndpoint()));
        }
        return regions;
    }

    //

    /**
     * Return information about EC2 availability zones that are currently available to the account.
     * The results include zones only for the Region you're currently using
     *
     * @return list of described EC2 availability zones
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<String> listAvailabilityZones() throws AWSException, CredentialStoreException {
        try {
            return listAvailabilityZones(getEC2Client());
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<String> listAvailabilityZones(AmazonEC2 ec2Client) throws AWSException {
        List<AvailabilityZone> ec2AvailabilityZones = ec2Client.describeAvailabilityZones().getAvailabilityZones();
        List<String> availabilityZones = new ArrayList<String>(ec2AvailabilityZones.size());
        for (AvailabilityZone ec2AvailabilityZone : ec2AvailabilityZones) {
            availabilityZones.add(ec2AvailabilityZone.getZoneName());
        }
        return availabilityZones;
    }

    //

    /**
     * Launches the specified number of instances. Every instances is launched in a security group.
     * If security group is not defined default security group is used.
     * Launching public images without a key pair ID will leave them inaccessible.
     *
     * @param imageId
     *         unique ID of machine image
     * @param instanceType
     *         specifies the instance type for the launched instances.
     *         valid values: t1.micro | m1.small | m1.medium | m1.large | m1.xlarge | m2.xlarge | m2.2xlarge
     *         | m2.4xlarge | c1.medium | c1.xlarge | hi1.4xlarge | cc1.4xlarge | cc2.8xlarge | cg1.4xlarge
     * @param numberOfInstances
     *         number of instances to launch, must be greater 0
     * @param keyName
     *         the name of the key pair to use
     * @param securityGroupsIds
     *         list of security groups into which instances will be launched
     * @param availabilityZone
     *         availability zone into which instances wil be launched
     * @return list containing unique ID of launched instances
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<String> runInstance(String imageId,
                                    String instanceType,
                                    int numberOfInstances,
                                    String keyName,
                                    List<String> securityGroupsIds,
                                    String availabilityZone) throws AWSException, CredentialStoreException {
        try {
            return runInstance(
                    getEC2Client(),
                    imageId,
                    instanceType,
                    numberOfInstances,
                    keyName,
                    securityGroupsIds,
                    availabilityZone
                              );
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<String> runInstance(AmazonEC2 ec2Client,
                                     String imageId,
                                     String instanceType,
                                     int numberOfInstances,
                                     String keyName,
                                     List<String> securityGroupsIds,
                                     String availabilityZone) {
        RunInstancesResult result = ec2Client.runInstances(new RunInstancesRequest()
                                                                   .withImageId(imageId)
                                                                   .withInstanceType(instanceType)
                                                                   .withMinCount(numberOfInstances)
                                                                   .withMaxCount(numberOfInstances)
                                                                   .withKeyName(keyName)
                                                                   .withSecurityGroupIds(securityGroupsIds)
                                                                   .withPlacement(new Placement().withAvailabilityZone(availabilityZone)));

        List<Instance> awsInstances = result.getReservation().getInstances();
        List<String> instances = new ArrayList<String>(awsInstances.size());

        for (Instance instance : awsInstances) {
            instances.add(instance.getInstanceId());
        }

        return instances;
    }

    /**
     * Stops instance that uses an Amazon EBS volumes as its root device.
     *
     * @param instanceId
     *         unique ID of instance
     * @param force
     *         forces the instance to stop
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void stopInstance(String instanceId, boolean force) throws AWSException, CredentialStoreException {
        try {
            stopInstance(getEC2Client(), instanceId, force);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void stopInstance(AmazonEC2 ec2Client, String instanceId, boolean force) {
        ec2Client.stopInstances(new StopInstancesRequest().withInstanceIds(instanceId).withForce(force));
    }

    //

    /**
     * Starts instance that uses an Amazon EBS volume as its root device.
     *
     * @param instanceId
     *         unique ID of instance
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void startInstance(String instanceId) throws AWSException, CredentialStoreException {
        try {
            startInstance(getEC2Client(), instanceId);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void startInstance(AmazonEC2 ec2Client, String instanceId) {
        ec2Client.startInstances(new StartInstancesRequest().withInstanceIds(instanceId));
    }

    //

    /**
     * Request reboot of specified instance. This operation is asynchronous; it only queues a request to
     * reboot the specified instance. The operation will succeed if the instance are valid and belong to
     * the user. Requests to reboot terminated instances are ignored.
     *
     * @param instanceId
     *         unique ID of instance
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void rebootInstance(String instanceId) throws AWSException, CredentialStoreException {
        try {
            rebootInstance(getEC2Client(), instanceId);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void rebootInstance(AmazonEC2 ec2Client, String instanceId) {
        ec2Client.rebootInstances(new RebootInstancesRequest().withInstanceIds(instanceId));
    }

    //

    /**
     * Shuts down instance. Terminated instances will remain visible after termination
     * (approximately one hour).
     *
     * @param instanceId
     *         unique ID of instance
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public void terminateInstance(String instanceId) throws AWSException, CredentialStoreException {
        try {
            terminateInstance(getEC2Client(), instanceId);
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private void terminateInstance(AmazonEC2 ec2Client, String instanceId) {
        ec2Client.terminateInstances(new TerminateInstancesRequest().withInstanceIds(instanceId));
    }

    //

    /**
     * Returns information about instances that authorized user owns.
     *
     * @return list of objects which contains various information about instance
     * @throws org.exoplatform.ide.extension.aws.server.AWSException
     *         if any error occurs when make request to Amazon API
     */
    public List<InstanceInfo> getInstances() throws AWSException, CredentialStoreException {
        try {
            return getInstances(getEC2Client());
        } catch (AmazonClientException e) {
            throw new AWSException(e);
        }
    }

    private List<InstanceInfo> getInstances(AmazonEC2 ec2Client) {
        List<Reservation> reservations = ec2Client.describeInstances(new DescribeInstancesRequest()).getReservations();
        List<InstanceInfo> instances = new ArrayList<InstanceInfo>();
        for (Reservation reservation : reservations) {
            for (Instance awsInstance : reservation.getInstances()) {
                instances.add(new InstanceInfoImpl.Builder()
                                      .id(awsInstance.getInstanceId())
                                      .publicDNSName(awsInstance.getPublicDnsName())
                                      .imageId(awsInstance.getImageId())
                                      .rootDeviceType(awsInstance.getRootDeviceType())
                                      .state(awsInstance.getState().getName())
                                      .imageType(awsInstance.getInstanceType())
                                      .availabilityZone(awsInstance.getPlacement())
                                      .keyName(awsInstance.getKeyName())
                                      .launchTime(awsInstance.getLaunchTime())
                                      .securityGroupsNames(reservation.getGroupNames())
                                      .tags(awsInstance.getTags())
                                      .build());
            }
        }
        return instances;
    }
}
