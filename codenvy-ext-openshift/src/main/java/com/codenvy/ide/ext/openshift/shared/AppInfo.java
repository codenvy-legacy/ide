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

import com.codenvy.ide.json.JsonArray;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AppInfo {
    /**
     * Get name of the application.
     *
     * @return application name.
     */
    String getName();

    /**
     * Return the application type.
     *
     * @return application type.
     */
    String getType();

    /**
     * Get url of the application Git-repository.
     *
     * @return url of the application Git-repository.
     */
    String getGitUrl();

    /**
     * Return the public url of the application.
     *
     * @return public url of the application.
     */
    String getPublicUrl();

    /**
     * Return time when application was created.
     * <p/>
     * Time returned as double-value because the Java long type cannot be represented in JavaScript as a numeric type.
     * http://code.google.com/intl/ru/webtoolkit/doc/latest/DevGuideCodingBasicsCompatibility.html
     *
     * @return time of creation the application.
     */
    double getCreationTime();

    /**
     * Get list of embeddable cartridges added to application.
     *
     * @return list of embeddable cartridges added to application.
     */
    JsonArray<OpenShiftEmbeddableCartridge> getEmbeddedCartridges();
}
