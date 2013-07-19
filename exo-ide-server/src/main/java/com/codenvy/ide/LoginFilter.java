/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide;

import com.codenvy.organization.client.WorkspaceManager;
import com.codenvy.organization.exception.OrganizationServiceException;
import com.codenvy.organization.model.Workspace;

import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.ConversationState;
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
        
        IdentityRegistry identityRegistry =  (IdentityRegistry)ExoContainerContext.getCurrentContainer().getComponentInstanceOfType(IdentityRegistry.class);
        List<String> roles = Arrays.asList("admin", "developer");
        identityRegistry.register((new Identity("tmp-user125",
                                                                        new HashSet<MembershipEntry>(), roles)));
        
        try {
            if (isTempUser((HttpServletRequest)request))
            {
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
        Workspace workspace = workspaceManager.getWorkspaceByName(ws);
        if (workspace.isTemporary())
        {
            HttpServletRequest request2 = (HttpServletRequest)request;
            if (request2.getUserPrincipal() == null)
            {
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
