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

import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * If user doesn't have permissions to repository, filter will deny request with 403.
 * Filter searches for .../workspace/.vfs/acl/ProjectName_acl file that contains permissions
 * if it doesn't exist request will be accepted, else if permissions is not READ or ALL request will
 * be denied with 403 FORBIDDEN.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class VFSPermissionsFilter implements Filter {

    private UserManager           userManager;
    private VFSPermissionsChecker vfsPermissionsChecker;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            userManager = new UserManager();
            vfsPermissionsChecker = new VFSPermissionsChecker();
        } catch (OrganizationServiceException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest)request;
        String fsRootPath = System.getProperty("com.codenvy.vfs.rootdir");
        int tokenPlace;
        String lastTokenBeforePath = "/git/";
        if ((tokenPlace = req.getRequestURL().indexOf(lastTokenBeforePath)) != -1) {
            //get path to project
            String url = req.getRequestURL().substring(tokenPlace + lastTokenBeforePath.length());
            url = url.replaceFirst("/info/refs", "");
            url = url.replaceFirst("/git-upload-pack", "");
            //adaptation to fs
            url = url.replaceAll("/", File.separator);
            //search for dotVFS directory
            File projectDirectory = new File(fsRootPath.concat(File.separator).concat(url));
            String auth;
            String user = "";
            String password = "";
            if ((auth = req.getHeader("authorization")) != null) {
                //get encoded password phrase
                String userAndPasswordEncoded = auth.substring(6);
                // decode Base64 user:password
                String userAndPasswordDecoded = new String(Base64.decodeBase64(userAndPasswordEncoded));
                //get username and password separator ':'
                int betweenUserAndPassword = userAndPasswordDecoded.indexOf(':');
                //get username - it is before first ':'
                user = userAndPasswordDecoded.substring(0, betweenUserAndPassword);
                //get password - it is after first ':'
                password = userAndPasswordDecoded.substring(betweenUserAndPassword + 1);
            }
                /*
                    Check if user authenticated and hasn't permissions to project, then
                    send response code 403
                */
            try {
                if (!user.isEmpty() &&
                    !(userManager.authenticateUser(user, password) && vfsPermissionsChecker.isAccessAllowed(user, userManager
                            .getUserMembershipRoles(user, projectDirectory.getParentFile().getName()), projectDirectory))) {
                    ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);

                 /*
                    if user wasn't required check project permissions to
                    any user, if it is not READ or ALL send response code 401 and header with BASIC type
                    of authentication
                 */
                } else if (user.isEmpty() && !vfsPermissionsChecker.isAccessAllowed(user, null, projectDirectory)) {
                    ((HttpServletResponse)response).addHeader("Cache-Control", "private");
                    ((HttpServletResponse)response).addHeader("WWW-Authenticate", "Basic");
                    ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
                return;
            } catch (OrganizationServiceException e) {
                throw new ServletException(e.getMessage(), e);
            }
        }
        chain.doFilter(req, response);
    }

    @Override
    public void destroy() {
    }
}