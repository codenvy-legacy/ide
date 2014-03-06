/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package com.codenvy.ide.api.preferences;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.codenvy.api.user.shared.dto.Profile;


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
    void flushPreferences(AsyncCallback<Profile> callback);
}