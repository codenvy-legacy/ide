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
package com.codenvy.ide.ext.cloudbees.shared;

/**
 * Application info.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ApplicationInfo.java Mar 15, 2012 9:25:11 AM azatsarynnyy $
 */
public interface ApplicationInfo {
    /** @return the id */
    String getId();

    /** @return the title */
    String getTitle();

    /** @return the status */
    String getStatus();

    /** @return the url */
    String getUrl();

    /** @return the instances */
    @Deprecated
    String getInstances();

    /** @return the securityMode */
    String getSecurityMode();

    /** @return the maxMemory */
    String getMaxMemory();

    /** @return the idleTimeout */
    String getIdleTimeout();

    /** @return the serverPull */
    String getServerPool();

    /** @return the container */
    String getContainer();

    /** @return size of the cluster */
    String getClusterSize();
}