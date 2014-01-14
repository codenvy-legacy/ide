/*
 * CODENVY CONFIDENTIAL
 * __________________
 *
 *  [2012] - [2014] Codenvy, S.A.
 *  All Rights Reserved.
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
package com.codenvy.api.deploy;

import com.codenvy.api.core.user.User;
import com.codenvy.api.core.user.UserState;

import org.everrest.core.tools.WebApplicationDeclaredRoles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.security.Principal;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Filter that set current user as "ide" on all requests.
 * This filter is needed in order to allow the user to be always logged on and
 * to avoiding unauthorized access in launched extension in SDK.
 *
 * @author Artem Zatsarynnyy
 */
@Singleton
public class SetIdeUserFilter implements Filter {
    private static final String USER_STATE_SESSION_ATTRIBUTE_NAME = UserState.class.getName();
    private static final Logger LOG                               = LoggerFactory.getLogger(SetIdeUserFilter.class);
    private WebApplicationDeclaredRoles webApplicationRoles;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        webApplicationRoles = new WebApplicationDeclaredRoles(filterConfig.getServletContext());
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        try {
            UserState.set(getUserState((HttpServletRequest)request));
            chain.doFilter(request, response);
        } finally {
            try {
                UserState.reset();
            } catch (Exception e) {
                LOG.warn("An error occurs while try to reset UserState. ", e);
            }
        }
    }

    private UserState getUserState(HttpServletRequest httpRequest) {
        final HttpSession session = httpRequest.getSession();
        UserState state = (UserState)session.getAttribute(USER_STATE_SESSION_ATTRIBUTE_NAME);
        final Principal principal = httpRequest.getUserPrincipal();
        if (state == null || (principal != null && state.getUser() instanceof IdeUser)) {
            final User user;
            if (principal == null) {
                // user is not authenticated
                user = new IdeUser();
            } else {
                Set<String> userRoles = new LinkedHashSet<>();
                for (String role : webApplicationRoles.getDeclaredRoles()) {
                    if (httpRequest.isUserInRole(role)) {
                        userRoles.add(role);
                    }
                }
                user = new AuthenticatedUser(principal, userRoles);
            }
            session.setAttribute(USER_STATE_SESSION_ATTRIBUTE_NAME, state = new UserState(user));
        }
        return state;
    }

    private static class IdeUser implements User {
        final Set<String> roles;

        IdeUser() {
            roles = new LinkedHashSet<>(2);
            roles.add("developer");
            roles.add("admin");
        }

        @Override
        public String getName() {
            return "ide";
        }

        @Override
        public boolean isMemberOf(String role) {
            return roles.contains(role);
        }
    }

    private static class AuthenticatedUser implements User {
        final String      name;
        final Set<String> roles;

        AuthenticatedUser(Principal principal, Set<String> roles) {
            this.roles = roles;
            this.name = principal.getName();
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public boolean isMemberOf(String role) {
            return roles.contains(role);
        }
    }
}
