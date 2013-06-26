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
package org.exoplatform.ide.extension.samples.client;

import com.google.gwt.i18n.client.Messages;
import com.google.gwt.i18n.client.LocalizableResource.Key;

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
     * SamplesListGrid
     */
    @Key("samplesListGrid.column.name")
    String samplesListRepositoryColumn();

    @Key("samplesListGrid.column.description")
    String samplesListDescriptionColumn();

    @Key("samplesListGrid.column.type")
    String samplesListTypeColumn();

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
    @Key("control.importFromGithub.id")
    String importFromGithubControlId();

    @Key("control.importFromGithub.title")
    String importFromGithubControlTitle();

    @Key("control.importFromGithub.prompt")
    String importFromGithubControlPrompt();

    @Key("control.loadSamples.id")
    String loadSamplesControlId();

    @Key("control.loadSamples.title")
    String loadSamplesControlTitle();

    @Key("control.loadSamples.prompt")
    String loadSamplesControlPrompt();

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
    @Key("import.github.title")
    String importFromGithubTitle();

    @Key("import.github.account")
    String importFromGithubAccount();

    @Key("import.github.selectProjectType")
    String importFromGithubSelectProjectType();

    @Key("import.github.login")
    String importFromGithubLogin();

    @Key("import.github.login.failed")
    String importFromGithubLoginFailed();

    @Key("git.read.only")
    String gitReadOnly();

    @Key("user.not.found")
    String userNotFound();

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
