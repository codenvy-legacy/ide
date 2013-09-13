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
package org.exoplatform.ide;

import org.everrest.core.RequestHandler;
import org.everrest.core.tools.ResourceLauncher;
import org.exoplatform.container.StandaloneContainer;

/**
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseTest {

    protected StandaloneContainer container;

    public ResourceLauncher launcher;

    public void setUp() throws Exception {
        String containerConf = BaseTest.class.getResource("/conf/standalone/test-configuration.xml").toString();

        StandaloneContainer.addConfigurationURL(containerConf);

        container = StandaloneContainer.getInstance();

        if (System.getProperty("java.security.auth.login.config") == null)
            System.setProperty("java.security.auth.login.config", Thread.currentThread().getContextClassLoader()
                                                                        .getResource("login.conf").toString());
        if (System.getProperty("org.exoplatform.ide.server.user-config-path") == null)
            System.setProperty("org.exoplatform.ide.server.user-config-path", "/ide-home/users/");

        RequestHandler handler = (RequestHandler)container.getComponentInstanceOfType(RequestHandler.class);
        launcher = new ResourceLauncher(handler);

    }
}
