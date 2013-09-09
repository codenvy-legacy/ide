/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 * [2012] - [2013] Codenvy, S.A.
 * All Rights Reserved.
 *
 * NOTICE:  All information contained herein is, and remains
 * the property of Codenvy S.A. and its suppliers,
 * if any.  The intellectual and technical concepts contained
 * herein are proprietary to Codenvy S.A.
 * and its suppliers and may be covered by U.S. and Foreign Patents,
 * patents in process, and are protected by trade secret or copyright law.
 * Dissemination of this information or reproduction of this material
 * is strictly forbidden unless prior written permission is obtained
 * from Codenvy S.A..
 */
package org.exoplatform.ide.client.framework.userinfo;

import com.google.web.bindery.autobean.shared.AutoBean.PropertyName;

import java.util.List;

import org.exoplatform.ide.client.framework.workspaceinfo.WorkspaceInfo;

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
    
    public void setWorkspaces(List<WorkspaceInfo> workspaces);
    
    public List<WorkspaceInfo> getWorkspaces();
    
    public String getClientId();

    public void setClientId(String clientId);
    
    
    public boolean isTemporary();
        
    
    public void setTemporary(boolean temporary);

}