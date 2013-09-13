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

import java.util.List;

/**
 * Request to update environment.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface UpdateEnvironmentRequest {
    /**
     * Get configuration template for deploy to environment.
     *
     * @return name of configuration template for deploy to environment
     */
    String getTemplateName();

    /**
     * Set configuration template for deploy to environment.
     *
     * @param templateName
     *         name of configuration template for deploy to environment
     */
    void setTemplateName(String templateName);

    /**
     * Get version of application to deploy.
     *
     * @return version of application to deploy
     */
    String getVersionLabel();

    /**
     * Set version of application to deploy.
     *
     * @param versionLabel
     *         version of application to deploy
     */
    void setVersionLabel(String versionLabel);

    /**
     * Get new environment description. Length: 0 - 200 characters.
     *
     * @return new environment description
     */
    String getDescription();

    /**
     * Set new environment description. Length: 0 - 200 characters.
     *
     * @param description
     *         environment description
     */
    void setDescription(String description);

    /**
     * Get list of configuration options for environment.
     *
     * @return list of configuration options new environment
     */
    List<ConfigurationOption> getOptions();

    /**
     * Set list of configuration options for environment.
     *
     * @param options
     *         list of configuration options for environment
     */
    void setOptions(List<ConfigurationOption> options);
}
