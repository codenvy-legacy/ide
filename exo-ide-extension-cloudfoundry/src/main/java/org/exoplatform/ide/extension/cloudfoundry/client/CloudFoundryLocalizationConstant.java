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
   
   @Key("button.rename")
   String renameButton();
   
   @Key("button.delete")
   String deleteButton();
   
   @Key("button.close")
   String closeButton();
   
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
   
   @Key("control.startApp.id")
   String startAppControlId();
   
   @Key("control.startApp.title")
   String startAppControlTitle();
   
   @Key("control.startApp.prompt")
   String startAppControlPrompt();
   
   @Key("control.restartApp.id")
   String restartAppControlId();
   
   @Key("control.restartApp.title")
   String restartAppControlTitle();
   
   @Key("control.restartApp.prompt")
   String restartAppControlPrompt();
   
   @Key("control.stopApp.id")
   String stopAppControlId();
   
   @Key("control.stopApp.title")
   String stopAppControlTitle();
   
   @Key("control.stopApp.prompt")
   String stopAppControlPrompt();
   
   @Key("control.updateApp.id")
   String updateAppControlId();
   
   @Key("control.updateApp.title")
   String updateAppControlTitle();
   
   @Key("control.updateApp.prompt")
   String updateAppControlPrompt();
   
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
   
   @Key("control.renameApp.id")
   String renameAppControlId();
   
   @Key("control.renameApp.title")
   String renameAppControlTitle();
   
   @Key("control.renameApp.prompt")
   String renameAppControlPrompt();
   
   @Key("control.mapUrl.id")
   String mapUrlControlId();
   
   @Key("control.mapUrl.title")
   String mapUrlControlTitle();
   
   @Key("control.mapUrl.prompt")
   String mapUrlControlPrompt();
   
   @Key("control.unmapUrl.id")
   String unmapUrlControlId();
   
   @Key("control.unmapUrl.title")
   String unmapUrlControlTitle();
   
   @Key("control.unmapUrl.prompt")
   String unmapUrlControlPrompt();
   
   @Key("control.updMemory.id")
   String updateMemoryControlId();
   
   @Key("control.updMemory.title")
   String updateMemoryTitle();
   
   @Key("control.updMemory.prompt")
   String updateMemoryPrompt();
   
   @Key("control.updInstances.id")
   String updateInstancesControlId();
   
   @Key("control.updInstances.title")
   String updateInstancesControlTitle();
   
   @Key("control.updInstances.prompt")
   String updateInstancesControlPrompt();
   
   @Key("control.settings.id")
   String settingsControlId();
   
   @Key("control.settings.title")
   String settingsControlTitle();
   
   @Key("control.settings.prompt")
   String settingsControlPrompt();
   
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
   
   @Key("createApp.warUrlIsNull")
   String createApplicationWarIsNull();
   
   /*
    * StartApplicationPresenter
    */
   @Key("startApp.appStarted")
   String applicationStarted(String name);
   
   @Key("startApp.appStarted.uris")
   String applicationStartedUris(String name, String uris);
   
   @Key("startApp.appStopped")
   String applicationStopped(String name);
   
   @Key("startApp.appRestarted")
   String applicationRestarted(String name);
   
   @Key("startApp.appRestarted.uris")
   String applicationRestartedUris(String name, String uris);
   
   /*
    * UpdateApplicationPresenter
    */
   @Key("update.applicationUpdated")
   String updateApplicationSuccess();
   
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
   
   @Key("appInfo.name")
   String appInfoName();
   
   @Key("appInfo.state")
   String appInfoState();
   
   @Key("appInfo.instances")
   String appInfoInstances();
   
   @Key("appInfo.version")
   String appInfoVersion();
   
   @Key("appInfo.uris")
   String appInfoUris();
   
   @Key("appInfo.resources")
   String appInfoResources();
   
   @Key("appInfo.resources.disk")
   String appInfoResourcesDisk();
   
   @Key("appInfo.resources.memory")
   String appInfoResourcesMemory();
   
   @Key("appInfo.staging")
   String appInfoStaging();
   
   @Key("appInfo.staging.model")
   String appInfoStagingModel();
   
   @Key("appInfo.staging.stack")
   String appInfoStagingStack();
   
   @Key("appInfo.services")
   String appInfoServices();
   
   @Key("appInfo.env")
   String appInfoEnvironments();
   
   /*
    * UnmapUrlGrid
    */
   @Key("unmapUrl.listGrid.field.url")
   String applicationUnmapUrlGridUrlField();
   
   /*
    * UnmapUrlsView
    */
   @Key("unmapUrl.view.title")
   String unmapUrlViewTitle();
   
   /*
    * DeleteApplication
    */
   @Key("deleteApplication.title")
   String deleteApplicationTitle();

   @Key("askForDeleteApplication")
   String deleteApplicationQuestion(String applicationTitle);
   
   @Key("applicationDeleted")
   String applicationDeletedMsg(String applicationTitle);
   
   @Key("delete.application.services")
   String deleteApplicationAskDeleteServices();
   
   /*
    * MapUnmapUrlPresenter
    */
   @Key("mapUrl.dialog.title")
   String mapUrlDialogTitle();
   
   @Key("mapUrl.dialog.message")
   String mapUrlDialogMessage();
   
   @Key("unmapUrl.dialog.title")
   String unmapUrlDialogTitle();
   
   @Key("unmapUrl.dialog.message")
   String unmapUrlDialogMessage();
   
   @Key("mapUrl.success")
   String mapUrlRegisteredSuccess(String url);
   
   @Key("unmapUrl.success")
   String unmapUrlUnregisteredSuccess(String url);
   
   @Key("unmapUrl.confirmationDialog.title")
   String unmapUrlConfirmationDialogTitle();
   
   @Key("unmapUrl.confirmationDialog.message")
   String unmapUrlConfirmationDialogMessage();
   
   /*
    * UpdatePropertiesPresenter
    */
   @Key("updateMemory.dialog.title")
   String updateMemoryDialogTitle();
   
   @Key("updateMemory.dialog.message")
   String updateMemoryDialogMessage();
   
   @Key("updateMemory.success")
   String updateMemorySuccess(String memory);
   
   @Key("updateMemory.dialog.invalidNumber.message")
   String updateMemoryInvalidNumberMessage();
   
   @Key("updateInstances.dialog.title")
   String updateInstancesDialogTitle();
   
   @Key("updateInstances.dialog.message")
   String updateInstancesDialogMessage();
   
   @Key("updateInstances.success")
   String updateInstancesSuccess(String expression);
   
   @Key("updateInstances.invalidValue.message")
   String updateInstancesInvalidValueMessage();
   
   /*
    * RenameApplicationPresenter
    */
   @Key("rename.view.title")
   String renameApplicationViewTitle();
   
   @Key("rename.field.name")
   String renameApplicationViewNameField();
   
   @Key("rename.application.success")
   String renameApplicationSuccess(String oldName, String newName);
   
   @Key("create.error.memoryFormat")
   String errorMemoryFormat();
   
   @Key("create.error.instancesFormat")
   String errorInstancesFormat();

}
