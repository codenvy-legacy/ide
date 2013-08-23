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
package com.codenvy.ide.commons;

import java.util.Collection;

/**
 * Created by The eXo Platform SAS.
 *
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class IdeUser {
    private String userId;

    private Collection<String> roles;
    
    private Collection<IDEWorkspace> workspaces;
    
    private String clientId;
    
    private boolean temporary;

    public IdeUser() {
    }

    /**
     * @param userId
     *         the userId to set
     * @param groups
     *         the groups to set
     * @param roles
     *         the roles to set
     * @param clientId
     */
    public IdeUser(String userId, Collection<String> roles, String clientId, Collection<IDEWorkspace> workspaces, boolean temporary) {
        this.userId = userId;
        this.roles = roles;
        this.clientId = clientId;
        this.workspaces = workspaces;
        this.temporary = temporary;
    }

    /** @return the userId */
    public String getUserId() {
        return userId;
    }

    /**
     * @param userId
     *         the userId to set
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /** @return the roles */
    public Collection<String> getRoles() {
        return roles;
    }

    /**
     * @param roles
     *         the roles to set
     */
    public void setRoles(Collection<String> roles) {
        this.roles = roles;
    }

    /** @return the client id */
    public String getClientId() {
        return clientId;
    }

    /**
     * @param clientId
     *         the client id to set
     */
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public Collection<IDEWorkspace> getWorkspaces() {
        return workspaces;
    }
    
    public void setWorkspaces(Collection<IDEWorkspace> workspaces) {
        this.workspaces = workspaces;
    }
    
    
    public boolean isTemporary() {
        return temporary;
    }
    
    public void setTemporary(boolean temporary) {
        this.temporary = temporary;
    }
    

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("\"user\":{\"userId\":\"").append(userId).append("\",\"clientId\":\"").append(clientId).append("\",\"temporary\":\"").append(temporary).append("\",\"roles\":[");
        int i = 0;
        int rMax = roles.size();
        for (String r : roles) {
            i++;
            builder.append(r);
            if (i != rMax)
              builder.append(", ");
        }
        return builder.append("]}").toString();
    }
}
