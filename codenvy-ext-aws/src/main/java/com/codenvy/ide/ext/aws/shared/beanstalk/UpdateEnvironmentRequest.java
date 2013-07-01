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
     * Get version of application to deploy.
     *
     * @return version of application to deploy
     */
    String getVersionLabel();

    /**
     * Get new environment description. Length: 0 - 200 characters.
     *
     * @return new environment description
     */
    String getDescription();

    /**
     * Get list of configuration options for environment.
     *
     * @return list of configuration options new environment
     */
    JsonArray<ConfigurationOption> getOptions();
}
