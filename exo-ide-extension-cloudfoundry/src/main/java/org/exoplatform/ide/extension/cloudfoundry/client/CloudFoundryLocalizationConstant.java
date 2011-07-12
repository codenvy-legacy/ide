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
package org.exoplatform.ide.extension.cloudfoundry.client;

import com.google.gwt.i18n.client.Messages;

/**
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: CloudFoundryLocalizationConstant.java Jul 12, 2011 10:25:38 AM vereshchaka $
 */
public interface CloudFoundryLocalizationConstant extends Messages
{
   /*
    * Buttons.
    */
   @Key("button.create")
   String createButton();
   
   @Key("button.cancel")
   String cancelButton();
   
   @Key("button.ok")
   String okButton();
   
   /*
    * Controls.
    */
   @Key("control.cloudfoundry.id")
   String cloudFoundryControlId();
   
   @Key("control.cloudfoundry.title")
   String cloudFoundryControlTitle();
   
   @Key("control.cloudfoundry.prompt")
   String cloudFoundryControlPrompt();
   
   @Key("control.createApp.id")
   String createAppControlId();
   
   @Key("control.createApp.title")
   String createAppControlTitle();
   
   @Key("control.createApp.prompt")
   String createAppControlPrompt();
   
   @Key("control.appInfo.id")
   String applicationInfoControlId();
   
   @Key("control.appInfo.title")
   String applicationInfoControlTitle();
   
   @Key("control.appInfo.prompt")
   String applicationInfoControlPrompt();
   
   @Key("control.deleteApp.id")
   String deleteApplicationControlId();
   
   @Key("control.deleteApp.title")
   String deleteApplicationControlTitle();
   
   @Key("control.deleteApp.prompt")
   String deleteApplicationControlPrompt();
   
   /*
    * LoginView.
    */
   @Key("login.title")
   String loginViewTitle();
   
   @Key("login.field.email")
   String loginViewEmailField();

   @Key("login.field.password")
   String loginViewPasswordField();
   
   /*
    * CreateApplicationView.
    */
   @Key("createApp.title")
   String createApplicationTitle();
   
   @Key("createApp.field.type")
   String createAppTypeField();
   
   @Key("createApp.field.autodetectType.label")
   String createAppAutodetectTypeLabel();
   
   @Key("createApp.field.name")
   String createAppNameField();
   
   @Key("createApp.field.url")
   String createAppUrlField();
   
   @Key("createApp.field.useDefaultUrl.label")
   String createAppUseDefaultUrlLabel();
   
   @Key("createApp.field.instances")
   String createAppInstancesField();
   
   @Key("createApp.field.memory")
   String createAppMemoryField();
   
   @Key("createApp.field.useDefaultMemorySize.label")
   String createAppUseDefaultMemorySizeLabel();
   
   @Key("createApp.field.change")
   String createAppChangeLabel();
   
   @Key("createApp.field.startAfterCreation")
   String createAppStartAfterCreationField();
   
   /*
    * CreateApplicationPresenter
    */
   @Key("createApp.appCreated")
   String applicationCreatedSuccessfully(String name);
   
   
   /*
    * Messages
    */
   @Key("loginSuccess")
   String loginSuccess();

   @Key("loginFailed")
   String loginFailed();
   
   /*
    * DeployApplicationPresenter
    */
   @Key("deployApplication.deployedSuccess")
   String deployApplicationSuccess();
   
   @Key("deployApplication.appInfo")
   String deployApplicationInfo();
   
   /*
    * ApplicationInfoView
    */
   @Key("appInfo.title")
   String applicationInfoTitle();
   
   /*
    * ApplicationInfoGrid
    */
   @Key("appInfo.listGrid.field.name")
   String applicationInfoListGridNameField();
   
   @Key("appInfo.listGrid.field.value")
   String applicationInfoListGridValueField();
   
   /*
    * DeleteApplicationEvent
    */
   @Key("deleteApplication.title")
   String deleteApplicationTitle();

   @Key("askForDeleteApplication")
   String deleteApplicationQuestion(String applicationTitle);
   
   @Key("applicationDeleted")
   String applicationDeletedMsg(String applicationTitle);

}
