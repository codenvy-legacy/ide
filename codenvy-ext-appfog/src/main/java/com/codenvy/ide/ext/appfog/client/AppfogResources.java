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