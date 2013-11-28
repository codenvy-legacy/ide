package org.exoplatform.ide.git.server.provider.rest;

import org.exoplatform.ide.git.server.provider.github.GitHubService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * Application instance for Git services.
 */
public class ProviderApplication extends Application {
    private Set<Class<?>> classes;

    private Set<Object> singletons;

    public ProviderApplication() {
        classes = new HashSet<>(2);
        classes.add(ProviderService.class);
        classes.add(GitHubService.class);
        singletons = new HashSet<>(1);
        singletons.add(new ProviderExceptionMapper());
    }

    /** @see javax.ws.rs.core.Application#getClasses() */
    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

    /** @see javax.ws.rs.core.Application#getSingletons() */
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
