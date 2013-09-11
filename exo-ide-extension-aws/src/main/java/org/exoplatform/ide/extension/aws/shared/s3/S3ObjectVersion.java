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
package org.exoplatform.ide.extension.aws.shared.s3;

/**
 * Describe information about specified S3 key version.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface S3ObjectVersion {
    /**
     * Get S3 bucket name
     *
     * @return name of the bucket
     */
    String getS3Bucket();

    /**
     * Set S3 bucket name
     *
     * @param s3Bucket
     *         name of the bucket
     */
    void setS3Bucket(String s3Bucket);

    /**
     * Get S3 key name
     *
     * @return name of the key where object is stored
     */
    String getS3Key();

    /**
     * Set S3 key name
     *
     * @param s3Key
     *         name of the key where object is stored
     */
    void setS3Key(String s3Key);

    /**
     * Get version ID of current S3 key
     *
     * @return ID of version
     */
    String getVersionId();

    /**
     * Set version ID for current S3 key
     *
     * @param versionId
     *         ID of version
     */
    void setVersionId(String versionId);

    /**
     * Get S3 key owner
     *
     * @return owner of the S3 key
     */
    S3Owner getOwner();

    /**
     * Set S3 key owner
     *
     * @param owner
     *         owner of the S3 key
     */
    void setOwner(S3Owner owner);

    /**
     * Get last modified date
     *
     * @return timestamp of the last modified date
     */
    long getLastModifiedDate();

    /**
     * Set last modified date
     *
     * @param lastModifiedDate
     *         timestamp of the last modified date
     */
    void setLastModifiedDate(long lastModifiedDate);

    /**
     * Get size of the S3 key
     *
     * @return size in bytes
     */
    long getSize();

    /**
     * Set size of the S3 key
     *
     * @param size
     *         size in bytes
     */
    void setSize(long size);
}
