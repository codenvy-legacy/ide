/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryApplicationStatistics;

import java.util.Arrays;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplicationStatisticsImpl implements CloudfoundryApplicationStatistics {
    /** Application name. */
    private String name;
    /** Application state. */
    private String state;
    /** IP address. */
    private String host;
    /** Port. */
    private int port = -1;
    /** Application URLs. */
    private String[] uris;

    /** Application uptime. If format X?d:XXh:XXm:XXs. */
    private String uptime;
    /** CPU cores. */
    private int cpuCores = -1;

    /** CPU usage in percents. */
    private double cpu  = -1;
    /** Used memory (in MB). */
    private int    mem  = -1;
    /** Used disk (in MB). */
    private int    disk = -1;

    /** Memory limit (in MB). */
    private int memLimit  = -1;
    /** Disk limit (in MB). */
    private int diskLimit = -1;

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getState() {
        return state;
    }

    @Override
    public void setState(String state) {
        this.state = state;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String[] getUris() {
        return uris;
    }

    @Override
    public void setUris(String[] uris) {
        this.uris = uris;
    }

    @Override
    public String getUptime() {
        return uptime;
    }

    @Override
    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    @Override
    public int getCpuCores() {
        return cpuCores;
    }

    @Override
    public void setCpuCores(int cores) {
        this.cpuCores = cores;
    }

    @Override
    public double getCpu() {
        return cpu;
    }

    @Override
    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    @Override
    public int getMem() {
        return mem;
    }

    @Override
    public void setMem(int mem) {
        this.mem = mem;
    }

    @Override
    public int getDisk() {
        return disk;
    }

    @Override
    public void setDisk(int disk) {
        this.disk = disk;
    }

    @Override
    public int getMemLimit() {
        return memLimit;
    }

    @Override
    public void setMemLimit(int memLimit) {
        this.memLimit = memLimit;
    }

    @Override
    public int getDiskLimit() {
        return diskLimit;
    }

    @Override
    public void setDiskLimit(int diskLimit) {
        this.diskLimit = diskLimit;
    }

    @Override
    public String toString() {
        return "CloudfoundryApplicationStatisticsImpl{" +
               "name='" + name + '\'' +
               ", state='" + state + '\'' +
               ", host='" + host + '\'' +
               ", port=" + port +
               ", uris=" + (uris == null ? null : Arrays.asList(uris)) +
               ", uptime='" + uptime + '\'' +
               ", cpuCores=" + cpuCores +
               ", cpu=" + cpu +
               ", mem=" + mem +
               ", disk=" + disk +
               ", memLimit=" + memLimit +
               ", diskLimit=" + diskLimit +
               '}';
    }
}
