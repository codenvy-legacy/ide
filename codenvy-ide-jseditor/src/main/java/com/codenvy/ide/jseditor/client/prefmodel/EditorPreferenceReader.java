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
package com.codenvy.ide.jseditor.client.prefmodel;

import javax.inject.Inject;
import javax.validation.constraints.NotNull;

import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.util.loging.Log;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;

/**
 * Utility to read {@link EditorPreferences} from the preferences.
 */
public class EditorPreferenceReader {

    /** The editor preference main property name. */
    private static final String PREFERENCE_PROPERTY = "editor";

    /** the preferences manager instance. */
    private final PreferencesManager preferencesManager;

    @Inject
    public EditorPreferenceReader(final PreferencesManager preferencesManager) {
        this.preferencesManager = preferencesManager;
    }

    /**
     * Returns the editor preference object.<br>
     * If there is none, return a properly initialized preference object.
     * @return editor preference
     */
    @NotNull
    public EditorPreferences getPreferences() {
        try {
            final String prefAsJson = this.preferencesManager.getValue(PREFERENCE_PROPERTY);
            JSONValue parseResult = JSONParser.parseStrict(prefAsJson);
            JSONValue propertyObject = parseResult.isObject();
            JavaScriptObject propertyValue = propertyObject.isObject().getJavaScriptObject();
            return propertyValue.cast();
        } catch (Exception e) {
            Log.error(KeymapPrefReader.class, "Unable to get editor references.", e);
        }

        return EditorPreferences.create();
    }

    /**
     * Stores the editor preference object in the preferences.
     * @param newPreferences the pref object to store
     */
    public void setPreferences(final EditorPreferences newPreferences) {
        final JSONObject json = new JSONObject(newPreferences);
        this.preferencesManager.setPreference(PREFERENCE_PROPERTY, json.toString());
    }
}
