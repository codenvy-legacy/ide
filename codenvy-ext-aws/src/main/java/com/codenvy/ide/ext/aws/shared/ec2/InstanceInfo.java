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
package com.codenvy.ide.ext.aws.shared.ec2;

import com.codenvy.ide.json.JsonArray;
import com.codenvy.ide.json.JsonStringMap;

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
