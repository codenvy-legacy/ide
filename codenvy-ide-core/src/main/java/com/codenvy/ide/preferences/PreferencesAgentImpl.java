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
package com.codenvy.ide.preferences;

import com.codenvy.ide.api.ui.preferences.PreferencesAgent;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.validation.constraints.NotNull;


/**
 * Implements PreferencesAgent and returns all available preferences.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class PreferencesAgentImpl implements PreferencesAgent {
    Array<Provider<? extends PreferencesPagePresenter>> preferences;

    /** Create PreferencesAgent. */
    @Inject
    public PreferencesAgentImpl() {
        preferences = Collections.createArray();
    }

    /** {@inheritDoc} */
    @Override
    public void addPage(@NotNull Provider<? extends PreferencesPagePresenter> page) {
        preferences.add(page);
    }

    /** @return all available preferences */
    public Array<Provider<? extends PreferencesPagePresenter>> getPreferences() {
        return preferences;
    }
}