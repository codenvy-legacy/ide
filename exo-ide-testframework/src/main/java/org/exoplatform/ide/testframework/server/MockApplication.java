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
package org.exoplatform.ide.testframework.server;

import org.exoplatform.ide.testframework.server.cloudbees.CloudbeesExceptionMapper;
import org.exoplatform.ide.testframework.server.cloudbees.MockCloudbeesService;
import org.exoplatform.ide.testframework.server.cloudfoundry.CloudfoundryExceptionMapper;
import org.exoplatform.ide.testframework.server.cloudfoundry.MockCloudfoundryService;
import org.exoplatform.ide.testframework.server.heroku.HerokuExceptionMapper;
import org.exoplatform.ide.testframework.server.heroku.MockHerokuService;
import org.exoplatform.ide.testframework.server.jenkins.MockJenkinsService;

import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jul 6, 2011 11:45:58 AM anya $
 */
public class MockApplication extends Application {
    private Set<Class<?>> classes;

    private Set<Object> singletons;

    public MockApplication() {
        classes = new HashSet<Class<?>>(1);
        classes.add(MockHerokuService.class);
        // classes.add(MockExpressService.class);
        // classes.add(MockGitRepoService.class);
        classes.add(MockCloudbeesService.class);
        classes.add(MockJenkinsService.class);

        classes.add(MockCloudfoundryService.class);

        singletons = new HashSet<Object>(1);
        singletons.add(new HerokuExceptionMapper());
        singletons.add(new CloudbeesExceptionMapper());

        singletons.add(new CloudfoundryExceptionMapper());
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
