package com.codenvy.api.bootstrap.servlet;

import com.codenvy.commons.env.EnvironmentContext;

import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.MembershipEntry;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Set up environment variable for API.
 *
 * @author <a href="mailto:aparfonov@codenvy.com">Andrey Parfonov</a>
 */
// TODO: Find common solution to do this. Avoid have few filters to do the same work.
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
        env.setVariable(EnvironmentContext.TMP_DIR, new File("../temp"));
        final String vfsRootDir = System.getProperty("com.codenvy.vfs.rootdir", "../temp/fs-root");
        env.setVariable(EnvironmentContext.VFS_ROOT_DIR, vfsRootDir);
        env.setVariable(EnvironmentContext.VFS_INDEX_DIR, new File("../temp/fs-index-root"));
        Set<MembershipEntry> e = new HashSet<>();
        ConversationState.setCurrent(new ConversationState(new Identity("user", e, new HashSet<>(Arrays.asList("developer")))));
        try {
            chain.doFilter(request, response);
        } finally {
            ConversationState.setCurrent(null);
            EnvironmentContext.reset();
        }
    }

    @Override
    public void destroy() {
    }
}
