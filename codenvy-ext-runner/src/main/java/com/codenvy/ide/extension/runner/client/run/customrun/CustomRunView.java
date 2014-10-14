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
package com.codenvy.ide.extension.runner.client.run.customrun;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.mvp.View;

import javax.annotation.Nullable;

/**
 * The view of {@link CustomRunPresenter}.
 *
 * @author Artem Zatsarynnyy
 */
public interface CustomRunView extends View<CustomRunView.ActionDelegate> {

    /**
     * Add environment to the tree.
     *
     * @param environmentTree
     *         environment to add
     */
    void addRunner(RunnerEnvironmentTree environmentTree);

    /**
     * Set description field value.
     *
     * @param description
     *         description to set
     */
    void setEnvironmentDescription(String description);

    /** Get memory size for runner. */
    String getRunnerMemorySize();

    /** Set memory size for runner. */
    void setRunnerMemorySize(String runnerRam);

    /** Get Total Workspace RAM. */
    String getTotalMemorySize();

    /** Set Total Workspace RAM. */
    void setTotalMemorySize(String memorySize);

    /** Get Available memory size. */
    String getAvailableMemorySize();

    /**
     * Set Available memory size.
     * It's value is calculated as (Workspace RAM - RAM allocated to current Runners)
     */
    void setAvailableMemorySize(String memorySize);

    /** Set "enable" state of the 'Run' button. */
    void setRunButtonState(boolean enabled);

    /** Set "enable" state of the radio buttons, the value memory of which is < than workspaceRam. */
    void setEnabledRadioButtons(int workspaceRam);

    /** Close dialog. */
    void close();

    /** Show dialog. */
    void showDialog();

    /** Show warning. */
    void showWarning(String warning);

    /** Performs when user select skip build. */
    boolean isSkipBuildSelected();

    /** Performs when user select 'Remember my options'. */
    boolean isRememberOptionsSelected();

    public interface ActionDelegate {
        /** Performs any actions appropriate in response to the user having pressed the Run button. */
        void onRunClicked();

        /** Performs any actions appropriate in response to the user having pressed the Cancel button. */
        void onCancelClicked();

        /** Performs any actions appropriate in response to the user having selected the environment in the tree. */
        void onEnvironmentSelected(@Nullable RunnerEnvironment environment);
    }
}