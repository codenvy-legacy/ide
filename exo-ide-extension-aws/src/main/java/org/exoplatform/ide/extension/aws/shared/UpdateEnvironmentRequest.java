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
 * Request to update environment.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface UpdateEnvironmentRequest
{
   /**
    * Get configuration template for deploy to environment.
    *
    * @return name of configuration template for deploy to environment
    */
   String getTemplateName();

   /**
    * Set configuration template for deploy to environment.
    *
    * @param templateName
    *    name of configuration template for deploy to environment
    */
   void setTemplateName(String templateName);

   /**
    * Get version of application to deploy.
    *
    * @return version of application to deploy
    */
   String getVersionLabel();

   /**
    * Set version of application to deploy.
    *
    * @param versionLabel
    *    version of application to deploy
    */
   void setVersionLabel(String versionLabel);

   /**
    * Get new environment description. Length: 0 - 200 characters.
    *
    * @return new environment description
    */
   String getDescription();

   /**
    * Set new environment description. Length: 0 - 200 characters.
    *
    * @param description
    *    environment description
    */
   void setDescription(String description);

   /**
    * Get list of configuration options for environment.
    *
    * @return list of configuration options new environment
    */
   List<ConfigurationOption> getOptions();

   /**
    * Set list of configuration options for environment.
    *
    * @param options
    *    list of configuration options for environment
    */
   void setOptions(List<ConfigurationOption> options);
}
