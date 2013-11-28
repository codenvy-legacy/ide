package com.codenvy.ide.git;
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
import org.mockito.Mock;
import org.mockito.testng.MockitoTestNGListener;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test different situations of user access to projects with different permissions.
 * Test related to @link com.codenvy.ide.git.VFSPermissionsFilter class.
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
    HttpServletResponse   response;
    @Mock
    HttpServletRequest    request;
    @Mock
    UserManager           userManager;
    @Mock
    FilterChain           filterChain;
    @Mock
    VFSPermissionsChecker vfsPermissionsChecker;

    /** Basic setups need for tests: create workspace, create project directory, set system com.codenvy.vfs.rootdir property */
    VFSPermissionsFilterTest()
            throws URISyntaxException, FileNotFoundException, OrganizationServiceException, NoSuchFieldException, IllegalAccessException {
        File workspace =
                new File(new File(Thread.currentThread().getContextClassLoader().getResource(".").toURI()).getParentFile(), WORKSPACE);
        System.setProperty("com.codenvy.vfs.rootdir", workspace.getParentFile().getAbsolutePath());
        projectDirectory = new File(workspace, "testProject");
        projectDirectory.mkdirs();
    }

    @BeforeMethod
    public void before() throws OrganizationServiceException, NoSuchFieldException, IllegalAccessException {
        when(userManager.authenticateUser(USER, PASSWORD)).thenReturn(true);
        when((request).getRequestURL())
                .thenReturn(new StringBuffer("http://host.com/git/").append(WORKSPACE).append("/testProject"));
        //set up UserManager mock instead of filter UserManager
        Field filterUserManager = filter.getClass().getDeclaredField("userManager");
        filterUserManager.setAccessible(true);
        filterUserManager.set(filter, userManager);
        //set up UserManager mock instead of filter UserManager
        Field filterUserPermissionsChecker = filter.getClass().getDeclaredField("vfsPermissionsChecker");
        filterUserPermissionsChecker.setAccessible(true);
        filterUserPermissionsChecker.set(filter, vfsPermissionsChecker);
    }

    @Test
    public void testProjectWithoutPermissionsAndEmptyUser() throws IOException, ServletException {
        //given
        when(vfsPermissionsChecker.isAccessAllowed("", null, projectDirectory)).thenReturn(true);
        //when
        filter.doFilter(request, response, filterChain);
        //then should be neither 401 no 403
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testProjectWithPermissionsToSpecificUserAndEmptyUser() throws IOException, ServletException {
        //given
        when(vfsPermissionsChecker.isAccessAllowed("", null, projectDirectory)).thenReturn(false);
        //when
        filter.doFilter(request, response, filterChain);
        //then
        verify(response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }


    @Test
    public void testProjectWithWorkspaceDeveloperGroupAllPermissionsAndExistingUserWithDeveloperRole()
            throws OrganizationServiceException, IOException, ServletException {
        //given
        Set<Role> userRoles = new HashSet(Arrays.asList(new Role("developer")));
        when(userManager.getUserMembershipRoles(USER, WORKSPACE)).thenReturn(userRoles);
        when(vfsPermissionsChecker.isAccessAllowed(USER, userRoles, projectDirectory)).thenReturn(true);
        when(request.getHeader("authorization")).thenReturn("BASIC " + (Base64.encodeBase64String((USER + ":" + PASSWORD).getBytes())));
        //when
        filter.doFilter(request, response, filterChain);
        //then should be neither 401 no 403
        verify(response, never()).sendError(HttpServletResponse.SC_UNAUTHORIZED);
        verify(response, never()).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

    @Test
    public void testProjectWithWorkspaceDeveloperGroupAllPermissionsAndNotExistingUserWithDeveloperRole()
            throws OrganizationServiceException, IOException, ServletException {
        //given
        Set<Role> userRoles = new HashSet(Arrays.asList(new Role("developer")));
        when(userManager.getUserMembershipRoles(USER, WORKSPACE)).thenReturn(userRoles);
        when(vfsPermissionsChecker.isAccessAllowed(USER, userRoles, projectDirectory)).thenReturn(true);
        when(request.getHeader("authorization"))
                .thenReturn("BASIC " + (Base64.encodeBase64String(("OTHERUSER" + ":" + PASSWORD).getBytes())));
        //when
        filter.doFilter(request, response, filterChain);
        //then
        verify(response).sendError(HttpServletResponse.SC_FORBIDDEN);
    }

}
