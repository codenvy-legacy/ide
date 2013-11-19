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
package com.codenvy.ide.ext.git.server.rest;

import javax.ws.rs.core.Application;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: SamplesServiceApplication.java Sep 2, 2011 12:20:58 PM vereshchaka $
 */
public class GitHubServiceApplication extends Application {
    private Set<Class< ? >> classes;

    private Set<Object>     singletons;

    public GitHubServiceApplication() {
        classes = new HashSet<Class< ? >>(1);
        classes.add(GitHubService.class);
        singletons = new HashSet<Object>(1);
        singletons.add(new GitHubExceptionMapper());
    }

    /** @see javax.ws.rs.core.Application#getClasses() */
    @Override
    public Set<Class< ? >> getClasses() {
        return classes;
    }

    /** @see javax.ws.rs.core.Application#getSingletons() */
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }

}
