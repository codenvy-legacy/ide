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
package org.exoplatform.ide.extension.openshift.server.rest;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ExpressApplication extends Application {
    private final Set<Class<?>> classes;
    private final Set<Object>   singletons;

    public ExpressApplication() {
        classes = new HashSet<Class<?>>(1);
        classes.add(ExpressService.class);
        singletons = new HashSet<Object>(1);
        singletons.add(new ExpressExceptionMapper());
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