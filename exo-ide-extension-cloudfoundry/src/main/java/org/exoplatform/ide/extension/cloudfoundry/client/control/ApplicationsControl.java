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
package org.exoplatform.ide.extension.cloudfoundry.client.control;

import org.exoplatform.ide.client.framework.annotation.RolesAllowed;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryClientBundle;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension;
import org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER;
import org.exoplatform.ide.extension.cloudfoundry.client.apps.ShowApplicationsEvent;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * @author <a href="mailto:evidolob@exoplatform.com">Evgen Vidolob</a>
 * @version $Id: Aug 18, 2011 evgen $
 */
@RolesAllowed({"workspace/developer"})
public class ApplicationsControl extends AbstractCloudFoundryControl {

    private static final String CF_ID = CloudFoundryExtension.LOCALIZATION_CONSTANT.appsControlId();

    private static final String WF_ID = CloudFoundryExtension.LOCALIZATION_CONSTANT.tier3WebFabricAppsControlId();

    public ApplicationsControl(PAAS_PROVIDER paasProvider) {
        super(paasProvider == WEB_FABRIC ? WF_ID : CF_ID);
        setTitle(CloudFoundryExtension.LOCALIZATION_CONSTANT.appsControlTitle());
        setPrompt(CloudFoundryExtension.LOCALIZATION_CONSTANT.appsControlPrompt());
        setEvent(new ShowApplicationsEvent(paasProvider));
        setImages(CloudFoundryClientBundle.INSTANCE.appsList(), CloudFoundryClientBundle.INSTANCE.appsListDisabled());
    }

    @Override
    public void initialize() {
        IDE.addHandler(VfsChangedEvent.TYPE, this);

        setVisible(true);
    }

    /** @see org.exoplatform.ide.extension.cloudfoundry.client.control.AbstractCloudFoundryControl#refresh() */
    @Override
    protected void refresh() {
        setEnabled(vfsInfo != null);
    }
}
