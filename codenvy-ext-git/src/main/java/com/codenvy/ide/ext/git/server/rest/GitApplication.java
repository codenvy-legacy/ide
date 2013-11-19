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
 * @author <a href="mailto:andrey.parfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: GitApplication.java 22811 2011-03-22 07:28:35Z andrew00x $
 */
public class GitApplication extends Application {
    private final Set<Object>     singletons;
    private final Set<Class< ? >> classes;

    public GitApplication() {
        classes = new HashSet<Class< ? >>(1);
        classes.add(GitService.class);

        singletons = new HashSet<Object>(7);
        singletons.add(new GitExceptionMapper());
        singletons.add(new StatusPageWriter());
        singletons.add(new BranchListWriter());
        singletons.add(new TagListWriter());
        singletons.add(new RemoteListWriter());
        singletons.add(new CommitMessageWriter());
        singletons.add(new MergeResultWriter());
    }

    /** @see javax.ws.rs.core.Application#getClasses() */
    @Override
    public Set<Class< ? >> getClasses() {
        return classes;
    }

    /**
     * session
     * 
     * @see javax.ws.rs.core.Application#getSingletons()
     */
    @Override
    public Set<Object> getSingletons() {
        return singletons;
    }
}
