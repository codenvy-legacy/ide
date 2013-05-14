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

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

/**
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 * @version $Id: $
 */
public interface AppfogResources extends ClientBundle {
    @Source("com/codenvy/ide/ext/appfog/images/appfog_36.png")
    ImageResource appfogLogo();

    /*
     * Buttons
     */
    @Source("com/codenvy/ide/ext/appfog/images/ok.png")
    ImageResource okButton();

    @Source("com/codenvy/ide/ext/appfog/images/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/cancel.png")
    ImageResource cancelButton();

    @Source("com/codenvy/ide/ext/appfog/images/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/delete.png")
    ImageResource deleteButton();

    @Source("com/codenvy/ide/ext/appfog/images/delete_Disabled.png")
    ImageResource deleteButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/add.png")
    ImageResource addButton();

    @Source("com/codenvy/ide/ext/appfog/images/add_Disabled.png")
    ImageResource addButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/edit.png")
    ImageResource editButton();

    @Source("com/codenvy/ide/ext/appfog/images/edit_Disabled.png")
    ImageResource editButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/properties.png")
    ImageResource propertiesButton();

    @Source("com/codenvy/ide/ext/appfog/images/properties_Disabled.png")
    ImageResource propertiesButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/start.png")
    ImageResource startButton();

    @Source("com/codenvy/ide/ext/appfog/images/start_Disabled.png")
    ImageResource startButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/restart.png")
    ImageResource restartButton();

    @Source("com/codenvy/ide/ext/appfog/images/restart_Disabled.png")
    ImageResource restartButtonDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/stop.png")
    ImageResource stopButton();

    @Source("com/codenvy/ide/ext/appfog/images/stop_Disabled.png")
    ImageResource stopButtonDisabled();

    /*
     * appfog controls
     */
    @Source("com/codenvy/ide/ext/appfog/images/appfog.png")
    ImageResource appfog();

    @Source("com/codenvy/ide/ext/appfog/images/appfog_Disabled.png")
    ImageResource appfogDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/appfog_48.png")
    ImageResource appfog48();

    @Source("com/codenvy/ide/ext/appfog/images/appfog_48_Disabled.png")
    ImageResource appfog48Disabled();

    @Source("com/codenvy/ide/ext/appfog/images/initializeApp.png")
    ImageResource createApp();

    @Source("com/codenvy/ide/ext/appfog/images/initializeApp_Disabled.png")
    ImageResource createAppDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/startApp.png")
    ImageResource startApp();

    @Source("com/codenvy/ide/ext/appfog/images/startApp_Disabled.png")
    ImageResource startAppDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/restartApp.png")
    ImageResource restartApp();

    @Source("com/codenvy/ide/ext/appfog/images/restartApp_Disabled.png")
    ImageResource restartAppDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/updateApp.png")
    ImageResource updateApp();

    @Source("com/codenvy/ide/ext/appfog/images/updateApp_Disabled.png")
    ImageResource updateAppDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/stopApp.png")
    ImageResource stopApp();

    @Source("com/codenvy/ide/ext/appfog/images/stopApp_Disabled.png")
    ImageResource stopAppDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("com/codenvy/ide/ext/appfog/images/appInfo_Disabled.png")
    ImageResource applicationInfoDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("com/codenvy/ide/ext/appfog/images/deleteApp_Disabled.png")
    ImageResource deleteApplicationDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/renameApp.png")
    ImageResource renameApplication();

    @Source("com/codenvy/ide/ext/appfog/images/renameApp_Disabled.png")
    ImageResource renameApplicationDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/app_map_url.png")
    ImageResource mapUrl();

    @Source("com/codenvy/ide/ext/appfog/images/app_map_url_Disabled.png")
    ImageResource mapUrlDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/app_unmap_url.png")
    ImageResource unmapUrl();

    @Source("com/codenvy/ide/ext/appfog/images/app_unmap_url_Disabled.png")
    ImageResource unmapUrlDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/app_instances.png")
    ImageResource appInstances();

    @Source("com/codenvy/ide/ext/appfog/images/app_instances_Disabled.png")
    ImageResource appInstancesDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/app_memory.png")
    ImageResource appMemory();

    @Source("com/codenvy/ide/ext/appfog/images/app_memory_Disabled.png")
    ImageResource appMemoryDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("com/codenvy/ide/ext/appfog/images/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();

    @Source("com/codenvy/ide/ext/appfog/images/apps-list.png")
    ImageResource appsList();

    @Source("com/codenvy/ide/ext/appfog/images/apps-list_Disabled.png")
    ImageResource appsListDisabled();
}