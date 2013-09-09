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
package org.exoplatform.ide.vfs.server;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/** @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a> */
public class VirtualFileSystemUser {
    private final String             userId;
    private final Collection<String> groups;

    VirtualFileSystemUser(String userId, Set<String> groups) {
        this.userId = userId;
        this.groups = Collections.unmodifiableSet(new HashSet<String>(groups));
    }

    public String getUserId() {
        return userId;
    }

    public Collection<String> getGroups() {
        return groups;
    }

    @Override
    public String toString() {
        return "VirtualFileSystemUser{" +
               "userId='" + userId + '\'' +
               ", groups=" + groups +
               '}';
    }
}
