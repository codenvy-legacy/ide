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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.ui.preferences.PreferencesPagePresenter;
import com.codenvy.ide.json.JsonArray;
import com.google.gwt.user.client.ui.AcceptsOneWidget;


/**
 * Interface of Preferences view.
 *
 * @author <a href="mailto:aplotnikov@exoplatform.com">Andrey Plotnikov</a>
 */
public interface PreferencesView extends View<PreferencesView.ActionDelegate> {
    /** Needs for delegate some function into preferences view. */
    public interface ActionDelegate {
        /**
         * Performs any actions appropriate in response to the user
         * having pressed the Close button
         */
        void onCloseClicked();

        /**
         * Performs any actions appropriate in response to the user
         * having pressed the Apply button
         */
        void onApplyClicked();

        /**
         * Performs any actions appropriate in response to the user
         * having pressed the OK button
         */
        void onOkClicked();

        /**
         * Performs any actions appropriate in response to select some preference.
         *
         * @param preference
         *         selected preference
         */
        void selectedPreference(PreferencesPagePresenter preference);
    }

    /** Close view. */
    void close();

    /** Show preferences. */
    void showPreferences();

    /**
     * Returns content panel.
     *
     * @return
     */
    AcceptsOneWidget getContentPanel();

    /**
     * Sets whether Apply button is enabled.
     *
     * @param isEnabled
     *         <code>true</code> to enable the button, <code>false</code>
     *         to disable it
     */
    void setApplyButtonEnabled(boolean isEnabled);

    /**
     * Sets available preferences.
     *
     * @param preferences
     */
    void setPreferences(JsonArray<PreferencesPagePresenter> preferences);
}