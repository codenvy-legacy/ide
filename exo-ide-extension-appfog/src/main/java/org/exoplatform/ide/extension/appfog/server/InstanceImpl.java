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
package org.exoplatform.ide.extension.appfog.server;


import org.exoplatform.ide.extension.appfog.shared.Instance;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InstanceImpl implements Instance {
    private String debugHost;
    private int    debugPort;
    private String consoleHost;
    private int    consolePort;

    public InstanceImpl(String debugHost, int debugPort, String consoleHost, int consolePort) {
        this.debugHost = debugHost;
        this.debugPort = debugPort;
        this.consoleHost = consoleHost;
        this.consolePort = consolePort;
    }

    public InstanceImpl() {
    }

    @Override
    public String getDebugHost() {
        return debugHost;
    }

    @Override
    public void setDebugHost(String host) {
        debugHost = host;
    }

    @Override
    public int getDebugPort() {
        return debugPort;
    }

    @Override
    public void setDebugPort(int port) {
        debugPort = port;
    }

    @Override
    public String getConsoleHost() {
        return consoleHost;
    }

    @Override
    public void setConsoleHost(String host) {
        consoleHost = host;
    }

    @Override
    public int getConsolePort() {
        return consolePort;
    }

    @Override
    public void setConsolePort(int port) {
        consolePort = port;
    }

    @Override
    public String toString() {
        return "InstanceImpl{" +
               "debugHost='" + debugHost + '\'' +
               ", debugPort=" + debugPort +
               ", consoleHost='" + consoleHost + '\'' +
               ", consolePort=" + consolePort +
               '}';
    }
}
