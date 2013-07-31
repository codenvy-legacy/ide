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

import com.codenvy.ide.ext.extruntime.server.ExtensionLauncherException;

import java.nio.file.Path;

/**
 * Interface represents a code servers starter. Class that implements this
 * interface should provide an implementation of starting of a code server.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CodeServerStarter.java Jul 26, 2013 10:23:07 AM azatsarynnyy $
 */
public interface CodeServerStarter {
    /**
     * Starts a new code server.
     * 
     * @param workingDirectory working directory of a code server
     * @return a new code server that started
     * @throws ExtensionLauncherException if any error has occurred while starting a code server
     */
    CodeServer start(Path workingDirectory) throws ExtensionLauncherException;

    /** Interface represents a code server. */
    public interface CodeServer {
        /**
         * Get code server's logs.
         * 
         * @return code server's logs
         * @throws ExtensionLauncherException if any error has occurred while getting code server logs
         */
        String getLogs() throws ExtensionLauncherException;

        /** Stop a code server. */
        void stop();
    }
}
