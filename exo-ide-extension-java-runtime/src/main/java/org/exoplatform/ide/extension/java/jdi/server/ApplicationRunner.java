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
import org.exoplatform.ide.extension.java.jdi.shared.DebugApplicationInstance;

import java.net.URL;

/**
 * Java application runner.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ApplicationRunner
{
   /**
    * Run Java web application.
    *
    * @param war
    *    location of .war file. It may be local or remote location
    * @return description of deployed application
    * @throws ApplicationRunnerException
    *    if any error occur when try to deploy application
    * @see ApplicationRunnerService#stopApplication(String)
    * @see ApplicationInstance
    */
   ApplicationInstance runApplication(URL war) throws ApplicationRunnerException;

   /**
    * Run Java web application in debug mode.
    *
    * @param war
    *    location of .war file. It may be local or remote location
    * @param suspend
    *    if <code>true</code> wait on startup for debugger connect
    * @return description of deployed application
    * @throws ApplicationRunnerException
    *    if any error occur when try to deploy application
    * @see ApplicationRunnerService#stopApplication(String)
    * @see ApplicationInstance
    */
   DebugApplicationInstance debugApplication(URL war, boolean suspend) throws ApplicationRunnerException;

   /**
    * Get application logs.
    *
    * @param name
    *    name of application
    * @return logs
    * @throws ApplicationRunnerException
    *    if any error occur when try to get application logs
    */
   String getLogs(String name) throws ApplicationRunnerException;

   /**
    * Stop application.
    *
    * @param name
    *    name of application
    * @throws ApplicationRunnerException
    *    if any error occur when try to stop application
    * @see ApplicationInstance#getName()
    */
   void stopApplication(String name) throws ApplicationRunnerException;
}
