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
package com.codenvy.ide.preferences;

import com.codenvy.api.user.gwt.client.UserProfileServiceClient;
import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link PreferencesManager}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class PreferencesManagerImpl implements PreferencesManager {
    private Map<String, String>      persistedPreferences;
    private Map<String, String>      changedPreferences;
    private UserProfileServiceClient userProfileService;

    /**
     * Create preferences.
     *
     * @param userProfileService
     */
    @Inject
    protected PreferencesManagerImpl(UserProfileServiceClient userProfileService) {
        this.persistedPreferences = new HashMap<>();
        this.changedPreferences = new HashMap<>();
        this.userProfileService = userProfileService;
    }

    /** {@inheritDoc} */
    @Override
    public String getValue(String preferenceName) {
        boolean isValueDeleted = changedPreferences.containsKey(preferenceName);
        String value = changedPreferences.get(preferenceName);
        if (value == null && !isValueDeleted) {
            value = persistedPreferences.get(preferenceName);
        }

        return value;
    }

    /** {@inheritDoc} */
    @Override
    public void setPreference(String name, String value) {
        changedPreferences.put(name, value);
    }

    /** {@inheritDoc} */
    @Override
    public void removeValue(String name) {
        setPreference(name, null);
    }

    /** {@inheritDoc} */
    @Override
    public void flushPreferences(final AsyncCallback<ProfileDescriptor> callback) {
        Map<String, String> attributes = new HashMap<String, String>();
        attributes.putAll(changedPreferences);

        userProfileService.updatePreferences(attributes, new AsyncRequestCallback<ProfileDescriptor>() {
            @Override
            protected void onSuccess(ProfileDescriptor result) {
                persistedPreferences.putAll(changedPreferences);
                changedPreferences.clear();
                callback.onSuccess(result);
            }

            @Override
            protected void onFailure(Throwable exception) {
                callback.onFailure(exception);
                Log.error(PreferencesManagerImpl.class, exception);
            }
        });
    }

    /**
     * Reads preferences from input map.
     *
     * @param preferences
     */
    public void load(Map<String, String> preferences) {
        persistedPreferences.putAll(preferences);
    }
}