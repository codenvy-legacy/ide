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
package com.codenvy.ide.ext.extruntime.server.codeserver;

import java.nio.file.Path;

/**
 * GWT code server configuration.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: GWTCodeServerConfiguration.java Aug 8, 2013 5:35:00 PM azatsarynnyy $
 */
public class GWTCodeServerConfiguration {
    /** Code server port. */
    private int  port;

    /** Code server working directory. */
    private Path workDir;

    /**
     * Constructs new {@link GWTCodeServerConfiguration} with the specified parameters.
     * 
     * @param port code server port
     * @param workDir code server working directory
     */
    public GWTCodeServerConfiguration(int port, Path workDir) {
        this.port = port;
        this.workDir = workDir;
    }

    /**
     * Returns code server port.
     * 
     * @return the code server port
     */
    public int getPort() {
        return port;
    }

    /**
     * Set the code server port.
     * 
     * @param port the code server port to set
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
     * @param workDir the code server working directory
     */
    public void setWorkDir(Path workDir) {
        this.workDir = workDir;
    }
}
