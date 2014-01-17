package com.codenvy.api.servlet;

import com.codenvy.commons.env.EnvironmentContext;

import javax.inject.Singleton;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * Set up environment variable for API.
 *
 * @author andrew00x
 */
// TODO: Find common solution to do this. Avoid have few filters to do the same work.
@Singleton
public class EnvironmentFilter implements Filter {
    static final String WS_NAME = "dev-monit";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final EnvironmentContext env = EnvironmentContext.getCurrent();
        env.setVariable(EnvironmentContext.WORKSPACE_NAME, WS_NAME);
        env.setVariable(EnvironmentContext.WORKSPACE_ID, WS_NAME);
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
