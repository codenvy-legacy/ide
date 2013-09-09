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
 * Request to create new environment.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CreateEnvironmentRequest {
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
     * Get name of new application environment. Length: 4 - 23 characters.
     *
     * @return name of new application environment
     */
    String getEnvironmentName();

    /**
     * Set name of new application environment. Length: 4 - 23 characters.
     *
     * @param environmentName
     *         name of new application environment
     * @see #getEnvironmentName()
     */
    void setEnvironmentName(String environmentName);

    /**
     * Get name of amazon solution stack, e.g. '64bit Amazon Linux running Tomcat 6'.
     *
     * @return name of amazon solution stack
     */
    String getSolutionStackName();

    /**
     * Set name of amazon solution stack, e.g. '64bit Amazon Linux running Tomcat 6'.
     * If this parameter set parameter <code>templateName</code> must not be set, see {@link #setTemplateName(String)}}.
     *
     * @param solutionStackName
     *         name of amazon solution stack
     */
    void setSolutionStackName(String solutionStackName);

    /**
     * Get name of configuration template to use for deploy version of application.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Set name of configuration template to use for deploy version of application. If this parameter set parameter
     * <code>solutionStackName</code> must not be set, see {@link #setSolutionStackName(String)}}.
     *
     * @param templateName
     *         name of configuration template
     */
    void setTemplateName(String templateName);

    /**
     * Get version of application for deploy.
     *
     * @return version of application for deploy
     */
    String getVersionLabel();

    /**
     * Set version of application for deploy.
     *
     * @param versionLabel
     *         version of application for deploy
     */
    void setVersionLabel(String versionLabel);

    /**
     * Get environment description. Length: 0 - 200 characters.
     *
     * @return environment description
     */
    String getDescription();

    /**
     * Set environment description. Length: 0 - 200 characters.
     *
     * @param description
     *         environment description
     */
    void setDescription(String description);

    /**
     * Get list of configuration options for new environment.
     *
     * @return list of configuration options for new environment
     */
    List<ConfigurationOption> getOptions();

    /**
     * Set list of configuration options for new environment.
     *
     * @param options
     *         list of configuration options for new environment
     */
    void setOptions(List<ConfigurationOption> options);
}
