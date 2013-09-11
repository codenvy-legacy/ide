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
package com.codenvy.vfs.impl.fs;

import com.codenvy.api.vfs.shared.AccessControlEntry;
import com.codenvy.api.vfs.shared.AccessControlEntryImpl;
import com.codenvy.api.vfs.shared.Principal;
import com.codenvy.api.vfs.shared.PrincipalImpl;
import com.codenvy.api.vfs.shared.VirtualFileSystemInfo.BasicPermissions;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Access Control List (ACL) contains set of permissionMap assigned to each user.
 * <p/>
 * NOTE: Implementation is not thread-safe and required external synchronization.
 *
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 */
public class AccessControlList {
    private final Map<Principal, Set<BasicPermissions>> permissionMap;

    public AccessControlList() {
        permissionMap = new HashMap<>(4);
    }

    public AccessControlList(AccessControlList accessControlList) {
        this(accessControlList.permissionMap);
    }

    public AccessControlList(Map<Principal, Set<BasicPermissions>> permissions) {
        this.permissionMap = copy(permissions);
    }

    private static Map<Principal, Set<BasicPermissions>> copy(Map<Principal, Set<BasicPermissions>> source) {
        Map<Principal, Set<BasicPermissions>> copy = new HashMap<>(source.size());
        for (Map.Entry<Principal, Set<BasicPermissions>> e : source.entrySet()) {
            if (!(e.getValue() == null || e.getValue().isEmpty())) {
                copy.put(new PrincipalImpl(e.getKey()), EnumSet.copyOf(e.getValue()));
            }
        }
        return copy;
    }

    public boolean isEmpty() {
        return permissionMap.isEmpty();
    }

    public List<AccessControlEntry> getEntries() {
        if (isEmpty()) {
            return Collections.emptyList();
        }
        List<AccessControlEntry> acl = new ArrayList<>(permissionMap.size());
        for (Map.Entry<Principal, Set<BasicPermissions>> e : permissionMap.entrySet()) {
            Set<BasicPermissions> basicPermissions = e.getValue();
            Set<String> plainPermissions = new HashSet<>(basicPermissions.size());
            for (BasicPermissions permission : e.getValue()) {
                plainPermissions.add(permission.value());
            }
            acl.add(new AccessControlEntryImpl(new PrincipalImpl(e.getKey()), plainPermissions));
        }
        return acl;
    }

    Map<Principal, Set<BasicPermissions>> getPermissionMap() {
        return copy(permissionMap);
    }

    public Set<BasicPermissions> getPermissions(Principal principal) {
        if (permissionMap.isEmpty()) {
            return null;
        }
        Set<BasicPermissions> userPermissions = permissionMap.get(principal);
        if (userPermissions == null) {
            return null;
        }
        return EnumSet.copyOf(userPermissions);
    }

    public void update(List<AccessControlEntry> acl, boolean override) {
        if (acl.isEmpty() && !override) {
            // Nothing to do if there is no updates and override flag is not set.
            return;
        }

        if (override) {
            // remove all existed permissions
            permissionMap.clear();
        }

        for (AccessControlEntry ace : acl) {
            final PrincipalImpl principal = new PrincipalImpl(ace.getPrincipal());
            Set<String> plainPermissions = ace.getPermissions();
            if (plainPermissions == null || plainPermissions.isEmpty()) {
                permissionMap.remove(principal);
            } else {
                Set<BasicPermissions> basicPermissions = permissionMap.get(principal);
                if (basicPermissions == null) {
                    basicPermissions = EnumSet.noneOf(BasicPermissions.class);
                    permissionMap.put(principal, basicPermissions);
                }
                for (String strPermission : plainPermissions) {
                    basicPermissions.add(BasicPermissions.fromValue(strPermission));
                }
            }
        }
    }

    void write(DataOutput output) throws IOException {
        output.writeInt(permissionMap.size());
        for (Map.Entry<Principal, Set<BasicPermissions>> entry : permissionMap.entrySet()) {
            Principal principal = entry.getKey();
            Set<BasicPermissions> permissions = entry.getValue();
            output.writeUTF(principal.getName());
            output.writeUTF(principal.getType().toString());
            output.writeInt(permissions.size());
            for (BasicPermissions permission : permissions) {
                output.writeUTF(permission.value());
            }
        }
    }

    static AccessControlList read(DataInput input) throws IOException {
        int recordsNum = input.readInt();
        HashMap<Principal, Set<BasicPermissions>> permissionsMap = new HashMap<>(recordsNum);
        int readRecords = 0;
        while (readRecords < recordsNum) {
            String principalName = input.readUTF();
            String principalType = input.readUTF();
            int permissionsNum = input.readInt();
            if (permissionsNum > 0) {
                Set<BasicPermissions> permissions = EnumSet.noneOf(BasicPermissions.class);
                int readPermissions = 0;
                while (readPermissions < permissionsNum) {
                    permissions.add(BasicPermissions.fromValue(input.readUTF()));
                    ++readPermissions;
                }
                permissionsMap.put(new PrincipalImpl(principalName, Principal.Type.valueOf(principalType)), permissions);
            }
            ++readRecords;
        }
        return new AccessControlList(permissionsMap);
    }
}
