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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.RunnerEnvironment;
import com.codenvy.ide.api.mvp.View;
import com.codenvy.ide.collections.Array;

import javax.validation.constraints.NotNull;

/**
 * The view of {@link RunOptionsPresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface RunOptionsView extends View<RunOptionsView.ActionDelegate> {

    /** Needs for delegate some function into Commit view. */
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