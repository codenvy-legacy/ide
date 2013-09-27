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
package org.exoplatform.ide.shell.conversationstate;

import java.util.Collection;

/**
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShellUser.java Mar 6, 2012 4:55:38 PM azatsarynnyy $
 */
public class ShellUser {
    private String userId;

    private Collection<String> roles;

    public ShellUser() {
    }

    /**
     * @param userId
     *         the userId to set
     * @param groups
     *         the groups to set
     * @param roles
     *         the roles to set
     */
    public ShellUser(String userId, Collection<String> roles) {
        this.userId = userId;
        this.roles = roles;
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

}
