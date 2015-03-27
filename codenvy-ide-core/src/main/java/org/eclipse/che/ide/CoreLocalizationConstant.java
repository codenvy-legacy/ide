/*******************************************************************************
 * Copyright (c) 2012-2015 Codenvy, S.A.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *   Codenvy, S.A. - initial API and implementation
 *******************************************************************************/
package org.eclipse.che.ide;

import com.google.gwt.i18n.client.Messages;

/** @author Andrey Plotnikov */
public interface CoreLocalizationConstant extends Messages {
    @Key("createProjectFromTemplate.nameField")
    String createProjectFromTemplateName();

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

    @Key("noTechnologyTitle")
    String noTechnologyTitle();

    @Key("noTechnologyMessage")
    String noTechnologyMessage();

    @Key("extension.title")
    String extensionTitle();

    @Key("extension.category")
    String extensionCategory();

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

    @Key("navigateToFile.view.file.field.prompt")
    String navigateToFileViewFileFieldPrompt();

    @Key("navigateToFile.canNotOpenFile")
    String navigateToFileCanNotOpenFile();

    @Key("appearance.title")
    String appearanceTitle();

    @Key("appearance.category")
    String appearanceCategory();

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

    @Key("deleteMultipleDialogTitle")
    String deleteMultipleDialogTitle();

    @Key("deleteNodeDialogQuestion")
    String deleteNodeDialogQuestion(String name);

    @Key("deleteFileDialogQuestion")
    String deleteFileDialogQuestion(String name);

    @Key("deleteFolderDialogQuestion")
    String deleteFolderDialogQuestion(String name);

    @Key("deleteProjectDialogQuestion")
    String deleteProjectDialogQuestion(String name);

    @Key("deleteMultipleDialogMessage")
    String deleteMultipleDialogMessage();

    @Key("stopProcessesBeforeDeletingProject")
    String stopProcessesBeforeDeletingProject();

    @Key("mixedProjectDeleteTitle")
    String mixedProjectDeleteTitle();

    @Key("mixedProjectDeleteMessage")
    String mixedProjectDeleteMessage();

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

    @Key("closeProjectBeforeRenaming")
    String closeProjectBeforeRenaming();

    @Key("stopProcessesBeforeRenamingProject")
    String stopProcessesBeforeRenamingProject();

    @Key("createProjectFromTemplate.descriptionField")
    String createProjectFromTemplateDescription();

    @Key("createProjectFromTemplate.projectPrivacy")
    String createProjectFromTemplateProjectPrivacy();

    @Key("createProjectFromTemplate.public")
    String createProjectFromTemplatePublic();

    @Key("createProjectFromTemplate.private")
    String createProjectFromTemplatePrivate();

    @Key("projectWizard.defaultTitleText")
    String projectWizardDefaultTitleText();

    @Key("projectWizard.titleText")
    String projectWizardTitleText();

    @Key("projectWizard.createModule.titleText")
    String projectWizardCreateModuleTitleText();

    @Key("projectWizard.defaultSaveButtonText")
    String projectWizardDefaultSaveButtonText();

    @Key("projectWizard.saveButtonText")
    String projectWizardSaveButtonText();

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

    @Key("uploadFile.overwrite")
    String uploadFileOverwrite();

    @Key("uploadFolderFromZip.name")
    String uploadFolderFromZipName();

    @Key("uploadFolderFromZip.description")
    String uploadFolderFromZipDescription();

    @Key("uploadFolderFromZip.title")
    String uploadFolderFromZipTitle();

    @Key("uploadFolderFromZip.overwrite")
    String uploadFolderFromZipOverwrite();

    @Key("uploadFolderFromZip.skipFirstLevel")
    String uploadFolderFromZipSkipFirstLevel();

    @Key("cancelButton")
    String cancelButton();

    @Key("uploadButton")
    String uploadButton();

    @Key("openFileFieldTitle")
    String openFileFieldTitle();

    @Key("uploadFolderFromZip.openZipFieldTitle")
    String uploadFolderFromZipOpenZipFieldTitle();

    @Key("projectExplorer.button.title")
    String projectExplorerButtonTitle();

    @Key("projectExplorer.titleBar.text")
    String projectExplorerTitleBarText();

    @Key("projectExplorer.problemProject.title")
    @DefaultMessage("Looks like your project needs to be configured")
    String projectExplorerProblemProjetTitle();

    @Key("importProject.message.success")
    String importProjectMessageSuccess();

    @Key("importProject.message.failure")
    String importProjectMessageFailure();

