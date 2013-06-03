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
package com.codenvy.ide.ext.java.jdi.shared;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public interface ApplicationInstance {
    String getName();

    String getHost();

    int getPort();

    String getStopURL();

    /**
     * Lifetime of application instance in minutes. After this time instance may be stopped.
     * Method may return -1 if lifetime of instance is unknown.
     *
     * @return application instance lifetime in minutes
     */
    int getLifetime();

    // when application started under debug.

    String getDebugHost();

    int getDebugPort();
}