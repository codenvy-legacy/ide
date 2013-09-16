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

/**
 * Describes AWS security group.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface SecurityGroupInfo {
    /**
     * Get AWS Access Key ID of the owner of the security group.
     *
     * @return ID of the owner of the security group
     */
    String getOwnerId();

    /**
     * Set AWS Access Key ID of the owner of the security group.
     *
     * @param ownerId
     *         ID of the owner of the security group
     */
    void setOwnerId(String ownerId);

    /**
     * Get name of this security group.
     *
     * @return name of this security group
     */
    String getName();

    /**
     * Set name of this security group.
     *
     * @param name
     *         name of this security group
     */
    void setName(String name);

    /**
     * Get ID of this security group.
     *
     * @return ID of this security group
     */
    String getId();

    /**
     * Set ID of this security group.
     *
     * @param id
     *         ID of this security group
     */
    void setId(String id);

    /**
     * Get description of this security group.
     *
     * @return description of this security group
     */
    String getDescription();

    /**
     * Set description of this security group.
     *
     * @param description
     *         description of this security group
     */
    void setDescription(String description);
}
