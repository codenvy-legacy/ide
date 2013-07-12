/*
 * Copyright (C) 2012 eXo Platform SAS.
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

import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.google.inject.Inject;
import com.google.inject.Singleton;


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
    private PreferencesView                     view;
    private PreferencesPagePresenter            currentPage;
    private JsonArray<PreferencesPagePresenter> preferences;
    private boolean                             hasDirtyPage;
    private PreferencesAgentImpl                agent;

    /**
     * Create presenter.
     * <p/>
     * For tests.
     *
     * @param view
     * @param agent
     */
    @Inject
    protected PreferencesPresenter(PreferencesView view, PreferencesAgentImpl agent) {
        this.view = view;
        this.view.setDelegate(this);
        this.agent = agent;
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
        preferences = agent.getPreferences();
        this.view.setPreferences(preferences);
        if (!preferences.isEmpty()) {
            selectedPreference(preferences.get(0));
        }
        view.showPreferences();
    }

    /** {@inheritDoc} */
    @Override
    public void onOkClicked() {
        onApplyClicked();
        onCloseClicked();
    }
}