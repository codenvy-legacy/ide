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
package org.exoplatform.ide.extension.aws.shared.ec2;

import java.util.List;
import java.util.Map;

/**
 * Information about Amazon EC2 instance
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface InstanceInfo {
    /**
     * Get instance ID e.g. "i-7a00642e".
     *
     * @return EC2 instance id
     */
    String getId();

    /**
     * Set instance ID.
     *
     * @param instanceId
     *         EC2 instance id
     */
    void setId(String instanceId);

    /**
     * Get public DNS name assigned to the instance. It will remains empty until the instance enter a running state.
     *
     * @return public DNS name
     */
    String getPublicDNSName();

    /**
     * Set public DNS name assigned to the instance.
     *
     * @param publicDNSName
     *         public DNS name
     */
    void setPublicDNSName(String publicDNSName);

    /**
     * Get image ID of AMI used to launch the instances. e.g. "ami-1cd4924e"
     *
     * @return machine image ID
     */
    String getImageId();

    /**
     * Set image ID of AMI used to launch the instance.
     *
     * @param imageId
     *         machine image ID
     */
    void setImageId(String imageId);

    /**
     * Get root device type used by the AMI.
     *
     * @return string containing root device type
     */
    String getRootDeviceType();

    /**
     * Set root device type used by the AMI.
     *
     * @param rootDeviceType
     *         string containing root device type
     */
    void setRootDeviceType(String rootDeviceType);

    /**
     * Get state of the current instance.
     *
     * @return state of the current instance
     */
    InstanceState getState();

    /**
     * Set state of the current instance.
     *
     * @param instanceState
     *         state of the current instance
     */
    void setState(InstanceState instanceState);

    /**
     * Get instance type.
     *
     * @return instance type
     */
    String getImageType();

    /**
     * Set instance type.
     *
     * @param imageType
     *         instance type
     *         valid values: t1.micro, m1.small, m1.medium, m1.large, m1.xlarge, m2.xlarge, m2.2xlarge, m2.4xlarge,
     *         c1.medium, c1.xlarge, hi1.4xlarge, cc1.4xlarge, cc2.8xlarge, cg1.4xlarge
     */
    void setImageType(String imageType);

    /**
     * Get placement where instance launched.
     *
     * @return location of the instance
     */
    String getAvailabilityZone();

    /**
     * Set placement where instance launched.
     *
     * @param availabilityZone
     *         location of the instance
     */
    void setAvailabilityZone(String availabilityZone);

    /**
     * Get key pair name.
     *
     * @return key pair name
     */
    String getKeyName();

    /**
     * Set kay pair name.
     *
     * @param keyName
     *         key pair name
     */
    void setKeyName(String keyName);

    /**
     * Get timestamp when instance was launched.
     *
     * @return time in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    long getLaunchTime();

    /**
     * Set timestamp when instance was launched.
     *
     * @param launchTime
     *         time in millisecond since January 1, 1970, 00:00:00 GMT
     */
    void setLaunchTime(long launchTime);

    /**
     * Get list of security groups into which instance is launched.
     *
     * @return list of security groups
     */
    List<String> getSetSecurityGroupsNames();

    /**
     * Set list of security groups into which instance is launched.
     *
     * @param securityGroupsIds
     *         list of security groups
     */
    void setSecurityGroupsNames(List<String> securityGroupsIds);

    /**
     * Get list of tags for this instance.
     *
     * @return map contains tags key and value
     */
    Map<String, String> getTags();

    /**
     * Set list of tags for this instance.
     *
     * @param tags
     *         map contains tags key and value
     */
    void setTags(Map<String, String> tags);
}
