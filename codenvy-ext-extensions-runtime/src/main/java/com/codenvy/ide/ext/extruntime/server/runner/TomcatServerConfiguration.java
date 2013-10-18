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

import java.net.URL;
import java.nio.file.Path;

/**
 * Tomcat server configuration.
 *
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: TomcatServerConfiguration.java Aug 8, 2013 5:35:00 PM azatsarynnyy $
 */
public class TomcatServerConfiguration {
    private Path   workDir;
    private int    port;
    private URL    ideWarUrl;

    public TomcatServerConfiguration(Path workDir, int port, URL ideWarUrl) {
        this.workDir = workDir;
        this.port = port;
        this.ideWarUrl = ideWarUrl;
    }

    public Path getWorkDir() {
        return workDir;
    }

    public int getPort() {
        return port;
    }

    public URL getIdeWarUrl() {
        return ideWarUrl;
    }
}
