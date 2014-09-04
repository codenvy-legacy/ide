/*******************************************************************************
 * Copyright (c) 2012-2014 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package com.codenvy.ide;

import com.google.gwt.i18n.client.Messages;

/** @author Andrey Plotnikov */
public interface CoreLocalizationConstant extends Messages {
    @Key("createProjectFromTemplate.nameField")
    String createProjectFromTemplateName();

    @Key("createProjectFromTemplate.selectTemplate")
    String createProjectFromTemplateSelectTemplate();

    @Key("createProject.warningTitle")
    String createProjectWarningTitle();

    @Key("noIncorrectProjectNameMessage")
    String noIncorrectProjectNameMessage();

    @Key("createProjectFromTemplate.project.exists")
    String createProjectFromTemplateProjectExists(String projectName);
    
    @Key("chooseTechnology")
    String chooseTechnology();

    @Key("chooseTechnologyTooltip")
    String chooseTechnologyTooltip();

    @Key("enteringProjectName")
    String enteringProjectName();

    @Key("checkingProjectsList")
    String checkingProjectsList();

    @Key("choosePaas")
    String choosePaaS();

    @Key("noTechnologyTitle")
    String noTechnologyTitle();

    @Key("noTechnologyMessage")
    String noTechnologyMessage();

    @Key("extension.title")
    String extensionTitle();

    @Key("enteringResourceName")
    String enteringResourceName();

    @Key("noIncorrectResourceName")
    String noIncorrectResourceName();

    @Key("resourceExists")
    String resourceExists(String resourceName);

    @Key("chooseResourceType")
    String chooseResourceType();

    @Key("navigateToFile.view.title")
    String navigateToFileViewTitle();

    @Key("navigateToFile.view.file.field.title")
    String navigateToFileViewFileFieldTitle();

    @Key("appearance.title")
    String appearanceTitle();

    /* DeleteItem */
    @Key("action.delete.text")
    String deleteItemActionText();

    @Key("action.delete.description")
    String deleteItemActionDescription();

    @Key("deleteNodeDialogTitle")
    String deleteNodeDialogTitle();

    @Key("deleteFileDialogTitle")
    String deleteFileDialogTitle();

    @Key("deleteFolderDialogTitle")
    String deleteFolderDialogTitle();

    @Key("deleteProjectDialogTitle")
    String deleteProjectDialogTitle();

    @Key("deleteNodeDialogQuestion")
    String deleteNodeDialogQuestion(String name);

    @Key("deleteFileDialogQuestion")
    String deleteFileDialogQuestion(String name);

    @Key("deleteFolderDialogQuestion")
    String deleteFolderDialogQuestion(String name);

    @Key("deleteProjectDialogQuestion")
    String deleteProjectDialogQuestion(String name);

    @Key("stopProcessesBeforeDeletingProject")
    String stopProcessesBeforeDeletingProject();

    /* RenameItem */
    @Key("action.rename.text")
    String renameItemActionText();

    @Key("action.rename.description")
    String renameItemActionDescription();

    @Key("renameNodeDialogTitle")
    String renameNodeDialogTitle();

    @Key("renameFileDialogTitle")
    String renameFileDialogTitle();

    @Key("renameFolderDialogTitle")
    String renameFolderDialogTitle();

    @Key("renameProjectDialogTitle")
    String renameProjectDialogTitle();

    @Key("renameDialogNewNameLabel")
    String renameDialogNewNameLabel();

    @Key("renameButton")
    String renameButton();

    @Key("renameResourceViewTitle")
    String renameResourceViewTitle();

    @Key("renameFieldTitle")
    String renameFieldTitle();

    @Key("stopProcessesBeforeRenamingProject")
    String stopProcessesBeforeRenamingProject();

    @Key("createProjectFromTemplate.descriptionField")
    String createProjectFromTemplateDescription();

    @Key("createProjectFromTemplate.projectPrivacy")
    String createProjectFromTemplateProjectPrivacy();

    @Key("createProjectFromTemplate.public")
    String createProjectFromTemplatePublic();

    @Key("createProjectFromTemplate.publicDescription")
    String createProjectFromTemplatePublicDescription();

    @Key("createProjectFromTemplate.private")
    String createProjectFromTemplatePrivate();

    @Key("createProjectFromTemplate.privateDescription")
    String createProjectFromTemplatePrivateDescription();

    @Key("createProjectFromTemplate.linkGetMoreRAM")
    String createProjectFromTemplatelinkGetMoreRAM();

    @Key("format.name")
    String formatName();

