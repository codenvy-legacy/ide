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
package org.exoplatform.ide.extension.openshift.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.openshift.shared.AppInfo;
import org.exoplatform.ide.extension.openshift.shared.Credentials;
import org.exoplatform.ide.extension.openshift.shared.RHUserInfo;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: OpenShiftAutoBeanFactory.java Mar 13, 2012 2:38:22 PM azatsarynnyy $
 */
public interface OpenShiftAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for an application info bean.
     *
     * @return an {@link AutoBean} of type {@link AppInfo}
     */
    AutoBean<AppInfo> appInfo();

    /**
     * A factory method for a RedHat user info bean.
     *
     * @return a {@link AutoBean} of type {@link RHUserInfo}
     */
    AutoBean<RHUserInfo> rhUserInfo();

    /**
     * A factory method for a credentials bean.
     *
     * @return an {@link AutoBean} of type {@link Credentials}
     */
    AutoBean<Credentials> credentials();
}
