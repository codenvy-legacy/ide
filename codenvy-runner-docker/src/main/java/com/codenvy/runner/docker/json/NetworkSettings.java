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

/** @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a> */
public class NetworkSettings {
    private String   ipAddress;
    private int      ipPrefixLen;
    private String   gateway;
    private String   bridge; // TODO : check it
    private String[] portMapping;

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getIpPrefixLen() {
        return ipPrefixLen;
    }

    public void setIpPrefixLen(int ipPrefixLen) {
        this.ipPrefixLen = ipPrefixLen;
    }

    public String getGateway() {
        return gateway;
    }

    public void setGateway(String gateway) {
        this.gateway = gateway;
    }

    public String getBridge() {
        return bridge;
    }

    public void setBridge(String bridge) {
        this.bridge = bridge;
    }

    public String[] getPortMapping() {
        return portMapping;
    }

    public void setPortMapping(String[] portMapping) {
        this.portMapping = portMapping;
    }

    @Override
    public String toString() {
        return "NetworkSettings{" +
               "ipAddress='" + ipAddress + '\'' +
               ", ipPrefixLen=" + ipPrefixLen +
               ", gateway='" + gateway + '\'' +
               ", bridge='" + bridge + '\'' +
               ", portMapping=" + Arrays.toString(portMapping) +
               '}';
    }
}
