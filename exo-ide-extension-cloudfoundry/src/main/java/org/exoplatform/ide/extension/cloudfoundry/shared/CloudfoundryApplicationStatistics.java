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
package org.exoplatform.ide.extension.cloudfoundry.shared;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface CloudfoundryApplicationStatistics {
    String getName();

    void setName(String name);

    String getState();

    void setState(String state);

    String getHost();

    void setHost(String host);

    int getPort();

    void setPort(int port);

    String[] getUris();

    void setUris(String[] uris);

    String getUptime();

    void setUptime(String uptime);

    int getCpuCores();

    void setCpuCores(int cores);

    double getCpu();

    void setCpu(double cpu);

    int getMem();

    void setMem(int mem);

    int getDisk();

    void setDisk(int disk);

    int getMemLimit();

    void setMemLimit(int memLimit);

    int getDiskLimit();

    void setDiskLimit(int diskLimit);
}
