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

    @Key("welcome.clone.title")
    String cloneTitle();

    @Key("welcome.clone.text")
    String cloneText();

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

    @Key("welcome.import.title")
    String importTitle();

    @Key("welcome.import.text")
    String importText();

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