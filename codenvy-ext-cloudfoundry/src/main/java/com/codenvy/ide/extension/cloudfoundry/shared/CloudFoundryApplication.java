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
package com.codenvy.ide.extension.cloudfoundry.shared;

import com.codenvy.ide.json.JsonArray;

/**
 * Cloud Foundry application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfo.java Mar 16, 2012 12:21:15 AM azatsarynnyy $
 */
public interface CloudFoundryApplication {
    String getName();

    JsonArray<String> getUris();

    int getInstances();

    int getRunningInstances();

    String getState();

    JsonArray<String> getServices();

    String getVersion();

    JsonArray<String> getEnv();

    CloudFoundryApplicationResources getResources();

    Staging getStaging();

    String getDebug();

    ApplicationMetaInfo getMeta();
}