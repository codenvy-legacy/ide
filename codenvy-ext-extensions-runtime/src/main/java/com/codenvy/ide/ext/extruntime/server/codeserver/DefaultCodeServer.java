/*
 * Copyright (C) 2013 eXo Platform SAS.
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
