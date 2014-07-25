/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.preferences;

import com.codenvy.api.user.shared.dto.ProfileDescriptor;
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
    void flushPreferences(AsyncCallback<ProfileDescriptor> callback);
}