/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2014] Codenvy, S.A. 
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
package com.codenvy.runner.docker.json;

/** @author andrew00x */
public class PortBinding {
    private String hostIp;
    private String hostPort;

    public String getHostIp() {
        return hostIp;
    }

    public void setHostIp(String hostIp) {
        this.hostIp = hostIp;
    }

    public String getHostPort() {
        return hostPort;
    }

    public void setHostPort(String hostPort) {
        this.hostPort = hostPort;
    }

    @Override
    public String toString() {
        return "PortBinding{" +
               "hostIp='" + hostIp + '\'' +
               ", hostPort='" + hostPort + '\'' +
               '}';
    }

    public PortBinding withHostIp(String hostIp) {
        this.hostIp = hostIp;
        return this;
    }

    public PortBinding withHostPort(String hostPort) {
        this.hostPort = hostPort;
        return this;
    }
}
