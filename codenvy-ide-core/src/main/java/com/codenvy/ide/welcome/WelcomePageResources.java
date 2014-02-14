/*
 * CODENVY CONFIDENTIAL
 * __________________
 * 
 *  [2012] - [2013] Codenvy, S.A. 
 *  All Rights Reserved.
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
package com.codenvy.ide.welcome;

import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public interface WelcomePageResources extends ClientBundle {
    public interface WelcomeCSS extends CssResource {
        String welcomeHeader();

        String welcomeHeaderText();

        String welcomeLabel();

        String link();

        String socialPanel();

        String item();
    }

    @Source({"welcome.css", "com/codenvy/ide/api/ui/style.css"})
    WelcomeCSS welcomeCSS();

    @Source("logo.png")
    ImageResource ideLogo();

    @Source("background.png")
    ImageResource welcomePageBgHeader();

    @Source("spliter.png")
    ImageResource welcomePageSpliter();

    @Source("tutorials.png")
    ImageResource welcomeTutorial();

    @Source("new-project.png")
    ImageResource welcomeProject();

    @Source("convert.png")
    ImageResource convertToProject();

    @Source("documentation.png")
    ImageResource documentation();

    @Source("support.png")
    ImageResource support();

    @Source("survey.png")
    ImageResource survey();

    @Source("invitation.png")
    ImageResource invitation();

    @Source("invite-background.png")
    ImageResource invitePageHeaderBackground();

    @Source("invite-background-48.png")
    ImageResource invitePageHeaderBackground48();

    @Source("welcome.png")
    ImageResource welcome();
}