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

    @Key("choosePaasTooltip")
    String choosePaaSTooltip();

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
