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
package org.exoplatform.ide.extension.aws.shared.beanstalk;

/**
 * Request for getting configuration of template or environment. Typically only <code>templateName</code> or
 * <code>environment</code> should be set but not both.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ConfigurationRequest {
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
     * Get name of configuration template.
     *
     * @return name of configuration template
     */
    String getTemplateName();

    /**
     * Get name of configuration template.
     *
     * @param templateName
     *         name of configuration template
     */
    void setTemplateName(String templateName);

    /**
     * Get name of application environment.
     *
     * @return name of application environment
     */
    String getEnvironmentName();

    /**
     * Set name of application environment.
     *
     * @param environmentName
     *         name of application environment
     */
    void setEnvironmentName(String environmentName);
}
