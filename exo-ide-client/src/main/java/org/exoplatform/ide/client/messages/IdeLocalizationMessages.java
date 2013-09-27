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

import com.google.gwt.i18n.client.Messages;

/**
 * Interface to represent the messages contained in resource bundle: 'IdeLocalizationMessages.properties'.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdeLocalizationMessages.java Jun 8, 2011 2:41:55 PM vereshchaka $
 */
public interface IdeLocalizationMessages extends Messages {

    /*
     * LockUnlockFileHandler
     */
    @Key("lockUnlockFile.cantLockFile")
    String lockUnlockFileCantLockFile(String fileName);

    /*
     * EditorController
     */
    @Key("editor.doYouWantToSaveFileBeforeClosing")
    String editorDoYouWantToSaveFileBeforeClosing(String fileName);

    /*
     * OpenFileWithPresenter
     */
    @Key("openFileWith.cantFindEditor")
    String openFileWithCantFindEditor(String mimeType);

    @Key("openFileWith.doYouWantToReopen")
    String openFileWithDoYouWantToReopen(String fileName);

    /*
     * HotKeyHelper
     */
    @Key("hotkeys.cantFindCodeCombination")
    String hotkeysCantFindCodeCombination(String hotkeys);

    /*
     * IDEConfigurationLoader IDEConfigurationUnmarshaller
     */
    @Key("configuration.invalidConfiguration")
    String configurationInvalidConfiguration(String message);

    /*
     * LoginPresenter IDEConfigurationInitializer IDEConfigurationInitializer
     */
    @Key("conf.missingVariable")
    String confMissingVariable(String variableName);

    /*
     * DeleteItemsPresenter
     */
    @Key("deleteItems.askDeleteOneItem")
    String deleteItemsAskDeleteOneItem(String name);

    @Key("deleteItems.askDeleteSeveralItems")
    String deleteItemsAskDeleteSeveralItems(int number);

    @Key("deleteItems.askDeleteModifiedFile")
    String deleteItemsAskDeleteModifiedFile(String fileName);

    @Key("deleteItems.askDeleteFolderWithModifiedFiles")
    String deleteItemsAskDeleteFolderWithModifiedFiles(String folderName, int numberOfFiles);

    @Key("deleteItems.askDeleteProjectWithModifiedFiles")
    String deleteItemsAskDeleteProjectWithModifiedFiles(String folderName, int numberOfFiles);

    @Key("deleteItems.askDeleteProject")
    String deleteItemsAskDeleteProject(String projectName);

    /*
     * CreateFileCommandHandler
     */
    @Key("createFile.cantFindEditorForType")
    String createFileCantFindEditorForType(String mimeType);

    /*
     * OpenFileCommandHandler
     */
    @Key("openFile.cantFindEditorForType")
    String openFileCantFindEditorForType(String mimeType);

    /*
     * PasteItemsCommandHandler
     */
    @Key("paste.saveFileBeforeCutAskDialog.text")
    String pasteSaveFileBeforeCutAskDialogText(String fileName);

    /*
     * CreateFileFromTemplatePresenter
     */
    @Key("template.askDeleteTemplate")
    String templateAskDeleteTemplate(String templateName);

    @Key("template.dialog.templateIsUsed.text")
    String askDeleteTemplateUsedInOtherProjects(String templateName, String projectsNames);

    /*
     * CreateProjectFromTemplatePresenter AbstractCreateFromTemplatePresenter
     */
    @Key("createProjectFromTemplate.askDeleteOneTemplate")
    String createFromTemplateAskDeleteOneTemplate(String templateName);

    /*
     * OpenLocalFilePresenter
     */
    @Key("openLocalFile.openingFailure")
    String openLocalFileOpeningFailure(String fileName);

    /*
     * RestoreToVersionCommandHandler
     */
    @Key("restoreToVersion.askDialog.restoreToVersion")
    String restoreToVersionAskDialog(String version);

    /*
     * ShowVersionListCommandHandler
     */
    @Key("showVersionList.itemHasNoVersions")
    String showVersionListItemHasNoVersions(String itemName);

    /*
     * VersionHistoryCommandHandler
     */
    @Key("versionHistory.itemHasNoVersions")
    String versionHistoryItemHasNoVersions(String itemName);

    /*
     * SelectWorkspacePresenter
     */
    @Key("selectWorkspace.askSaveFileBeforeClosing")
    String selectWorkspaceAskSaveFileBeforeClosing(String fileName);

    /*
     * Open File by URL
     */
    @Key("openFileByURL.errorMessage")
    String openFileByURLErrorMessage(String message);

    /*
     * Open File by path
     */
    @Key("openFileByPath.errorMessage")
    String openFileByPathErrorMessage(String message);

}
