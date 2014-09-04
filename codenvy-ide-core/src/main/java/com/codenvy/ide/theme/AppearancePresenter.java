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
package com.codenvy.ide.theme;

import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.codenvy.ide.ui.dialogs.ask.Ask;
import com.codenvy.ide.ui.dialogs.ask.AskHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * @author Evgen Vidolob
 */
@Singleton
public class AppearancePresenter extends AbstractPreferencesPagePresenter implements AppearanceView.ActionDelegate {


    private AppearanceView     view;
    private ThemeAgent         themeAgent;
    private PreferencesManager preferencesManager;

    private boolean dirty = false;
    private String themeId;

    @Inject
    public AppearancePresenter(AppearanceView view, CoreLocalizationConstant constant, ThemeAgent themeAgent,
                               PreferencesManager preferencesManager) {
        super(constant.appearanceTitle(), constant.appearanceCategory(), null);
        this.view = view;
        this.themeAgent = themeAgent;
        this.preferencesManager = preferencesManager;
        view.setDelegate(this);
    }

    @Override
    public void doApply() {
        if (isDirty()) {
            preferencesManager.setPreference("Theme", themeId);
            preferencesManager.flushPreferences(new AsyncCallback<ProfileDescriptor>() {
                @Override
                public void onFailure(Throwable caught) {
                    // ignore
                }

                @Override
                public void onSuccess(ProfileDescriptor result) {
                    Ask ask = new Ask("Restart Codenvy", "Restart Codenvy to activate changes in Appearances?", new AskHandler() {
                        @Override
                        public void onOk() {
                            themeAgent.setCurrentThemeId(themeId);
                            Window.Location.reload();
                        }
                    });
                    ask.show();
                }
            });
        }
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        view.setThemes(themeAgent.getThemes(), themeAgent.getCurrentThemeId());
    }

    @Override
    public void themeSelected(String themeId) {
        this.themeId = themeId;
        dirty = !themeId.equals(themeAgent.getCurrentThemeId());
        delegate.onDirtyChanged();
    }
}