    @Key("importProject.message.startWithWhiteSpace")
    String importProjectMessageStartWithWhiteSpace();

    @Key("importProject.message.urlInvalid")
    String importProjectMessageUrlInvalid();

    @Key("importProject.message.unableGetSshKey")
    String importProjectMessageUnableGetSshKey();

    @Key("importProjectFromLocation.name")
    String importProjectFromLocationName();

    @Key("importProjectFromLocation.description")
    String importProjectFromLocationDescription();

    @Key("importLocalProject.name")
    String importLocalProjectName();

    @Key("importLocalProject.description")
    String importLocalProjectDescription();

    @Key("importLocalProject.openZipTitle")
    String importLocalProjectOpenZipTitle();

    @Key("importProject.importButton")
    String importProjectButton();

    @Key("importProject.importing")
    String importingProject();

    @Key("importProject.uriFieldTitle")
    String importProjectUriFieldTitle();

    @Key("importProject.viewTitle")
    String importProjectViewTitle();

    @Key("importProject.importer.info")
    String importProjectImporterInfo();

    @Key("importProject.project.info")
    String importProjectInfo();

    @Key("importProject.name.prompt")
    String importProjectNamePrompt();

    @Key("importProject.description.prompt")
    String importProjectDescriptionPrompt();

    @Key("importProject.zipImporter.skipFirstLevel")
    String importProjectZipImporterSkipFirstLevel();

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

    @Key("action.projectConfiguration.description")
    String actionProjectConfigurationDescription();

    @Key("action.projectConfiguration.title")
    String actionProjectConfigurationTitle();

    @Key("action.findAction.description")
    String actionFindActionDescription();

    @Key("action.findAction.title")
    String actionFindActionTitle();

    @Key("action.showHiddenFiles.title")
    String actionShowHiddenFilesTitle();

    @Key("action.showHiddenFiles.description")
    String actionShowHiddenFilesDescription();

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
    String newResourceLabel(String title);

    @Key("newResource.invalidName")
    String invalidName();

    /* Messages */
    @Key("messages.changesMayBeLost")
    String changesMayBeLost();

    @Key("messages.allFilesSaved")
    String allFilesSaved();

    @Key("messages.someFilesCanNotBeSaved")
    String someFilesCanNotBeSaved();

    @Key("messages.appWillBeStopped")
    String appWillBeStopped(String appName);

    @Key("messages.workspaceRamLessRequiredRam")
    String getMoreRam(int requiredMemorySize, int workspaceMemorySize);

    @Key("messages.updateProjectWorkspaceRamLessRequired")
    String messagesUpdateProjectWorkspaceRamLessRequired(int requiredMemorySize, int workspaceMemorySize);

    @Key("messages.getResourcesFailed")
    String messagesGetResourcesFailed();

    @Key("messages.getProjectTypesFailed")
    String messagesGetProjectTypesFailed();

    @Key("messages.saveChanges")
    String messagesSaveChanges(String name);

    @Key("messages.promptSaveChanges")
    String messagesPromptSaveChanges();

    @Key("messages.unableOpenFile")
    public String unableOpenFile(String path);

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

    @Key("save")
    String save();

    @Key("apply")
    String apply();

    @Key("refresh")
    String refresh();

    @Key("delete")
    @DefaultMessage("Delete")
    String delete();

    @Key("codenvy.projectClosed.title")
    String codenvyTabTitle();

    @Key("codenvy.projectOpened.title")
    String codenvyTabTitle(String projectName);

    @Key("projectProblem.title")
    String projectProblemTitle();

    @Key("projectProblem.message")
    String projectProblemMessage();

    @Key("action.expandEditor.title")
    String actionExpandEditorTitle();

    @Key("closeProject.title")
    String closeProjectTitle();

    @Key("askWindow.close.title")
    String askWindowCloseTitle();

    /* Outline */
    @Key("outline.button.title")
    String outlineButtonTitle();

    @Key("outline.titleBar.text")
    String outlineTitleBarText();

    @Key("outline.notAvailable.message")
    String outlineNotAvailableMessage();

    @Key("outline.noFileOpened.message")
    String outlineNoFileOpenedMessage();

    /* Privacy */
    @Key("privacy.tooltip.publicHeader")
    String privacyTooltipPublicHeader();

    @Key("privacy.tooltip.publicMessage")
    String privacyTooltipPublicMessage();

    @Key("privacy.tooltip.privateHeader")
    String privacyTooltipPrivateHeader();

    @Key("privacy.tooltip.privateMessage")
    String privacyTooltipPrivateMessage();
}
