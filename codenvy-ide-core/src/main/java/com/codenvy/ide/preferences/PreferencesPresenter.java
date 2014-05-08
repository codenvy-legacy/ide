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

import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;
import com.google.inject.Inject;
import com.google.inject.Singleton;

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
    private PreferencesView view;
    private Set<PreferencesPagePresenter> presenters;
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
        if (preferences == null) {
            preferences = Collections.createArray();
            for (PreferencesPagePresenter presenter : presenters) {
                preferences.add(presenter);
            }
        }
        this.view.setPreferences(preferences);
        if (currentPage != null) {
            selectedPreference(currentPage);
        } else if (!preferences.isEmpty()) {
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