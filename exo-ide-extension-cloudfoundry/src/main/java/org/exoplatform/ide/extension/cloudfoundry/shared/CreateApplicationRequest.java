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
package org.exoplatform.ide.extension.cloudfoundry.shared;

/**
 * @author <a href="mailto:azhuleva@exoplatform.com">Ann Shumilova</a>
 * @version $Id: Jun 7, 2012 11:49:20 AM anya $
 */
public interface CreateApplicationRequest {

    /** @return the server */
    public abstract String getServer();

    /**
     * @param server
     *         the server to set
     */
    public abstract void setServer(String server);

    /** @return the name */
    public abstract String getName();

    /**
     * @param name
     *         the name to set
     */
    public abstract void setName(String name);

    /** @return the type */
    public abstract String getType();

    /**
     * @param type
     *         the type to set
     */
    public abstract void setType(String type);

    /** @return the url */
    public abstract String getUrl();

    /**
     * @param url
     *         the url to set
     */
    public abstract void setUrl(String url);

    /** @return the instances */
    public abstract int getInstances();

    /**
     * @param instances
     *         the instances to set
     */
    public abstract void setInstances(int instances);

    /** @return the memory */
    public abstract int getMemory();

    /**
     * @param memory
     *         the memory to set
     */
    public abstract void setMemory(int memory);

    /** @return the nostart */
    public abstract boolean isNostart();

    /**
     * @param nostart
     *         the nostart to set
     */
    public abstract void setNostart(boolean nostart);

    /** @return the vfsId */
    public abstract String getVfsid();

    /**
     * @param vfsId
     *         the vfsId to set
     */
    public abstract void setVfsid(String vfsId);

    /** @return the projectId */
    public abstract String getProjectid();

    /**
     * @param projectId
     *         the projectId to set
     */
    public abstract void setProjectid(String projectId);

    /** @return the war */
    public abstract String getWar();

    /**
     * @param war
     *         the war to set
     */
    public abstract void setWar(String war);

}