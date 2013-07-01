/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General  License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General  License for more details.
 *
 * You should have received a copy of the GNU Lesser General 
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.aws.shared.beanstalk;

/**
 * Request to create new application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CreateApplicationRequest {
    /**
     * Get name of application. This name must be unique within AWS Beanstalk account. Length: 1 - 100 characters.
     *
     * @return application name
     */
    String getApplicationName();

    /**
     * Get application description. Length: 0 - 200 characters.
     *
     * @return application description
     */
    String getDescription();

    /**
     * Name of S3 bucket where initial version of application uploaded before deploy to AWS Beanstalk. If this parameter
     * not specified random name generated and new S3 bucket created.
     *
     * @return S3 bucket name
     */
    String getS3Bucket();

    /**
     * Name of S3 key where initial version of  application uploaded before deploy to AWS Beanstalk. If this parameter
     * not specified random name generated and new S3 file created. If file with specified key already exists it content
     * will be overridden.
     *
     * @return S3 key
     */
    String getS3Key();

    /**
     * URL to pre-build war file. May be present for java applications ONLY.
     *
     * @return URL to pre-build war file
     */
    String getWar();
}
