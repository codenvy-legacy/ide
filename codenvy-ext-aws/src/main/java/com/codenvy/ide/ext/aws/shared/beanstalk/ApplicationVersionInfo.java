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

/**
 * Info about version of AWS Beanstalk application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ApplicationVersionInfo {
    /**
     * The name of the application associated with this release.
     *
     * @return The name of the application associated with this release.
     */
    String getApplicationName();

    /**
     * The description of this application version.
     *
     * @return The description of this application version.
     */
    String getDescription();

    /**
     * A label uniquely identifying the version for the associated  application.
     *
     * @return A label uniquely identifying the version for the associated application.
     */
    String getVersionLabel();

    /**
     * The location where the source bundle is located for this version.
     *
     * @return The location where the source bundle is located for this version.
     */
    S3Item getS3Location();

    /**
     * The creation date of the application version.
     *
     * @return The creation date of the application version.
     */
    double getCreated();

    /**
     * The last modified date of the application version.
     *
     * @return The last modified date of the application version.
     */
    double getUpdated();
}
