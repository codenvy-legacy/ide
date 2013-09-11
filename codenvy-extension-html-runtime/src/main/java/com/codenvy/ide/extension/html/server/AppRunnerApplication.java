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
package com.codenvy.ide.extension.html.server;

import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * JAX-RS application for running HTML applications.
 * 
 * @author <a href="mailto:azatsarynnyy@codenvy.com">Artem Zatsarynnyy</a>
 * @version $Id: AppRunnerApplication.java Jun 26, 2013 1:12:46 PM azatsarynnyy $
 */
public class AppRunnerApplication extends Application {
    private final Set<Class< ? >> classes;
    private final Set<Object>     objects;

    public AppRunnerApplication() {
        classes = new HashSet<Class< ? >>(1);
        classes.add(HtmlApplicationRunnerService.class);
        objects = new HashSet<Object>(1);
        objects.add(new ApplicationRunnerExceptionMapper());
    }

    @Override
    public Set<Class< ? >> getClasses() {
        return classes;
    }

    @Override
    public Set<Object> getSingletons() {
        return objects;
    }
}
