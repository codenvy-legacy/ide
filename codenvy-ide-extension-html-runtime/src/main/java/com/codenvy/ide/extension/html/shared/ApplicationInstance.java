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
package com.codenvy.ide.extension.html.shared;

/**
 * Interface represents application instance.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInstance.java Jun 26, 2013 1:05:22 PM azatsarynnyy $
 */
public interface ApplicationInstance {
    /**
     * Returns name of the runned application.
     * 
     * @return runned app's name
     */
    String getName();

    /**
     * Set name of the runned application.
     * 
     * @param name runned app's name
     */
    void setName(String name);

    /**
     * Returns port of the runned application.
     * 
     * @return runned app's port
     */
    int getPort();

    /**
     * Set port of the runned application.
     * 
     * @param port runned app's port
     */
    void setPort(int port);

    /**
     * Returns lifetime of application instance in minutes. After this time application may be stopped automatically. Method may return -1
     * if lifetime of instance is unknown.
     * 
     * @return application lifetime, in minutes
     */
    int getLifetime();

    /**
     * Set application lifetime, in minutes.
     * 
     * @param lifetime application lifetime, in minutes
     */
    void setLifetime(int lifetime);
}
