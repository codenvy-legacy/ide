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
package org.exoplatform.ide.vfs.shared;

import java.util.HashSet;
import java.util.Set;

/**
 * Representation of Access Control Entry used to interaction with client via JSON.
 *
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: AccessControlEntryImpl.java 79579 2012-02-17 13:27:25Z andrew00x $
 */
public class AccessControlEntryImpl implements AccessControlEntry {
    /** Principal. */
    private Principal principal;

    /** Permissions. */
    private Set<String> permissions;

    /** Empty AccessControlEntryImpl instance. Both principal and permissions are not set. */
    public AccessControlEntryImpl() {
    }

    /**
     * AccessControlEntryImpl instance with specified principal and permissions.
     *
     * @param principal
     *         principal
     * @param permissions
     *         permissions
     */
    public AccessControlEntryImpl(Principal principal, Set<String> permissions) {
        this.principal = principal;
        this.permissions = permissions;
    }

    @Override
    public Set<String> getPermissions() {
        if (permissions == null) {
            permissions = new HashSet<String>();
        }
        return permissions;
    }

    @Override
    public void setPermissions(Set<String> permissions) {
        this.permissions = permissions;
    }

    @Override
    public Principal getPrincipal() {
        return principal;
    }

    @Override
    public void setPrincipal(Principal principal) {
        this.principal = principal;
    }

    @Override
    public String toString() {
        return "AccessControlEntryImpl [principal=" + principal + ", permissions=" + permissions + ']';
    }
}
