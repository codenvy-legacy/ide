package com.codenvy.ide.env;

import com.codenvy.commons.env.EnvironmentContext;
import com.codenvy.commons.user.User;
import com.codenvy.commons.user.UserImpl;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.security.Principal;
import java.util.Arrays;

/**
 * Set up environment variable. Only for local packaging with single workspace. Don't use it in production packaging.
 *
 * @author andrew00x
 */
public class SingleEnvironmentFilter implements Filter {
    private String wsName;
    private String wsId;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        wsName = filterConfig.getInitParameter("ws-name");
        wsId = filterConfig.getInitParameter("ws-id");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final EnvironmentContext env = EnvironmentContext.getCurrent();
        env.setWorkspaceName(wsName);
        env.setWorkspaceId(wsId);
        final User user = getUser((HttpServletRequest)request);
        env.setUser(user);
        try {
            chain.doFilter(new HttpServletRequestWrapper((HttpServletRequest)request) {
                @Override
                public String getRemoteUser() {
                    return user.getName();
                }

                @Override
                public boolean isUserInRole(String role) {
                    return user.isMemberOf(role);
                }

                @Override
                public Principal getUserPrincipal() {
                    return new Principal() {
                        @Override
                        public String getName() {
                            return user.getName();
                        }
                    };
                }
            }, response);
        } finally {
            EnvironmentContext.reset();
        }
    }

    @Override
    public void destroy() {
    }

    protected User getUser(HttpServletRequest httpRequest) {
        return new UserImpl("ide", "dummy_token", Arrays.asList("developer", "admin", "system/admin", "system/manager", "user"));
    }
}
