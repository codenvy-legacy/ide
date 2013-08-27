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
 * Create new configuration template.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CreateConfigurationTemplateRequest {
    /**
     * Get name of the application to associate with this configuration template.
     *
     * @return name of the application to associate with this configuration template
     */
    String getApplicationName();

    /**
     * Set name of the application to associate with this configuration template.
     *
     * @param name
     *         name of the application to associate with this configuration template
     */
    void setApplicationName(String name);

    /**
     * Get name of configuration template. This name must be unique per application. Length: 1- 100 characters.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Set name of configuration template. This name must be unique per application. Length: 1- 100 characters.
     *
     * @param templateName
     *         name of configuration template
     */
    void setTemplateName(String templateName);

    /**
     * Get name of amazon solution stack used by this mew template , e.g. '64bit Amazon Linux running Tomcat 6'.
     *
     * @return name of amazon solution stack
     */
    String getSolutionStackName();

    /**
     * Set name of amazon solution stack used by this mew template , e.g. '64bit Amazon Linux running Tomcat 6'.
     *
     * @param solutionStackName
     *         name of amazon solution stack
     */
    void setSolutionStackName(String solutionStackName);

    /**
     * Get source application to copy configuration values to create a new configuration. See {@link
     * #getSourceTemplateName()}.
     *
     * @return name of source application
     */
    String getSourceApplicationName();

    /**
     * Set source application to copy configuration values to create a new configuration. See {@link
     * #setSourceTemplateName(String)}.
     *
     * @param sourceApplicationName
     *         name of source application
     */
    void setSourceApplicationName(String sourceApplicationName);

    /**
     * Get name of source template. See {@link #getSourceApplicationName()}.
     *
     * @return name of source template
     */
    String getSourceTemplateName();

    /**
     * Set name of source template. See {@link #setSourceApplicationName(String)}.
     *
     * @param sourceTemplateName
     *         name of source template
     */
    void setSourceTemplateName(String sourceTemplateName);

    /**
     * Get id of the environment used with this configuration template.
     *
     * @return id of the environment used with this configuration template
     */
    String getEnvironmentId();

    /**
     * Set id of the environment used with this configuration template.
     *
     * @param environmentId
     *         id of the environment used with this configuration template
     */
    void setEnvironmentId(String environmentId);

    /**
     * Get configuration template description. Length: 0 - 200 characters.
     *
     * @return configuration template description
     */
    String getDescription();

    /**
     * Set configuration template description. Length: 0 - 200 characters.
     *
     * @param description
     *         configuration template description
     */
    void setDescription(String description);

    /**
     * Get configuration options.
     *
     * @return configuration options
     */
    List<ConfigurationOption> getOptions();

    /**
     * Set configuration options. Options specified in this list override options copied from solution stack or source
     * configuration template.
     *
     * @param options
     *         configuration options
     */
    void setOptions(List<ConfigurationOption> options);
}
