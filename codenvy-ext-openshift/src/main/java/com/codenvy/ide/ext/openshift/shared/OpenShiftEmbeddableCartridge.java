/*
 * Copyright (C) 2013 eXo Platform SAS.
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
package com.codenvy.ide.ext.openshift.shared;

import com.codenvy.ide.json.JsonStringMap;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface OpenShiftEmbeddableCartridge {
    /**
     * Get information about cartridge name.
     *
     * @return cartridge name
     */
    String getName();

    /**
     * Get url to control cartridge.
     *
     * @return url link
     */
    String getUrl();

    /**
     * Contains info which should be displayed to the user. It contains important info, e.g. url, username, password for database.
     *
     * @return information about failed creation cartridge.
     */
    String getCreationLog();

    /**
     * Get information about cartridge properties such as login, password, url etc.
     *
     * @return json array with properties
     */
    JsonStringMap<String> getProperties();
}
