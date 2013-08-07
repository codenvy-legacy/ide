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

import com.codenvy.ide.ext.extruntime.server.codeserver.CodeServerStarter.CodeServer;
import com.codenvy.ide.ext.extruntime.server.tools.ProcessUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Default code server inplementation.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: DefaultCodeServer.java Jul 26, 2013 3:15:52 PM azatsarynnyy $
 */
public class DefaultCodeServer implements CodeServer {
    /** Process that represents a started code server. */
    private Process process;

    /** Path to code server's log file. */
    private Path    logFilePath;

    /**
     * Creates default code server.
     * 
     * @param process {@link Process} that represents this code server
     * @param logFilePath {@link Path} to code server's log file
     */
    public DefaultCodeServer(Process process, Path logFilePath) {
        this.process = process;
        this.logFilePath = logFilePath;
    }

    /** {@inheritDoc} */
    @Override
    public void stop() {
        // TODO
        // Use com.codenvy.api.tools.ProcessUtil from 'codenvy-organization-api' project when it finished.

        // Use ProcessUtil because java.lang.Process.destroy() method doesn't
        // kill all child processes (see http://bugs.sun.com/view_bug.do?bug_id=4770092).
        ProcessUtil.kill(process);
    }

    /** {@inheritDoc} */
    @Override
    public String getLogs() throws CodeServerException {
        try {
            // It should work fine for the files less than 2GB (Integer.MAX_VALUE).
            // One recompiling procedure writes about 1KB output information to logs.
            return new String(Files.readAllBytes(logFilePath));
        } catch (IOException e) {
            throw new CodeServerException("Unable to get code server's logs.");
        }
    }
}
