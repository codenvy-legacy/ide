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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * CloudFoundry client resources (images).
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryClientBundle.java Jul 12, 2011 10:24:35 AM vereshchaka $
 */
public interface CloudFoundryClientBundle extends ClientBundle {
    CloudFoundryClientBundle INSTANCE = GWT.<CloudFoundryClientBundle> create(CloudFoundryClientBundle.class);

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/cloudfoundry_36.png")
    ImageResource cloudFoundryLogo();

    /*
     * Buttons
     */
    @Source("org/exoplatform/ide/extension/cloudfoundry/images/ok.png")
    ImageResource okButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/cancel.png")
    ImageResource cancelButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/delete.png")
    ImageResource deleteButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/delete_Disabled.png")
    ImageResource deleteButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/add.png")
    ImageResource addButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/add_Disabled.png")
    ImageResource addButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/edit.png")
    ImageResource editButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/edit_Disabled.png")
    ImageResource editButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/properties.png")
    ImageResource propertiesButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/properties_Disabled.png")
    ImageResource propertiesButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/start.png")
    ImageResource startButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/start_Disabled.png")
    ImageResource startButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/restart.png")
    ImageResource restartButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/restart_Disabled.png")
    ImageResource restartButtonDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/stop.png")
    ImageResource stopButton();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/stop_Disabled.png")
    ImageResource stopButtonDisabled();

    /*
     * CloudFoundry controls.
     */
    @Source("org/exoplatform/ide/extension/cloudfoundry/images/cloudfoundry.png")
    ImageResource cloudFoundry();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/cloudfoundry_Disabled.png")
    ImageResource cloudFoundryDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/cloudfoundry_48.png")
    ImageResource cloudFoundry48();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/cloudfoundry_48_Disabled.png")
    ImageResource cloudFoundry48Disabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/initializeApp.png")
    ImageResource createApp();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/initializeApp_Disabled.png")
    ImageResource createAppDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/startApp.png")
    ImageResource startApp();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/startApp_Disabled.png")
    ImageResource startAppDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/restartApp.png")
    ImageResource restartApp();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/restartApp_Disabled.png")
    ImageResource restartAppDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/updateApp.png")
    ImageResource updateApp();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/updateApp_Disabled.png")
    ImageResource updateAppDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/stopApp.png")
    ImageResource stopApp();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/appInfo_Disabled.png")
    ImageResource applicationInfoDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/deleteApp_Disabled.png")
    ImageResource deleteApplicationDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/renameApp.png")
    ImageResource renameApplication();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/renameApp_Disabled.png")
    ImageResource renameApplicationDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_map_url.png")
    ImageResource mapUrl();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_map_url_Disabled.png")
    ImageResource mapUrlDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_unmap_url.png")
    ImageResource unmapUrl();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_unmap_url_Disabled.png")
    ImageResource unmapUrlDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_instances.png")
    ImageResource appInstances();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_instances_Disabled.png")
    ImageResource appInstancesDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_memory.png")
    ImageResource appMemory();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/app_memory_Disabled.png")
    ImageResource appMemoryDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/apps-list.png")
    ImageResource appsList();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/apps-list_Disabled.png")
    ImageResource appsListDisabled();

    /*
     * Tier3 Web Fabric controls.
     */
    @Source("org/exoplatform/ide/extension/cloudfoundry/images/tier3WebFabric_16.png")
    ImageResource tier3WebFabric16();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/tier3WebFabric_16_Disabled.png")
    ImageResource tier3WebFabric16Disabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/tier3WebFabric_48.png")
    ImageResource tier3WebFabric48();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/tier3WebFabric_48_Disabled.png")
    ImageResource tier3WebFabric48Disabled();

    @Source("org/exoplatform/ide/extension/cloudfoundry/images/tier3WebFabric_36.png")
    ImageResource tier3WebFabricLogo();

}
