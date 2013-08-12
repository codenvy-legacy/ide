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
     * Get name of new application environment. Length: 4 - 23 characters.
     *
     * @return name of new application environment
     */
    String getEnvironmentName();

    /**
     * Get name of amazon solution stack, e.g. '64bit Amazon Linux running Tomcat 6'.
     *
     * @return name of amazon solution stack
     */
    String getSolutionStackName();

    /**
     * Get name of configuration template to use for deploy version of application.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Get version of application for deploy.
     *
     * @return version of application for deploy
     */
    String getVersionLabel();

    /**
     * Get environment description. Length: 0 - 200 characters.
     *
     * @return environment description
     */
    String getDescription();

    /**
     * Get list of configuration options for new environment.
     *
     * @return list of configuration options for new environment
     */
    JsonArray<ConfigurationOption> getOptions();
}
