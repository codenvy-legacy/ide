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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.cloudfoundry.shared.*;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:azatsarynnyy@exoplatform.org">Artem Zatsarynnyy</a>
 * @version $Id: CloudFoundryAutoBeanFactory.java Mar 16, 2012 12:27:47 PM azatsarynnyy $
 */
public interface CloudFoundryAutoBeanFactory extends AutoBeanFactory {

    /**
     * A factory method for an application info bean.
     *
     * @return an {@link AutoBean} of type {@link CloudFoundryApplication}
     */
    AutoBean<CloudFoundryApplication> cloudFoundryApplication();

    /**
     * A factory method for a system info bean.
     *
     * @return an {@link AutoBean} of type {@link SystemInfo}
     */
    AutoBean<SystemInfo> systemInfo();

    /**
     * A factory method for a system resources bean.
     *
     * @return an {@link AutoBean} of type {@link SystemResources}
     */
    AutoBean<SystemResources> systemResources();

    /**
     * A factory method for a system resources bean.
     *
     * @return an {@link AutoBean} of type {@link Framework}
     */
    AutoBean<Framework> framework();

    /**
     * A factory method for a credentials bean.
     *
     * @return an {@link AutoBean} of type {@link Credentials}
     */
    AutoBean<Credentials> credentials();

    /**
     * A factory method for a create application request bean.
     *
     * @return an {@link AutoBean} of type {@link CreateApplicationRequest}
     */
    AutoBean<CreateApplicationRequest> createApplicationRequest();

    /**
     * A factory method for CloudFoundry services bean.
     *
     * @return an {@link AutoBean} of type {@link CloudfoundryServices}
     */
    AutoBean<CloudfoundryServices> services();

    /**
     * A factory method for CloudFoundry system service bean.
     *
     * @return an {@link AutoBean} of type {@link SystemService}
     */
    AutoBean<SystemService> systemService();

    /**
     * A factory method for CloudFoundry provisioned service bean.
     *
     * @return an {@link AutoBean} of type {@link ProvisionedService}
     */
    AutoBean<ProvisionedService> provisionedService();
}
