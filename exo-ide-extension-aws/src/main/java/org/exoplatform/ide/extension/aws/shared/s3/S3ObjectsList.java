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

import java.util.List;

/**
 * Information about S3 objects which contains in specified S3 bucket
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface S3ObjectsList {
    /**
     * Get list of S3 objects with their properties
     *
     * @return list containing S3 objects and their description
     */
    List<S3Object> getObjects();

    /**
     * Set list of S3 objects with their properties
     *
     * @param objects
     *         list containing S3 objects and their description
     */
    void setObjects(List<S3Object> objects);

    /**
     * Get S3 bucket name in which retrieve objects
     *
     * @return name of S3 bucket
     */
    String getS3Bucket();

    /**
     * Set S3 bucket name in which retrieve objects
     *
     * @param s3Bucket
     *         name of S3 bucket
     */
    void setS3Bucket(String s3Bucket);

    /**
     * Get prefix which restricting what keys will be listed
     *
     * @return name of prefix
     */
    String getPrefix();

    /**
     * Set name of prefix which restricting what keys will be listed
     *
     * @param prefix
     *         name of prefix
     */
    void setPrefix(String prefix);

    /**
     * Get key marker indicating where listing results should begin
     *
     * @return value of key marker which indicate from what position should begin listing
     */
    String getNextMarker();

    /**
     * Set key marker indicating where listing results should begin
     *
     * @param nextMarker
     *         value of key marker which indicate from what position should begin listing,
     *         value must be equals or greater 0
     */
    void setNextMarker(String nextMarker);

    /**
     * Get the maximum number of results to return
     *
     * @return value of the maximum number results to return
     */
    int getMaxKeys();

    /**
     * Set the maximum number of the results to return
     *
     * @param maxKeys
     *         value of the maximum number results to return, if max keys -1 then will show objects in result set with
     *         no limitations
     */
    void setMaxKeys(int maxKeys);
}
