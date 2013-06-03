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
package com.codenvy.ide.ext.cloudbees.client;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/**
 * CloudBees client resources (images).
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudBeesResources.java Jun 23, 2011 10:03:22 AM vereshchaka $
 */
public interface CloudBeesResources extends ClientBundle {
    public interface CloudBeesCSS extends CssResource {
        String login();

        String loginFont();

        String loginErrorFont();

        String appInfo();

        String event();

        String project();

        String labelH();

        String link();

        String textinput();
    }

    @Source({"CloudBees.css", "com/codenvy/ide/api/ui/style.css"})
    CloudBeesCSS cloudBeesCSS();

    /*
     * Buttons
     */
    @Source("com/codenvy/ide/ext/cloudbees/images/cloudbees_36.png")
    ImageResource cloudBeesLogo();

    @Source("com/codenvy/ide/ext/cloudbees/images/ok.png")
    ImageResource okButton();

    @Source("com/codenvy/ide/ext/cloudbees/images/ok_Disabled.png")
    ImageResource okButtonDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/cancel.png")
    ImageResource cancelButton();

    @Source("com/codenvy/ide/ext/cloudbees/images/cancel_Disabled.png")
    ImageResource cancelButtonDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/properties.png")
    ImageResource propertiesButton();

    @Source("com/codenvy/ide/ext/cloudbees/images/properties_Disabled.png")
    ImageResource propertiesButtonDisabled();

    /*
     * CloudBees controls
     */
    @Source("com/codenvy/ide/ext/cloudbees/images/cloudbees.png")
    ImageResource cloudBees();

    @Source("com/codenvy/ide/ext/cloudbees/images/cloudbees_Disabled.png")
    ImageResource cloudBeesDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/cloudbees_48.png")
    ImageResource cloudBees48();

    @Source("com/codenvy/ide/ext/cloudbees/images/cloudbees_48_Disabled.png")
    ImageResource cloudBees48Disabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/initializeApp.png")
    ImageResource initializeApp();

    @Source("com/codenvy/ide/ext/cloudbees/images/initializeApp_Disabled.png")
    ImageResource initializeAppDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("com/codenvy/ide/ext/cloudbees/images/appInfo_Disabled.png")
    ImageResource applicationInfoDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("com/codenvy/ide/ext/cloudbees/images/deleteApp_Disabled.png")
    ImageResource deleteApplicationDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/updateApp.png")
    ImageResource updateApplication();

    @Source("com/codenvy/ide/ext/cloudbees/images/updateApp_Disabled.png")
    ImageResource updateApplicationDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/apps-list.png")
    ImageResource appList();

    @Source("com/codenvy/ide/ext/cloudbees/images/apps-list_Disabled.png")
    ImageResource appListDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/create_account.png")
    ImageResource createAccount();

    @Source("com/codenvy/ide/ext/cloudbees/images/create_account_Disabled.png")
    ImageResource createAccountDisabled();

    @Source("com/codenvy/ide/ext/cloudbees/images/switchAccount.png")
    ImageResource switchAccount();

    @Source("com/codenvy/ide/ext/cloudbees/images/switchAccount_Disabled.png")
    ImageResource switchAccountDisabled();
}