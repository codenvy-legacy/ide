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

import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.internal.RunnerConfiguration;

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
