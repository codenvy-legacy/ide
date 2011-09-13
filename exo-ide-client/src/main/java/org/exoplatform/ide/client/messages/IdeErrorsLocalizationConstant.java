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
package org.exoplatform.ide.client.messages;

import javax.ws.rs.DefaultValue;

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle:
 *      'IdeErrorsLocalizationConstant.properties'.
 * <p/>
 * Localization messages for errors.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 *
 */
public interface IdeErrorsLocalizationConstant extends Constants
{
   /*
    * ControlsRegistration
    */
   @Key("registerControl")
   String controlsRegistration();
   
   /*
    * IDEConfigurationInitializer
    */
   @Key("conf.userHasNoRoles")
   String userHasNoRoles();
   
   @Key("conf.workspaceWasNotSet.title")
   String confWorkspaceWasNotSetTitle();
   
   @Key("conf.workspaceWasNotSet.text")
   String confWorkspaceWasNotSetText();
   
   /*
    * FindTextPresenter
    */
   @Key("findText.stringNotFound")
   String findTextStringNotFound();
   
   /*
    * GoToLinePresenter
    */
   @Key("goToLine.lineNumberOutOfRange")
   String goToLineLineNumberOutOfRange();
   
   @Key("goToLine.CantParseLineNumber")
   String goToLineCantParseLineNumber();
   
   /*
    * LockUnlockFileHandler
    */
   @Key("lockFile.serviceNotDeployed")
   String lockFileServiceNotDeployed();
   
   @Key("lockFile.lockOperationFailure")
   String lockFileLockOperationFailure();
   
   /*
    * OpenFileWithPresenter
    */
   @Key("openFileWith.savingSettingsFailure")
   String openFileWithSavingSettingsFailure();
   
   /*
    * CustomizeHotKeysPresenter
    */
   @Key("hotkeys.cantSaveHotkeys")
   String hotkeysCantSaveHotkeys();
   
   /*
    * IDEConfigurationLoader
    */
   @Key("confLoader.cantReadConf")
   String confLoaderCantReadConfiguration();
   
   @Key("conf.invalidConf.title")
   String confInvalidConfTitle();
   
   /*
    * RestServicesUnmarshaller
    */
   @Key("restService.parse.exception")
   String restServiceParseError();
   
   /*
    * ApplicationSettingsUnmarshaller
    */
   @Key("appSettings.cantParse")
   String appSettingsCantParse();
   
   /*
    * TemplateCreatedCallback
    */
   @Key("template.creationFailure")
   String templateCreationFailure();
   
   @Key("template.deleteFailure")
   String templateDeleteFailure();
   
   /*
    * TemplateListUnmarshaller
    */
   @Key("template.cantParseTemplate")
   String templateCantParseTemplate();
   
   /*
    * DeleteItemsPresenter
    */
   @Key("deleteFile.unlockFailure")
   String deleteFileUnlockFailure();
   
   @Key("deleteFile.deleteItemFailure")
   String deleteFileFailure();
   
   /*
    * NavigationModule
    */
   @Key("navigation.upload.noTargetSelected")
   String navigationUploadNoTargetSelected();
   
   /*
    * RenameItemPresenter
    */
   @Key("renameItem.cantRenameMimeTypeToOpenedFile")
   String renameItemCantRenameMimeTypeToOpenedFile();
   
   /*
    * SearchFilesPresenter
    */
   @Key("searchFiles.searchError")
   String searchFileSearchError();
   
   /*
    * WorkspacePresenter
    */
   @Key("workspace.receiveChildrenError")
   String workspaceReceiveChildrenError();
   
   /*
    * FileClosedHandler
    */
   @Key("fileClosed.unlockFailure")
   String fileClosedUnlockFailure();
   
   /*
    * GoToFolderCommandHandler
    */
   @Key("goToFolder.receiveChildrenFailure")
   String goToFolderReceiveChildrenFailure();
   
   /*
    * PasteItemsCommandHandler
    */
   @Key("pasteItems.cantMoveToTheSameFolder")
   String pasteItemsCantMoveToTheSameFolder();
   
   @Key("pasteItems.cantCopyToTheSameFolder")
   String pasteItemsCantCopyToTheSameFolder();
   
   /*
    * SaveFileAsCommandHandler
    */
   @Key("saveFileAs.targetNotSelected")
   String saveFileAsTargetNotSelected();
   
   /*
    * CreateFileFromTemplatePresenter
    */
   @Key("createFileFromTemplate.enterName")
   String createFileFromTemplateEnterName();
   
   /*
    * CreateFolderPresenter
    */
   @Key("createFolder.selectParentFolder")
   String createFolderSelectParentFolder();
   
   /*
    * RestServicesDiscoveryPresenter
    */
   @Key("restServicesDiscovery.getWadlFailure")
   String restServicesDiscoveryGetWadlFailure();
   
   @Key("restServicesDiscovery.getRestServices")
   String restServicesDiscoveryGetRestServicesFailure();
   
   /*
    * CustomizeToolbarPresenter
    */
   @Key("customizeToolbar.saveFailure")
   String customizeToolbarSaveFailure();
   
   /*
    * OpenLocalFilePresenter
    */
   @Key("openLocalFile.openingFailure")
   String openLocalFileOpeningFailure();
   
   /*
    * UploadFilePresenter
    */
   @Key("uploadFile.fileUploadingFailure")
   String uploadFileUploadingFailure();
   
   /*
    * UploadPresenter
    */
   @Key("upload.folderUploadingFailure")
   String uploadFolderUploadingFailure();
   
   /*
    * ShowVersionListCommandHandler
    */
   @Key("versions.receiveVersionsFailure")
   String versionsReceiveVersionsFailure();
   
   /*
    * IDEConfigurationUnmarshaller
    */
   @Key("configuration.cantParseApplicationSettings")
   String configurationCantParseApplicationSettings();
   
   @Key("configuration.receivedJsonValueNotAnObject")
   String configurationReceivedJsonValueNotAnObject();
   
   @Key("project.cantCreateProjectIfMultiselectionParent")
   @DefaultStringValue("Can't create project you must select only one parent folder")
   String cantCreateProjectIfMultiselectionParent();
   
   @Key("project.cantCreateProjectIfProjectNameNotSet")
   @DefaultStringValue("Project name can't be empty or null")
   String cantCreateProjectIfProjectNameNotSet();
}
