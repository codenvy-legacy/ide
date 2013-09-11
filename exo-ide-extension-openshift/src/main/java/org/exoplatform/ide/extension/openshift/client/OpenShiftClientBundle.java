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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * OpenShift client resources (images).
 *
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: May 26, 2011 10:45:11 AM anya $
 */
public interface OpenShiftClientBundle extends ClientBundle {
    OpenShiftClientBundle INSTANCE = GWT.<OpenShiftClientBundle>create(OpenShiftClientBundle.class);

    @Source("org/exoplatform/ide/extension/openshift/images/controls/openshift_32.png")
    ImageResource openShiftLogo();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/ok.png")
    ImageResource okButton();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/properties.png")
    ImageResource propertiesButton();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/properties_Disabled.png")
    ImageResource propertiesButtonDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/cancel.png")
    ImageResource cancelButton();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/createApp.png")
    ImageResource createApplicationControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/createApp_Disabled.png")
    ImageResource createApplicationControlDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/destroyApp.png")
    ImageResource destroyApplicationControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/destroyApp_Disabled.png")
    ImageResource destroyApplicationControlDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/appInfo.png")
    ImageResource applicationInfoControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/appInfo_Disabled.png")
    ImageResource applicationInfoControlDisabled();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/switchAccount.png")
    ImageResource switchAccount();

    @Source("org/exoplatform/ide/extension/heroku/images/controls/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/preview.png")
    ImageResource previewControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/preview_Disabled.png")
    ImageResource previewControlDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/apps-list.png")
    ImageResource userInfoControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/apps-list_Disabled.png")
    ImageResource userInfoControlDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/createDomain.png")
    ImageResource createDomainControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/createDomain_Disabled.png")
    ImageResource createDomainControlDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/updateKey.png")
    ImageResource updateKeyControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/updateKey_Disabled.png")
    ImageResource updateKeyControlDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/openshift.png")
    ImageResource openShiftControl();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/openshift_Disabled.png")
    ImageResource openShiftControlDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/openshift_48.png")
    ImageResource openShiftControl48();

    @Source("org/exoplatform/ide/extension/openshift/images/controls/openshift_48_Disabled.png")
    ImageResource openShiftControl48Disabled();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/start.png")
    ImageResource startButton();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/start_Disabled.png")
    ImageResource startButtonDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/restart.png")
    ImageResource restartButton();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/restart_Disabled.png")
    ImageResource restartButtonDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/stop.png")
    ImageResource stopButton();

    @Source("org/exoplatform/ide/extension/openshift/images/buttons/stop_Disabled.png")
    ImageResource stopButtonDisabled();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/delete.png")
    ImageResource deleteCartridge();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/start.png")
    ImageResource startCartridge();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/stop.png")
    ImageResource stopCartridge();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/restart.png")
    ImageResource restartCartridge();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/reload.png")
    ImageResource reloadCartridge();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/credential.png")
    ImageResource credentialCartridge();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/add_cartridge.png")
    ImageResource addCartridge();

    @Source("org/exoplatform/ide/extension/openshift/images/cartridges/add_cartridge_disable.png")
    ImageResource addCartridgeDisable();
}
