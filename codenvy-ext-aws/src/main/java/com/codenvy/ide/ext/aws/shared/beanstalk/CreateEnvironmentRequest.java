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
