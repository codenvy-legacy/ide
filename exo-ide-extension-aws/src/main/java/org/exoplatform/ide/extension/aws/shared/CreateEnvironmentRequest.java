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
package org.exoplatform.ide.extension.aws.shared;

import java.util.List;

/**
 * Request to create new environment.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CreateEnvironmentRequest
{
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
    *    application name
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
    *    name of new application environment
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
    *    name of amazon solution stack
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
    *    name of configuration template
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
    *    version of application for deploy
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
    *    environment description
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
    *    list of configuration options for new environment
    */
   void setOptions(List<ConfigurationOption> options);
}
