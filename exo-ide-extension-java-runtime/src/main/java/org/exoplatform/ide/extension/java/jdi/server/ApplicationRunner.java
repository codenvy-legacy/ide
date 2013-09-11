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
package org.exoplatform.ide.extension.java.jdi.server;

import org.exoplatform.ide.extension.java.jdi.shared.ApplicationInstance;

import java.net.URL;
import java.util.Map;

/**
 * Java application runner.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ApplicationRunner {
    /**
     * Run Java web application.
     *
     * @param war
     *         location of .war file. It may be local or remote location
     * @param params
     *         optional application params, may be <code>null</code> or empty
     * @return description of deployed application
     * @throws ApplicationRunnerException
     *         if any error occur when try to deploy application
     * @see ApplicationRunnerService#stopApplication(String)
     * @see ApplicationInstance
     */
    ApplicationInstance runApplication(URL war, Map<String, String> params) throws ApplicationRunnerException;

    /**
     * Run Java web application in debug mode.
     *
     * @param war
     *         location of .war file. It may be local or remote location
     * @param suspend
     *         if <code>true</code> wait on startup for debugger connect
     * @param params
     *         optional application params, may be <code>null</code> or empty
     * @return description of deployed application
     * @throws ApplicationRunnerException
     *         if any error occur when try to deploy application
     * @see ApplicationRunnerService#stopApplication(String)
     * @see ApplicationInstance
     */
    ApplicationInstance debugApplication(URL war, boolean suspend, Map<String, String> params)
            throws ApplicationRunnerException;

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
     * Stop application.
     *
     * @param name
     *         name of application
     * @throws ApplicationRunnerException
     *         if any error occur when try to stop application
     * @see ApplicationInstance#getName()
     */
    void stopApplication(String name) throws ApplicationRunnerException;

    /**
     * Prolong the expiration time of the application.
     *
     * @param name
     *         name of application
     * @param time
     *         time on which is need to prolong the application's expiration time
     * @throws ApplicationRunnerException
     *         if any error occur when try to prolong the expiration time
     * @see ApplicationRunnerService#prolongExpirationTime(String, long)
     * @see ApplicationInstance
     */
    void prolongExpirationTime(String name, long time) throws ApplicationRunnerException;

    /**
     * Update already deployed Java web application.
     *
     * @param war
     *         location of .war file. It may be local or remote location. File from this location will be used for update.
     * @throws ApplicationRunnerException
     *         if any error occur when try to update application
     */
    void updateApplication(String name, URL war) throws ApplicationRunnerException;
}
