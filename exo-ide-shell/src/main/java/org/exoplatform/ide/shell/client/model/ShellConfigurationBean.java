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
package org.exoplatform.ide.shell.client.model;

import org.exoplatform.ide.shell.shared.ShellConfiguration;
import org.exoplatform.ide.shell.shared.UserInfo;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 12, 2011 evgen $
 */
public class ShellConfigurationBean implements ShellConfiguration {
    private UserInfo userInfo;

    private String entryPoint;

    /**
     *
     */
    public ShellConfigurationBean() {
    }

    /**
     * @param userInfo
     * @param entryPoint
     */
    public ShellConfigurationBean(UserInfo userInfo, String entryPoint) {
        super();
        this.userInfo = userInfo;
        this.entryPoint = entryPoint;
    }

    /** @see org.exoplatform.ide.shell.shared.ShellConfiguration#getUserInfo() */
    @Override
    public UserInfo getUser() {
        return userInfo;
    }

    /** @see org.exoplatform.ide.shell.shared.ShellConfiguration#setUserInfo(org.exoplatform.ide.shell.shared.UserInfo) */
    @Override
    public void setUser(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /** @see org.exoplatform.ide.shell.shared.ShellConfiguration#getDefaultEntrypoint() */
    @Override
    public String getDefaultEntrypoint() {
        return entryPoint;
    }

    /** @see org.exoplatform.ide.shell.shared.ShellConfiguration#setDefaultEntrypoint(java.lang.String) */
    @Override
    public void setDefaultEntrypoint(String entryPoint) {
        this.entryPoint = entryPoint;
    }

}
