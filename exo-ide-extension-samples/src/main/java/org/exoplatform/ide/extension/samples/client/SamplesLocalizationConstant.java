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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.i18n.client.Messages;

/**
 * Interface to represent the constants contained in resource bundle: 'IdeSamplesLocalizationConstant.properties'.
 * <p/>
 * Localization message for forms from start page view.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdeSamplesLocalizationConstant.java Aug 25, 2011 5:57:11 PM vereshchaka $
 */
public interface SamplesLocalizationConstant extends Messages {
    /*
     * Buttons
     */
    @Key("button.ok")
    String okButton();

    @Key("button.cancel")
    String cancelButton();

    @Key("button.next")
    String nextButton();

    @Key("button.back")
    String backButton();

    @Key("button.finish")
    String finishButton();

    @Key("button.login")
    String loginButton();

    @Key("button.convert")
    String convertButton();

    @Key("button.authenticate")
    String authenticateButton();

    /*
     * WelcomeView
     */
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

    /*
     * ShowSamplesPresenter
     */
    @Key("showSamples.error.selectRepo")
    String showSamplesErrorSelectRepository();

    /*
     * WizardDeploymentStepView
     */
    @Key("wizard.deployment.title")
    String wizardDeploymentTitle();

    @Key("wizard.deployment.text")
    String wizardDeploymentText();

    @Key("wizard.deployment.paas")
    String wizardDeploymentPaas();

    /*
     * Project
     */
    @Key("project.name")
    String projectName();

    @Key("project.type")
    String projectType();

    /*
     * Login
     */
    @Key("login.title")
    String loginViewTitle();

    @Key("login.label")
    String loginViewLabel(String paas);

    @Key("login.field.email")
    String loginViewEmailField();

    @Key("login.field.password")
    String loginViewPasswordField();

    @Key("login.success")
    String loginSuccess(String paas);

    @Key("login.fail")
    String loginFail(String paas);

    /*
     * Controls
     */
    @Key("control.welcome.id")
    String welcomeControlId();
    
    @Key("control.welcome.title")
    String welcomeControlTitle();

    @Key("control.welcome.prompt")
    String welcomeControlPrompt();
    
    /*
     * LoadSamplesView
     */
    @Key("import.load.dialog.title")
    String importLoadDialogTitle();

    @Key("import.load.title")
    String importLoadTitle();

    @Key("import.load.text")
    String importLoadText();

    /*
     * ImportFromGitHub
     */

    @Key("import.github.account")
    String importFromGithubAccount();
    
    @Key("import.github.explanation.label")
    String importFromGitHubExplanationLabel();

    @Key("import.github.selectProjectType")
    String importFromGithubSelectProjectType();

    @Key("import.github.login")
    String importFromGithubLogin();

    @Key("import.github.login.failed")
    String importFromGithubLoginFailed();

    @Key("git.read.only")
    String gitReadOnly();



    @Key("login.oauth.title")
    String loginOAuthTitle();

    @Key("login.oauth.label")
    String loginOAuthLabel();

    @Key("github.sshkey.title")
    String githubSshKeyTitle();

    @Key("github.sshkey.label")
    String githubSshKeyLabel();
    
    /*
     * GetStartedView
     */
    @Key("noIncorrectProjectNameMessage")
    String noIncorrectProjectNameMessage();

    @Key("noIncorrectProjectNameTitle")
    String noIncorrectProjectNameTitle();
    
    @Key("projectNameStartWith_Message")
    String projectNameStartWith_Message();
    
    @Key("joinCodenvyMessage")
    String joinCodenvyMessage();
    
    @Key("joinCodenvyTitle")
    String joinCodenvyTitle();
    
    @Key("switchWorkspaceMessage")
    String switchWorkspaceMessage();
    
    @Key("switchWorkspaceTitle")
    String switchWorkspaceTitle();

    @Key("switchWorkspace")
    String switchWorkspace();
}
