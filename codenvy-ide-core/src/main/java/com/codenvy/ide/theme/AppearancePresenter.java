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

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.preferences.AbstractPreferencePagePresenter;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author Evgen Vidolob */
@Singleton
public class AppearancePresenter extends AbstractPreferencePagePresenter implements AppearanceView.ActionDelegate {

    public static final String PREF_IDE_THEME = "ide.theme";

    private AppearanceView     view;
    private ThemeAgent         themeAgent;
    private PreferencesManager preferencesManager;
    private boolean dirty = false;
    private String themeId;

    @Inject
    public AppearancePresenter(AppearanceView view,
                               CoreLocalizationConstant constant,
                               ThemeAgent themeAgent,
                               PreferencesManager preferencesManager) {
        super(constant.appearanceTitle(), constant.appearanceCategory(), null);
        this.view = view;
        this.themeAgent = themeAgent;
        this.preferencesManager = preferencesManager;
        view.setDelegate(this);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        String currentThemeId = preferencesManager.getValue(PREF_IDE_THEME);
        if (currentThemeId == null || currentThemeId.isEmpty()) {
            currentThemeId = themeAgent.getCurrentThemeId();
        }
        view.setThemes(themeAgent.getThemes(), currentThemeId);
    }

    @Override
    public void themeSelected(String themeId) {
        this.themeId = themeId;
        dirty = !themeId.equals(themeAgent.getCurrentThemeId());
        delegate.onDirtyChanged();
    }

    @Override
    public void storeChanges() {
        preferencesManager.setValue(PREF_IDE_THEME, themeId);
        dirty = false;
    }

    @Override
    public void revertChanges() {
        String currentThemeId = preferencesManager.getValue(PREF_IDE_THEME);
        if (currentThemeId == null || currentThemeId.isEmpty()) {
            currentThemeId = themeAgent.getCurrentThemeId();
        }
        view.setThemes(themeAgent.getThemes(), currentThemeId);

        dirty = false;
    }

}
