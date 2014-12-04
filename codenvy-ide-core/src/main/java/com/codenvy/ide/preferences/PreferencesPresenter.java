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
import com.codenvy.ide.api.preferences.PreferencePagePresenter;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.rest.AsyncRequestCallback;
import com.codenvy.ide.rest.StringMapUnmarshaller;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.rpc.AsyncCallback;
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
public class PreferencesPresenter implements PreferencesView.ActionDelegate, PreferencePagePresenter.DirtyStateListener {

    private PreferencesView view;

    private Set<PreferencePagePresenter> preferences;

    private Map<String, Set<PreferencePagePresenter>> preferencesMap;

    private PreferencesManager preferencesManager;

    private DialogFactory dialogFactory;

    private UserProfileServiceClient userProfileService;

    /**
     * Create presenter.
     * <p/>
     * For tests.
     *
     * @param view
     * @param preferences
     * @param preferencesManager
     * @param dialogFactory
     * @param userProfileService
     */
    @Inject
    protected PreferencesPresenter(PreferencesView view,
                                   Set<PreferencePagePresenter> preferences,
                                   PreferencesManager preferencesManager,
                                   DialogFactory dialogFactory,
                                   UserProfileServiceClient userProfileService) {
        this.view = view;
        this.preferences = preferences;
        this.preferencesManager = preferencesManager;
        this.dialogFactory = dialogFactory;
        this.userProfileService = userProfileService;

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
        preference.go(view.getContentPanel());
    }

    /** Shows preferences. */
    public void showPreferences() {
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
        try {
            for (PreferencePagePresenter preference : preferences) {
                if (preference.isDirty()) {
                    preference.storeChanges();
                }
            }

            preferencesManager.flushPreferences(new AsyncCallback<ProfileDescriptor>() {
                @Override
                public void onSuccess(ProfileDescriptor result) {
                    view.enableSaveButton(false);
                }

                @Override
                public void onFailure(Throwable error) {
                    dialogFactory.createMessageDialog("", "Unable to save preferences", null).show();
                }
            });
        } catch (Throwable error) {
            dialogFactory.createMessageDialog("", "Unable to save preferences", null).show();
        }
    }

    @Override
    public void onRefreshClicked() {
        try {
            userProfileService.getPreferences(new AsyncRequestCallback<Map<String, String>>(new StringMapUnmarshaller()) {
                @Override
                protected void onSuccess(Map<String, String> preferences) {
                    /**
                     * Reload preferences by Preferences Manager
                     */
                    if (preferencesManager instanceof PreferencesManagerImpl) {
                        ((PreferencesManagerImpl)preferencesManager).load(preferences);
                    }

                    /**
                     * Revert changes on every preference page
                     */
                    for (PreferencePagePresenter p: PreferencesPresenter.this.preferences) {
                        p.revertChanges();
                    }
                }

                @Override
                protected void onFailure(Throwable exception) {
                    dialogFactory.createMessageDialog("", "Unable to refresh preferences", null).show();
                }
            });

        } catch (Throwable error) {
            dialogFactory.createMessageDialog("", "Unable to refresh preferences", null).show();
        }

    }

    /** {@inheritDoc} */
    @Override
    public void onCloseClicked() {
        view.close();
    }

}
