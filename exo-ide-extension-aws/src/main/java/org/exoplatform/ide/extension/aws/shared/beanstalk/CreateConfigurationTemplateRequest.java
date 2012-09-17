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

import java.util.List;

/**
 * Create new configuration template.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CreateConfigurationTemplateRequest
{
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
    *    name of the application to associate with this configuration template
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
    *    name of configuration template
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
    *    name of amazon solution stack
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
    *    name of source application
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
    *    name of source template
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
    *    id of the environment used with this configuration template
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
    *    configuration template description
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
    *    configuration options
    */
   void setOptions(List<ConfigurationOption> options);
}
