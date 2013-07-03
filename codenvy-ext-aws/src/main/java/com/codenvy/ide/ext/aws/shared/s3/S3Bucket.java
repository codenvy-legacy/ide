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
     * Get timestamp of S3 bucket creation
     *
     * @return time of creation S3 bucket in milliseconds since January 1, 1970, 00:00:00 GMT
     */
    Long getCreated();

    /**
     * Get information about S3 bucket owner
     *
     * @return object containing information about S3 bucket owner, such as owner id and name
     */
    S3Owner getOwner();
}
