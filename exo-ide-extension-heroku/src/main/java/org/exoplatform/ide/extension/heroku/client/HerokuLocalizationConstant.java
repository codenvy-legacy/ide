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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Jun 6, 2011 11:16:02 AM anya $
 */
public interface HerokuLocalizationConstant extends Messages {
    // Buttons:
    @DefaultMessage("Cancel")
    @Key("cancelButton")
    String cancelButton();

    @DefaultMessage("Create")
    @Key("createButton")
    String createButton();

    @Key("changeButton")
    String changeButton();

    @DefaultMessage("Login")
    @Key("loginButton")
    String loginButton();

    @DefaultMessage("Rename")
    @Key("renameButton")
    String renameButton();

    @DefaultMessage("Ok")
    @Key("okButton")
    String okButton();

    @DefaultMessage("Try As Demo")
    @Key("loginDemoButton")
    String loginDemoButton();

    @Key("runButton")
    String runButton();

    @Key("closeButton")
    String closeButton();

    @Key("helpButton")
    String helpButton();

    @Key("deleteButton")
    String deleteButton();

    @Key("rakeButton")
    String rakeButton();

    @Key("logsButton")
    String logsButton();

    @Key("importButton")
    String importButton();

    @DefaultMessage("Logged in Heroku successfully.")
    @Key("loginSuccess")
    String loginSuccess();

    @Key("loginFailed")
    String loginFailed();

    @DefaultMessage("Public keys are successfully deployed on Heroku.")
    @Key("addKeysSuccess")
    String addKeysSuccess();

    @DefaultMessage("Keys are successfully removed from Heroku.")
    @Key("clearKeysSuccess")
    String clearKeysSuccess();

    @Key("deleteApplicationSuccess")
    String deleteApplicationSuccess();

    @Key("importApplicationSuccess")
    String importApplicationSuccess(String application);

    @DefaultMessage("PaaS/Heroku/Deploy public key")
    @Key("control.addKey.id")
    String addKeyControlId();

    @DefaultMessage("Deploy public key...")
    @Key("control.addKey.title")
    String addKeyControlTitle();

    @DefaultMessage("Deploy public key on Heroku...")
    @Key("control.addKey.prompt")
    String addKeyControlPrompt();

    @Key("creatingProject")
    String creatingProject();

    @Key("deployToHeroku")
    String deployToHeroku();

    @Key("control.listApp.id")
    String listApplicationsControlId();

    @Key("control.listApp.title")
    String listApplicationsControlTitle();

    @Key("control.listApp.prompt")
    String listApplicationsControlPrompt();

    @DefaultMessage("PaaS/Heroku/Remove public keys...")
    @Key("control.clearKeys.id")
    String clearKeysId();

    @DefaultMessage("Remove public keys...")
    @Key("control.clearKeys.title")
    String clearKeysTitle();

    @DefaultMessage("Remove public keys from Heroku...")
    @Key("control.clearKeys.prompt")
    String clearKeysPrompt();

    @DefaultMessage("PaaS/Heroku/Create application...")
    @Key("control.createApplication.id")
    String createApplicationControlId();

    @DefaultMessage("Create application...")
    @Key("control.createApplication.title")
    String createApplicationControlTitle();

    @DefaultMessage("Create application on Heroku...")
    @Key("control.createApplication.prompt")
    String createApplicationControlPrompt();

    @DefaultMessage("PaaS/Heroku/Delete application...")
    @Key("control.deleteApplication.id")
    String deleteApplicationControlId();

    @DefaultMessage(" Delete application...")
    @Key("control.deleteApplication.title")
    String deleteApplicationControlTitle();

    @DefaultMessage("Delete application on Heroku...")
    @Key("control.deleteApplication.prompt")
    String deleteApplicationControlPrompt();

    @DefaultMessage("PaaS/Heroku/Rename application...")
    @Key("control.renameApplication.id")
    String renameApplicationControlId();

    @DefaultMessage("Rename application...")
    @Key("control.renameApplication.title")
    String renameApplicationControlTitle();

    @DefaultMessage("Rename application on Heroku...")
    @Key("control.renameApplication.prompt")
    String renameApplicationControlPrompt();

    @DefaultMessage("PaaS/Heroku/Application info...")
    @Key("control.showApplicationInfo.id")
    String showApplicationInfoControlId();

    @DefaultMessage("Application info...")
    @Key("control.showApplicationInfo.title")
    String showApplicationInfoControlTitle();

    @DefaultMessage("Show application info...")
    @Key("control.showApplicationInfo.prompt")
    String showApplicationInfoControlPrompt();

    @Key("control.switchAccount.id")
    String switchAccountControlId();

    @Key("control.switchAccount.switch.title")
    String switchAccountControlSwitchTitle();

    @Key("control.switchAccount.switch.prompt")
    String switchAccountControlSwitchPrompt();

    @Key("control.rake.id")
    String rakeControlId();

    @Key("control.rake.title")
    String rakeControlTitle();

    @Key("control.rake.prompt")
    String rakeControlPrompt();

    @Key("control.changeStack.id")
    String changeStackControlId();

