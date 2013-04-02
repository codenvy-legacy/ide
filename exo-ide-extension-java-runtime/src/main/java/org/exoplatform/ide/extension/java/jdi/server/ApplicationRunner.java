/*
 * Copyright (C) 2012 eXo Platform SAS.
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
