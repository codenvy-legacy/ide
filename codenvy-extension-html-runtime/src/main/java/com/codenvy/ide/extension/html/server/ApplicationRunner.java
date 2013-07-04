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
package com.codenvy.ide.extension.html.server;

import com.codenvy.ide.extension.html.shared.ApplicationInstance;

import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * HTML application runner.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationRunner.java Jun 26, 2013 2:23:09 PM azatsarynnyy $
 */
public interface ApplicationRunner {
    /**
     * Run HTML application.
     * 
     * @param vfs virtual file system that contains project
     * @param projectId ID of project folder
     * @param wsMountPath mount path for the project's workspace
     * @return description of the runned application
     * @throws ApplicationRunnerException if any error occur when try to deploy application
     * @throws VirtualFileSystemException if any error occur when try to access application files over Virtual File System
     * @see HtmlApplicationRunnerService#stopApplication(String)
     * @see ApplicationInstance
     */
    ApplicationInstance runApplication(VirtualFileSystem vfs, String projectId, String wsMountPath) throws ApplicationRunnerException,
                                                                                                   VirtualFileSystemException;

    /**
     * Stop HTML application.
     * 
     * @param name name of application to stop
     * @throws ApplicationRunnerException if any error occur when try to stop application
     * @see ApplicationInstance#getName()
     */
    void stopApplication(String name) throws ApplicationRunnerException;

    /**
     * Returns runned application by name.
     * 
     * @param name name of the runned application
     * @return description of the runned application
     * @throws ApplicationRunnerException if application wasn't found
     */
    RunnedApplication getApplicationByName(String name) throws ApplicationRunnerException;
}
