/*
 * Copyright (C) 2011 eXo Platform SAS.
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