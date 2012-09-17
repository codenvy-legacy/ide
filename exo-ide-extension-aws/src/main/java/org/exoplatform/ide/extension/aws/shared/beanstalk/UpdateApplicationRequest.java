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
 * Request to update application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface UpdateApplicationRequest
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
    * Get new description of application. Length: 0 - 200 characters.
    *
    * @return application description
    */
   String getDescription();

   /**
    * Set new application description. Length: 0 - 200 characters.
    *
    * @param description
    *    application description
    * @see #getDescription()
    */
   void setDescription(String description);
}
