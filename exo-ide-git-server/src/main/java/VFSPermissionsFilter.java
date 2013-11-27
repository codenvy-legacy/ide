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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * If user doesn't have permissions to repository, filter will deny request with 403.
 * Filter searches for .../workspace/.vfs/acl/ProjectName_acl file that contains permissions
 * if it doesn't exist request will be accepted, else if permissions is not READ or ALL request will
 * be denied with 403 FORBIDDEN.
 *
 * @author <a href="mailto:evoevodin@codenvy.com">Eugene Voevodin</a>
 */
public class VFSPermissionsFilter implements Filter {

    private UserManager userManager;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        try {
            userManager = new UserManager();
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
            if ((auth = req.getHeader("authorization")) != null) {
                //get encoded password phrase
                String userAndPasswordEncoded = auth.substring(6);
                // decode Base64 user:password
                String userAndPasswordDecoded = new String(Base64.decodeBase64(userAndPasswordEncoded));
                //get username and password separator ':'
                int betweenUserAndPassword = userAndPasswordDecoded.indexOf(':');
                //get username - it is before first ':'
                String user = userAndPasswordDecoded.substring(0, betweenUserAndPassword);
                //get password - it is after first ':'
                String password = userAndPasswordDecoded.substring(betweenUserAndPassword + 1);
                /*
                    Check if user authenticated and hasn't permissions to project, then
                    send response code 403
                */
                if (!user.isEmpty() && !(isUserAuthenticated(user, password) && accessAllowed(user, projectDirectory))) {
                    ((HttpServletResponse)response).sendError(HttpServletResponse.SC_FORBIDDEN);
                    return;
                }
                /*
                    if user wasn't required check project permissions to
                    any user, if it is not READ or ALL send response code 401 and header with BASIC type
                    of authentication
                 */
            } else if (!accessAllowed("", projectDirectory)) {
                ((HttpServletResponse)response).addHeader("Cache-Control", "private");
                ((HttpServletResponse)response).addHeader("WWW-Authenticate", "Basic");
                ((HttpServletResponse)response).sendError(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }
        chain.doFilter(req, response);
    }

    /**
     * Check user permissions to project using project acls.
     * If user has READ or ALL permissions he has access.
     * Permission check chain: any user , specific user, user groups.
     *
     * @param name
     *         username
     * @param projectDirectory
     *         directory where project is situated
     * @return <code>true</code> if user has READ or ALL permissions, <code>false</code> if he doesn't
     * @throws IOException
     *         when it's not possible to get ACL
     * @throws ServletException
     *         when it's not possible to get user membership roles
     */
    private boolean accessAllowed(String name, File projectDirectory) throws IOException, ServletException {
        String projectName = projectDirectory.getName();
        //go to parent project acl file that under ../projectDirectory/.vfs/acl/projectName_acl
        File projectAcl = new File(projectDirectory.getParentFile(), ".vfs"
                .concat(File.separator)
                .concat("acl")
                .concat(File.separator)
                .concat(projectName.concat("_acl")));
        if (!projectAcl.exists()) {
            return true;
        }
        AccessControlList acl = new AccessControlListSerializer().read(new DataInputStream(new FileInputStream(projectAcl)));
        Set<VirtualFileSystemInfo.BasicPermissions> resultPermissions = new HashSet<>();
        PrincipalImpl principal = new PrincipalImpl(VirtualFileSystemInfo.ANY_PRINCIPAL, Principal.Type.USER);
        //get permissions to any principal
        if (acl.getPermissions(principal) != null) {
            resultPermissions = acl.getPermissions(principal);
        }
        if (!name.isEmpty()) {
            //get permissions to specific user
            principal.setName(name);
            if (acl.getPermissions(principal) != null) {
                resultPermissions.addAll(acl.getPermissions(principal));
            }
            //get permissions to userGroup
            principal.setType(Principal.Type.GROUP);
            Set<Role> userMembershipRoles;
            try {
                userMembershipRoles = userManager.getUserMembershipRoles(name, projectDirectory.getParentFile().getName());
            } catch (OrganizationServiceException e) {
                throw new ServletException(e.getMessage(), e);
            }
            for (Role role : userMembershipRoles) {
                principal.setName("workspace/".concat(role.getName()));
                if (acl.getPermissions(principal) != null) {
                    resultPermissions.addAll(acl.getPermissions(principal));
                }
            }
        }
        return resultPermissions.contains(VirtualFileSystemInfo.BasicPermissions.READ) ||
               resultPermissions.contains(VirtualFileSystemInfo.BasicPermissions.ALL);
    }

    /**
     * Check user exists in organization.
     * First of all organization header will be parsed to username and password,
     * then UserManager will check parsed credentials.
     *
     * @param user
     *         request sender
     * @param password
     *         request sender password
     * @return <code>true</code> if user exists, <code>false</code> if doesn't
     * @throws ServletException
     */
    private boolean isUserAuthenticated(String user, String password) throws ServletException {
        try {
            return !user.isEmpty() && userManager.authenticateUser(user, password);
        } catch (OrganizationServiceException e) {
            throw new ServletException(e.getMessage(), e);
        }
    }

    @Override
    public void destroy() {
    }

    public void setUserManager(UserManager userManager) {
        this.userManager = userManager;
    }
}