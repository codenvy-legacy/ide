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
package com.codenvy.ide.ext.aws.shared.s3;

import com.codenvy.ide.dto.DTO;

/**
 * Describe information about specified S3 key version.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
@DTO
public interface S3ObjectVersion {
    /**
     * Get S3 bucket name
     *
     * @return name of the bucket
     */
    String getS3Bucket();

    /**
     * Get S3 key name
     *
     * @return name of the key where object is stored
     */
    String getS3Key();

    /**
     * Get version ID of current S3 key
     *
     * @return ID of version
     */
    String getVersionId();

    /**
     * Get S3 key owner
     *
     * @return owner of the S3 key
     */
    S3Owner getOwner();

    /**
     * Get last modified date
     *
     * @return timestamp of the last modified date
     */
    Long getLastModifiedDate();

    /**
     * Get size of the S3 key
     *
     * @return size in bytes
     */
    Long getSize();
}
