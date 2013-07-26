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
package com.codenvy.ide.ext.extruntime.server;

/**
 * Interface represents a starter of code server processes.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: CodeServerStarter.java Jul 26, 2013 10:23:07 AM azatsarynnyy $
 */
public interface CodeServerStarter {
    /**
     * Starts a new code server process.
     * 
     * @return a new Process object that represents a started code server
     * @throws ExtensionLauncherException if any error has occurred while starting a code server process
     */
    Process start() throws ExtensionLauncherException;

    /**
     * Get code server logs.
     * 
     * @return code server logs
     * @throws ExtensionLauncherException if any error has occurred while getting code server logs
     */
    String getLogs() throws ExtensionLauncherException;
}
