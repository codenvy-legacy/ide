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
package org.exoplatform.ide.client.messages;

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants contained in resource bundle: 'IdeErrorsLocalizationConstant.properties'.
 * <p/>
 * Localization messages for errors.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdePreferencesLocalizationConstant.java Jun 3, 2011 12:58:29 PM vereshchaka $
 */
public interface IdeErrorsLocalizationConstant extends Constants {
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

    @Key("create.project.error.vfs.info.not.set")
    String createProjectErrorVFSInfoNotSets();

}
