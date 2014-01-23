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

import com.codenvy.commons.lang.ExpirableCache;
import com.codenvy.organization.client.UserManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.exception.UserExistenceException;
import com.codenvy.organization.model.Role;
import com.codenvy.organization.model.User;
import com.codenvy.organization.util.MD5HexPasswordEncrypter;
import com.codenvy.organization.util.PasswordEncrypter;

import org.apache.commons.codec.binary.Base64;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * If user doesn't have permissions to repository, filter will deny request with 403.
 * Filter searches for .../workspace/.vfs/acl/ProjectName_acl file that contains permissions
 * if it doesn't exist request will be accepted, else if permissions is not READ or ALL request will
 * be denied with 403 FORBIDDEN.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class VFSPermissionsFilter implements Filter {

    private UserManager                     userManager;
    private VFSPermissionsChecker           vfsPermissionsChecker;
    private ExpirableCache<String, Boolean> credentialsCache;
    private PasswordEncrypter               passwordEncrypter;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            userManager = new UserManager();
            vfsPermissionsChecker = new VFSPermissionsChecker();
            credentialsCache = new ExpirableCache<>(TimeUnit.MINUTES.toMillis(5), 20);
            passwordEncrypter = new MD5HexPasswordEncrypter();
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
            File projectDirectory = Paths.get(fsRootPath, url).toFile();
            String auth;
            String userName = "";
            String password = "";
            if ((auth = req.getHeader("authorization")) != null) {
                //get encoded password phrase
                String userAndPasswordEncoded = auth.substring(6);
                // decode Base64 user:password
                String userAndPasswordDecoded = new String(Base64.decodeBase64(userAndPasswordEncoded));
                //get username and password separator ':'
                int betweenUserAndPassword = userAndPasswordDecoded.indexOf(':');
                //get username - it is before first ':'
                userName = userAndPasswordDecoded.substring(0, betweenUserAndPassword);
                //get password - it is after first ':'
                password = userAndPasswordDecoded.substring(betweenUserAndPassword + 1);
            }

            // Check if user authenticated and hasn't permissions to project, then send response code 403
            try {
                User user = null;
                if (!userName.isEmpty()) {
                    try {
                        user = userManager.getUserByAlias(userName);
                        Set<Role> userMembershipRoles = user.getMembership(projectDirectory.getParentFile().getName()).getRoles();

                        String encryptedPassword = new String(passwordEncrypter.encrypt(password.getBytes()));
                        Boolean authenticated = credentialsCache.get((userName + encryptedPassword));
                        if (authenticated == null) {
                            authenticated = userManager.authenticateUser(userName, password);
                            credentialsCache.put(userName + encryptedPassword, authenticated);
                        }

                        if (!authenticated || !vfsPermissionsChecker.isAccessAllowed(userName, userMembershipRoles, projectDirectory)) {
                            ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
                            return;
                        }
                    } catch (UserExistenceException ignore) {
                        //ignore, let user be anonymous
                    }
                }

                if (userName.isEmpty() || user == null) {
                    // if user wasn't required check project permissions to any user,
                    // if it is not READ or ALL send response code 401 and header with BASIC type of authentication

                    if (!vfsPermissionsChecker.isAccessAllowed("", null, projectDirectory)) {
                        ((HttpServletResponse)response).addHeader("Cache-Control", "private");
                        ((HttpServletResponse)response).addHeader("WWW-Authenticate", "Basic");
                        ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                        return;
                    }
                }
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