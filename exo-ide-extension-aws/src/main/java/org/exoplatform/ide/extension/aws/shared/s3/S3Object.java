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
 * Information about S3 object
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface S3Object {
    /**
     * Get name of S3 bucket where object stored.
     *
     * @return name of S3 bucket
     */
    String getS3Bucket();

    /**
     * Set name of S3 bucket where object stored.
     *
     * @param s3Bucket
     *         name of S3 bucket
     */
    void setS3Bucket(String s3Bucket);

    /**
     * Get S3 key under which this object is stored.
     *
     * @return S3 key under which this object is stored
     */
    String getS3Key();

    /**
     * Set S3 key under which this object is stored.
     *
     * @param s3Key
     *         S3 key under which this object is stored
     */
    void setS3Key(String s3Key);

    /**
     * Get hex encoded MD5 hash of this object's contents, as computed by Amazon S3.
     *
     * @return hex encoded MD5 hash of this object's contents
     */
    String getETag();

    /**
     * Set hex encoded MD5 hash of this object's contents, as computed by Amazon S3.
     *
     * @param eTag
     *         hex encoded MD5 hash of this object's contents
     */
    void setETag(String eTag);

    /**
     * Get size of object in bytes.
     *
     * @return size of object in bytes
     */
    long getSize();

    /**
     * Set size of object in bytes.
     *
     * @param size
     *         size of object in bytes
     */
    void setSize(long size);

    /**
     * Get time when this object was last modified.
     *
     * @return time when this object was last modified
     */
    long getUpdated();

    /**
     * Get time when this object was last modified.
     *
     * @param updated
     *         time when this object was last modified
     */
    void setUpdated(long updated);

    /**
     * Get class of storage used by Amazon S3 to store this object.
     *
     * @return class of storage used by Amazon S3 to store this object
     */
    String getStorageClass();

    /**
     * Set class of storage used by Amazon S3 to store this object.
     *
     * @param storageClass
     *         class of storage used by Amazon S3 to store this object
     */
    void setStorageClass(String storageClass);

    /**
     * Get owner of this object.
     *
     * @return owner of this object
     */
    S3Owner getOwner();

    /**
     * Set owner of this object.
     *
     * @param owner
     *         owner of this object
     */
    void setOwner(S3Owner owner);
}
