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
 * PHP application runner.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationRunner.java Apr 17, 2013 4:33:17 PM azatsarynnyy $
 *
 */
public interface ApplicationRunner {
    /**
     * Run PHP application.
     *
     * @param vfs
     *         virtual file system that contains project
     * @param projectId
     *         ID of project folder
     * @return description of deployed application
     * @throws ApplicationRunnerException
     *         if any error occur when try to deploy application
     * @throws VirtualFileSystemException
     *         if any error occur when try to access application files over Virtual File System
     * @see ApplicationRunnerService#stopApplication(String)
     * @see org.exoplatform.ide.extension.php.shared.ApplicationInstance
     */
    ApplicationInstance runApplication(VirtualFileSystem vfs, String projectId) throws ApplicationRunnerException,
                                                                                       VirtualFileSystemException;

    /**
     * Stop PHP application.
     *
     * @param name
     *         name of application
     * @throws ApplicationRunnerException
     *         if any error occur when try to stop application
     * @see ApplicationInstance#getName()
     */
    void stopApplication(String name) throws ApplicationRunnerException;
}
