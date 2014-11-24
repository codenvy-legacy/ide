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
import com.codenvy.ide.api.app.AppContext;
import com.codenvy.ide.api.preferences.AbstractPreferencePagePresenter;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.theme.ThemeAgent;
import com.codenvy.ide.ui.dialogs.DialogFactory;
import com.google.gwt.user.client.ui.AcceptsOneWidget;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/** @author Evgen Vidolob */
@Singleton
public class AppearancePresenter extends AbstractPreferencePagePresenter implements AppearanceView.ActionDelegate {

    private AppearanceView     view;
    private ThemeAgent         themeAgent;
    private PreferencesManager preferencesManager;
    private DialogFactory      dialogFactory;
    private boolean dirty = false;
    private String themeId;
    private AppContext appContext;

    @Inject
    public AppearancePresenter(AppearanceView view,
                               CoreLocalizationConstant constant,
                               ThemeAgent themeAgent,
                               PreferencesManager preferencesManager,
                               DialogFactory dialogFactory,
                               AppContext appContext) {
        super(constant.appearanceTitle(), constant.appearanceCategory(), null);
        this.view = view;
        this.themeAgent = themeAgent;
        this.preferencesManager = preferencesManager;
        this.dialogFactory = dialogFactory;
        this.appContext = appContext;
        view.setDelegate(this);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);

        String currentThemeId = preferencesManager.getValue("theme");
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
        preferencesManager.setPreference("ide.theme", themeId);
        dirty = false;
    }

    @Override
    public void revertChanges() {
        String currentThemeId = preferencesManager.getValue("theme");
        if (currentThemeId == null || currentThemeId.isEmpty()) {
            currentThemeId = themeAgent.getCurrentThemeId();
        }
        view.setThemes(themeAgent.getThemes(), currentThemeId);

        dirty = false;
    }

//    @Override
//    public void doApply() {
//        if (isDirty()) {
//            preferencesManager.setPreference("Theme", themeId);
//            preferencesManager.flushPreferences(new AsyncCallback<ProfileDescriptor>() {
//                @Override
//                public void onFailure(Throwable ignore) {
//                }
//
//                @Override
//                public void onSuccess(ProfileDescriptor result) {
//                    dialogFactory.createConfirmDialog("Restart Codenvy", "Restart Codenvy to activate changes in Appearances?",
//                                                      new ConfirmCallback() {
//                                                          @Override
//                                                          public void accepted() {
//                                                              themeAgent.setCurrentThemeId(themeId);
//                                                              Window.Location.reload();
//                                                          }
//                                                      }, null).show();
//                }
//            });
//        }
//    }

}
