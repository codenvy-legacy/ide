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
package com.codenvy.runner.webapps;

import com.codenvy.api.runner.internal.RunnerConfiguration;
import com.codenvy.api.runner.internal.dto.RunRequest;

/**
 * Configuration of Web applications runner.
 *
 * @author Artem Zatsarynnyy
 */
public class ApplicationServerRunnerConfiguration extends RunnerConfiguration {
    private final String server;
    private final int    httpPort;

    private String debugTransport;

    public ApplicationServerRunnerConfiguration(String server, int memory, int httpPort, RunRequest runRequest) {
        super(memory, runRequest);
        this.server = server;
        this.httpPort = httpPort;
    }

    public String getServer() {
        return server;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public String getDebugTransport() {
        return debugTransport;
    }

    public void setDebugTransport(String debugTransport) {
        this.debugTransport = debugTransport;
    }

    @Override
    public String toString() {
        return "ApplicationServerRunnerConfiguration{" +
               "memory=" + getMemory() +
               ", links=" + getLinks() +
               ", request=" + getRequest() +
               ", debugHost='" + getDebugHost() + '\'' +
               ", debugPort=" + getDebugPort() +
               ", debugSuspend=" + isDebugSuspend() +
               ", server='" + server + '\'' +
               '}';
    }
}
