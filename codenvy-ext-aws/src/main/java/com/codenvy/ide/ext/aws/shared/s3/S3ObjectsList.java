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
import com.codenvy.ide.json.JsonArray;

/**
 * Information about S3 objects which contains in specified S3 bucket
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface S3ObjectsList {
    /**
     * Get list of S3 objects with their properties
     *
     * @return list containing S3 objects and their description
     */
    JsonArray<S3Object> getObjects();

    /**
     * Get S3 bucket name in which retrieve objects
     *
     * @return name of S3 bucket
     */
    String getS3Bucket();

    /**
     * Get prefix which restricting what keys will be listed
     *
     * @return name of prefix
     */
    String getPrefix();

    /**
     * Get key marker indicating where listing results should begin
     *
     * @return value of key marker which indicate from what position should begin listing
     */
    String getNextMarker();

    /**
     * Get the maximum number of results to return
     *
     * @return value of the maximum number results to return
     */
    double getMaxKeys();
}
