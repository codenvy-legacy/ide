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
}