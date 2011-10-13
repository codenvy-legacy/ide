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

/**
 * Interface to represent the constants contained in resource bundle:
 *      'IdeSamplesLocalizationConstant.properties'.
 * <p/>
 * Localization message for forms from start page view.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdeSamplesLocalizationConstant.java Aug 25, 2011 5:57:11 PM vereshchaka $
 *
 */
public interface SamplesLocalizationConstant extends Messages
{
   /*
    * Buttons
    */
   @Key("button.import")
   String importButton();
   
   @Key("button.cancel")
   String cancelButton();
   
   @Key("button.create")
   String createButton();
   
   @Key("button.next")
   String nextButton();
   
   @Key("button.back")
   String backButton();
   
   @Key("button.finish")
   String finishButton();
   
   @Key("button.login")
   String loginButton();
   
   /*
    * WelcomeView
    */
   @Key("welcome.aboutIde")
   String aboutIde();
   
   @Key("welcome.tutorial.title")
   String tutorialTitle();
   
   @Key("welcome.tutorial.text")
   String tutorialText();
   
   @Key("welcome.sample.title")
   String sampleTitle();
   
   @Key("welcome.sample.text")
   String sampleText();
   
   @Key("welcome.title")
   String welcomeTitle();
   
   @Key("welcome.project.title")
   String projectTitle();
   
   @Key("welcome.project.text")
   String projectText();
   
   /*
    * SelectLocationView
    */
   @Key("location.title")
   String selectLocationTitle();
   
   @Key("location.error.folderNameEmpty")
   String selectLocationErrorFolderNameEmpty();
   
   @Key("location.error.parentFolderNotSelected")
   String selectLocationErrorParentFolderNotSelected();
   
   @Key("location.error.cantCreateFolder")
   String selectLocationErrorCantCreateFolder();
   
   /*
    * SamplesListGrid
    */
   @Key("samplesListGrid.column.name")
   String samplesListListColumnName();
   
   /*
    * ShowSamplesView
    */
   @Key("showSamples.title")
   String showSamplesTitle();
   
   /*
    * ShowSamplesPresenter
    */
   @Key("showSamples.error.selectRepo")
   String showSamplesErrorSelectRepository();
   
   /*
    * WizardDefinitionStepView
    */
   @Key("wizard.definition.dialog.title")
   String wizardDefinitionDialogTitle();
   
   @Key("wizard.definition.title")
   String wizardDefinitionTitle();
   
   @Key("wizard.definition.text")
   String wizardDefinitionText();
   
   @Key("wizard.definition.name")
   String wizardDefinitionName();
   
   @Key("wizard.definition.type")
   String wizardDefinitionType();
   
   /*
    * WizardDeploymentStepView
    */
   @Key("wizard.deployment.dialog.title")
   String wizardDeploymentDialogTitle();
   
   @Key("wizard.deployment.title")
   String wizardDeploymentTitle();
   
   @Key("wizard.deployment.text")
   String wizardDeploymentText();
   
   @Key("wizard.deployment.paas")
   String wizardDeploymentPaas();
   
   @Key("wizard.deployment.paas.settings")
   String paasDeploySettings();
   
   @Key("wizard.deployment.cloudfoundry.url")
   String cloudFoundryAppUrlField();
   
   @Key("wizard.deployment.cloudfoundry.target")
   String cloudFoundryAppTargetField();
   
   /*
    * WizardFinishStepView
    */
   @Key("wizard.finish.dialog.title")
   String wizardFinishDialogTitle();
   
   @Key("wizard.finish.title")
   String wizardFinishTitle();
   
   @Key("wizard.finish.text")
   String wizardFinishText();
   
   @Key("wizard.finish.name")
   String wizardFinishName();
   
   @Key("wizard.finish.type")
   String wizardFinishType();
   
   @Key("wizard.finish.error.projectPropertiesAreNull")
   String wizardFinishErrorProjectPropertiesAreNull();
   
   @Key("wizard.finish.error.noFolderSelected")
   String wizardFinishErrorNoFolderSelected();
   
   @Key("wizard.finish.error.cantCreateProject")
   String wizardFinishErrorCantCreateProject();
   
   @Key("wizard.finish.deployment")
   String wizardFinishDeployment();
   
   @Key("wizard.finish.deployment.none")
   String wizardFinishDeploymentNone();
   
   @Key("wizard.finish.deployment.cloudbees")
   String wizardFinishDeploymentCloudBees();
   
   @Key("wizard.finish.deployment.cloudfoundry")
   String wizardFinishDeploymentCloudFoundry();
   
   /*
    * WizardLocationStepView
    */
   @Key("wizard.location.newFolder")
   String wizardLocationNewFolder();
   
   /*
    * WizardSourceStepView
    */
   @Key("wizard.source.dialog.title")
   String wizardSourceDialogTitle();
   
   @Key("wizard.source.title")
   String wizardSourceTitle();
   
   @Key("wizard.source.text")
   String wizardSourceText();
   
   @Key("wizard.source.source")
   String wizardSourceLable();
   
   /*
    * WizardLocationStepView
    */
   @Key("wizard.location.title")
   String wizardLocationTitle();
   
   @Key("wizard.location.text")
   String wizardLocationText();
   
   /*
    * CreateApplicationPresenter
    */
   @Key("cloudfoundry.application.created")
   String cloudFoundryDeploySuccess(String name);
   
   @Key("cloudfoundry.application.startedNoUrls")
   String cloudFoundryApplicationStartedWithNoUrls();
   
   @Key("cloudfoundry.application.appStarted.uris")
   String cloudFoundryApplicationStartedOnUrls(String name, String uris);
   
   @Key("cloudfoundry.application.nameField")
   String cloudFoundryAppNameField();
   
   @Key("cloudfoundry.application.deploy.title")
   String cloudFoundryDeployTitle();
   
   @Key("cloudfoundry.application.deploy.text")
   String cloudFoundryDeployText();
   
   @Key("cloudfoundry.application.deploy.failure")
   String cloudFoundryDeployFailure();
   
   /*
    * CloudBees
    */
   @Key("cloudbees.label.domain")
   String cloudBeesDomainLabel();
   
   @Key("cloudbees.label.name")
   String cloudBeesNameLabel();
   
   @Key("cloudbees.label.id")
   String cloudBeesIdLabel();
   
   @Key("cloudbees.deploy.success")
   String cloudBessDeploySuccess();
   
   @Key("cloudbees.deploy.application.info")
   String cloudBeesDeployApplicationInfo();
   
   @Key("cloudbees.deploy.failure")
   String cloudBeesDeployFailure();
   
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

}
