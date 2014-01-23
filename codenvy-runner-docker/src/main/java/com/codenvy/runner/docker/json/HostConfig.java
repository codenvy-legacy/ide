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
package com.codenvy.runner.docker.json;

import java.util.Arrays;
import java.util.Map;

/** @author andrew00x */
public class HostConfig {
    private String[]                   binds;
    private LxcConfParam[]             lxcConf;
    private boolean                    privileged;
    private Map<String, PortBinding[]> portBindings;
    private boolean                    publishAllPorts;

    public String[] getBinds() {
        return binds;
    }

    public void setBinds(String[] binds) {
        this.binds = binds;
    }

    public LxcConfParam[] getLxcConf() {
        return lxcConf;
    }

    public void setLxcConf(LxcConfParam[] lxcConf) {
        this.lxcConf = lxcConf;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public Map<String, PortBinding[]> getPortBindings() {
        return portBindings;
    }

    public void setPortBindings(Map<String, PortBinding[]> portBindings) {
        this.portBindings = portBindings;
    }

    public boolean isPublishAllPorts() {
        return publishAllPorts;
    }

    public void setPublishAllPorts(boolean publishAllPorts) {
        this.publishAllPorts = publishAllPorts;
    }

    @Override
    public String toString() {
        return "HostConfig{" +
               "binds=" + Arrays.toString(binds) +
               ", lxcConf=" + Arrays.toString(lxcConf) +
               ", privileged=" + privileged +
               ", portBindings=" + portBindings +
               ", publishAllPorts=" + publishAllPorts +
               '}';
    }

    // ----------------------

    public HostConfig withBinds(String... binds) {
        this.binds = binds;
        return this;
    }

    public HostConfig withLxcConf(LxcConfParam... lxcConf) {
        this.lxcConf = lxcConf;
        return this;
    }

    public HostConfig withPrivileged(boolean privileged) {
        this.privileged = privileged;
        return this;
    }

    public HostConfig withPortBindings(Map<String, PortBinding[]> portBindings) {
        this.portBindings = portBindings;
        return this;
    }

    public HostConfig withPublishAllPorts(boolean publishAllPorts) {
        this.publishAllPorts = publishAllPorts;
        return this;
    }
}
