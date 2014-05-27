/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.ide.api.user;

import com.codenvy.api.user.shared.dto.Profile;

/**
 * Stores information about current user.
 *
 * @author Artem Zatsarynnyy
 */
public class UserInfo {
    private Profile profile;

    /**
     * Checks whether current user anonymous.
     *
     * @return <code>true</code> if current user is anonymous, <code>false</code> - otherwise
     */
    public boolean isAnonymous() {
        return profile == null;
    }

    /**
     * Get current user's profile.
     *
     * @return current user's profile
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Sets current user's profile.
     *
     * @param profile
     *         user's profile
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }
}
