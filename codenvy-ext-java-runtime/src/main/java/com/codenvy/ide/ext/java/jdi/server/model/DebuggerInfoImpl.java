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
package com.codenvy.ide.ext.java.jdi.server.model;

import com.codenvy.ide.ext.java.jdi.shared.DebuggerInfo;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DebuggerInfoImpl implements DebuggerInfo {
    private String host;
    private int    port;
    private String id;
    private String vmName;
    private String vmVersion;

    public DebuggerInfoImpl(String host,
                            int port,
                            String id,
                            String vmName,
                            String vmVersion) {
        this.host = host;
        this.port = port;
        this.id = id;
        this.vmName = vmName;
        this.vmVersion = vmVersion;
    }

    public DebuggerInfoImpl() {
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getVmName() {
        return vmName;
    }

    @Override
    public String getVmVersion() {
        return vmVersion;
    }

    @Override
    public void setHost(String host) {
        this.host = host;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void setVmName(String vmName) {
        this.vmName = vmName;
    }

    @Override
    public void setVmVersion(String vmVersion) {
        this.vmVersion = vmVersion;
    }

    @Override
    public String toString() {
        return "DebuggerInfoImpl{" +
               "host='" + host + '\'' +
               ", port=" + port +
               ", id='" + id + '\'' +
               ", vmName='" + vmName + '\'' +
               ", vmVersion='" + vmVersion + '\'' +
               '}';
    }
}
