/*
 * Copyright (C) 2012 eXo Platform SAS.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.codenvy.ide.ext.appfog.client;

import com.codenvy.ide.ext.appfog.shared.*;
import com.codenvy.ide.extension.cloudfoundry.shared.Credentials;
import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

/**
 * The interface for the AutoBean generator.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
public interface AppfogAutoBeanFactory extends AutoBeanFactory {
    /**
     * A factory method for an application info bean.
     *
     * @return an {@link AutoBean} of type {@link AppfogApplication}
     */
    AutoBean<AppfogApplication> appfogApplication();

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
     * @return an {@link AutoBean} of type {@link CreateAppfogApplicationRequest}
     */
    AutoBean<CreateAppfogApplicationRequest> createAppfogApplicationRequest();

    /**
     * A factory method for AppFog services bean.
     *
     * @return an {@link AutoBean} of type {@link AppfogServices}
     */
    AutoBean<AppfogServices> services();

    /**
     * A factory method for AppFog system service bean.
     *
     * @return an {@link AutoBean} of type {@link AppfogSystemService}
     */
    AutoBean<AppfogSystemService> systemService();

    /**
     * A factory method for AppFog provisioned service bean.
     *
     * @return an {@link AutoBean} of type {@link AppfogProvisionedService}
     */
    AutoBean<AppfogProvisionedService> provisionedService();

    /**
     * A factory method for AppFog infrastructure bean.
     *
     * @return an {@link AutoBean} of type {@link InfraDetail}
     */
    AutoBean<InfraDetail> infraDetail();
}