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
package com.codenvy.ide.extension.cloudfoundry.shared;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 7, 2012 11:49:20 AM anya $
 */
public interface CreateApplicationRequest {
    /** @return the server */
    String getServer();

    /** @return the name */
    String getName();

    /** @return the type */
    String getType();

    /** @return the url */
    String getUrl();

    /** @return the instances */
    int getInstances();

    /** @return the memory */
    int getMemory();

    /** @return the nostart */
    boolean isNostart();

    /** @return the vfsId */
    String getVfsid();

    /** @return the projectId */
    String getProjectid();

    /** @return the war */
    String getWar();

    /** @return CloudFoundry provider like CloudFoundry, Tier3 Web Fabric, etc. */
    String getPaasprovider();
}