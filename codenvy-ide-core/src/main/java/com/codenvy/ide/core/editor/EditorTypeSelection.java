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
package com.codenvy.ide.core.editor;

import javax.inject.Singleton;

import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.util.loging.Log;

@Singleton
public class EditorTypeSelection {

    public static final String PREFERENCE_PROPERTY_NAME = "editorType";

    private EditorType         editorType               = EditorType.CLASSIC;

    public void setEditorType(final EditorType editorType) {
        if (editorType != null) {
            this.editorType = editorType;
        } else {
            Log.warn(EditorTypeSelection.class, "Cannot set editor type to null - keep previous value.");
        }
    }

    public EditorType getEditorType() {
        return this.editorType;
    }

    public void loadFromPreferences(final PreferencesManager preferencesManager) {
        final String pref = preferencesManager.getValue(PREFERENCE_PROPERTY_NAME);
        if (pref != null && !pref.isEmpty()) {
            try {
                this.editorType = EditorType.valueOf(EditorType.class, pref);
                Log.info(EditorTypeSelection.class, "Found editor type preference: " + pref);
            } catch (final IllegalArgumentException e) {
                Log.error(EditorTypeSelection.class, "Invalid value for editor type preference: " + pref);
                // keep the previous value
            }
        }
    }
}
