/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
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
