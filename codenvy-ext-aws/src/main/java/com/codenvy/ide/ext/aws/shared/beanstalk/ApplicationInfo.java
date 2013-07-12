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
 * Info about AWS Beanstalk application.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ApplicationInfo {
    /**
     * The name of the application.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>1 - 100<br/>
     *
     * @return The name of the application.
     */
    String getName();

    /**
     * User-defined description of the application.
     * <p/>
     * <b>Constraints:</b><br/>
     * <b>Length: </b>0 - 200<br/>
     *
     * @return User-defined description of the application.
     */
    String getDescription();

    /**
     * The date when the application was created.
     *
     * @return The date when the application was created.
     */
    double getCreated();

    /**
     * The date when the application was last modified.
     *
     * @return The date when the application was last modified.
     */
    double getUpdated();

    /**
     * The names of the versions for this application.
     *
     * @return The names of the versions for this application.
     */
    JsonArray<String> getVersions();

    /**
     * The names of the configuration templates associated with this
     * application.
     *
     * @return The names of the configuration templates associated with this
     *         application.
     */
    JsonArray<String> getConfigurationTemplates();
}
