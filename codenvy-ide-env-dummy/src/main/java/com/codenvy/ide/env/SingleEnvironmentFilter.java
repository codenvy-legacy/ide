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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Set up environment variable. Only for local packaging with single workspace. Don't use it in production packaging.
 *
 * @author andrew00x
 */
public class SingleEnvironmentFilter implements Filter {
//    private String wsName;
//    private String wsId;

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

//        wsName = Constants.WORKSPACE.getName();
//        wsId = Constants.WORKSPACE.getId();

//        wsName = filterConfig.getInitParameter("ws-name");
//        wsId = filterConfig.getInitParameter("ws-id");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final EnvironmentContext env = EnvironmentContext.getCurrent();
        env.setWorkspaceName("default");
        env.setWorkspaceId("1q2w3e");
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

        List roles = new ArrayList();
        roles.addAll(Arrays.asList("system/admin", "system/manager", "user"));

        //return new UserImpl(Constants.USER.getEmail(), Constants.USER.getId(), Constants.TOKEN.getValue(), roles);

        return new UserImpl("ide", "dummy_token", Arrays.asList("workspace/developer", "workspace/developer", "system/admin", "system/manager", "user"));
    }
}
