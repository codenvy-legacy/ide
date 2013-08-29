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
package org.exoplatform.ide.testframework.server.cloudfoundry.bean;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryApplicationStatistics {
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
    private double cpu = -1;

    /** Used memory (in MB). */
    private int mem = -1;

    /** Used disk (in MB). */
    private int disk = -1;

    /** Memory limit (in MB). */
    private int memLimit = -1;

    /** Disk limit (in MB). */
    private int diskLimit = -1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String[] getUris() {
        return uris;
    }

    public void setUris(String[] uris) {
        this.uris = uris;
    }

    public String getUptime() {
        return uptime;
    }

    public void setUptime(String uptime) {
        this.uptime = uptime;
    }

    public int getCpuCores() {
        return cpuCores;
    }

    public void setCpuCores(int cores) {
        this.cpuCores = cores;
    }

    public double getCpu() {
        return cpu;
    }

    public void setCpu(double cpu) {
        this.cpu = cpu;
    }

    public int getMem() {
        return mem;
    }

    public void setMem(int mem) {
        this.mem = mem;
    }

    public int getDisk() {
        return disk;
    }

    public void setDisk(int disk) {
        this.disk = disk;
    }

    public int getMemLimit() {
        return memLimit;
    }

    public void setMemLimit(int memLimit) {
        this.memLimit = memLimit;
    }

    public int getDiskLimit() {
        return diskLimit;
    }

    public void setDiskLimit(int diskLimit) {
        this.diskLimit = diskLimit;
    }
}
