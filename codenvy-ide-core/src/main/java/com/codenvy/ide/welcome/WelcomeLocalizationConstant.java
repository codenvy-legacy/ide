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

import com.google.gwt.i18n.client.Messages;

/**
 * Localization message for forms from welcome page view.
 *
 * @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a>
 */
public interface WelcomeLocalizationConstant extends Messages {
    @Key("welcome.aboutIde")
    String aboutIde();

    @Key("welcome.tutorial.title")
    String tutorialTitle();

    @Key("welcome.tutorial.text")
    String tutorialText();

    @Key("welcome.title")
    String welcomeTitle();

    @Key("welcome.project.title")
    String projectTitle();

    @Key("welcome.project.text")
    String projectText();

    @Key("welcome.convert.title")
    String convertTitle();

    @Key("welcome.convert.text")
    String convertText();

    @Key("welcome.documentation.title")
    String documentationTitle();

    @Key("welcome.documentation.text")
    String documentationText();

    @Key("welcome.support.title")
    String supportTitle();

    @Key("welcome.support.text")
    String supportText();

    @Key("welcome.survey.title")
    String surveyTitle();

    @Key("welcome.survey.text")
    String surverText();

    @Key("welcome.invitation.title")
    String invitationTitle();

    @Key("welcome.invitation.text")
    String invitationText();
}