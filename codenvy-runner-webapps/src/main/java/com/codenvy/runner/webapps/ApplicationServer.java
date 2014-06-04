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
package com.codenvy.runner.webapps;

import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationProcess;
import com.codenvy.api.runner.internal.DeploymentSources;

/**
 * Application server to deploy an app.
 *
 * @author Artem Zatsarynnyy
 */
public interface ApplicationServer {

    /** Application server name. */
    String getName();

    String getDescription();

    /**
     * Deploy {@code DeploymentSources} to application server.
     *
     * @param appDir
     *         root directory where for application server
     * @param toDeploy
     *         {@code DeploymentSources} to deploy
     * @param runnerConfiguration
     *         configuration of application server to run application
     * @param callback
     *         Callback
     * @return {@code ApplicationProcess} that represents a deployed app
     * @throws RunnerException
     *         if an error occurs when try to deploy {@code DeploymentSources} to application server
     */
    ApplicationProcess deploy(java.io.File appDir, DeploymentSources toDeploy, ApplicationServerRunnerConfiguration runnerConfiguration,
                              ApplicationProcess.Callback callback) throws RunnerException;
}
