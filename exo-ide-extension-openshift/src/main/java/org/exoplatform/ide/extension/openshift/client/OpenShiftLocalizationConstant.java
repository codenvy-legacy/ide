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
package org.exoplatform.ide.extension.openshift.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 6, 2011 11:16:02 AM anya $
 *
 */
public interface OpenShiftLocalizationConstant extends Messages
{
   /*Buttons:*/
   @DefaultMessage("Cancel")
   @Key("cancelButton")
   String cancelButton();

   @DefaultMessage("Create")
   @Key("createButton")
   String createButton();

   @DefaultMessage("Login")
   @Key("loginButton")
   String loginButton();

   @DefaultMessage("Ok")
   @Key("okButton")
   String okButton();

   /*Controls:*/
   @DefaultMessage("PaaS/OpenShift/Create domain...")
   @Key("control.createDomain.id")
   String createDomainControlId();

   @DefaultMessage("Create domain...")
   @Key("control.createDomain.title")
   String createDomainControlTitle();

   @DefaultMessage("Create domain on OpenShift...")
   @Key("control.createDomain.prompt")
   String createDomainControlPrompt();

   @DefaultMessage("PaaS/OpenShift/Create application...")
   @Key("control.createApplication.id")
   String createApplicationControlId();

   @DefaultMessage("Create application...")
   @Key("control.createApplication.title")
   String createApplicationControlTitle();

   @DefaultMessage("Create application on OpenShift...")
   @Key("control.createApplication.prompt")
   String createApplicationControlPrompt();

   @DefaultMessage("PaaS/OpenShift/Delete application...")
   @Key("control.deleteApplication.id")
   String deleteApplicationControlId();

   @DefaultMessage("Delete application...")
   @Key("control.deleteApplication.title")
   String deleteApplicationControlTitle();

   @DefaultMessage("Delete application on OpenShift...")
   @Key("control.deleteApplication.prompt")
   String deleteApplicationControlPrompt();

   @DefaultMessage("PaaS/OpenShift/Application info...")
   @Key(" control.showApplicationInfo.id")
   String showApplicationInfoControlId();

   @DefaultMessage("Application info...")
   @Key("control.showApplicationInfo.title")
   String showApplicationInfoControlTitle();

   @DefaultMessage("Show application info...")
   @Key("control.showApplicationInfo.prompt")
   String showApplicationInfoControlPrompt();

   @DefaultMessage("PaaS/OpenShift")
   @Key("control.openshift.id")
   String openShiftControlId();

   @DefaultMessage("OpenShift")
   @Key("control.openshift.title")
   String openShiftControlTitle();

   @DefaultMessage("OpenShift")
   @Key("control.openshift.prompt")
   String openShiftControlPrompt();
   
   @DefaultMessage("PaaS/OpenShift/User info...")
   @Key("control.showUserInfo.id")
   String showUserInfoControlId();

   @DefaultMessage("User info...")
   @Key("control.showUserInfo.title")
   String showUserInfoControlTitle();

   @DefaultMessage("Show user info...")
   @Key("control.showUserInfo.prompt")
   String showUserInfoControlPrompt();

   @Key("control.updatePublicKey.id")
   String updatePublicKeyControlId();

   @Key("control.updatePublicKey.title")
   String updatePublicKeyControlTitle();

   @Key("control.updatePublicKey.prompt")
   String updatePublicKeyControlPrompt();
   
   /*Login view*/
   @DefaultMessage("Log in OpenShift")
   @Key("loginView.title")
   String loginViewTitle();

   @DefaultMessage("Email:")
   @Key("loginView.field.email")
   String loginViewEmailField();

   @DefaultMessage("Password:")
   @Key("loginView.field.password")
   String loginViewPasswordField();

   /*Create domain view*/
   @DefaultMessage("Create new domain")
   @Key("createDomainView.title")
   String createDomainViewTitle();

   @DefaultMessage("Enter domain name:")
   @Key("createDomainView.name.field")
   String createDomainViewNameField();

