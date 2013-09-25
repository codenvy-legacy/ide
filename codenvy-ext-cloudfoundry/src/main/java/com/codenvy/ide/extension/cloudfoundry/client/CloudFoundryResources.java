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
package com.codenvy.ide.extension.cloudfoundry.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * CloudFoundry client resources.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryClientBundle.java Jul 12, 2011 10:24:35 AM vereshchaka $
 */
public interface CloudFoundryResources extends ClientBundle {
    public interface CloudFoundryCSS extends CssResource {
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

    @Source({"CloudFoundry.css", "com/codenvy/ide/api/ui/style.css"})
    CloudFoundryCSS cloudFoundryCss();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/cloudfoundry_36.png")
    ImageResource cloudFoundryLogo();

    /*
     * Buttons
     */
    @Source("com/codenvy/ide/extension/cloudfoundry/images/ok.png")
    ImageResource okButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/cancel.png")
    ImageResource cancelButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/delete.png")
    ImageResource deleteButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/add.png")
    ImageResource addButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/edit.png")
    ImageResource editButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/properties.png")
    ImageResource propertiesButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/start.png")
    ImageResource startButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/restart.png")
    ImageResource restartButton();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/stop.png")
    ImageResource stopButton();

    /*
     * cloudfoundry controls
     */
    @Source("com/codenvy/ide/extension/cloudfoundry/images/cloudfoundry.png")
    ImageResource cloudFoundry();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/cloudfoundry_48.png")
    ImageResource cloudFoundry48();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/initializeApp.png")
    ImageResource createApp();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/startApp.png")
    ImageResource startApp();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/restartApp.png")
    ImageResource restartApp();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/updateApp.png")
    ImageResource updateApp();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/stopApp.png")
    ImageResource stopApp();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/renameApp.png")
    ImageResource renameApplication();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/app_map_url.png")
    ImageResource mapUrl();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/app_unmap_url.png")
    ImageResource unmapUrl();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/app_instances.png")
    ImageResource appInstances();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/app_memory.png")
    ImageResource appMemory();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("com/codenvy/ide/extension/cloudfoundry/images/apps-list.png")
    ImageResource appsList();
}