    @Key("control.changeStack.title")
    String changeStackControlTitle();

    @Key("control.changeStack.prompt")
    String changeStackControlPrompt();

    @Key("control.logs.id")
    String logsControlId();

    @Key("control.logs.title")
    String logsControlTitle();

    @Key("control.logs.prompt")
    String logsControlPrompt();

    // Create Application view
    @DefaultMessage("Create application on Heroku")
    @Key("createApplicationView.title")
    String createApplicationViewTitle();

    @DefaultMessage("Location of Git repository:")
    @Key("createApplicationView.gitLocation")
    String createApplicationViewGitLocation();

    @DefaultMessage("Enter application name (optional):")
    @Key("createApplicationView.applicationName")
    String createApplicationViewApplicationName();

    @DefaultMessage("Enter remote repository name (optional):")
    @Key("createApplicationView.remoteName")
    String createApplicationViewRemoteName();

    // Application information view:
    @DefaultMessage("Heroku application information")
    @Key("applicationInfoView.title")
    String applicationInfoViewTitle();

    @DefaultMessage("Property")
    @Key("applicationInfoGid.field.name")
    String applicationInfoGridNameField();

    @DefaultMessage("Value")
    @Key("applicationInfoGid.field.value")
    String applicationInfoGridValueField();

    /* Import Application view */
    @Key("importApplicationView.title")
    String importApplicationViewTitle();

    @Key("importApplicationView.field.application")
    String importApplicationViewApplicationField();

    @Key("importApplicationView.field.project")
    String importApplicationViewProjectField();

    @Key("importApplicationView.field.deploy")
    String importApplicationViewDeployField();

    /* Manage Applications View */
    @Key("manageApplicationsView.title")
    String manageApplicationsViewTitle();

    @Key("applicationsListGrid.field.name")
    String applicationsListGridFieldName();

    @Key("applicationsListGrid.field.url")
    String applicationsListGridFieldUrl();

    @Key("applicationsListGrid.field.stack")
    String applicationsListGridFieldStack();

    @Key("applicationsListGrid.field.environment")
    String applicationsListGridFieldEnvironment();

    @Key("applicationsListGrid.button.delete")
    String applicationsListGridButtonDelete();

    @Key("applicationsListGrid.button.rename")
    String applicationsListGridButtonRename();

    @Key("applicationsListGrid.button.change")
    String applicationsListGridButtonChange();

    @Key("applicationsListGrid.button.info")
    String applicationsListGridButtonInfo();

    @Key("applicationsListGrid.button.import")
    String applicationsListGridButtonImport();

    /* Messages */
    @Key("createApplicationSuccess")
    String createApplicationSuccess(String application);

    @Key("renameApplicationSuccess")
    String renameApplicationSuccess(String oldName, String newName);

    @Key("stack.list.unmarshal.failed")
    String stackListUnmarshalFailed();

    @Key("creatingApplication")
    String creatingApplication();

    @Key("applicationCreated")
    String applicationCreated();

    /* Delete application */
    @Key("deleteApplication.title")
    String deleteApplicationTitle();

    @Key("askForDeleteApplication")
    String deleteApplicationQuestion(String application);

    /* Remove keys */
    @Key("removeKeys.title")
    String removeKeysTitle();

    @Key("askRemoveKeys")
    String askRemoveKeys();

    /* Login view */
    @Key("loginView.title")
    String loginViewTitle();

    @Key("loginView.field.password")
    String loginViewPasswordField();

    @Key("loginView.field.email")
    String loginViewEmailField();

    /* Rename application */
    @Key("renameApplicationView.title")
    String renameApplicationViewTitle();

    @Key("renameApplicationView.field.name")
    String renameApplicationViewNameField();

    /* Rake command */
    @Key("rakeView.title")
    String rakeViewTitle();

    @Key("rakeView.field.command")
    String rakeViewCommandField();

    @Key("rakeView.command.example")
    String rakeViewCommandExample();

    /* Change application's stack */
    @Key("changeStackView.title")
    String changeStackViewTitle();

    @Key("changeStackView.stack.field")
    String changeStackViewStackField();

    @Key("changeStackView.beta.field")
    String changeStackViewBetaField();

    /* Application logs view */
    @Key("logsView.title")
    String logsViewTitle();

    @Key("logsView.logLines.field")
    String logsViewLogLinesField();

    @Key("logsView.getLogs.button")
    String logsViewGetLogsButton();

    /* Manage project view */
    @Key("manage.project.view.title")
    String manageProjectViewTitle();

    @Key("manage.project.application")
    String manageProjectApplication();

    @Key("manage.project.application.url")
    String manageProjectApplicationUrl();

    @Key("manage.project.application.stack")
    String manageProjectApplicationStack();

    @Key("manage.project.application.actions")
    String manageProjectApplicationActions();

    @Key("manage.project.application.rename")
    String manageProjectApplicationRename();

    @Key("manage.project.application.edit.stack")
    String manageProjectApplicationEditStack();

    @Key("manage.project.application.properties")
    String manageProjectApplicationProperties();
}
