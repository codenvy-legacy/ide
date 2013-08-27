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
package org.exoplatform.ide.extension.appfog.server.json;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class InstanceInfo {
    private int    index;
    private String state;
    private long   since;
    private String debug_ip;
    private int    debug_port;
    private String console_ip;
    private int    console_port;

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getSince() {
        return since;
    }

    public void setSince(long since) {
        this.since = since;
    }

    public String getDebug_ip() {
        return debug_ip;
    }

    public void setDebug_ip(String debugIp) {
        this.debug_ip = debugIp;
    }

    public int getDebug_port() {
        return debug_port;
    }

    public void setDebug_port(int debugPort) {
        this.debug_port = debugPort;
    }

    public String getConsole_ip() {
        return console_ip;
    }

    public void setConsole_ip(String consoleIp) {
        this.console_ip = consoleIp;
    }

    public int getConsole_port() {
        return console_port;
    }

    public void setConsole_port(int consolePort) {
        this.console_port = consolePort;
    }

    @Override
    public String toString() {
        return "InstanceInfo{" +
               "index=" + index +
               ", state='" + state + '\'' +
               ", since=" + since +
               ", debug_ip='" + debug_ip + '\'' +
               ", debug_port=" + debug_port +
               ", console_ip='" + console_ip + '\'' +
               ", console_port=" + console_port +
               '}';
    }
}
