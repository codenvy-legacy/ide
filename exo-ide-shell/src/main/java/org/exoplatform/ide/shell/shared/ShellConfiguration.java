/*
 * Copyright (C) 2011 eXo Platform SAS.
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
package org.exoplatform.ide.shell.shared;


/**
 * Interface describe shell configuration.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShellConfiguration.java Mar 28, 2012 11:29:43 AM azatsarynnyy $
 */
public interface ShellConfiguration {

    /**
     * Returns information about user.
     *
     * @return the user information
     */
    public UserInfo getUser();

    /**
     * Sets information about user.
     *
     * @param userInfo
     *         the user information
     */
    public void setUser(UserInfo userInfo);

    /**
     * Returns entry point.
     *
     * @return the entry point
     */
    public String getDefaultEntrypoint();

    /**
     * Change the entry point.
     *
     * @param entryPoint
     *         the entry point
     */
    public void setDefaultEntrypoint(String entryPoint);

}