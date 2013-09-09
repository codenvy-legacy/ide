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
