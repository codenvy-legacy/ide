/**
 * 
 */
package com.codenvy.ide.logger;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.core.Application;

/**
 * @author <a href="vsvydenko@codenvy.com">Vfleriy Svydenko</a>
 * @version $Id: LogEventService.java 34027 10.06.13 11:46 vsvydenko $
 */
public class LogEventApplication extends Application {
    private Set<Class<?>> classes;
    private Set<Object>   singletons;

    public LogEventApplication() {
        classes = new HashSet<Class<?>>(1);
        classes.add(LogEventService.class);
        singletons = new HashSet<Object>(1);
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
