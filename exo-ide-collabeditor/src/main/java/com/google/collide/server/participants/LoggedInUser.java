/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.google.collide.server.participants;

/** A logged in user. */
public final class LoggedInUser {
    private final String  name;
    /**
     * Unique user identifier. Even if the same user (with the same username) logged in from different browser this id
     * must be unique for each user.
     */
    private final String  id;
    /** Workspace id which user logged in. */
    private final String  workspace; // At the moment user may be logged in to only one workspace at the time.
    private       boolean readOnly;

    public LoggedInUser(String name, String id, String workspace, boolean readOnly) {
        this.name = name;
        this.id = id;
        this.workspace = workspace;
        this.readOnly = readOnly;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getWorkspace() {
        return workspace;
    }

    public boolean isLoggedIn(String workspace) {
        return this.workspace.equals(workspace);
    }

    public boolean isReadOnly() {
        return readOnly;
    }
}
