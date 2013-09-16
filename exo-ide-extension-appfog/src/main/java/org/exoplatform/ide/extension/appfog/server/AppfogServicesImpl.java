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
package org.exoplatform.ide.extension.appfog.server;

import org.exoplatform.ide.extension.appfog.shared.AppfogProvisionedService;
import org.exoplatform.ide.extension.appfog.shared.AppfogServices;
import org.exoplatform.ide.extension.appfog.shared.AppfogSystemService;

import java.util.Arrays;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public class AppfogServicesImpl implements AppfogServices {
    private AppfogSystemService[]      systemServices;
    private AppfogProvisionedService[] provisionedServices;

    public AppfogServicesImpl() {
    }

    public AppfogServicesImpl(AppfogSystemService[] systemServices, AppfogProvisionedService[] provisionedServices) {
        this.systemServices = systemServices;
        this.provisionedServices = provisionedServices;
    }

    @Override
    public AppfogSystemService[] getAppfogSystemService() {
        return systemServices;
    }

    @Override
    public void setAppfogSystemService(AppfogSystemService[] system) {
        this.systemServices = system;
    }

    @Override
    public AppfogProvisionedService[] getAppfogProvisionedService() {
        return provisionedServices;
    }

    @Override
    public void setAppfogProvisionedService(AppfogProvisionedService[] provisioned) {
        this.provisionedServices = provisioned;
    }

    @Override
    public String toString() {
        return "AppfogServicesImpl{" +
               "systemServices=" + (systemServices == null ? null : Arrays.asList(systemServices)) +
               ", provisionedServices=" + (provisionedServices == null ? null : Arrays.asList(provisionedServices)) +
               '}';
    }
}
