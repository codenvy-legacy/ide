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

import com.codenvy.ide.api.preferences.PreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
public class PreferencesPresenter implements PreferencesView.ActionDelegate, PreferencesPagePresenter.DirtyStateListener {
    private PreferencesView                 view;
    private Set<PreferencesPagePresenter>   presenters;
    private PreferencesPagePresenter        currentPage;
    private Array<PreferencesPagePresenter> preferences;
    private boolean                         hasDirtyPage;

    /**
     * Create presenter.
     * <p/>
     * For tests.
     *
     * @param view
     * @param presenters
     */
    @Inject
    protected PreferencesPresenter(PreferencesView view, Set<PreferencesPagePresenter> presenters) {
        this.view = view;
        this.presenters = presenters;
        this.view.setDelegate(this);
    }

    /** {@inheritDoc} */
    @Override
    public void onDirtyChanged() {
        if (currentPage != null && !hasDirtyPage) {
            hasDirtyPage = currentPage.isDirty();
        }

        view.setApplyButtonEnabled(hasDirtyPage);
    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

    /** {@inheritDoc} */
    @Override
    public void onApplyClicked() {
        for (int i = 0; i < preferences.size(); i++) {
            PreferencesPagePresenter page = preferences.get(i);
            if (page.isDirty()) {
                page.doApply();
            }
        }

        hasDirtyPage = false;

        onDirtyChanged();
    }

    /** {@inheritDoc} */
    @Override
    public void selectedPreference(PreferencesPagePresenter preference) {
        currentPage = preference;
        currentPage.setUpdateDelegate(this);
        onDirtyChanged();
        currentPage.go(view.getContentPanel());
    }

    /** Shows preferences. */
    public void showPreferences() {
        Map<String, Set<PreferencesPagePresenter>> preferencesMap = new HashMap<>();
        if (preferences == null) {
            preferences = Collections.createArray();
            for (PreferencesPagePresenter presenter : presenters) {
                preferences.add(presenter);
                Set<PreferencesPagePresenter> preferences;
                if (!preferencesMap.isEmpty() && preferencesMap.containsKey(presenter.getCategory())) {
                    preferences = preferencesMap.get(presenter.getCategory());
                } else {
                    preferences = new HashSet<PreferencesPagePresenter>();
                }
                preferences.add(presenter);
                preferencesMap.put(presenter.getCategory(), preferences);
            }
        }
        this.view.setPreferences(preferencesMap, currentPage);
        view.showPreferences();
        if (preferences != null && preferences.size() > 0){
            view.selectPreference(preferences.get(0));
        }
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        onApplyClicked();
        onCloseClicked();
    }
}