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
