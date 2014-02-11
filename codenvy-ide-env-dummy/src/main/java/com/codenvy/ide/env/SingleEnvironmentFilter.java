package com.codenvy.ide.env;

import com.codenvy.commons.env.EnvironmentContext;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipalLookupService;

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
        env.setVariable(EnvironmentContext.WORKSPACE_NAME, wsName);
        env.setVariable(EnvironmentContext.WORKSPACE_ID, wsId);
        env.setVariable(EnvironmentContext.GIT_SERVER, "git");
        try {
            chain.doFilter(request, response);
        } finally {
            EnvironmentContext.reset();
        }
    }

    @Override
    public void destroy() {
    }
}
