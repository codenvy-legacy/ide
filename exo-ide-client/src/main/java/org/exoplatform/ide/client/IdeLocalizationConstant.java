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
package org.exoplatform.ide.client;

import com.google.gwt.i18n.client.Constants;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 * 
 * 
 * Interface to represent the constants contained in resource bundle:
 *      'IdeLocalizationConstant.properties'.
 */

public interface IdeLocalizationConstant extends Constants
{

   /*
    * Buttons
    */
   @DefaultStringValue("Cancel")
   @Key("cancelButton")
   String cancelButton();

   @DefaultStringValue("Save")
   @Key("saveButton")
   String saveButton();

   @DefaultStringValue("Delete")
   @Key("deleteButton")
   String deleteButton();

   @DefaultStringValue("Create")
   @Key("createButton")
   String createButton();

   @DefaultStringValue("OK")
   @Key("okButton")
   String okButton();

   @DefaultStringValue("Add")
   @Key("addButton")
   String addButton();

   @DefaultStringValue("Open")
   @Key("openButton")
   String openButton();

   @DefaultStringValue("Close")
   @Key("closeButton")
   String closeButton();

   @DefaultStringValue("Search")
   @Key("searchButton")
   String searchButton();

   @DefaultStringValue("Yes")
   @Key("yesButton")
   String yesButton();

   @DefaultStringValue("No")
   @Key("noButton")
   String noButton();

   @DefaultStringValue("Back")
   @Key("backButton")
   String backButton();

   @DefaultStringValue("Finish")
   @Key("finishButton")
   String finishButton();

   /* Create Folder */
   @DefaultStringValue("Create Folder")
   @Key("createFolderFormTitle")
   String createFolderFormTitle();

   @DefaultStringValue("Create")
   @Key("createFolderFormSubmitButtonTitle")
   String createFolderFormSubmitButtonTitle();

   @DefaultStringValue("Name of new folder:")
   @Key("createFolderFormFieldTitle")
   String createFolderFormFieldTitle();

   @DefaultStringValue("New folder")
   @Key("newFolderName")
   String newFolderName();

   @DefaultStringValue("Project Name")
   @Key("projectName")
   String projectName();

   @DefaultStringValue("Project Type")
   @Key("projectType")
   String projectType();

   /*
    * DocumentationView
    */
   @Key("documentation.title")
   String documentationTitle();

   /*
    * PropertiesView
    */
   @Key("properties.title")
   String propertiesTitle();

   @Key("properties.noPropertiesMsg")
   String propertiesNoPropertiesMsg();

   /*
    * OutputViewEx
    */
   @Key("output.title")
   String outputTitle();

   @Key("output.clear")
   String outputClear();

   /*
    * OutlineViewExtended
    */
   @Key("outline.title")
   String outlineTitle();

   @Key("outline.title.refreshing.mark.title")
   String outlineTitleRefreshingMarkTitle();

   /*
    * PreviewHTMLView
    */
   @Key("previewHtml.title")
   String previewHtmlTitle();

   /*
    * ShowImagesView
    */
   @Key("showImages.title")
   String showImagesTitle();

   /*
    * PanelController
    */
   @Key("panel.notEnoughSpaceToDisplay")
   String panelNotEnoughSpaceToDisplay();

   /*
    * Controls
    */
   @Key("control.showImages")
   String showImagesControl();

   @Key("control.about")
   String aboutControl();

   @Key("control.documentation.title")
   String documentationControlTitle();

   @Key("control.documentation.show")
   String showDocumentationControl();

   @Key("control.documentation.hide")
   String hideDocumentationControl();

   @Key("control.deleteCurrentLine")
   String deleteCurrentLineControl();

   @Key("control.findReplace")
   String findReplaceControl();

   @Key("control.format")
   String formatControl();

   @Key("control.goToLine")
   String goToLineControl();

   @Key("control.lockFile.lock")
   String lockFileLockControl();

   @Key("control.lockFile.unlock")
   String lockFileUnlockControl();

   @Key("control.quickTextSearch")
   String quickTextSearchControl();

   @Key("control.redoTyping")
   String redoTypingControl();

   @Key("control.undoTyping")
   String undoTypingControl();

