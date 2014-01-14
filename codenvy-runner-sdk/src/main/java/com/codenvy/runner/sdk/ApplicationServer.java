/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2013] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.runner.sdk;

import com.codenvy.api.runner.RunnerException;
import com.codenvy.api.runner.internal.ApplicationProcess;

import java.util.zip.ZipFile;

/**
 * Application server to deploy a web app.
 *
 * @author Artem Zatsarynnyy
 */
public interface ApplicationServer {

    /** Application server name. */
    String getName();

    /**
     * Deploy {@code DeploymentSources} to application server.
     *
     * @param appDir
     *         root directory where for application server
     * @param warFile
     *         web app file to deploy
     * @param runnerConfiguration
     *         configuration of application server to run application
     * @param codeServerProcess
     *         may be <code>null</code> if no need to run code server
     * @param stopCallback
     *         an implementation should invoke stopped() method on provided <code>stopCallback</code> when this
     *         application
     *         server stopped
     * @return {@code ApplicationProcess} that represents a deployed app
     * @throws com.codenvy.api.runner.RunnerException
     *         if an error occurs when try to deploy app to application server
     */
    ApplicationProcess deploy(java.io.File appDir, ZipFile warFile, SDKRunnerConfiguration runnerConfiguration,
                              CodeServer.CodeServerProcess codeServerProcess, StopCallback stopCallback) throws RunnerException;

    /** Will be notified when {@code ApplicationServer} stopped. */
    public interface StopCallback {
        void stopped();
    }
}