    @Key("format.description")
    String formatDescription();

    @Key("undo.name")
    String undoName();

    @Key("undo.description")
    String undoDescription();

    @Key("redo.name")
    String redoName();

    @Key("redo.description")
    String redoDescription();

    @Key("uploadFile.name")
    String uploadFileName();

    @Key("uploadFile.description")
    String uploadFileDescription();

    @Key("uploadFile.title")
    String uploadFileTitle();

    @Key("cancelButton")
    String cancelButton();

    @Key("uploadButton")
    String uploadButton();

    @Key("openFileFieldTitle")
    String openFileFieldTitle();

    @Key("projectExplorer.button.title")
    String projectExplorerButtonTitle();

    @Key("projectExplorer.titleBar.text")
    String projectExplorerTitleBarText();

    @Key("importProject.messageSuccess")
    String importProjectMessageSuccess();

    @Key("importProject.messageFailure")
    String importProjectMessageFailure();

    @Key("importProject.name")
    String importProjectName();

    @Key("importProject.description")
    String importProjectDescription();

    @Key("importProject.importButton")
    String importProjectButton();

    @Key("importProject.importing")
    String importingProject();

    @Key("importProject.importerFieldTitle")
    String importProjectImporterFieldTitle();

    @Key("importProject.uriFieldTitle")
    String importProjectUriFieldTitle();

    @Key("importProject.projectNameFieldTitle")
    String importProjectProjectNameFieldTitle();

    @Key("importProject.viewTitle")
    String importProjectViewTitle();

    @Key("importProject.warningTitle")
    String importProjectWarningTitle();
    
    @Key("importProject.importer.info")
    String importProjectImporterInfo();
    
    @Key("importProject.name.prompt")
    String importProjectNamePrompt();
    
    @Key("importProject.description.prompt")
    String importProjectDescriptionPrompt();
    
    @Key("import.project.error")
    String importProjectError();
    
    /* Actions */
    @Key("action.newFolder.title")
    String actionNewFolderTitle();

    @Key("action.newFolder.description")
    String actionNewFolderDescription();

    @Key("action.newFile.title")
    String actionNewFileTitle();

    @Key("action.newFile.description")
    String actionNewFileDescription();

    @Key("action.newXmlFile.title")
    String actionNewXmlFileTitle();

    @Key("action.newXmlFile.description")
    String actionNewXmlFileDescription();

    /* Redirect Actions */
    @Key("action.redirectToHelp.title")
    String actionRedirectToHelpTitle();

    @Key("action.redirectToHelp.description")
    String actionRedirectToHelpDescription();

    @Key("action.redirectToHelp.url")
    String actionRedirectToHelpUrl();

    @Key("action.redirectToForums.title")
    String actionRedirectToForumsTitle();

    @Key("action.redirectToForums.description")
    String actionRedirectToForumsDescription();

    @Key("action.redirectToForums.url")
    String actionRedirectToForumsUrl();

    @Key("action.redirectToFeedback.title")
    String actionRedirectToFeedbackTitle();

    @Key("action.redirectToFeedback.description")
    String actionRedirectToFeedbackDescription();

    @Key("action.redirectToFeedback.url")
    String actionRedirectToFeedbackUrl();

    /* NewResource */
    @Key("newResource.title")
    String newResourceTitle(String title);

    @Key("newResource.label")
    String newResourceLabel();

    /* Messages */
    @Key("messages.changesMayBeLost")
    String changesMayBeLost();

    @Key("messages.allFilesSaved")
    String allFilesSaved();

    @Key("messages.someFilesCanNotBeSaved")
    String someFilesCanNotBeSaved();

    @Key("messages.importProject.enteredWrongUri")
    String importProjectEnteredWrongUri();

    @Key("messages.unable-open-not-file")
    String unableOpenNotFile(String path);

    @Key("messages.unable-open-file")
    String unableOpenFile(String path);

    /* Buttons */
    @Key("ok")
    String ok();

    @Key("cancel")
    String cancel();

    @Key("open")
    String open();

    @Key("next")
    String next();

    @Key("back")
    String back();

    @Key("finish")
    String finish();

    @Key("close")
    String close();

    @Key("apply")
    String apply();

    @Key("delete")
    @DefaultMessage("Delete")
    String delete();

    @Key("codenvy.projectClosed.title")
    String codenvyTabTitle();

    @Key("codenvy.projectOpened.title")
    String codenvyTabTitle(String projectName);

    @Key("action.expandEditor.title")
    String actionExpandEditorTitle();
}
