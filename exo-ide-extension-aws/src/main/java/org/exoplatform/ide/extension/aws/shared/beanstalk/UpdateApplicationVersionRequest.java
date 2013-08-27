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
 * Request to update version of application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface UpdateApplicationVersionRequest {
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
     * Get label of the version to update.
     *
     * @return label of the version to update
     */
    String getVersionLabel();

    /**
     * Set label of the version to update.
     *
     * @param versionLabel
     *         label of the version to update
     */
    void setVersionLabel(String versionLabel);

    /**
     * Get new description of application version. Length: 0 - 200 characters.
     *
     * @return application version description
     */
    String getDescription();

    /**
     * Set new application description version. Length: 0 - 200 characters.
     *
     * @param description
     *         application version description
     * @see #getDescription()
     */
    void setDescription(String description);
}
