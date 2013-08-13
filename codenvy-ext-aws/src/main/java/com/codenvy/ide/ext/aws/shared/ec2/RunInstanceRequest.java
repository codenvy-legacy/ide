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
