/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
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