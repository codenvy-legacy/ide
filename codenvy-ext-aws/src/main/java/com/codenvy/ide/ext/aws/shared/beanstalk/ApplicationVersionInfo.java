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

import com.codenvy.ide.dto.DTO;

/**
 * Info about version of AWS Beanstalk application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@DTO
public interface ApplicationVersionInfo {
    /**
     * The name of the application associated with this release.
     *
     * @return The name of the application associated with this release.
     */
    String getApplicationName();

    /**
     * The description of this application version.
     *
     * @return The description of this application version.
     */
    String getDescription();

    /**
     * A label uniquely identifying the version for the associated  application.
     *
     * @return A label uniquely identifying the version for the associated application.
     */
    String getVersionLabel();

    /**
     * The location where the source bundle is located for this version.
     *
     * @return The location where the source bundle is located for this version.
     */
    S3Item getS3Location();

    /**
     * The creation date of the application version.
     *
     * @return The creation date of the application version.
     */
    double getCreated();

    /**
     * The last modified date of the application version.
     *
     * @return The last modified date of the application version.
     */
    double getUpdated();
}
