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
package org.exoplatform.ide.extension.heroku.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 6, 2011 11:16:02 AM anya $
 *
 */
public interface HerokuLocalizationConstant extends Messages
{
   //Buttons:
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

   //Controls:
   @DefaultMessage("Logged in Heroku successfully.")
   @Key("loginSuccess")
   String loginSuccess();

   @DefaultMessage("Public keys are successfully deployed on Heroku.")
   @Key("addKeysSuccess")
   String addKeysSuccess();

   @DefaultMessage("Keys are successfully removed from Heroku.")
   @Key("clearKeysSuccess")
   String clearKeysSuccess();

   @DefaultMessage("Log in Heroku failed")
   @Key("loginFailed")
   String loginFailed();

   @Key("deleteApplicationSuccess")
   String deleteApplicationSuccess();

   @DefaultMessage("PaaS/Heroku/Deploy public key")
   @Key("control.addKey.id")
   String addKeyControlId();

   @DefaultMessage("Deploy public key...")
   @Key("control.addKey.title")
   String addKeyControlTitle();

   @DefaultMessage("Deploy public key on Heroku...")
   @Key("control.addKey.prompt")
   String addKeyControlPrompt();

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

   //Create Application view
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

   //Application information view:
   @DefaultMessage("Heroku application information")
   @Key("applicationInfoView.title")
   String applicationInfoViewTitle();

   @DefaultMessage("Property")
   @Key("applicationInfoGid.field.name")
   String applicationInfoGridNameField();

   @DefaultMessage("Value")
   @Key("applicationInfoGid.field.value")
   String applicationInfoGridValueField();

   /*Messages*/
   @Key("createApplicationSuccess")
   String createApplicationSuccess(String application);

   @Key("renameApplicationSuccess")
   String renameApplicationSuccess(String oldName, String newName);

   @Key("stack.list.unmarshal.failed")
   String stackListUnmarshalFailed();

   /*Delete application*/
   @Key("deleteApplication.title")
   String deleteApplicationTitle();

   @Key("askForDeleteApplication")
   String deleteApplicationQuestion(String application);

   /*Remove keys*/
   @Key("removeKeys.title")
   String removeKeysTitle();

   @Key("askRemoveKeys")
   String askRemoveKeys();

   /*Login view*/
   @Key("loginView.title")
   String loginViewTitle();

   @Key("loginView.field.password")
   String loginViewPasswordField();

   @Key("loginView.field.email")
   String loginViewEmailField();

   /*Rename application*/
   @Key("renameApplicationView.title")
   String renameApplicationViewTitle();

   @Key("renameApplicationView.field.name")
   String renameApplicationViewNameField();

   /*Rake command*/
   @Key("rakeView.title")
   String rakeViewTitle();

   @Key("rakeView.field.command")
   String rakeViewCommandField();

   @Key("rakeView.command.example")
   String rakeViewCommandExample();

   /*Change application's stack*/
   @Key("changeStackView.title")
   String changeStackViewTitle();

   @Key("changeStackView.stack.field")
   String changeStackViewStackField();

   @Key("changeStackView.beta.field")
   String changeStackViewBetaField();

   /*Application logs view*/
   @Key("logsView.title")
   String logsViewTitle();

   @Key("logsView.logLines.field")
   String logsViewLogLinesField();

   @Key("logsView.getLogs.button")
   String logsViewGetLogsButton();

   /*Manage project view*/
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