   @Key("control.showLineNumbers.show")
   String showLineNumbersShowControl();

   @Key("control.showLineNumbers.hide")
   String showLineNumbersHideControl();

   @Key("control.customizeHotkeys")
   String customizeHotkeysControl();

   @Key("control.copyItems.title")
   String copyItemsTitleControl();

   @Key("control.copyItems.prompt")
   String copyItemsPromptControl();

   @Key("control.cutItems.title")
   String cutItemsTitleControl();

   @Key("control.cutItems.prompt")
   String cutItemsPromptControl();

   @Key("control.deleteItems.title")
   String deleteItemsTitleControl();

   @Key("control.deleteItems.prompt")
   String deleteItemsPromptControl();

   @Key("control.output.title")
   String outputTitleControl();

   @Key("control.output.prompt")
   String outputPromptControl();

   /*
    * DOWNLOAD CONTROL
    */
   @Key("control.download.title")
   String downloadTitleControl();

   @Key("control.download.prompt")
   String downloadPromptControl();

   @Key("control.downloadZippedFolder")
   String downloadZippedFolderControl();

   /*
    * Open File By URL
    */
   @Key("control.getFilebyURL.id")
   String openFileByURLControlId();

   @Key("control.getFilebyURL.title")
   String openFileByURLControlTitle();

   @Key("control.getFilebyURL.prompt")
   String openFileByURLControlPrompt();

   @Key("view.openFileByURL.title")
   String openFileByURLViewTitle();

   @Key("view.openFileByURL.urlFieldTitle")
   String openFileByURLFieldTitle();

   @Key("view.openFileByURL.openButton")
   String openFileByURLOpenButtonTitle();

   @Key("view.openFileByURL.cancelButton")
   String openFileByURLCancelButtonTitle();

   /*
    * GET FILE URL
    */
   @Key("control.getItemUrl")
   String getItemUrlControl();

   @Key("control.goToFolder")
   String goToFolderControl();

   @Key("control.openFileByPath")
   String openFileByPathControl();

   @Key("control.openLocalFile")
   String openLocalFileControl();

   @Key("control.pasteItems.title")
   String pasteItemsTitleControl();

   @Key("control.pasteItems.prompt")
   String pasteItemsPromptControl();

   @Key("control.refresh.title")
   String refreshTitleControl();

   @Key("control.refresh.prompt")
   String refreshPromptControl();

   @Key("control.rename.title")
   String renameTitleControl();

   @Key("control.rename.prompt")
   String renamePromptControl();

   @Key("control.saveAll")
   String saveAllControl();

   @Key("control.saveFileAs")
   String saveFileAsControl();

   @Key("control.saveFileAsTemplate")
   String saveFileAsTemplateControl();

   @Key("control.saveFile")
   String saveFileControl();

   @Key("control.searchFiles")
   String searchFilesControl();

   @Key("control.uploadFile")
   String uploadFileControl();

   @Key("control.uploadFolder")
   String uploadFolderControl();

   @Key("control.createFileFromTemplate.title")
   String createFileFromTemplateTitleControl();

   @Key("control.createFileFromTemplate.prompt")
   String createFileFromTemplatePromptControl();

   @Key("control.createFolder.title")
   String createFolderTitleControl();

   @Key("control.createFolder.prompt")
   String createFolderPromptControl();

   @Key("control.outline.title")
   String outlineTitleControl();

   @Key("control.outline.prompt.show")
   String outlinePromptShowControl();

   @Key("control.outline.prompt.hide")
   String outlinePromptHideControl();

   @Key("control.permissions")
   String permissionsControl();

   @Key("control.htmlPreview")
   String htmlPreview();

   /*
    * 
    */
   @Key("control.createProjectFromTemplate.title")
   String createProjectFromTemplateTitleControl();

   @Key("control.createProjectFromTemplate.prompt")
   String createProjectFromTemplatePromptControl();

   @Key("control.createProjectTemplate.title")
   String createProjectTemplateTitleControl();

   @Key("control.createProjectTemplate.prompt")
   String createProjectTemplatePromptControl();

   @Key("control.projectExplorer.title")
   String projectExplorerControlTitle();

