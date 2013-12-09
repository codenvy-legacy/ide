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
package com.codenvy.ide.server;

import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Workspace;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.MembershipEntry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * Provide login redirection to SSO server on client side. Filter also wraps original request and delegate Principal request to the
 * Principal what comes from SSO server.
 */
public class LoginFilter implements Filter {

    private static WorkspaceManager workspaceManager;

    @Override
    public void init(FilterConfig config) throws ServletException {
        try {
            workspaceManager = new WorkspaceManager();
        } catch (OrganizationServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletResponse httpResp = (HttpServletResponse)response;

        IdentityRegistry identityRegistry =
                (IdentityRegistry)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityRegistry.class);
        List<String> roles = Arrays.asList("admin", "developer");
        identityRegistry.register((new Identity("tmp-user125",
                                                new HashSet<MembershipEntry>(), roles)));

        try {
            if (isTempUser((HttpServletRequest)request)) {
                chain.doFilter(new RequestWrapper((HttpServletRequest)request), response);
            } else
                chain.doFilter(request, response);
        } catch (OrganizationServiceException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    static class RequestWrapper extends HttpServletRequestWrapper {

        private HttpServletRequest request;

        public RequestWrapper(HttpServletRequest request) {
            super(request);
            this.request = request;
        }

        @Override
        public String getRemoteUser() {
            return "tmp-user125";
        }

        @Override
        public boolean isUserInRole(String role) {
            return true;
        }

        @Override
        public Principal getUserPrincipal() {
            return new Principal() {
                @Override
                public String getName() {
                    return "tmp-user125";
                }
            };
        }

    }

    private boolean isTempUser(HttpServletRequest request) throws OrganizationServiceException {
        String url = request.getRequestURI();
        String[] split = url.split("/");
        String ws = split[3];
        if (ws.equals("ide") || ws.equals("oauth"))
            return false;
        Workspace workspace = workspaceManager.getWorkspaceByName(ws);
        if (workspace.isTemporary()) {
            HttpServletRequest request2 = (HttpServletRequest)request;
            if (request2.getUserPrincipal() == null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub

    }
}
