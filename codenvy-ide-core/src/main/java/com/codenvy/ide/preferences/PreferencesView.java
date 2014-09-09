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

import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.api.preferences.PreferencesPagePresenter;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import java.util.Map;
import java.util.Set;

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
    /**
     * Select the pointed preference.
     * 
     * @param preference preference to select.
     */
    void selectPreference(PreferencesPagePresenter preference);
    

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
    void setPreferences(Map<String, Set<PreferencesPagePresenter>> preferences, PreferencesPagePresenter firstPage);
}