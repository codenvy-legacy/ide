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

    @Source("com/codenvy/ide/ext/cloudbees/images/cancel.png")
    ImageResource cancelButton();

    @Source("com/codenvy/ide/ext/cloudbees/images/properties.png")
    ImageResource propertiesButton();

    /*
     * CloudBees controls
     */
    @Source("com/codenvy/ide/ext/cloudbees/images/cloudbees.png")
    ImageResource cloudBees();

    @Source("com/codenvy/ide/ext/cloudbees/images/cloudbees_48.png")
    ImageResource cloudBees48();

    @Source("com/codenvy/ide/ext/cloudbees/images/initializeApp.png")
    ImageResource initializeApp();

    @Source("com/codenvy/ide/ext/cloudbees/images/appInfo.png")
    ImageResource applicationInfo();

    @Source("com/codenvy/ide/ext/cloudbees/images/deleteApp.png")
    ImageResource deleteApplication();

    @Source("com/codenvy/ide/ext/cloudbees/images/updateApp.png")
    ImageResource updateApplication();

    @Source("com/codenvy/ide/ext/cloudbees/images/apps-list.png")
    ImageResource appList();


    @Source("com/codenvy/ide/ext/cloudbees/images/create_account.png")
    ImageResource createAccount();

    @Source("com/codenvy/ide/ext/cloudbees/images/switchAccount.png")
    ImageResource switchAccount();
}