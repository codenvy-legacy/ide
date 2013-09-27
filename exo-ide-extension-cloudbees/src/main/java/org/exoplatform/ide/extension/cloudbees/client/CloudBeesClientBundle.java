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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * CloudBees client resources (images).
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesClientBundle.java Jun 23, 2011 10:03:22 AM vereshchaka $
 */
public interface CloudBeesClientBundle extends ClientBundle {
    CloudBeesClientBundle INSTANCE = GWT.<CloudBeesClientBundle>create(CloudBeesClientBundle.class);

    /*
     * Buttons
     */
    @Source("org/exoplatform/ide/extension/cloudbees/images/cloudbees_36.png")
    ImageResource cloudBeesLogo();

    @Source("org/exoplatform/ide/extension/cloudbees/images/ok.png")
    ImageResource okButton();

    @Source("org/exoplatform/ide/extension/cloudbees/images/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/cancel.png")
    ImageResource cancelButton();

    @Source("org/exoplatform/ide/extension/cloudbees/images/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/properties.png")
    ImageResource propertiesButton();

    @Source("org/exoplatform/ide/extension/cloudbees/images/properties_Disabled.png")
    ImageResource propertiesButtonDisabled();

    /*
     * CloudBees controls
     */
    @Source("org/exoplatform/ide/extension/cloudbees/images/cloudbees.png")
    ImageResource cloudBees();

    @Source("org/exoplatform/ide/extension/cloudbees/images/cloudbees_Disabled.png")
    ImageResource cloudBeesDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/cloudbees_48.png")
    ImageResource cloudBees48();

    @Source("org/exoplatform/ide/extension/cloudbees/images/cloudbees_48_Disabled.png")
    ImageResource cloudBees48Disabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/initializeApp.png")
    ImageResource initializeApp();

    @Source("org/exoplatform/ide/extension/cloudbees/images/initializeApp_Disabled.png")
    ImageResource initializeAppDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("org/exoplatform/ide/extension/cloudbees/images/appInfo_Disabled.png")
    ImageResource applicationInfoDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("org/exoplatform/ide/extension/cloudbees/images/deleteApp_Disabled.png")
    ImageResource deleteApplicationDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/updateApp.png")
    ImageResource updateApplication();

    @Source("org/exoplatform/ide/extension/cloudbees/images/updateApp_Disabled.png")
    ImageResource updateApplicationDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/apps-list.png")
    ImageResource appList();

    @Source("org/exoplatform/ide/extension/cloudbees/images/apps-list_Disabled.png")
    ImageResource appListDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/create_account.png")
    ImageResource createAccount();

    @Source("org/exoplatform/ide/extension/cloudbees/images/create_account_Disabled.png")
    ImageResource createAccountDisabled();

    @Source("org/exoplatform/ide/extension/cloudbees/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("org/exoplatform/ide/extension/cloudbees/images/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();
}
