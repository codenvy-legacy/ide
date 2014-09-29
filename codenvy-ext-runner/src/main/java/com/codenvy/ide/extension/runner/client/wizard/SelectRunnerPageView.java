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

package com.codenvy.ide.extension.runner.client.wizard;

import com.codenvy.api.runner.dto.RunnerDescriptor;
import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import java.util.Collection;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(SelectRunnerPageViewImpl.class)
public interface SelectRunnerPageView extends View<SelectRunnerPageView.ActionDelegate> {
    void showRunners(Collection<RunnerDescriptor> runnerDescriptors);

    void selectRunner(String runnerName);

    /**
     * Set selected environment.
     *
     * @param environmentName
     *         runner environment
     */
    void setSelectedEnvironment(String environmentName);

    /** Set  recommended memory size for runner. */
    void setRecommendedMemorySize(String recommendedRam);

    /** Get recommended memory size for runner. */
    String getRecommendedMemorySize();

    public interface ActionDelegate{
        void runnerSelected(RunnerDescriptor runner);

        void runnerEnvironmentSelected(String environmentId);

        void recommendedMemoryChanged();
    }
}
