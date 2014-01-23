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

import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Role;

import org.exoplatform.ide.vfs.impl.fs.AccessControlList;
import org.exoplatform.ide.vfs.impl.fs.AccessControlListSerializer;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.servlet.ServletException;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/** @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a> */
public class VFSPermissionsCheckerTest {

    final static String WORKSPACE = "workspace";
    final File projectDirectory;
    final VFSPermissionsChecker userPermissionsChecker = new VFSPermissionsChecker();

    VFSPermissionsCheckerTest() throws URISyntaxException {
        File workspace =
                new File(new File(Thread.currentThread().getContextClassLoader().getResource(".").toURI()).getParentFile(), WORKSPACE);
        projectDirectory = new File(workspace, "testProject");
        projectDirectory.mkdirs();
    }

    /* If project doesn't have permissions to any principal, empty user should have access */
    @Test
    public void testEmptyUserAndProjectWithoutPermissions() throws IOException, ServletException, OrganizationServiceException {
        assertTrue(userPermissionsChecker.isAccessAllowed("", null, projectDirectory));
    }

    /* If project has ALL permissions to any principal, empty user should have access */
    @Test
    public void testEmptyUserAndProjectWithUserAllPermissions() throws IOException, ServletException, OrganizationServiceException {
        //given
        Principal principal = new PrincipalImpl(VirtualFileSystemInfo.ANY_PRINCIPAL, Principal.Type.USER);
        createProjectACL(getPermissionsMap(principal, VirtualFileSystemInfo.BasicPermissions.ALL));
        //then
        assertTrue(userPermissionsChecker.isAccessAllowed("", null, projectDirectory));
    }

    /* If project has ALL permissions to "workspace/developer" group, user with role "developer" should have access */
    @Test
    public void testProjectWithWorkspaceDeveloperGroupAllPermissionsAndUserWithDeveloperRole()
            throws OrganizationServiceException, IOException, ServletException {
        //given
        PrincipalImpl userPrincipal = new PrincipalImpl("workspace/developer", Principal.Type.GROUP);
        createProjectACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        //then
        assertTrue(userPermissionsChecker.isAccessAllowed("user", getSetOfRoles("developer"), projectDirectory));
        assertFalse(userPermissionsChecker.isAccessAllowed("user", getSetOfRoles("president"), projectDirectory));
    }

    /* If project has ALL permissions to specific user, only this user should have access  */
    @Test
    public void testProjectWithAllPermissionsToSpecificUserAndUserWithCorrectCredentials() throws IOException, ServletException {
        //given
        PrincipalImpl userPrincipal = new PrincipalImpl("user", Principal.Type.USER);
        createProjectACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        //then
        assertTrue(userPermissionsChecker.isAccessAllowed("user", getSetOfRoles("developer"), projectDirectory));
        assertFalse(userPermissionsChecker.isAccessAllowed("ChuckNorris", getSetOfRoles("developer"), projectDirectory));
    }

    @Test
    public void shouldReadPermissionsFromWorkspaceAclIfProjectAclDoesNotExists() throws Exception {
        //given
        PrincipalImpl userPrincipal = new PrincipalImpl("user", Principal.Type.USER);
        createWorkspaceACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        //then
        assertTrue(userPermissionsChecker.isAccessAllowed("user", getSetOfRoles("developer"), projectDirectory));
        assertFalse(userPermissionsChecker.isAccessAllowed("ChuckNorris", getSetOfRoles("developer"), projectDirectory));
    }

    /* delete project acl file after test */
    @AfterMethod
    public void deleteACLs() {
        File projectACL = new File(projectDirectory.getParentFile(),
                                   ".vfs".concat(File.separator).concat("acl").concat(File.separator).concat(
                                           projectDirectory.getName().concat("_acl")));
        if (projectACL.exists()) {
            projectACL.delete();
        }

        File workspaceACL = new File(projectDirectory.getParentFile(),
                                     ".vfs".concat(File.separator).concat("acl").concat(File.separator).concat("_acl"));

        if (workspaceACL.exists()) {
            workspaceACL.delete();
        }
    }


    /**
     * Used with VFSPermissionsChecker
     *
     * @param userRoles roles names
     * @return set of given roles
     */
    private Set<Role> getSetOfRoles(String... userRoles) {
        Set<Role> setOfRoles = new HashSet<>();
        for (String role : userRoles) {
            setOfRoles.add(new Role(role));
        }
        return setOfRoles;
    }

    /**
     * Used with AccessControlList
     *
     * @param principal
     *         principal that will be written to acl file
     * @param permissions
     *         permissions that will be written to acl file with given principal
     * @return permissions map
     */
    private Map<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> getPermissionsMap(Principal principal,
                                                                                          VirtualFileSystemInfo.BasicPermissions...
                                                                                                  permissions) {
        Set<VirtualFileSystemInfo.BasicPermissions> setOfGivenPermissions = new HashSet<>();
        setOfGivenPermissions.addAll(Arrays.asList(permissions));
        HashMap<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> resultPermissions = new HashMap<>();
        resultPermissions.put(principal, setOfGivenPermissions);
        return resultPermissions;
    }

    /**
     * Create project aclFile and write given permissions to it
     *
     * @param permissionsMap
     *         map with permissions
     * @throws java.io.IOException
     *         when it is not possible to write permissions
     */
    private void createProjectACL(Map<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> permissionsMap)
            throws IOException {
        File aclDir = new File(projectDirectory.getParentFile(), ".vfs".concat(File.separator).concat("acl"));
        if (!aclDir.exists()) {
            aclDir.mkdirs();
        }
        File aclFile = new File(aclDir, projectDirectory.getName().concat("_acl"));
        AccessControlListSerializer serializer = new AccessControlListSerializer();
        AccessControlList acl = new AccessControlList(permissionsMap);
        serializer.write(new DataOutputStream(new FileOutputStream(aclFile)), acl);
    }

    /**
     * Create workspace aclFile and write given permissions to it
     *
     * @param permissionsMap
     *         map with permissions
     * @throws java.io.IOException
     *         when it is not possible to write permissions
     */
    private void createWorkspaceACL(Map<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> permissionsMap)
            throws IOException {
        File aclDir = new File(projectDirectory.getParentFile(), ".vfs".concat(File.separator).concat("acl"));
        if (!aclDir.exists()) {
            aclDir.mkdirs();
        }
        File aclFile = new File(aclDir, "_acl");
        AccessControlListSerializer serializer = new AccessControlListSerializer();
        AccessControlList acl = new AccessControlList(permissionsMap);
        serializer.write(new DataOutputStream(new FileOutputStream(aclFile)), acl);
    }
}