   @Key("control.projectExplorer.prompt")
   String projectExplorerControlPrompt();

   @Key("control.restServicesDiscovery")
   String restServicesDiscoveryControl();

   @Key("control.customizeToolbar")
   String customizeToolbarControl();

   @Key("control.restoreToVersion")
   String restoreToVersionControl();

   @Key("control.showVersionsPanel")
   String showVersionsPanelControl();

   @Key("control.viewNextVersion.title")
   String viewNextVersionTitleControl();

   @Key("control.viewNextVersion.prompt")
   String viewNextVersionPromptControl();

   @Key("control.viewPreviousVersion.title")
   String viewPreviousVersionTitleControl();

   @Key("control.viewPrevioutVersion.prompt")
   String viewPreviousVersionPromptControl();

   @Key("control.viewVersionHistory.title")
   String viewVersionHistoryTitleControl();

   @Key("control.viewVersionHistory.prompt.show")
   String viewVersionHistoryPromptShowControl();

   @Key("control.viewVersionHistory.prompt.hide")
   String viewVersionHistoryPromptHideControl();

   @Key("control.viewVersionList.title")
   String viewVersionListTitleControl();

   @Key("control.viewVersionList.prompt")
   String viewVersionListPromptControl();

   @Key("control.selectWorkspace")
   String selectWorkspaceControl();

   @Key("control.minMax.maximize")
   String minMaxMaximizeControl();

   @Key("control.minMax.restore")
   String minMaxRestoreControl();

   @Key("control.properties.title")
   String showPropertiesTitleControl();

   @Key("control.properties.prompt.show")
   String showPropertiesShowPromptControl();

   @Key("control.properties.prompt.hide")
   String showPropertiesHidePromptControl();

   /*
    * Controls from menu View
    */
   @Key("control.progress.id")
   String progressControlId();

   @Key("control.progress.title")
   String progressControlTitle();

   @Key("control.progress.prompt")
   String progressControlPrompt();

   /*
    * Controls from menu "New"
    */
   @Key("control.new.text.title")
   String controlNewTextTitle();

   @Key("control.new.text.prompt")
   String controlNewTextPrompt();

   /*
    * Menu
    */

   @Key("menu.file")
   String fileMenu();

   @Key("menu.edit")
   String editMenu();

   @Key("menu.view")
   String viewMenu();

   @Key("menu.run")
   String runMenu();

   @Key("menu.git")
   String gitMenu();

   @Key("menu.paas")
   String paasMenu();

   @Key("menu.ssh")
   String sshMenu();

   @Key("menu.window")
   String windowMenu();

   @Key("menu.help")
   String helpMenu();

   @Key("menu.New")
   String newMenu();

   @Key("menu.new.googleGadget")
   String googleGadgetNewMenu();

   @Key("menu.new.restService")
   String restServiceNewMenu();

   @Key("menu.new.pogo")
   String pogoNewMenu();

   @Key("menu.new.dataObject")
   String dataObjectNewMenu();

   @Key("menu.new.html")
   String htmlNewMenu();

   @Key("menu.new.javaScript")
   String javaScriptNewMenu();

   @Key("menu.new.css")
   String cssNewMenu();

   @Key("menu.new.template")
   String templateNewMenu();

   @Key("menu.new.xml")
   String xmlNewMenu();

   @Key("menu.new.text")
   String textNewMenu();

   /*
    * AskForValueDialog
    */
   @Key("ask.button.yes")
   String askValueDialogYesButton();

   @Key("ask.button.cancel")
   String askValueDialogCancelButton();

   @Key("ask.button.no")
   String askValueDialogNoButton();

   /**************************************************************************
    *
    * Server Disconnected Dialog
    *    
    **************************************************************************/

   @DefaultStringValue("Server disconnected.<br>Retry?")
   @Key("dialog.serverDisconnected")
   String serverDisconnected();

   /**************************************************************************
   *
   * Login Dialog
   * org.exoplatform.ide.client.dialogs.LoginDialog
   *    
   **************************************************************************/

   /*************************************************************************
    * Progress view
    *************************************************************************/

   @Key("progress.title")
   String progressViewTitle();

   @Key("progress.remove.finished.button")
   String progressRemoveAllFinished();
}
