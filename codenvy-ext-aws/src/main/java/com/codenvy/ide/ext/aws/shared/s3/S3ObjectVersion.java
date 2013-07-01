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
