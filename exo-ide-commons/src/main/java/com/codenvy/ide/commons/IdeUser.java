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
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public class IdeUser {
    
    private String userId;
    
    private String firstName;
    
    private String lastName;

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
    public IdeUser(String userId, String firstName, String lastName, Collection<String> roles, String clientId, Collection<IDEWorkspace> workspaces, boolean temporary) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
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
    
    /**
     * Returns first name.
     * 
     * @return first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets new value of first name.
     * 
     * @param firstName new value of first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    
    /**
     * Returns last name.
     * 
     * @return last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets new value of last name.
     * 
     * @param lastName new value of last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        builder.append("\"user\":{\"userId\":\"").append(userId).append("\",\"clientId\":\"").append(clientId).append("\",\"temporary\":\"")
               .append(temporary).append("\",\"roles\":[");
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
