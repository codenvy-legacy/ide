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
package org.exoplatform.ide.client.framework.configuration;

import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.userinfo.UserInfo;
import org.exoplatform.ide.client.framework.workspaceinfo.CurrentWorkspaceInfo;
import org.exoplatform.ide.client.framework.workspaceinfo.WorkspaceInfo;

/**
 * Been used to transport initialization configuration
 *
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: May 25, 2011 evgen $
 */
public class IDEInitialConfiguration {

    private IDEConfiguration ideConfiguration;

    private ApplicationSettings settings;

    private UserInfo userInfo;
    
    private CurrentWorkspaceInfo currentWorkspace;
    
    private WorkspaceInfo workspaceInfo;

    /**
     *
     */
    public IDEInitialConfiguration() {
    }

    /**
     * @param ideConfiguration
     * @param isDiscoverable
     * @param settings
     * @param userInfo
     */
    public IDEInitialConfiguration(IDEConfiguration ideConfiguration,
                                          ApplicationSettings settings, UserInfo userInfo) {
        super();
        this.ideConfiguration = ideConfiguration;
        this.settings = settings;
        this.userInfo = userInfo;
    }

    /** @return the ideConfiguration */
    public IDEConfiguration getIdeConfiguration() {
        return ideConfiguration;
    }

    /**
     * @param ideConfiguration
     *         the ideConfiguration to set
     */
    public void setIdeConfiguration(IDEConfiguration ideConfiguration) {
        this.ideConfiguration = ideConfiguration;
    }

 
    /** @return the settings */
    public ApplicationSettings getSettings() {
        return settings;
    }

    /**
     * @param settings
     *         the settings to set
     */
    public void setSettings(ApplicationSettings settings) {
        this.settings = settings;
    }

    /** @return the userInfo */
    public UserInfo getUserInfo() {
        return userInfo;
    }

    /**
     * @param userInfo
     *         the userInfo to set
     */
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }

    /** @return the currentWorkspace */
    public CurrentWorkspaceInfo getCurrentWorkspace() {
        return currentWorkspace;
    }

    /**
     * @param currentWorkspace
     *         the currentWorkspace to set
     */
    public void setCurrentWorkspace(CurrentWorkspaceInfo currentWorkspace) {
        this.currentWorkspace = currentWorkspace;
    }

    /** @return the WorkspaceInfo */
    public WorkspaceInfo getWorkspaceInfo() {
        return workspaceInfo;
    }

    /**
     * @param workspaceInfo
     *         the WorkspaceInfo to set
     */
    public void setWorkspaceInfo(WorkspaceInfo workspaceInfo) {
        this.workspaceInfo = workspaceInfo;
    }
}
