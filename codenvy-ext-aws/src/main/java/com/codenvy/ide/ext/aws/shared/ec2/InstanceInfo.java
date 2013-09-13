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
package com.codenvy.ide.ext.aws.shared.ec2;

import com.codenvy.ide.dto.DTO;
import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;

/**
 * Information about Amazon EC2 instance
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface InstanceInfo {
    /**
     * Get instance ID e.g. "i-7a00642e".
     *
     * @return EC2 instance id
     */
    String getId();

    /**
     * Get public DNS name assigned to the instance. It will remains empty until the instance enter a running state.
     *
     * @return public DNS name
     */
    String getPublicDNSName();

    /**
     * Get image ID of AMI used to launch the instances. e.g. "ami-1cd4924e"
     *
     * @return machine image ID
     */
    String getImageId();

    /**
     * Get root device type used by the AMI.
     *
     * @return string containing root device type
     */
    String getRootDeviceType();

    /**
     * Get state of the current instance.
     *
     * @return state of the current instance
     */
    InstanceState getState();

    /**
     * Get instance type.
     *
     * @return instance type
     */
    String getImageType();

    /**
     * Get placement where instance launched.
     *
     * @return location of the instance
     */
    String getAvailabilityZone();

    /**
     * Get key pair name.
     *
     * @return key pair name
     */
    String getKeyName();

    /**
     * Get timestamp when instance was launched.
     *
     * @return time in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    double getLaunchTime();

    /**
     * Get list of security groups into which instance is launched.
     *
     * @return list of security groups
     */
    JsonArray<String> getSetSecurityGroupsNames();

    /**
     * Get list of tags for this instance.
     *
     * @return map contains tags key and value
     */
    JsonStringMap<String> getTags();
}
