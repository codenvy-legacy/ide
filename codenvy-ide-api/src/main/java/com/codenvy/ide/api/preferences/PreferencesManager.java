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
package com.codenvy.ide.api.preferences;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Manager for preferences.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface PreferencesManager {
    /**
     * Returns preference's value for the given preference's name.
     *
     * @param preferenceName
     *         the preference's name whose associated value will be returned
     * @return preference's value
     */
    String getValue(String preferenceName);

    /**
     * Associates the preference's value with the preference's name. If preferences previously contained this preference, the old value is
     * replaced by new one.
     *
     * @param name
     *         preference's name with which the preference's value will be associated
     * @param value
     *         preference's value will be associated with preference's name
     */
    void setPreference(String name, String value);

    /**
     * Removes given preference from preferences.
     *
     * @param name
     *         preference's name which preference needs to remove
     */
    void removeValue(String name);

    /**
     * Persists preferences by passing it to the server side.
     *
     * @param callback
     */
    void flushPreferences(AsyncCallback<Void> callback);
}