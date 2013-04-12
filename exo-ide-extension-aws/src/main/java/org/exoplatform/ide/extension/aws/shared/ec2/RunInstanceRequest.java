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
package org.exoplatform.ide.extension.aws.shared.ec2;

import java.util.List;

/**
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
     * Set ID of AMI to run.
     *
     * @param imageId
     *         ID of AMI to run
     */
    void setImageId(String imageId);

    /**
     * Get type of instance. Must be one of the {@link org.exoplatform.ide.extension.aws.shared.ec2.Image#availableInstanceTypes()}
     *
     * @return instance type
     */
    String getInstanceType();

    /**
     * Set type of instance. Must be one of the {@link org.exoplatform.ide.extension.aws.shared.ec2.Image#availableInstanceTypes()}
     *
     * @param instanceType
     *         instance type
     */
    void setInstanceType(String instanceType);

    int getNumberOfInstances();

    void setNumberOfInstances(int numberOfInstances);

    String getKeyName();

    void setKeyName(String keyName);

    List<String> getSecurityGroupsIds();

    void setSecurityGroupsIds(List<String> securityGroupsIds);

    String getAvailabilityZone();

    void setAvailabilityZone(String availabilityZone);
}
