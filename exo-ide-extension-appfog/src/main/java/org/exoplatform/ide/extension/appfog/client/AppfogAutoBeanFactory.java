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
package org.exoplatform.ide.extension.appfog.client;

import com.google.web.bindery.autobean.shared.AutoBean;
import com.google.web.bindery.autobean.shared.AutoBeanFactory;

import org.exoplatform.ide.extension.appfog.shared.*;
import org.exoplatform.ide.extension.cloudfoundry.shared.Credentials;
import org.exoplatform.ide.extension.cloudfoundry.shared.Framework;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemInfo;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemResources;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AppfogAutoBeanFactory extends AutoBeanFactory {
    AutoBean<AppfogApplication> appfogApplication();

    AutoBean<SystemInfo> systemInfo();

    AutoBean<SystemResources> systemResources();

    AutoBean<Framework> framework();

    AutoBean<Credentials> credentials();

    AutoBean<CreateAppfogApplicationRequest> createAppfogApplicationRequest();

    AutoBean<AppfogServices> services();

    AutoBean<AppfogSystemService> systemService();

    AutoBean<AppfogProvisionedService> provisionedService();

    AutoBean<InfraDetail> infraDetail();
}
