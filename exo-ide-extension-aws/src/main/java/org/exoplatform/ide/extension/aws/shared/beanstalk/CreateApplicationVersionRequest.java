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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

/**
 * Request to create new version of application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CreateApplicationVersionRequest {
    /**
     * Get name of application.
     *
     * @return application name
     */
    String getApplicationName();

    /**
     * Set name of application.
     *
     * @param name
     *         application name
     * @see #getApplicationName()
     */
    void setApplicationName(String name);

    /**
     * Get label identifying new version. Must be unique per application. Length: 1 - 100 characters.
     *
     * @return version label
     */
    String getVersionLabel();

    /**
     * Set label identifying new version. Must be unique per application. Length: 1 - 100 characters.
     *
     * @param versionLabel
     *         version label
     */
    void setVersionLabel(String versionLabel);

    /**
     * Name of S3 bucket where version of application uploaded before deploy to AWS Beanstalk. If this parameter not
     * specified random name generated and new S3 bucket created.
     *
     * @return S3 bucket name
     */
    String getS3Bucket();

    /**
     * Set S3 bucket name.
     *
     * @param s3Bucket
     *         S3 bucket name
     * @see #getS3Bucket()
     */
    void setS3Bucket(String s3Bucket);

    /**
     * Name of S3 key where version of application uploaded before deploy to AWS Beanstalk. If this parameter not
     * specified random name generated and new S3 file created. If file with specified key already exists it content
     * will be overridden.
     *
     * @return S3 key
     */
    String getS3Key();

    /**
     * Set S3 key.
     *
     * @param s3Key
     *         S3 key
     * @see #getS3Key()
     */
    void setS3Key(String s3Key);

    /**
     * Get application version description. Length: 0 - 200 characters.
     *
     * @return application version description
     */
    String getDescription();

    /**
     * Set application version description. Length: 0 - 200 characters.
     *
     * @param description
     *         application version description
     * @see #getDescription()
     */
    void setDescription(String description);

    /**
     * URL to pre-build war file. May be present for java applications ONLY.
     *
     * @return URL to pre-build war file
     */
    String getWar();

    /**
     * Set URL to pre-build war file.
     *
     * @param war
     *         URL to pre-build war file
     * @see #getWar()
     */
    void setWar(String war);
}
