/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
