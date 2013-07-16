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
package org.exoplatform.ide.client.framework.userinfo;

import com.codenvy.ide.commons.IDEWorkspace;
import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

/**
 * Interface describe information about user.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: UserInfo.java Mar 28, 2012 14:55:45 PM azatsarynnyy $
 */
public interface UserInfo {

    /**
     * Returns the user's name.
     *
     * @return user's name
     */
    @PropertyName(value = "userId")
    public String getName();

    /**
     * Change the user's name.
     *
     * @param name
     *         user's name
     */
    @PropertyName(value = "userId")
    public void setName(String name);

    /**
     * Returns the list of the user's groups.
     *
     * @return the user's groups
     */
    public List<String> getGroups();

    /**
     * Sets the list of the user's groups.
     *
     * @param groups
     *         the user's groups
     */
    public void setGroups(List<String> groups);

    /**
     * Returns the list of the user's roles.
     *
     * @return the user's roles
     */
    public List<String> getRoles();

    /**
     * Sets the list of the user's roles.
     *
     * @param roles
     *         the user's roles
     */
    public void setRoles(List<String> roles);
    
    public void setWorkspaces(List<IDEWorkspace> workspaces);
    
    public List<IDEWorkspace> getWorkspaces();

    public String getClientId();

    public void setClientId(String clientId);

}