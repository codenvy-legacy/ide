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
