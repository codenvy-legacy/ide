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

import com.codenvy.ide.api.preferences.PreferencesAgent;
import com.codenvy.ide.api.preferences.PreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

import javax.annotation.Nonnull;


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
    public void addPage(@Nonnull Provider<? extends PreferencesPagePresenter> page) {
        preferences.add(page);
    }

    /** @return all available preferences */
    public Array<Provider<? extends PreferencesPagePresenter>> getPreferences() {
        return preferences;
    }
}