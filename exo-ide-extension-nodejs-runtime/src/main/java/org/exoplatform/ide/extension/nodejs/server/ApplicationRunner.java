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
package org.exoplatform.ide.extension.nodejs.server;

import org.exoplatform.ide.extension.nodejs.shared.ApplicationInstance;
import org.exoplatform.ide.vfs.server.VirtualFileSystem;
import org.exoplatform.ide.vfs.server.exceptions.VirtualFileSystemException;

/**
 * Node.js application runner.
 * 
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: ApplicationRunner.java Apr 18, 2013 5:20:17 PM vsvydenko $
 *
 */
public interface ApplicationRunner {
    /**
     * Run Node.js application.
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
     * @see org.exoplatform.ide.extension.nodejs.shared.ApplicationInstance
     */
    ApplicationInstance runApplication(VirtualFileSystem vfs, String projectId) throws ApplicationRunnerException,
                                                                                       VirtualFileSystemException;

    /**
     * Get application logs.
     *
     * @param name
     *         name of application
     * @return logs
     * @throws ApplicationRunnerException
     *         if any error occur when try to get application logs
     */
    String getLogs(String name) throws ApplicationRunnerException;

    /**
     * Stop Node.js application.
     *
     * @param name
     *         name of application
     * @throws ApplicationRunnerException
     *         if any error occur when try to stop application
     * @see ApplicationInstance#getName()
     */
    void stopApplication(String name) throws ApplicationRunnerException;
}
