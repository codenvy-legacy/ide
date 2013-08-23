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
 * Interface to represent the constants for template and project forms contained in resource bundle:
 * 'IdeTemplateLocalizationConstant.properties'.
 *
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplateConstant.java Jun 2, 2011 2:59:52 PM vereshchaka $
 */
public interface IdeTemplateLocalizationConstant extends Messages {

    /*
     * TemplateTree
     */
    @Key("template.tree.from")
    String from();

    /*
     * TemplateListGrid
     */
    @Key("template.listGrid.name")
    String listGridName();

    @Key("template.listGrid.description")
    String listGridDescription();

    /*
     * SaveAsTemplateForm
     */
    @Key("saveAsTemplate.title")
    String saveAsTemplateTitle();

    @Key("saveAsTemplate.type")
    String saveAsTemplateType();

    @Key("saveAsTemplate.name")
    String saveAsTemplateName();

    @Key("saveAsTemplate.description")
    String saveAsTemplateDescription();

    /*
     * CreateFileFromTemplateForm CreateFileFromTemplateView
     */
    @Key("createFileFromTemplate.createButton")
    String createFileFromTemplateCreateButton();

    @Key("createFileFromTemplate.formTitle")
    String createFileFromTemplateFormTitle();

    @Key("createFileFromTemplate.nameField")
    String createFileFromTemplateNameField();

    @Key("createFileFromTemplate.name")
    String createFileFromTemplateName();

    /*
     * CreateProjectFromTemplateForm
     */
    @Key("createProjectFromTemplate.formTitle")
    String createProjectFromTemplateTitle();

    @Key("createProjectFromTemplate.newModule.formTitle")
    String createProjectFromTemplateNewModuleTitle();

    @Key("createProjectFromTemplate.nameField")
    String createProjectFromTemplateName();

    @Key("createProjectFromTemplate.selectProject")
    String createProjectFromTemplateSelectProject();

    @Key("createProjectFromTemplate.deployField")
    String createProjectFromTemplateDeployField();

    @Key("createProjectFromTemplate.descriptionField")
    String createProjectFromTemplateDescriptionField();

    @Key("createProjectFromTemplate.selectTemplate")
    String createProjectFromTemplateSelectTemplate();

    @Key("createProjectFromTemplate.project.nameInvalid")
    String createProjectFromTemplateProjectNameInvalid();
    
    @Key("createProjectFromTemplate.project.exists")
    String createProjectFromTemplateProjectExists(String projectName);

    @Key("creatingProject")
    String creatingProject();

    @Key("noRegistedDeployAction")
    String noRegistedDeployAction(String target);

    @Key("noProjectTempate")
    String noProjectTempate();

    @Key("noProjectTemplateForTarget")
    String noProjectTemplateForTarget(String target);

    @Key("chooseTechnology")
    String chooseTechnology();

    @Key("chooseTechnologyTooltip")
    String chooseTechnologyTooltip();

    @Key("choosePaas")
    String choosePaaS();

    @Key("choosePaasTooltip")
    String choosePaaSTooltip();

    @Key("noTechnologyTitle")
    String noTechnologyTitle();

    @Key("noTechnologyMessage")
    String noTechnologyMessage();

    @Key("useJRebelPlugin")
    String useJRebelPlugin();

    @Key("whatIsJRebel")
    String whatIsJRebel();

    @Key("zeroturnaround")
    String zeroturnaround();
    
    @Key("noIncorrectProjectNameMessage")
    String noIncorrectProjectNameMessage();
    
    @Key("noIncorrectProjectNameTitle")
    String noIncorrectProjectNameTitle();
    
    @Key("projectNameStartWith_Message")
    String projectNameStartWith_Message();

    /*
     * CreateProjectTemplateForm
     */
    @Key("createProjectTemplate.title")
    String createProjectTemplateTitle();

    @Key("createProjectTemplate.addFolderButton")
    String createProjectTemplateAddFolderBtn();

    @Key("createProjectTemplate.addFileButton")
    String createProjectTemplateAddFileBtn();

    @Key("createProjectTemplate.addFolder.title")
    String createProjectTemplateAddFolderTitle();

    @Key("createProjectTemplate.addFolder.text")
    String createProjectTemplateAddFolderText();

    @Key("createProjectTemplate.addFolder.default")
    String createProjectTemplateAddFolderDefault();

    @Key("createProjectTemplate.textField.name")
    String createProjectTemplateTextFieldName();

    @Key("createProjectTemplate.textArea.description")
    String createProjectTemplateTextAreaDescription();

    /*
     * TemplateServiceImpl
     */
    @Key("template.emptyProject.name")
    String templateEmptyProjectName();

    @Key("template.emptyProject.description")
    String templateEmptyProjectDescription();

    @Key("template.emptyXml.name")
    String templateEmptyXmlName();

    @Key("template.emptyXml.description")
    String templateEmptyXmlDescription();

    @Key("template.emptyHtml.name")
    String templateEmptyHtmlName();

    @Key("template.emptyHtml.description")
    String templateEmptyHtmlDescription();

    @Key("template.emptyText.name")
    String templateEmptyTextName();

    @Key("template.emptyText.description")
    String templateEmptyTextDescription();

    @Key("template.openSocialGadget.name")
    String templateOpenSocialGadgetName();

    @Key("template.openSocialGadget.description")
    String templateOpenSocialGadgetDescription();

    @Key("template.groovyRestService.name")
    String templateGroovyRestServiceName();

    @Key("template.groovyRestService.description")
    String templateGroovyRestServiceDescription();

    @Key("template.groovyTemplate.name")
    String templateGroovyTemplateName();

    @Key("template.groovyTemplate.description")
    String templateGroovyTemplateDescription();

    /*
     * CreateProjectTemplatePresenter
     */
    @Key("button.addFile")
    String addFileButton();

    @Key("createProjectTemplate.enterNameFirst")
    String createProjectTemplateEnterNameFirst();

    @Key("createProjectTemplate.valueCantBeEmpty")
    String createProjectTemplateValueCantBeEmpty();

    @Key("createProjectTemplate.folderAlreadyExists")
    String createProjectTemplateFolderAlreadyExists();

    @Key("createProjectTemplate.projectAlreadyExists")
    String createProjectTemplateProjectAlreadyExists();

    @Key("createProjectTemplate.fileAlreadyExists")
    String createProjectTemplateFileAlreadyExists();

    @Key("createProjectTemplate.templateCreated")
    String createProjectTemplateCreated();

    /*
     * CreateFileFromTemplatePresenter
     */
    @Key("createFileFromTemplate.enterFileNameFirst")
    String createFileFromTemplateEnterFileNameFirst();

    /*
     * SaveAsTemplatePresenter
     */
    @Key("saveAsTemplate.enterNameFirst")
    String saveAsTemplateEnterNameFirst();

    @Key("saveAsTemplate.templateCreated")
    String saveAsTemplateCreated();

    @Key("saveAsTemplate.openFileForTemplate")
    String saveAsTemplateOpenFileForTemplate();

    /*
     * CreateFileFromTemplatePresenter
     */
    @Key("template.askDeleteTemplateDialog.title")
    String askDeleteTemplateDialogTitle();

    /*
     * CreateProjectFromTemplatePresenter
     */
    @Key("createProjectFromTemplate.askDeleteSeveralTemplates")
    String createFromTemplateAskDeleteSeveralTemplates();

}
