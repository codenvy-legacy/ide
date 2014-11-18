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

import com.codenvy.ide.api.preferences.PreferencePagePresenter;
import com.codenvy.ide.util.loging.Log;
import com.google.inject.Inject;
import com.google.inject.Singleton;

import javax.ws.rs.Path;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;


/**
 * PreferencesPresenter is presentation of preference pages.
 * It manages preference pages. It's responsible for the communication user and wizard page.
 * In typical usage, the client instantiates this class with list of preferences.
 * The presenter serves as the preference page container and orchestrates the
 * presentation of its pages.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
@Singleton
public class PreferencesPresenter implements PreferencesView.ActionDelegate, PreferencePagePresenter.DirtyStateListener {

    private PreferencesView view;

    private Set<PreferencePagePresenter> preferences;

    private Map<String, Set<PreferencePagePresenter>> preferencesMap;

    /**
     * Create presenter.
     * <p/>
     * For tests.
     *
     * @param view
     * @param preferences
     */
    @Inject
    protected PreferencesPresenter(PreferencesView view, Set<PreferencePagePresenter> preferences) {
        this.view = view;
        this.preferences = preferences;

        this.view.setDelegate(this);

        for (PreferencePagePresenter preference : preferences) {
            preference.setUpdateDelegate(this);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onDirtyChanged() {
        for (PreferencePagePresenter p : preferences) {
            if (p.isDirty()) {
                view.enableSaveButton(true);
                return;
            }
        }
        view.enableSaveButton(false);
    }

    /** {@inheritDoc} */
    @Override
    public void onPreferenceSelected(PreferencePagePresenter preference) {
        Log.trace("<< com.codenvy.ide.preferences.PreferencesPresenter.onPreferenceSelected " + preference.getCategory() + " : " + preference.getTitle());
        preference.go(view.getContentPanel());
    }

    /** Shows preferences. */
    public void showPreferences() {

        Log.trace(">> PreferencesPresenter.showPreferences()");

        if (preferencesMap != null) {
            view.show();
            return;
        }

        preferencesMap = new HashMap<>();
        for (PreferencePagePresenter preference : preferences) {
            Set<PreferencePagePresenter> prefsList = preferencesMap.get(preference.getCategory());
            if (prefsList == null) {
                prefsList = new HashSet<PreferencePagePresenter>();
                preferencesMap.put(preference.getCategory(), prefsList);
            }

            prefsList.add(preference);
        }
        view.setPreferences(preferencesMap);

        view.show();
        view.enableSaveButton(false);
        view.selectPreference(preferencesMap.entrySet().iterator().next().getValue().iterator().next());
    }

    @Override
    public void onSaveClicked() {
        Log.trace("storeChanges clicked");
    }

    @Override
    public void onRefreshClicked() {
        Log.trace("refresh clicked");
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

}
