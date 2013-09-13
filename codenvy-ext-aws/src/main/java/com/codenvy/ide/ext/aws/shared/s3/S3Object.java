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
 * Information about S3 object
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface S3Object {
    /**
     * Get name of S3 bucket where object stored.
     *
     * @return name of S3 bucket
     */
    String getS3Bucket();

    /**
     * Get S3 key under which this object is stored.
     *
     * @return S3 key under which this object is stored
     */
    String getS3Key();

    /**
     * Get hex encoded MD5 hash of this object's contents, as computed by Amazon S3.
     *
     * @return hex encoded MD5 hash of this object's contents
     */
    String getETag();

    /**
     * Get size of object in bytes.
     *
     * @return size of object in bytes
     */
    double getSize();

    /**
     * Get time when this object was last modified.
     *
     * @return time when this object was last modified
     */
    double getUpdated();

    /**
     * Get class of storage used by Amazon S3 to store this object.
     *
     * @return class of storage used by Amazon S3 to store this object
     */
    String getStorageClass();

    /**
     * Get owner of this object.
     *
     * @return owner of this object
     */
    S3Owner getOwner();
}
