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

/** @author <a href="mailto:aplotnikov@codenvy.com">Andrey Plotnikov</a> */
public interface CoreLocalizationConstant extends Messages {
    @Key("createProjectFromTemplate.nameField")
    String createProjectFromTemplateName();

    @Key("createProjectFromTemplate.selectTemplate")
    String createProjectFromTemplateSelectTemplate();

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

    @Key("selectProjectType")
    String selectProjectType(String project);

    @Key("setProjectTypeTitle")
    String setProjectTypeTitle();

    @Key("navigateToFile.view.title")
    String navigateToFileViewTitle();

    @Key("navigateToFile.view.file.field.title")
    String navigateToFileViewFileFieldTitle();

    @Key("appearance.title")
    String appearanceTitle();

    @Key("deleteResourceQuestion")
    String deleteResourceQuestion(String resource);

    @Key("renameButton")
    String renameButton();

    @Key("renameResourceViewTitle")
    String renameResourceViewTitle();

    @Key("renameFieldTitle")
    String renameFieldTitle();

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

    @Key("format.name")
    String formatName();

    @Key("format.description")
    String formatDescription();

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

    @Key("importProject.name")
    String importProjectName();

    @Key("importProject.description")
    String importProjectDescription();

    @Key("importProject.importButton")
    String importProjectButton();

    @Key("importProject.importerFieldTitle")
    String importProjectImporterFieldTitle();

    @Key("importProject.uriFieldTitle")
    String importProjectUriFieldTitle();

    @Key("importProject.projectNameFieldTitle")
    String importProjectProjectNameFieldTitle();

    @Key("importProject.viewTitle")
    String importProjectViewTitle();

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

    /* NewResource */
    @Key("newResource.title")
    String newResourceTitle(String title);

    @Key("newResource.label")
    String newResourceLabel();

    /* Messages */
    @Key("messages.changesMayBeLost")
    String changesMayBeLost();

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
}
