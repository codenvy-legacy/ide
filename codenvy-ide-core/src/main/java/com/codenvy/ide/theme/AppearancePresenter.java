/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2014] Codenvy, S.A.
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
package com.codenvy.ide.theme;

import com.codenvy.ide.CoreLocalizationConstant;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.api.ui.preferences.AbstractPreferencesPagePresenter;
import com.codenvy.ide.api.ui.theme.ThemeAgent;
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


    private AppearanceView view;
    private ThemeAgent     themeAgent;
    private PreferencesManager preferencesManager;

    private boolean dirty = false;
    private String themeId;

    @Inject
    public AppearancePresenter(AppearanceView view, CoreLocalizationConstant constant, ThemeAgent themeAgent,
                               PreferencesManager preferencesManager) {
        super(constant.appearanceTitle(), null);
        this.view = view;
        this.themeAgent = themeAgent;
        this.preferencesManager = preferencesManager;
        view.setDelegate(this);
    }

    @Override
    public void doApply() {
        if (isDirty()) {
            preferencesManager.setPreference("Theme", themeId);
            preferencesManager.flushPreferences(new AsyncCallback<Void>() {
                @Override
                public void onFailure(Throwable caught) {
                    // ignore
                }

                @Override
                public void onSuccess(Void result) {
                    if (Window.confirm("Restart Codenvy to activate changes in Appearances?")) {
                        Window.Location.reload();
                    }
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
