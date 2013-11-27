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

import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Role;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.ide.vfs.impl.fs.AccessControlList;
import org.exoplatform.ide.vfs.impl.fs.AccessControlListSerializer;
import org.exoplatform.ide.vfs.shared.Principal;
import org.exoplatform.ide.vfs.shared.PrincipalImpl;
import org.exoplatform.ide.vfs.shared.VirtualFileSystemInfo;
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.*;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.mockito.Mockito.*;

/**
 * Test different situations of user access to projects with different permissions.
 * Test related to @link VFSPermissionsFilter class.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */

@Listeners(MockitoTestNGListener.class)

public class VFSPermissionsFilterTest {

    final static String               USER      = "username";
    final static String               PASSWORD  = "password";
    final static String               WORKSPACE = "workspace";
    final static VFSPermissionsFilter filter    = new VFSPermissionsFilter();
    final File projectDirectory;
    @Mock
    HttpServletResponse response;
    @Mock
    HttpServletRequest  request;
    @Mock
    UserManager         userManager;
    @Mock
    FilterChain         filterChain;

    VFSPermissionsFilterTest() throws URISyntaxException, FileNotFoundException, OrganizationServiceException {
        File workspace =
                new File(new File(Thread.currentThread().getContextClassLoader().getResource(".").toURI()).getParentFile(), WORKSPACE);
        System.setProperty("com.codenvy.vfs.rootdir", workspace.getParentFile().getAbsolutePath());
        projectDirectory = new File(workspace, "testProject");

        projectDirectory.mkdirs();
        filter.setUserManager(userManager);
    }

    @BeforeMethod
    public void before() throws OrganizationServiceException {
        when(userManager.authenticateUser(USER, PASSWORD)).thenReturn(true);
        when((request).getRequestURL())
                .thenReturn(new StringBuffer("http://host.com/git/").append(WORKSPACE).append("/testProject"));
        filter.setUserManager(userManager);
    }

    /* If project is not private, empty user should have access */
    @Test
    public void testEmptyUserAndProjectWithoutACL() throws IOException, ServletException {
        filter.doFilter(request, response, filterChain);
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /* If project has ALL permissions to any principal, empty user should have access */
    @Test
    public void testEmptyUserAndProjectWithUserAllPermissions() throws IOException, ServletException, OrganizationServiceException {
        PrincipalImpl userPrincipal = new PrincipalImpl(VirtualFileSystemInfo.ANY_PRINCIPAL, Principal.Type.USER);
        createACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        filter.doFilter(request, response, filterChain);
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /* If project has ALL permissions to "workspace/developer" group, user with role "developer" should have access */
    @Test
    public void testProjectWithWorkspaceDeveloperGroupAllPermissionsAndUserWithDeveloperRole()
            throws OrganizationServiceException, IOException, ServletException {
        //set up user role to developer
        when(userManager.getUserMembershipRoles(USER, WORKSPACE))
                .thenReturn(new HashSet<>(Arrays.asList(new Role("developer"))));
        //create principal that will be used in acl
        PrincipalImpl userPrincipal = new PrincipalImpl("workspace/developer", Principal.Type.GROUP);
        createACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        //add special behaviour to authenticate user with given encoded(username:password)
        when(request.getHeader("authorization")).thenReturn("BASIC " + (Base64.encodeBase64String((USER + ":" + PASSWORD).getBytes())));
        filter.doFilter(request, response, filterChain);
        //should be neither 401 no 403
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /* if user doesn't have "developer" role he should not get access to project with GROUP "workspace/developer" permissions */
    @Test
    public void testProjectWithWorkspaceDeveloperGroupAllPermissionsAndUserWithoutDeveloperRole()
            throws OrganizationServiceException, IOException, ServletException {
        //set up user role
        when(userManager.getUserMembershipRoles(USER, WORKSPACE))
                .thenReturn(new HashSet<>(Arrays.asList(new Role("president"))));
        //create principal that will be used in acl
        PrincipalImpl userPrincipal = new PrincipalImpl("workspace/developer", Principal.Type.GROUP);
        createACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        //add special behaviour to authenticate user with given encoded(username:password)
        when(request.getHeader("authorization")).thenReturn("BASIC " + (Base64.encodeBase64String((USER + ":" + PASSWORD).getBytes())));
        filter.doFilter(request, response, filterChain);
        //should be forbidden cause user doesn't have access
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /* if user gives wrong credentials then he should got 401 */
    @Test
    public void testProjectWithAllPermissionsAndUserThatDoesNotExistAtOrganization()
            throws OrganizationServiceException, IOException, ServletException {
        //set up userManager behaviour with Chuck Norris
        when(userManager.authenticateUser("Chuck", "Norris")).thenReturn(false);
        //create principal that will be used in acl
        PrincipalImpl userPrincipal = new PrincipalImpl("workspace/developer", Principal.Type.GROUP);
        createACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        filter.doFilter(request, response, filterChain);
        //should be 401
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

    /* if project permissions for specific user (username) in this case, user with name (username)
        and correct credentials should have access */
    @Test
    public void testProjectWithAllPermissionsToSpecificUserAndUserWithCorrectCredentials() throws IOException, ServletException {
        //create principal that will be used in acl
        PrincipalImpl userPrincipal = new PrincipalImpl(USER, Principal.Type.USER);
        createACL(getPermissionsMap(userPrincipal, VirtualFileSystemInfo.BasicPermissions.ALL));
        //add special behaviour to authenticate user with given encoded(username:password)
        when(request.getHeader("authorization")).thenReturn("BASIC " + (Base64.encodeBase64String((USER + ":" + PASSWORD).getBytes())));
        filter.doFilter(request, response, filterChain);
        //should be neither 401 no 403
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    /* delete project acl file after test */
    @AfterMethod
    public void deleteACL() {
        File projectACL = new File(projectDirectory.getParentFile(),
                                   ".vfs".concat(File.separator).concat("acl").concat(File.separator).concat(
                                           projectDirectory.getName().concat("_acl")));
        if (projectACL.exists()) {
            projectACL.delete();
        }
    }


    /**
     * Used with AccessControlList
     *
     * @param principal principal that will be written to acl file
     * @param permissions permissions that will be written to acl file with given principal
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
     * Create project aclFile and write here given permissions
     *
     * @param permissionsMap map with permissions
     * @throws IOException when it is not possible to write permissions
     */
    private void createACL(Map<Principal, Set<VirtualFileSystemInfo.BasicPermissions>> permissionsMap)
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
}
