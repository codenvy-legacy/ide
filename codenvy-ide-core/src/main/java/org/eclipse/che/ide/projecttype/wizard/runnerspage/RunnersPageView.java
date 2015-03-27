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
package org.eclipse.che.ide.projecttype.wizard.runnerspage;

import org.eclipse.che.api.project.shared.dto.RunnerEnvironment;
import org.eclipse.che.api.project.shared.dto.RunnerEnvironmentTree;
import org.eclipse.che.ide.api.mvp.View;
import com.google.inject.ImplementedBy;

import javax.annotation.Nullable;

/**
 * @author Evgen Vidolob
 */
@ImplementedBy(RunnersPageViewImpl.class)
public interface RunnersPageView extends View<RunnersPageView.ActionDelegate> {

    void showRunnerDescription(String description);

    void addRunner(RunnerEnvironmentTree environmentTree);

    void selectRunnerEnvironment(String environmentId);

    void clearTree();

    public interface ActionDelegate {
        void environmentSelected(@Nullable RunnerEnvironment environment);
    }
}
