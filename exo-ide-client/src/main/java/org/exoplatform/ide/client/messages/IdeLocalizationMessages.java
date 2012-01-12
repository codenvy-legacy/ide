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

import com.google.gwt.i18n.client.Messages;

/**
 * Interface to represent the messages contained in resource bundle:
 *      'IdeLocalizationMessages.properties'.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: IdeLocalizationMessages.java Jun 8, 2011 2:41:55 PM vereshchaka $
 *
 */
public interface IdeLocalizationMessages extends Messages
{

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
    * IDEConfigurationLoader
    * IDEConfigurationUnmarshaller
    */
   @Key("configuration.invalidConfiguration")
   String configurationInvalidConfiguration(String message);

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
    * CreateProjectFromTemplatePresenter
    * AbstractCreateFromTemplatePresenter
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

}
