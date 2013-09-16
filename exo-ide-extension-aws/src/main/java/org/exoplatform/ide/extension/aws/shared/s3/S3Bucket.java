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
 * Information about Amazon S3 bucket
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface S3Bucket {
    /**
     * Get S3 bucket name
     *
     * @return S3 bucket name
     */
    String getName();

    /**
     * Set S3 bucket name
     *
     * @param name
     *         S3 bucket name
     */
    void setName(String name);

    /**
     * Get timestamp of S3 bucket creation
     *
     * @return time of creation S3 bucket in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    long getCreated();

    /**
     * Set timestamp of S3 bucket creation
     *
     * @param creationDate
     *         time in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    void setCreated(long creationDate);

    /**
     * Get information about S3 bucket owner
     *
     * @return object containing information about S3 bucket owner, such as owner id and name
     */
    S3Owner getOwner();

    /**
     * Set information about S3 bucket owner
     *
     * @param owner
     *         object containing information about S3 bucket owner
     */
    void setOwner(S3Owner owner);
}
