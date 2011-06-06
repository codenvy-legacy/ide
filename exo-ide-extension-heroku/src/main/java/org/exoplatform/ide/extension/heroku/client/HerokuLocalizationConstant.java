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

import com.google.gwt.i18n.client.Constants;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:  Jun 6, 2011 11:16:02 AM anya $
 *
 */
public interface HerokuLocalizationConstant extends Constants
{
   //Buttons:
   @DefaultStringValue("Cancel")
   @Key("cancelButton")
   String cancelButton();
   
   @DefaultStringValue("Create")
   @Key("createButton")
   String createButton();
   
   @DefaultStringValue("Login")
   @Key("loginButton")
   String loginButton();
   
   @DefaultStringValue("Rename")
   @Key("renameButton")
   String renameButton();
   
   @DefaultStringValue("Ok")
   @Key("okButton")
   String okButton();
   
   //Controls:
   @DefaultStringValue("Logged in Heroku successfully.")
   @Key("loginSuccess")
   String loginSuccess();

   @DefaultStringValue("Public keys are successfully deployed on Heroku.")
   @Key("addKeysSuccess")
   String addKeysSuccess();

   @DefaultStringValue("Keys are successfully removed from Heroku.")
   @Key("clearKeysSuccess")
   String clearKeysSuccess();

   @DefaultStringValue("Log in Heroku failed")
   @Key("loginFailed")
   String loginFailed();

   @DefaultStringValue("Application is successfully deleted on Heroku.")
   @Key("deleteApplicationSuccess")
   String deleteApplicationSuccess();

   @DefaultStringValue("PaaS/Heroku/Deploy public key")
   @Key("control.addKey.id")
   String addKeyControlId();

   @DefaultStringValue("Deploy public key...")
   @Key("control.addKey.title")
   String addKeyControlTitle();

   @DefaultStringValue("Deploy public key on Heroku...")
   @Key("control.addKey.prompt")
   String addKeyControlPrompt();

   @DefaultStringValue("PaaS/Heroku/Remove public keys...")
   @Key("control.clearKeys.id")
   String clearKeysId();

   @DefaultStringValue("Remove public keys...")
   @Key("control.clearKeys.title")
   String clearKeysTitle();

   @DefaultStringValue("Remove public keys from Heroku...")
   @Key("control.clearKeys.prompt")
   String clearKeysPrompt();

   @DefaultStringValue("PaaS/Heroku/Create application...")
   @Key("control.createApplication.id")
   String createApplicationControlId();

   @DefaultStringValue("Create application...")
   @Key("control.createApplication.title")
   String createApplicationControlTitle();

   @DefaultStringValue("Create application on Heroku...")
   @Key("control.createApplication.prompt")
   String createApplicationControlPrompt();

   @DefaultStringValue("PaaS/Heroku/Delete application...")
   @Key("control.deleteApplication.id")
   String deleteApplicationControlId();

   @DefaultStringValue(" Delete application...")
   @Key("control.deleteApplication.title")
   String deleteApplicationControlTitle();

   @DefaultStringValue("Delete application on Heroku...")
   @Key("control.deleteApplication.prompt")
   String deleteApplicationControlPrompt();

   @DefaultStringValue("PaaS/Heroku/Rename application...")
   @Key("control.renameApplication.id")
   String renameApplicationControlId();

   @DefaultStringValue("Rename application...")
   @Key("control.renameApplication.title")
   String renameApplicationControlTitle();

   @DefaultStringValue("Rename application on Heroku...")
   @Key("control.renameApplication.prompt")
   String renameApplicationControlPrompt();

   @DefaultStringValue("PaaS/Heroku/Application info...")
   @Key("control.showApplicationInfo.id")
   String showApplicationInfoControlId();

   @DefaultStringValue("Application info...")
   @Key("control.showApplicationInfo.title")
   String showApplicationInfoControlTitle();

   @DefaultStringValue("Show application info...")
   @Key("control.showApplicationInfo.prompt")
   String showApplicationInfoControlPrompt();
   
   //Create Application view
   @DefaultStringValue("Create application on Heroku")
   @Key("createApplicationView.title")
   String createApplicationViewTitle();
   
   @DefaultStringValue("Location of Git repository:")
   @Key("createApplicationView.gitLocation")
   String createApplicationViewGitLocation();
   
   @DefaultStringValue("Enter application name (optional):")
   @Key("createApplicationView.applicationName")
   String createApplicationViewApplicationName();
   
   @DefaultStringValue("Enter remote repository name (optional):")
   @Key("createApplicationView.remoteName")
   String createApplicationViewRemoteName();
   
   //Application information view:
   @DefaultStringValue("Heroku application information")
   @Key("applicationInfoView.title")
   String applicationInfoViewTitle();
   
   @DefaultStringValue("Property")
   @Key("applicationInfoGid.field.name")
   String applicationInfoGridNameField();
   
   @DefaultStringValue("Value")
   @Key("applicationInfoGid.field.value")
   String applicationInfoGridValueField();
}
