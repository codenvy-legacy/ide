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
package org.exoplatform.ide.extension.cloudfoundry.server.json;

import java.util.Arrays;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StatsInfo {
    private String   name;
    /* IP address. */
    private String   host;
    private int      port;
    private String[] uris;
    private double   uptime;
    /* Memory quota (in bytes). */
    private long     mem_quota;
    /* Disk quota (in bytes). */
    private long     disk_quota;
    /* ??? */
    private long     fds_quota;
    /* CPU cores. */
    private int      cores;

    private StatsUsage usage;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public double getUptime() {
        return uptime;
    }

    public void setUptime(double uptime) {
        this.uptime = uptime;
    }

    public long getMem_quota() {
        return mem_quota;
    }

    public void setMem_quota(long mem_quota) {
        this.mem_quota = mem_quota;
    }

    public long getDisk_quota() {
        return disk_quota;
    }

    public void setDisk_quota(long disk_quota) {
        this.disk_quota = disk_quota;
    }

    public long getFds_quota() {
        return fds_quota;
    }

    public void setFds_quota(long fds_quota) {
        this.fds_quota = fds_quota;
    }

    public int getCores() {
        return cores;
    }

    public void setCores(int cores) {
        this.cores = cores;
    }

    public StatsUsage getUsage() {
        return usage;
    }

    public void setUsage(StatsUsage usage) {
        this.usage = usage;
    }

    @Override
    public String toString() {
        return "StatsInfo [name=" + name + ", host=" + host + ", port=" + port + ", uris=" + Arrays.toString(uris)
               + ", uptime=" + uptime + ", mem_quota=" + mem_quota + ", disk_quota=" + disk_quota + ", fds_quota="
               + fds_quota + ", cores=" + cores + ", usage=" + usage + "]";
    }
}
