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
package com.codenvy.ide.extension.runner.client.run;

import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link CustomRunPresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface CustomRunView extends View<CustomRunView.ActionDelegate> {

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Run button. */
        void onRunClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having changed something. */
        void onValueChanged();
    }

    /**
     * Returns chosen environment.
     *
     * @return {@link RunnerEnvironment}
     */
    RunnerEnvironment getSelectedEnvironment();

    /**
     * Set available environments.
     *
     * @param environments
     *         runner environments
     */
    void setEnvironments(@NotNull Array<RunnerEnvironment> environments);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();

    /** Get memory size from memory field. */
    int getMemorySize() throws NumberFormatException;

    /**
     * Change the enable state of the run button.
     *
     * @param enabled
     *         <code>true</code> to enable the button, <code>false</code> to disable it
     */
    void setEnabledRunButton(boolean enabled);
}