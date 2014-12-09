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
package com.codenvy.ide.extension.runner.client.manage.ram;

import com.codenvy.ide.api.preferences.AbstractPreferencePagePresenter;
import com.codenvy.ide.api.preferences.PreferencesManager;
import com.codenvy.ide.extension.runner.client.RunnerLocalizationConstant;
import com.google.gwt.user.client.ui.AcceptsOneWidget;

import javax.inject.Inject;

import static com.codenvy.ide.extension.runner.client.RunnerExtension.PREFS_RUNNER_RAM_SIZE_DEFAULT;

/**
 * @author Vitaly Parfonov
 */
public class RamManagePresenter extends AbstractPreferencePagePresenter implements RamManagerView.ActionDelegate {

    private RunnerLocalizationConstant localizationConstant;
    private RamManagerView             view;
    private PreferencesManager         preferencesManager;
    private boolean dirty = false;

    /**
     * Create preference page.
     */
    @Inject
    public RamManagePresenter(RunnerLocalizationConstant localizationConstant,
                              RamManagerView view,
                              PreferencesManager preferencesManager) {
        super(localizationConstant.titlesRamManager(), null);
        this.localizationConstant = localizationConstant;
        this.view = view;
        this.preferencesManager = preferencesManager;
        this.view.setDelegate(this);
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void validateRamSize(String value) {
        if (value.isEmpty()) {
            dirty = true;
            view.hideWarnMessage();
            delegate.onDirtyChanged();
            return;
        }
        try {
            final int ram = Integer.parseInt(value);
            if (ram % 128 == 0) {
                dirty = true;
                view.hideWarnMessage();
            } else {
                dirty = false;
                view.showWarnMessage(localizationConstant.ramSizeMustBeMultipleOf("128"));
            }
            delegate.onDirtyChanged();
        } catch (NumberFormatException e) {
            dirty = false;
            view.showWarnMessage(localizationConstant.enteredValueNotCorrect());
            delegate.onDirtyChanged();
        }
    }

    @Override
    public void go(AcceptsOneWidget container) {
        container.setWidget(view);
        String ramSize = preferencesManager.getValue(PREFS_RUNNER_RAM_SIZE_DEFAULT);
        if (ramSize != null) {
            view.showRam(ramSize.replace("\"", ""));
        }
    }

    @Override
    public void storeChanges() {
        String ramSize = view.getRam().replace("\"", "");
        preferencesManager.setPreference(PREFS_RUNNER_RAM_SIZE_DEFAULT, ramSize);
        dirty = false;
    }

    @Override
    public void revertChanges() {
        String ramSize = preferencesManager.getValue(PREFS_RUNNER_RAM_SIZE_DEFAULT);
        if (ramSize != null) {
            view.showRam(ramSize.replace("\"", ""));
            view.hideWarnMessage();
        }
        dirty = false;
    }

}
