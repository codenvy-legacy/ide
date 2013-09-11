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
package org.exoplatform.ide.extension.java.server;

import org.everrest.core.RequestHandler;
import org.everrest.core.ResourceBinder;
import org.everrest.core.tools.ResourceLauncher;
import org.everrest.core.tools.SimpleSecurityContext;
import org.everrest.test.mock.MockPrincipal;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.security.ConversationState;
import org.exoplatform.services.security.Identity;
import org.junit.BeforeClass;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:tnemov@gmail.com">Evgen Vidolob</a>
 * @version $Id: Base Mar 30, 2011 11:35:14 AM evgen $
 */

public class Base {

    protected static StandaloneContainer container;

    public int maxBufferSize = 200 * 1024;

    public static ResourceBinder binder;

    public static ResourceLauncher launcher;

    public static int resourceNumber = 0;

    protected static SimpleSecurityContext adminSecurityContext;

    protected final Log log = ExoLogger.getLogger(this.getClass().getSimpleName());

    @BeforeClass
    public static void setUp() throws Exception {
        String containerConf = Base.class.getResource("/conf/standalone/test-configuration.xml").toString();

        StandaloneContainer.addConfigurationURL(containerConf);

        container = StandaloneContainer.getInstance();

        if (System.getProperty("java.security.auth.login.config") == null)
            System.setProperty("java.security.auth.login.config", Thread.currentThread().getContextClassLoader()
                                                                        .getResource("login.conf").toString());

        binder = (ResourceBinder)container.getComponentInstanceOfType(ResourceBinder.class);
        resourceNumber = binder.getSize();
        RequestHandler handler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
        launcher = new ResourceLauncher(handler);
        ConversationState state = new ConversationState(new Identity("root"));
        ConversationState.setCurrent(state);
        Set<String> adminRoles = new HashSet<String>();
        adminRoles.add("administrators");
        Set<String> devRoles = new HashSet<String>();
        devRoles.add("developers");
        adminSecurityContext = new SimpleSecurityContext(new MockPrincipal("root"), adminRoles, "BASIC", false);
    }

}
