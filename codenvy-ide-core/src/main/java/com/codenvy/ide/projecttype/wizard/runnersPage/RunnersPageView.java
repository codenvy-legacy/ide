/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.projecttype.wizard.runnersPage;

import com.codenvy.api.project.shared.dto.RunnerEnvironment;
import com.codenvy.api.project.shared.dto.RunnerEnvironmentTree;
import com.codenvy.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.annotation.Nullable;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(RunnersPageViewImpl.class)
public interface RunnersPageView extends View<RunnersPageView.ActionDelegate> {
    /** Get recommended memory size for runner. */
    int getRecommendedMemorySize();

    /** Set  recommended memory size for runner. */
    void setRecommendedMemorySize(int recommendedRam);

    void showRunnerDescription(String description);

    void addRunner(RunnerEnvironmentTree environmentTree);

    void selectRunnerEnvironment(String environmentId);

    public interface ActionDelegate {
        void recommendedMemoryChanged();

        void environmentSelected(@Nullable RunnerEnvironment environment);
    }
}
