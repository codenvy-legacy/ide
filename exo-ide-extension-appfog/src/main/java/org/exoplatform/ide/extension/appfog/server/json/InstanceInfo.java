/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
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
