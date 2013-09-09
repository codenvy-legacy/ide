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
import org.exoplatform.ide.extension.cloudfoundry.client.login.LoginEvent;

import static org.exoplatform.ide.extension.cloudfoundry.client.CloudFoundryExtension.PAAS_PROVIDER.WEB_FABRIC;

/**
 * Control for switching between accounts.
 * 
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Aug 16, 2011 12:54:05 PM anya $
 */
@RolesAllowed("developer")
public class SwitchAccountControl extends AbstractCloudFoundryControl {

    private static final String CF_ID  = CloudFoundryExtension.LOCALIZATION_CONSTANT.switchAccountControlId();

    private static final String WF_ID  = CloudFoundryExtension.LOCALIZATION_CONSTANT.switchTier3WebFabricAccountControlId();

    private static final String TITLE  = CloudFoundryExtension.LOCALIZATION_CONSTANT.switchAccountControlTitle();

    private static final String PROMPT = CloudFoundryExtension.LOCALIZATION_CONSTANT.switchAccountControlPrompt();

    public SwitchAccountControl(PAAS_PROVIDER paasProvider) {
        super(paasProvider == WEB_FABRIC ? WF_ID : CF_ID);
        setTitle(TITLE);
        setPrompt(PROMPT);
        setImages(CloudFoundryClientBundle.INSTANCE.switchAccount(),
                  CloudFoundryClientBundle.INSTANCE.switchAccountDisabled());
        setEvent(new LoginEvent(paasProvider));
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
