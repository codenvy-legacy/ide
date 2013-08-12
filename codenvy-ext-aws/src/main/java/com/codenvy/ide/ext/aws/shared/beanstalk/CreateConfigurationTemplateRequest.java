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

import com.codenvy.ide.json.JsonArray;

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
     * Get name of configuration template. This name must be unique per application. Length: 1- 100 characters.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Get name of amazon solution stack used by this mew template , e.g. '64bit Amazon Linux running Tomcat 6'.
     *
     * @return name of amazon solution stack
     */
    String getSolutionStackName();

    /**
     * Get source application to copy configuration values to create a new configuration. See {@link
     * #getSourceTemplateName()}.
     *
     * @return name of source application
     */
    String getSourceApplicationName();

    /**
     * Get name of source template. See {@link #getSourceApplicationName()}.
     *
     * @return name of source template
     */
    String getSourceTemplateName();

    /**
     * Get id of the environment used with this configuration template.
     *
     * @return id of the environment used with this configuration template
     */
    String getEnvironmentId();

    /**
     * Get configuration template description. Length: 0 - 200 characters.
     *
     * @return configuration template description
     */
    String getDescription();

    /**
     * Get configuration options.
     *
     * @return configuration options
     */
    JsonArray<ConfigurationOption> getOptions();
}
