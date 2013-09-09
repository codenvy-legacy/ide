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
package org.exoplatform.ide.shell.shared;

import java.util.List;

/**
 * Interface describe information about user.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: UserInfo.java Mar 28, 2012 11:33:45 AM azatsarynnyy $
 */
public interface UserInfo {

    /**
     * Returns the user's id.
     *
     * @return {@link String} user's id
     */
    public String getUserId();

    /**
     * Change the user's id.
     *
     * @param id
     *         user's id
     */
    public void setUserId(String id);

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

}