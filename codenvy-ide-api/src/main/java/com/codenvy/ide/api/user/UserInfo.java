/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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
