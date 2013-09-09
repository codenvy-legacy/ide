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
package org.exoplatform.ide.extension.cloudbees.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.cloudbees.shared.ApplicationInfo;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesAccount;
import org.exoplatform.ide.extension.cloudbees.shared.CloudBeesUser;
import org.exoplatform.ide.extension.cloudbees.shared.Credentials;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: CloudBeesAutoBeanFactory.java Mar 15, 2012 10:38:48 AM azatsarynnyy $
 */
public interface CloudBeesAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for an application info bean.
     *
     * @return an {@link AutoBean} of type {@link ApplicationInfo}
     */
    AutoBean<ApplicationInfo> applicationInfo();

    /**
     * A factory method for a credentials bean.
     *
     * @return an {@link AutoBean} of type {@link Credentials}
     */
    AutoBean<Credentials> credentials();

    /**
     * A factory method for a CloudBees account bean.
     *
     * @return an {@link AutoBean} of type {@link CloudBeesAccount}
     */
    AutoBean<CloudBeesAccount> account();

    /**
     * A factory method for a CloudBees user bean.
     *
     * @return an {@link AutoBean} of type {@link CloudBeesUser}
     */
    AutoBean<CloudBeesUser> user();
}
