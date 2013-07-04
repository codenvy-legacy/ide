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

/**
 * Class represents runned HTML-application.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: RunnedApplication.java Jul 1, 2013 11:55:48 AM azatsarynnyy $
 */
class RunnedApplication {
    final String name;
    final String projectName;
    final long   expirationTime;
    final String projectPath;

    RunnedApplication(String name, long expirationTime, String projectName, String projectPath) {
        this.name = name;
        this.expirationTime = expirationTime;
        this.projectName = projectName;
        this.projectPath = projectPath;
    }

    boolean isExpired() {
        return expirationTime < System.currentTimeMillis();
    }
}
