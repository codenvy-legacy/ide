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
