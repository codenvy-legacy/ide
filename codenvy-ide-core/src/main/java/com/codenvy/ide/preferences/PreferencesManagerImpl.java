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
package com.codenvy.ide.preferences;

import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.client.DtoClientImpls;
import com.codenvy.ide.json.JsonCollections;
import com.codenvy.ide.json.JsonStringMap;
import com.codenvy.ide.json.js.JsoStringMap;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.api.user.UserClientService;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * The implementation of {@link PreferencesManager}.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
@Singleton
public class PreferencesManagerImpl implements PreferencesManager {
    private JsonStringMap<String> persistedPreferences;

    private JsonStringMap<String> changedPreferences;

    private UserClientService userService;

    /**
     * Create preferences.
     *
     * @param userService
     */
    @Inject
    protected PreferencesManagerImpl(UserClientService userService) {
        this.persistedPreferences = JsonCollections.createStringMap();
        this.changedPreferences = JsoStringMap.create();
        this.userService = userService;
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
    public void flushPreferences(final AsyncCallback<Void> callback) {
        DtoClientImpls.UpdateUserAttributesImpl attributes = DtoClientImpls.UpdateUserAttributesImpl.make();
        attributes.setAttributes(changedPreferences);

        try {
            userService.updateUserAttributes(attributes, new AsyncRequestCallback<Void>() {
                @Override
                protected void onSuccess(Void result) {
                    changedPreferences.iterate(new JsonStringMap.IterationCallback<String>() {
                        @Override
                        public void onIteration(String key, String value) {
                            persistedPreferences.put(key, value);
                        }
                    });
                    changedPreferences = JsoStringMap.create();

                    callback.onSuccess(result);
                }

                @Override
                protected void onFailure(Throwable exception) {
                    callback.onFailure(exception);
                    Log.error(PreferencesManagerImpl.class, exception);
                }
            });
        } catch (RequestException e) {
            Log.error(PreferencesManagerImpl.class, e);
        }
    }

    /**
     * Reads preferences from input map.
     *
     * @param preferences
     */
    public void load(JsonStringMap<String> preferences) {
        persistedPreferences.putAll(preferences);
    }
}