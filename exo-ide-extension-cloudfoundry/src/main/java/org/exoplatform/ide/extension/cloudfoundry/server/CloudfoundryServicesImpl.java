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
package org.exoplatform.ide.extension.cloudfoundry.server;

import org.exoplatform.ide.extension.cloudfoundry.shared.CloudfoundryServices;
import org.exoplatform.ide.extension.cloudfoundry.shared.ProvisionedService;
import org.exoplatform.ide.extension.cloudfoundry.shared.SystemService;

import java.util.Arrays;

/**
 * @author <a href="mailto:aparfonov@exoplatform.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class CloudfoundryServicesImpl implements CloudfoundryServices {
    private SystemService[]      system;
    private ProvisionedService[] provisioned;

    public CloudfoundryServicesImpl(SystemService[] system, ProvisionedService[] provisioned) {
        this.system = system;
        this.provisioned = provisioned;
    }

    public CloudfoundryServicesImpl() {
    }

    @Override
    public SystemService[] getSystem() {
        return system;
    }

    @Override
    public void setSystem(SystemService[] system) {
        this.system = system;
    }

    @Override
    public ProvisionedService[] getProvisioned() {
        return provisioned;
    }

    @Override
    public void setProvisioned(ProvisionedService[] provisioned) {
        this.provisioned = provisioned;
    }

    @Override
    public String toString() {
        return "CloudfoundryServicesImpl{" +
               "system=" + (system == null ? null : Arrays.asList(system)) +
               ", provisioned=" + (provisioned == null ? null : Arrays.asList(provisioned)) +
               '}';
    }
}
