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

/**
 * The RunInstances operation launches a specified number of instances.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RunInstanceRequest {
    /**
     * Get ID of AMI to run.
     *
     * @return ID of AMI to run
     */
    String getImageId();

    /**
     * Get type of instance. Must be one of the {@link org.exoplatform.ide.extension.aws.shared.ec2.Image#availableInstanceTypes()}
     *
     * @return instance type
     */
    String getInstanceType();

    /**
     * The number of instances to launch. If the value is more than
     * Amazon EC2 can launch, the largest possible number above minCount will
     * be launched instead. <p> Between 1 and the maximum number allowed for
     * your account (default: 20).
     *
     * @return The number of instances to launch. If the value is more than
     *         Amazon EC2 can launch, the largest possible number above minCount will
     *         be launched instead. <p> Between 1 and the maximum number allowed for
     *         your account (default: 20).
     */
    int getNumberOfInstances();

    /**
     * The name of the key pair.
     *
     * @return The name of the key pair.
     */
    String getKeyName();

    /**
     * Returns the value of the SecurityGroupIds property for this object.
     *
     * @return The value of the SecurityGroupIds property for this object.
     */
    JsonArray<String> getSecurityGroupsIds();

    /**
     * Specifies the placement constraints (Availability Zones) for launching
     * the instances.
     *
     * @return Specifies the placement constraints (Availability Zones) for launching
     *         the instances.
     */
    String getAvailabilityZone();
}
