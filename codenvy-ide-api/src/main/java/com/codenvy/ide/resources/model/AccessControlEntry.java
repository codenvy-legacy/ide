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
package com.codenvy.ide.resources.model;

import com.codenvy.ide.collections.Array;
import com.codenvy.ide.collections.Collections;

/**
 * Representation of Access Control Entry used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AccessControlEntry.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class AccessControlEntry {
    /** Principal name. */
    private String principal;

    /** Permissions. */
    private Array<String> permissions;

    /** Empty AccessControlEntry instance. Both principal and permissions are not set. */
    public AccessControlEntry() {
    }

    /**
     * AccessControlEntry instance with specified principal and permissions.
     *
     * @param principal
     *         principal
     * @param permissions
     *         permissions
     */
    public AccessControlEntry(String principal, Array<String> permissions) {
        this.principal = principal;
        this.permissions = permissions;
    }

    /** @return principal's permissions */
    public Array<String> getPermissions() {
        if (permissions == null) {
            permissions = Collections.<String>createArray();
        }
        return permissions;
    }

    /**
     * @param permissions
     *         new set of permissions
     */
    public void setPermissions(Array<String> permissions) {
        this.permissions = permissions;
    }

    /** @return principal name */
    public String getPrincipal() {
        return principal;
    }

    /**
     * @param principal
     *         principal name
     */
    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    /** @see java.lang.Object#toString() */
    @Override
    public String toString() {
        return "AccessControlEntry [principal=" + principal + ", permissions=" + permissions + ']';
    }
}
