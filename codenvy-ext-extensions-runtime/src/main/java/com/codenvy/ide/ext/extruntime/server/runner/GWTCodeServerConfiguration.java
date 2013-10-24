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
package com.codenvy.ide.ext.extruntime.server.runner;

import java.nio.file.Path;

/**
 * GWT code server configuration.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GWTCodeServerConfiguration.java Aug 8, 2013 5:35:00 PM azatsarynnyy $
 */
public class GWTCodeServerConfiguration {
    /** GWT code server's bind address. */
    private       String bindAddress;
    /** GWT code server's port. */
    private       int    port;
    /**
     * GWT code server's working directory for internal use. It's the root of the directory tree where the GWT code
     * server will write compiler output. If not supplied, a system temporary directory should be used.
     */
    private       Path   workDir;
    private final String customModuleName;

    /**
     * Constructs new {@link GWTCodeServerConfiguration} with the specified parameters.
     *
     * @param bindAddress
     *         code server bind address
     * @param port
     *         code server port
     * @param workDir
     * @param customModuleName
     */
    public GWTCodeServerConfiguration(String bindAddress, int port, Path workDir, String customModuleName) {
        this.bindAddress = bindAddress;
        this.port = port;
        this.workDir = workDir;
        this.customModuleName = customModuleName;
    }

    /**
     * Returns code server's bind address.
     *
     * @return the code server's bind address
     */
    public String getBindAddress() {
        return bindAddress;
    }

    /**
     * Set the code server's bind address.
     *
     * @param port
     *         the code server's bind address to set
     */
    public void setBindAddress(String bindAddress) {
        this.bindAddress = bindAddress;
    }

    /**
     * Returns code server's port.
     *
     * @return the code server's port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the code server's port.
     *
     * @param port
     *         the code server's port to set
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Returns code server working directory.
     *
     * @return code server working directory
     */
    public Path getWorkDir() {
        return workDir;
    }

    /**
     * Set the code server working directory.
     *
     * @param workDir
     *         the code server working directory
     */
    public void setWorkDir(Path workDir) {
        this.workDir = workDir;
    }

    public String getCustomModuleName() {
        return customModuleName;
    }
}
