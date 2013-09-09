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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

/**
 * Request ro delete version of application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface DeleteApplicationVersionRequest {
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
     * Get label of the version to delete.
     *
     * @return label of the version to delete
     */
    String getVersionLabel();

    /**
     * Set label of the version to delete.
     *
     * @param versionLabel
     *         label of the version to delete
     */
    void setVersionLabel(String versionLabel);

    /**
     * Delete or not S3 bundle associated with this version.
     *
     * @return <code>true</code> if need to delete S3 bundle associated with this version and <code>false</code>
     *         otherwise
     */
    boolean isDeleteS3Bundle();

    /**
     * Delete or not S3 bundle associated with this version.
     *
     * @param deleteS3Bundle
     *         <code>true</code> if need to delete S3 bundle associated with this version and <code>false</code> otherwise
     */
    void setDeleteS3Bundle(boolean deleteS3Bundle);
}
