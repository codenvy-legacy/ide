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
package com.codenvy.runner.sdk;

import com.codenvy.api.runner.dto.RunRequest;
import com.codenvy.api.runner.internal.RunnerConfiguration;

/**
 * Configuration of Codenvy extensions runner.
 *
 * @author Artem Zatsarynnyy
 */
public class SDKRunnerConfiguration extends RunnerConfiguration {
    private final String server;
    /** Specifies the domain name or IP address of the code server. */
    private final String codeServerBindAddress;
    /** Specifies the HTTP port for the code server. */
    private final int    codeServerPort;
    private final int    httpPort;

    private String debugTransport;

    public SDKRunnerConfiguration(String server,
                                  int memory,
                                  int httpPort,
                                  String codeServerBindAddress,
                                  int codeServerPort,
                                  RunRequest runRequest) {
        super(memory, runRequest);
        this.server = server;
        this.httpPort = httpPort;
        this.codeServerBindAddress = codeServerBindAddress;
        this.codeServerPort = codeServerPort;
    }

    public String getServer() {
        return server;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public String getCodeServerBindAddress() {
        return codeServerBindAddress;
    }

    public int getCodeServerPort() {
        return codeServerPort;
    }

    public String getDebugTransport() {
        return debugTransport;
    }

    public void setDebugTransport(String debugTransport) {
        this.debugTransport = debugTransport;
    }

    @Override
    public String toString() {
        return "SDKRunnerConfiguration{" +
               "memory=" + getMemory() +
               ", codeServerPort=" + codeServerPort +
               ", codeServerBindAddress='" + codeServerBindAddress + '\'' +
               ", links=" + getLinks() +
               ", request=" + getRequest() +
               ", debugHost='" + getDebugHost() + '\'' +
               ", debugPort=" + getDebugPort() +
               ", debugSuspend=" + isDebugSuspend() +
               ", server='" + server + '\'' +
               '}';
    }
}
