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

   @DefaultMessage("Application's working directory:")
   @Key("createApplicationView.workdir.field")
   String createApplicationViewWorkDirField();

   @DefaultMessage("Choose application type:")
   @Key("createApplicationView.type.field")
   String createApplicationViewTypeField();

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
   
   @DefaultMessage("Please, select folder(place for OpenShift application) in browser tree.")
   @Key("selectFolder")
   String selectFolder();
}
