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

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AppfogClientBundle extends ClientBundle {
    AppfogClientBundle INSTANCE = GWT.<AppfogClientBundle>create(AppfogClientBundle.class);

    @Source("org/exoplatform/ide/extension/appfog/images/appfog_36.png")
    ImageResource appfogLogo();

    /*
     * Buttons
     */
    @Source("org/exoplatform/ide/extension/appfog/images/ok.png")
    ImageResource okButton();

    @Source("org/exoplatform/ide/extension/appfog/images/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/cancel.png")
    ImageResource cancelButton();

    @Source("org/exoplatform/ide/extension/appfog/images/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/delete.png")
    ImageResource deleteButton();

    @Source("org/exoplatform/ide/extension/appfog/images/delete_Disabled.png")
    ImageResource deleteButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/add.png")
    ImageResource addButton();

    @Source("org/exoplatform/ide/extension/appfog/images/add_Disabled.png")
    ImageResource addButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/edit.png")
    ImageResource editButton();

    @Source("org/exoplatform/ide/extension/appfog/images/edit_Disabled.png")
    ImageResource editButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/properties.png")
    ImageResource propertiesButton();

    @Source("org/exoplatform/ide/extension/appfog/images/properties_Disabled.png")
    ImageResource propertiesButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/start.png")
    ImageResource startButton();

    @Source("org/exoplatform/ide/extension/appfog/images/start_Disabled.png")
    ImageResource startButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/restart.png")
    ImageResource restartButton();

    @Source("org/exoplatform/ide/extension/appfog/images/restart_Disabled.png")
    ImageResource restartButtonDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/stop.png")
    ImageResource stopButton();

    @Source("org/exoplatform/ide/extension/appfog/images/stop_Disabled.png")
    ImageResource stopButtonDisabled();

    /*
     * appfog controls
     */
    @Source("org/exoplatform/ide/extension/appfog/images/appfog.png")
    ImageResource appfog();

    @Source("org/exoplatform/ide/extension/appfog/images/appfog_Disabled.png")
    ImageResource appfogDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/appfog_48.png")
    ImageResource appfog48();

    @Source("org/exoplatform/ide/extension/appfog/images/appfog_48_Disabled.png")
    ImageResource appfog48Disabled();

    @Source("org/exoplatform/ide/extension/appfog/images/initializeApp.png")
    ImageResource createApp();

    @Source("org/exoplatform/ide/extension/appfog/images/initializeApp_Disabled.png")
    ImageResource createAppDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/startApp.png")
    ImageResource startApp();

    @Source("org/exoplatform/ide/extension/appfog/images/startApp_Disabled.png")
    ImageResource startAppDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/restartApp.png")
    ImageResource restartApp();

    @Source("org/exoplatform/ide/extension/appfog/images/restartApp_Disabled.png")
    ImageResource restartAppDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/updateApp.png")
    ImageResource updateApp();

    @Source("org/exoplatform/ide/extension/appfog/images/updateApp_Disabled.png")
    ImageResource updateAppDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/stopApp.png")
    ImageResource stopApp();

    @Source("org/exoplatform/ide/extension/appfog/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("org/exoplatform/ide/extension/appfog/images/appInfo_Disabled.png")
    ImageResource applicationInfoDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("org/exoplatform/ide/extension/appfog/images/deleteApp_Disabled.png")
    ImageResource deleteApplicationDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/renameApp.png")
    ImageResource renameApplication();

    @Source("org/exoplatform/ide/extension/appfog/images/renameApp_Disabled.png")
    ImageResource renameApplicationDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/app_map_url.png")
    ImageResource mapUrl();

    @Source("org/exoplatform/ide/extension/appfog/images/app_map_url_Disabled.png")
    ImageResource mapUrlDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/app_unmap_url.png")
    ImageResource unmapUrl();

    @Source("org/exoplatform/ide/extension/appfog/images/app_unmap_url_Disabled.png")
    ImageResource unmapUrlDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/app_instances.png")
    ImageResource appInstances();

    @Source("org/exoplatform/ide/extension/appfog/images/app_instances_Disabled.png")
    ImageResource appInstancesDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/app_memory.png")
    ImageResource appMemory();

    @Source("org/exoplatform/ide/extension/appfog/images/app_memory_Disabled.png")
    ImageResource appMemoryDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("org/exoplatform/ide/extension/appfog/images/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();

    @Source("org/exoplatform/ide/extension/appfog/images/apps-list.png")
    ImageResource appsList();

    @Source("org/exoplatform/ide/extension/appfog/images/apps-list_Disabled.png")
    ImageResource appsListDisabled();
}
