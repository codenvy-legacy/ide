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
package org.exoplatform.ide.extension.java.jdi.server.model;

import org.exoplatform.ide.extension.java.jdi.shared.DebuggerInfo;

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
    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public void setId(String id) {
        this.id = id;
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
