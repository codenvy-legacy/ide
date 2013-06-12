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
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * AppFog client resources.
 *
 * @author <a href="mailto:vzhukovskii@exoplatform.com">Vladislav Zhukovskii</a>
 */
public interface AppfogResources extends ClientBundle {
    public interface AppFogCSS extends CssResource {
        String login();

        String loginFont();

        String loginErrorFont();

        String project();

        String labelH();

        String link();

        String textinput();

        String appInfo();

        String event();
    }

    @Source({"AppFog.css", "com/codenvy/ide/api/ui/style.css"})
    AppFogCSS appFogCSS();

    @Source("com/codenvy/ide/ext/appfog/images/appfog_36.png")
    ImageResource appfogLogo();

    /*
     * Buttons
     */
    @Source("com/codenvy/ide/ext/appfog/images/ok.png")
    ImageResource okButton();

    @Source("com/codenvy/ide/ext/appfog/images/cancel.png")
    ImageResource cancelButton();

    @Source("com/codenvy/ide/ext/appfog/images/delete.png")
    ImageResource deleteButton();

    @Source("com/codenvy/ide/ext/appfog/images/add.png")
    ImageResource addButton();

    @Source("com/codenvy/ide/ext/appfog/images/edit.png")
    ImageResource editButton();

    @Source("com/codenvy/ide/ext/appfog/images/properties.png")
    ImageResource propertiesButton();

    @Source("com/codenvy/ide/ext/appfog/images/start.png")
    ImageResource startButton();

    @Source("com/codenvy/ide/ext/appfog/images/restart.png")
    ImageResource restartButton();


    @Source("com/codenvy/ide/ext/appfog/images/stop.png")
    ImageResource stopButton();

    /*
     * appfog controls
     */
    @Source("com/codenvy/ide/ext/appfog/images/appfog.png")
    ImageResource appfog();

    @Source("com/codenvy/ide/ext/appfog/images/appfog_48.png")
    ImageResource appfog48();

    @Source("com/codenvy/ide/ext/appfog/images/initializeApp.png")
    ImageResource createApp();

    @Source("com/codenvy/ide/ext/appfog/images/startApp.png")
    ImageResource startApp();

    @Source("com/codenvy/ide/ext/appfog/images/restartApp.png")
    ImageResource restartApp();

    @Source("com/codenvy/ide/ext/appfog/images/updateApp.png")
    ImageResource updateApp();

    @Source("com/codenvy/ide/ext/appfog/images/stopApp.png")
    ImageResource stopApp();

    @Source("com/codenvy/ide/ext/appfog/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("com/codenvy/ide/ext/appfog/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("com/codenvy/ide/ext/appfog/images/renameApp.png")
    ImageResource renameApplication();

    @Source("com/codenvy/ide/ext/appfog/images/app_map_url.png")
    ImageResource mapUrl();

    @Source("com/codenvy/ide/ext/appfog/images/app_unmap_url.png")
    ImageResource unmapUrl();

    @Source("com/codenvy/ide/ext/appfog/images/app_instances.png")
    ImageResource appInstances();

    @Source("com/codenvy/ide/ext/appfog/images/app_memory.png")
    ImageResource appMemory();

    @Source("com/codenvy/ide/ext/appfog/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("com/codenvy/ide/ext/appfog/images/apps-list.png")
    ImageResource appsList();
}