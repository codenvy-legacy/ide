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
package org.exoplatform.ide.shell.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.shell.client.maven.BuildStatus;
import org.exoplatform.ide.shell.shared.Login;
import org.exoplatform.ide.shell.shared.ShellConfiguration;

/**
 * The interface for the {@link AutoBean} generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: ShellAutoBeanFactory.java Mar 27, 2012 1:03:50 PM azatsarynnyy $
 */
public interface ShellAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for a login command bean.
     *
     * @return an {@link AutoBean} of type {@link Login}
     */
    AutoBean<Login> login();

    /**
     * A factory method for a shell configuration bean.
     *
     * @return an {@link AutoBean} of type {@link ShellConfiguration}
     */
    AutoBean<ShellConfiguration> shellConfiguration();

    /**
     * A factory method for a status bean.
     *
     * @return an {@link AutoBean} of type {@link BuildStatus}
     */
    AutoBean<BuildStatus> buildStatus();
}
