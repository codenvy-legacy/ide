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
package com.codenvy.ide.git;

import com.codenvy.organization.model.Role;

import org.exoplatform.ide.vfs.impl.fs.AccessControlList;
import org.exoplatform.ide.vfs.impl.fs.AccessControlListSerializer;
import org.exoplatform.ide.vfs.shared.*;

import java.io.*;
import java.io.File;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * Used with @link VFSPermissionsFilter to check user permissions
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class VFSPermissionsChecker {

    /**
     * Check user permissions to project using project acls.
     * If user has READ or ALL permissions he has access.
     * Permission check chain: any user , specific user, user groups.
     *
     * @param user
     *         username
     * @param userMembershipRoles
     *         roles specific to user
     * @param projectDirectory
     *         directory where project is situated
     * @return <code>true</code> if user has READ or ALL permissions, <code>false</code> if he doesn't
     * @throws java.io.IOException
     *         when it's not possible to get ACL
     */
    public boolean isAccessAllowed(String user, Set<Role> userMembershipRoles, File projectDirectory) throws IOException {
        String projectName = projectDirectory.getName();
        //acl folder by path ../projectDirectory/.vfs/acl
        File aclDirectory = Paths.get(projectDirectory.getParentFile().getAbsolutePath(), ".vfs", "acl").toFile();
        // project acl is projectName_acl
        File aclFile = new File(aclDirectory, projectName + "_acl");
        if (!aclFile.exists()) {
            // if there is no acl for project find it for workspace
            aclFile = new File(aclDirectory, "_acl");
            if (!aclFile.exists()) {
                return true;
            }
        }
        AccessControlList acl = new AccessControlListSerializer().read(new DataInputStream(new FileInputStream(aclFile)));
        Set<VirtualFileSystemInfo.BasicPermissions> resultPermissions = new HashSet<>();
        PrincipalImpl principal = new PrincipalImpl(VirtualFileSystemInfo.ANY_PRINCIPAL, Principal.Type.USER);
        //get permissions to any principal
        if (acl.getPermissions(principal) != null) {
            resultPermissions = acl.getPermissions(principal);
        }
        if (!user.isEmpty()) {
            //get permissions to specific user
            principal.setName(user);
            if (acl.getPermissions(principal) != null) {
                resultPermissions.addAll(acl.getPermissions(principal));
            }
            //get permissions to userGroup
            principal.setType(Principal.Type.GROUP);
            if (userMembershipRoles != null) {
                for (Role role : userMembershipRoles) {
                    principal.setName("workspace/".concat(role.getName()));
                    if (acl.getPermissions(principal) != null) {
                        resultPermissions.addAll(acl.getPermissions(principal));
                    }
                }
            }
        }
        return resultPermissions.contains(VirtualFileSystemInfo.BasicPermissions.READ) ||
               resultPermissions.contains(VirtualFileSystemInfo.BasicPermissions.ALL);
    }
}
