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
package org.exoplatform.ide.shell.client.model;

import org.exoplatform.ide.shell.shared.UserInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 *
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class UserInfoBean implements UserInfo {

    public static final String DEFAULT_USER_NAME = "DefaultUser";

    private String userId;

    private List<String> groups;

    private List<String> roles;

    public UserInfoBean() {
    }

    public UserInfoBean(String id) {
        this.userId = id;
    }

    public UserInfoBean(String id, List<String> groups, List<String> roles) {
        this.userId = id;
        this.groups = groups;
        this.roles = roles;
    }

    /** @see org.exoplatform.ide.shell.shared.UserInfo#getUserId() */
    @Override
    public String getUserId() {
        return userId;
    }

    /** @see org.exoplatform.ide.shell.shared.UserInfo#setUserId(java.lang.String) */
    @Override
    public void setUserId(String id) {
        this.userId = id;
    }

    /** @see org.exoplatform.ide.shell.shared.UserInfo#getGroups() */
    @Override
    public List<String> getGroups() {
        if (groups == null)
            groups = new ArrayList<String>();
        return groups;
    }

    /** @see org.exoplatform.ide.shell.shared.UserInfo#setGroups(java.util.List) */
    @Override
    public void setGroups(List<String> groups) {
        this.groups = groups;
    }

    /** @see org.exoplatform.ide.shell.shared.UserInfo#getRoles() */
    @Override
    public List<String> getRoles() {
        if (roles == null)
            roles = new ArrayList<String>();
        return roles;
    }

    /** @see org.exoplatform.ide.shell.shared.UserInfo#setRoles(java.util.List) */
    @Override
    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

}
