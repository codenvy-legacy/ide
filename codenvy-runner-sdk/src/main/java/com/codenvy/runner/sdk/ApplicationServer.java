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

import java.io.File;
import java.util.zip.ZipFile;

/**
 * Application server to deploy a web app.
 *
 * @author Artem Zatsarynnyy
 */
public interface ApplicationServer {

    /** Application server name. */
    String getName();

    String getDescription();

    /**
     * Deploy WAR to application server.
     *
     * @param workDir
     *         root directory for this application server
     * @param warToDeploy
     *         WAR file to deploy
     * @param extensionJar
     *         JAR with extension
     * @param runnerConfiguration
     *         runner configuration
     * @param codeServerProcess
     *         may be <code>null</code> if no need to run GWT Code Server
     * @param callback
     *         Callback
     * @return {@code ApplicationProcess} that represents a deployed app
     * @throws com.codenvy.api.runner.RunnerException
     *         if an error occurs when try to deploy app to application server
     */
    ApplicationProcess deploy(java.io.File workDir,
                              ZipFile warToDeploy,
                              File extensionJar,
                              SDKRunnerConfiguration runnerConfiguration,
                              CodeServer.CodeServerProcess codeServerProcess,
                              ApplicationProcess.Callback callback) throws RunnerException;
}