   //Create application view
   @DefaultMessage("Create new OpenShift application")
   @Key("createApplicationView.title")
   String createApplicationViewTitle();

   @DefaultMessage("Enter application name:")
   @Key("createApplicationView.name.field")
   String createApplicationViewNameField();

   @DefaultMessage("Application working directory:")
   @Key("createApplicationView.workdir.field")
   String createApplicationViewWorkDirField();

   @DefaultMessage("Choose application type:")
   @Key("createApplicationView.type.field")
   String createApplicationViewTypeField();

   /*User info view*/
   @DefaultMessage("User information")
   @Key("userInfoView.title")
   String userInfoViewTitle();

   @DefaultMessage("Login")
   @Key("userInfoView.field.login")
   String userInfoViewLoginField();

   @DefaultMessage("Domain")
   @Key("userInfoView.field.domain")
   String userInfoViewDomainField();

   @DefaultMessage("Applications")
   @Key("userInfoView.applications")
   String userInfoViewApplications();

   /*Messages*/
   /**
    * @param domainName domain name
    * @return {@link String}
    */
   @DefaultMessage("Domain {0} is successfully created.")
   @Key("createDomainSuccess")
   String createDomainSuccess(String domainName);

   @DefaultMessage("Application {0} is successfully created.")
   @Key("createApplicationSuccess")
   String createApplicationSuccess(String application);

   @DefaultMessage("Logged in OpenShift successfully.")
   @Key("loginSuccess")
   String loginSuccess();

   @DefaultMessage("Failed to log in OpenShift.")
   @Key("loginFailed")
   String loginFailed();

   @DefaultMessage("Please, select folder(place for OpenShift application) in browser tree.")
   @Key("selectFolder")
   String selectFolder();

   @DefaultMessage("Do you want to delete application <b>{0}</b> on OpenShift?")
   @Key("deleteApplication")
   String deleteApplication(String applicationName);

   @DefaultMessage("Delete OpenShift application")
   @Key("deleteApplicationTitle")
   String deleteApplicationTitle();

   @DefaultMessage("Application <b>{0}</b> is successfully deleted.")
   @Key("deleteApplicationSuccess")
   String deleteApplicationSuccess(String applicationName);
   
   @Key("updatePublicKeySuccess")
   String updatePublicKeySuccess();

   @Key("updatePublicKeyFailed")
   String updatePublicKeyFailed();

   @DefaultMessage("OpenShift application information")
   @Key("applicationInfoView.title")
   String applicationInfoViewTitle();

   @DefaultMessage("Property")
   @Key("applicationInfoGid.field.name")
   String applicationInfoGridNameField();

   @DefaultMessage("Value")
   @Key("applicationInfoGid.field.value")
   String applicationInfoGridValueField();
   
   /*Application properties*/
   @DefaultMessage("Name")
   @Key("application.name")
   String applicationName();

   @DefaultMessage("Type")
   @Key("application.type")
   String applicationType();

   @DefaultMessage("Public URL")
   @Key("application.publicUrl")
   String applicationPublicUrl();

   @DefaultMessage("Git URL")
   @Key("application.gitUrl")
   String applicationGitUrl();

   @DefaultMessage("Creation time")
   @Key("application.createTime")
   String applicationCreationTime();

   @DefaultMessage("Express exit code : <b>{0}</b>")
   @Key("express.exit.code")
   String expressExitCode(String code);

   @DefaultMessage("Create OpenShift application {0} failed.")
   @Key("createApplicationFail")
   String createApplicationFail(String applicationName);

   @DefaultMessage("Create OpenShift domain {0} failed.")
   @Key("createDomainFail")
   String createDomainFail(String domainName);

   @DefaultMessage("Delete OpenShift application {0} failed.")
   @Key("deleteApplicationFail")
   String deleteApplicationFail(String applicationName);

   @DefaultMessage("Get application information failed.")
   @Key("getApplicationInfoFail")
   String getApplicationInfoFail();

   @DefaultMessage("Get user information failed.")
   @Key("getUserInfoFail")
   String getUserInfoFail();
}
