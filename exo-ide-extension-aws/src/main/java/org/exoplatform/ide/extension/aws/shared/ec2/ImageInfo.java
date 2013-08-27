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

import java.util.Map;

/**
 * Information about specific Amazon machine image
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ImageInfo {
    /**
     * Get unique ID of the AMI.
     *
     * @return Amazon machine image ID
     */
    String getAmiId();

    /**
     * Set unique ID for the AMI
     *
     * @param amiId
     *         Amazon machine image ID
     */
    void setAmiId(String amiId);

    /**
     * Get the location of the AMI.
     *
     * @return the location of the AMI
     */
    String getManifest();

    /**
     * Set the location for the AMI.
     *
     * @param manifest
     *         the location of the AMI
     */
    void setManifest(String manifest);

    /**
     * Get current state of the AMI.
     *
     * @return state of the AMI
     */
    ImageState getState();

    /**
     * Set current state of the AMI
     *
     * @param state
     *         state of the AMI, valid values: available, deregistered
     */
    void setState(ImageState state);

    /**
     * Get EC2 machine image owner ID
     *
     * @return ID of the machine image owner
     */
    String getOwnerId();

    /**
     * Set EC2 machine image owner ID
     *
     * @param ownerId
     *         ID of the machine image owner
     */
    void setOwnerId(String ownerId);

    /**
     * Get the AWS account alias (e.g., "amazon", "redhat", "self", etc.) or AWS account ID that owns the AMI.
     *
     * @return account alias or account ID that owns the AMI
     */
    String getOwnerAlias();

    /**
     * Set the AWS account alias or AWS account ID that owns the AMI
     *
     * @param ownerAlias
     *         account alias or account ID that owns the AMI
     */
    void setOwnerAlias(String ownerAlias);

    /**
     * Get tags for the EC2 machine image
     *
     * @return list of tags contains tag key and value
     */
    Map<String, String> getTags();

    /**
     * Set tags for the EC2 machine image
     *
     * @param tags
     *         list of tags contains tag key and value
     */
    void setTags(Map<String, String> tags);
}
