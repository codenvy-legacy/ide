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
package org.exoplatform.ide.client.framework.workspaceinfo;

/**
 * Interface describe information about current workspace.
 *
 * @author <a href="mailto:vsvydenko@codenvy.com">Valeriy Svydenko</a>
 * @version $Id: CurrentWorkspaceInfo.java 
 */
public interface CurrentWorkspaceInfo {
    
    /**
     * Returns the workspace's URL.
     *
     * @return workspace's URL
     */
    public String getUrl();

    /**
     * Change the workspace's URL.
     *
     * @param name
     *         workspace's URL
     */
    public void setUrl(String url); 

    /**
     * Returns the workspace's temporary.
     *
     * @return workspace's temporary
     */
    public boolean isTemporary();

    /**
     * Change the workspace's temporary.
     *
     * @param name
     *         workspace's temporary
     */
    public void setTemporary(boolean temporary);

    /**
     * Returns the workspace's name.
     *
     * @return workspace's name
     */
    public String getName();

    /**
     * Change the workspace's name.
     *
     * @param name
     *         workspace's name
     */
    public void setName(String name);

    /**
     * Returns the workspace's id.
     *
     * @return workspace's id
     */
    public String getId();

    /**
     * Change the workspace's id.
     *
     * @param id
     *         workspace's id
     */
    public void setId(String id);
}
