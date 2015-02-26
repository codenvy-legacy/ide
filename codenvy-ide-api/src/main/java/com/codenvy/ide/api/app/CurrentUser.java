/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide.api.app;

import com.codenvy.api.user.shared.dto.ProfileDescriptor;
import java.util.Map;

/**
 * Describes current state of user.
 *
 * @author Vitaly Parfonov
 */
public class CurrentUser {

    private ProfileDescriptor   profileDescriptor;
    private Map<String, String> preferences;

    public CurrentUser() {
    }

    public CurrentUser(ProfileDescriptor profileDescriptor, Map<String, String> preferences) {
        this.profileDescriptor = profileDescriptor;
        this.preferences = preferences;
    }

    /**
     * Return current ProfileDescriptor
     *
     * @return
     */
    public ProfileDescriptor getProfile() {
        return profileDescriptor;
    }

    public void setProfile(ProfileDescriptor profileDescriptor) {
        this.profileDescriptor = profileDescriptor;
    }

    /**
     * Return current preferences
     *
     * @return
     */
    public Map<String, String> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, String> preferences) {
        this.preferences = preferences;
    }

    /**
     * Determines whether the user is permanent.
     *
     * @return <b>true</b> for permanent user, <b>false</b> otherwise
     */
    public boolean isUserPermanent() {
        return preferences == null || !"true".equals(preferences.get("temporary"));
    }

}
