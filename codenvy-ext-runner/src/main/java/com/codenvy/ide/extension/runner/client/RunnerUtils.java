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
package com.codenvy.ide.extension.runner.client;

import com.codenvy.api.runner.dto.ApplicationProcessDescriptor;
import com.codenvy.api.runner.dto.RunnerMetric;

import javax.annotation.Nullable;
import javax.validation.constraints.NotNull;

/**
 * @author Vitaly Parfonov
 */
public class RunnerUtils {

    @Nullable
    public static RunnerMetric getRunnerMetric(@NotNull ApplicationProcessDescriptor processDescriptor, String metricName) {
        for (RunnerMetric runnerStat : processDescriptor.getRunStats()) {
            if (metricName.equals(runnerStat.getName())) {
                return runnerStat;
            }
        }
        return null;
    }
}
