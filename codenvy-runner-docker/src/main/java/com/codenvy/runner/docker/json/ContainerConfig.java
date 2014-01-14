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
import java.util.HashMap;
import java.util.Map;

/** @author andrew00x */
public class ContainerConfig {
    private String hostname = "";
    private String user     = "";
    private boolean  privileged;
    private String[] env;
    private int      cpuShares;
    private long     memory;
    private long     memorySwap;
    private boolean  attachStdin;
    private boolean  attachStdout;
    private boolean  attachStderr;
    // From docker code:
    // We will receive port specs in the format of ip:public:private/proto
    private String[] portSpecs;
    private boolean  tty;
    private boolean  openStdin;
    private boolean  stdinOnce;
    private String[] cmd;
    private String   dns;
    private String   image;
    private Map<String, String> volumes     = new HashMap<>();
    private String              volumesFrom = "";
    private String              workingDir  = "";
    private boolean networkDisabled;

    public String getHostname() {
        return hostname;
    }

    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public boolean isPrivileged() {
        return privileged;
    }

    public void setPrivileged(boolean privileged) {
        this.privileged = privileged;
    }

    public String[] getEnv() {
        return env;
    }

    public void setEnv(String[] env) {
        this.env = env;
    }

    public int getCpuShares() {
        return cpuShares;
    }

    public void setCpuShares(int cpuShares) {
        this.cpuShares = cpuShares;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    public long getMemorySwap() {
        return memorySwap;
    }

    public void setMemorySwap(long memorySwap) {
        this.memorySwap = memorySwap;
    }

    public boolean isAttachStdin() {
        return attachStdin;
    }

    public void setAttachStdin(boolean attachStdin) {
        this.attachStdin = attachStdin;
    }

    public boolean isAttachStdout() {
        return attachStdout;
    }

    public void setAttachStdout(boolean attachStdout) {
        this.attachStdout = attachStdout;
    }

    public boolean isAttachStderr() {
        return attachStderr;
    }

    public void setAttachStderr(boolean attachStderr) {
        this.attachStderr = attachStderr;
    }

    public String[] getPortSpecs() {
        return portSpecs;
    }

    public void setPortSpecs(String[] portSpecs) {
        this.portSpecs = portSpecs;
    }

    public boolean isTty() {
        return tty;
    }

    public void setTty(boolean tty) {
        this.tty = tty;
    }

    public boolean isOpenStdin() {
        return openStdin;
    }

    public void setOpenStdin(boolean openStdin) {
        this.openStdin = openStdin;
    }

    public boolean isStdinOnce() {
        return stdinOnce;
    }

    public void setStdinOnce(boolean stdinOnce) {
        this.stdinOnce = stdinOnce;
    }

    public String[] getCmd() {
        return cmd;
    }

    public void setCmd(String[] cmd) {
        this.cmd = cmd;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Map<String, String> getVolumes() {
        return volumes;
    }

    public void setVolumes(Map<String, String> volumes) {
        this.volumes = volumes;
    }

    public String getVolumesFrom() {
        return volumesFrom;
    }

    public void setVolumesFrom(String volumesFrom) {
        this.volumesFrom = volumesFrom;
    }

    public String getWorkingDir() {
        return workingDir;
    }

    public void setWorkingDir(String workingDir) {
        this.workingDir = workingDir;
    }

    public boolean isNetworkDisabled() {
        return networkDisabled;
    }

    public void setNetworkDisabled(boolean networkDisabled) {
        this.networkDisabled = networkDisabled;
    }

    @Override
    public String toString() {
        return "ContainerConfig{" +
               "hostname='" + hostname + '\'' +
               ", user='" + user + '\'' +
               ", privileged=" + privileged +
               ", env=" + Arrays.toString(env) +
               ", cpuShares=" + cpuShares +
               ", memory=" + memory +
               ", memorySwap=" + memorySwap +
               ", attachStdin=" + attachStdin +
               ", attachStdout=" + attachStdout +
               ", attachStderr=" + attachStderr +
               ", portSpecs=" + Arrays.toString(portSpecs) +
               ", tty=" + tty +
               ", openStdin=" + openStdin +
               ", stdinOnce=" + stdinOnce +
               ", cmd=" + Arrays.toString(cmd) +
               ", dns='" + dns + '\'' +
               ", image='" + image + '\'' +
               ", volumes=" + volumes +
               ", volumesFrom='" + volumesFrom + '\'' +
               ", workingDir='" + workingDir + '\'' +
               ", networkDisabled=" + networkDisabled +
               '}';
    }

    // -------------------

    public ContainerConfig withHostname(String hostname) {
        this.hostname = hostname;
        return this;
    }

    public ContainerConfig withUser(String user) {
        this.user = user;
        return this;
    }

    public ContainerConfig withPrivileged(boolean privileged) {
        this.privileged = privileged;
        return this;
    }

    public ContainerConfig withEnv(String... env) {
        this.env = env;
        return this;
    }

    public ContainerConfig withCpuShares(int cpuShares) {
        this.cpuShares = cpuShares;
        return this;
    }

    public ContainerConfig withMemory(long memory) {
        this.memory = memory;
        return this;
    }

    public ContainerConfig withMemorySwap(long memorySwap) {
        this.memorySwap = memorySwap;
        return this;
    }

    public ContainerConfig withAttachStdin(boolean attachStdin) {
        this.attachStdin = attachStdin;
        return this;
    }

    public ContainerConfig withAttachStdout(boolean attachStdout) {
        this.attachStdout = attachStdout;
        return this;
    }

    public ContainerConfig withAttachStderr(boolean attachStderr) {
        this.attachStderr = attachStderr;
        return this;
    }

    public ContainerConfig withPortSpecs(String... portSpecs) {
        this.portSpecs = portSpecs;
        return this;
    }

    public ContainerConfig withTty(boolean tty) {
        this.tty = tty;
        return this;
    }

    public ContainerConfig withOpenStdin(boolean openStdin) {
        this.openStdin = openStdin;
        return this;
    }

    public ContainerConfig withStdinOnce(boolean stdinOnce) {
        this.stdinOnce = stdinOnce;
        return this;
    }

    public ContainerConfig withCmd(String... cmd) {
        this.cmd = cmd;
        return this;
    }

    public ContainerConfig withDns(String dns) {
        this.dns = dns;
        return this;
    }

    public ContainerConfig withImage(String image) {
        this.image = image;
        return this;
    }

    public ContainerConfig withVolumes(Map<String, String> volumes) {
        this.volumes = volumes;
        return this;
    }

    public ContainerConfig withVolumesFrom(String volumesFrom) {
        this.volumesFrom = volumesFrom;
        return this;
    }

    public ContainerConfig withWorkingDir(String workingDir) {
        this.workingDir = workingDir;
        return this;
    }

    public ContainerConfig withNetworkDisabled(boolean networkDisabled) {
        this.networkDisabled = networkDisabled;
        return this;
    }
}
