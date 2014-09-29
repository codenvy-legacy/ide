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

import com.codenvy.api.runner.dto.RunnerEnvironment;

/**
 * Adapter that allows to display {@link RunnerEnvironment} in {@link CustomRunViewImpl}.
 *
 * @author Artem Zatsarynnyy
 */
public class RunnerEnvironmentAdapter implements Environment {
    private final RunnerEnvironment runnerEnvironment;

    public RunnerEnvironmentAdapter(RunnerEnvironment runnerEnvironment) {
        this.runnerEnvironment = runnerEnvironment;
    }

    @Override
    public String getId() {
        return runnerEnvironment.getId();
    }

    @Override
    public String getDisplayName() {
        return runnerEnvironment.getDisplayName() == null ? runnerEnvironment.getId() : runnerEnvironment.getDisplayName();
    }

    @Override
    public String getDescription() {
        return runnerEnvironment.getDescription();
    }

    /** Get adapted {@link RunnerEnvironment}. */
    public RunnerEnvironment getRunnerEnvironment() {
        return runnerEnvironment;
    }
}
