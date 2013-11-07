// Copyright 2012 Google Inc. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.collide.client.bootstrap;

import org.exoplatform.ide.client.framework.module.IDE;

/**
 * Bootstrap information for the client.
 * <p/>
 * <p>It contains a description of the files contained in the project workspace,
 * as well as user identification information and a sessionID token that is sent
 * along with subsequent requests.
 */
public final class BootstrapSession {

    private static BootstrapSession instance;

    private String userId;

    private String activeClientId;

    /** Use this method to obtain an instance of the Session object. */
    public static BootstrapSession getBootstrapSession() {
        if (instance == null) {
            instance = new BootstrapSession();
        }
        return instance;
    }

    protected BootstrapSession() {
    }

    /** @return The active client ID for the current tab. */
    public String getActiveClientId() {
        return activeClientId;
    }

    /** @return The user's handle. This is his name or email. */
    public String getUsername() {
        return IDE.user.getUserId();
    }

    /** @return The user's unique ID (obfuscated GAIA ID). */
    public String getUserId() {
        return userId;
    }

    /** @param userId */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setActiveClientId(String activeClientId) {
        this.activeClientId = activeClientId;
    }
}
