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

/**
 * Request ro delete version of application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface DeleteApplicationVersionRequest
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
    * Get label of the version to delete.
    *
    * @return label of the version to delete
    */
   String getVersionLabel();

   /**
    * Set label of the version to delete.
    *
    * @param versionLabel
    *    label of the version to delete
    */
   void setVersionLabel(String versionLabel);

   /**
    * Delete or not S3 bundle associated with this version.
    *
    * @return <code>true</code> if need to delete S3 bundle associated with this version and <code>false</code>
    *         otherwise
    */
   boolean isDeleteS3Bundle();

   /**
    * Delete or not S3 bundle associated with this version.
    *
    * @param deleteS3Bundle
    *    <code>true</code> if need to delete S3 bundle associated with this version and <code>false</code> otherwise
    */
   void setDeleteS3Bundle(boolean deleteS3Bundle);
}
