/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.aws.shared.s3;

import com.codenvy.ide.json.JsonArray;

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
