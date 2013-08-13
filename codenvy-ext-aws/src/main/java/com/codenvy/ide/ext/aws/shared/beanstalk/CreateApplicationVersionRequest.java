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
package com.codenvy.ide.ext.aws.shared.beanstalk;

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
     * Get label identifying new version. Must be unique per application. Length: 1 - 100 characters.
     *
     * @return version label
     */
    String getVersionLabel();

    /**
     * Name of S3 bucket where version of application uploaded before deploy to AWS Beanstalk. If this parameter not
     * specified random name generated and new S3 bucket created.
     *
     * @return S3 bucket name
     */
    String getS3Bucket();

    /**
     * Name of S3 key where version of application uploaded before deploy to AWS Beanstalk. If this parameter not
     * specified random name generated and new S3 file created. If file with specified key already exists it content
     * will be overridden.
     *
     * @return S3 key
     */
    String getS3Key();

    /**
     * Get application version description. Length: 0 - 200 characters.
     *
     * @return application version description
     */
    String getDescription();

    /**
     * URL to pre-build war file. May be present for java applications ONLY.
     *
     * @return URL to pre-build war file
     */
    String getWar();
}
