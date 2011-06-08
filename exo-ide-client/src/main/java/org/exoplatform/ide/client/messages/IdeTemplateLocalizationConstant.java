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

import com.google.gwt.i18n.client.Constants;

/**
 * Interface to represent the constants for template and project forms contained in resource bundle:
 *      'IdeTemplateLocalizationConstant.properties'.
 *      
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: TemplateConstant.java Jun 2, 2011 2:59:52 PM vereshchaka $
 *
 */
public interface IdeTemplateLocalizationConstant extends Constants
{
   
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
    * CreateFileFromTemplateForm
    * CreateFileFromTemplateView
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
   
   @Key("createProjectFromTemplate.nameField")
   String createProjectFromTemplateName();
   
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
   
   @Key("template.googleGadget.name")
   String templateGoogleGadgetName();
   
   @Key("template.googleGadget.description")
   String templateGoogleGadgetDescription();
   
   @Key("template.groovyRestService.name")
   String templateGroovyRestServiceName();
   
   @Key("template.groovyRestService.description")
   String templateGroovyRestServiceDescription();
   
   @Key("template.groovyTemplate.name")
   String templateGroovyTemplateName();
   
   @Key("template.groovyTemplate.description")
   String templateGroovyTemplateDescription();
   
   @Key("template.netvibesWidget.name")
   String templateNetvibesWidgetName();
   
   @Key("template.netvibesWidget.description")
   String templateNetvibesWidgetDescription();
   
   @Key("template.netvibesWidgetFlash.name")
   String templateNetvibesWidgetFlashName();
   
   @Key("template.netvibesWidgetFlash.description")
   String templateNetvibesWidgetFlashDescription();
   
   @Key("template.netvibesWidgetChart.name")
   String templateNetvibesWidgetChartName();
   
   @Key("template.netvibesWidgetChart.description")
   String templateNetvibesWidgetChartDescription();
   
   @Key("template.netvibesWidgetTabView.name")
   String templateNetvibesWidgetTabViewName();
   
   @Key("template.netvibesWidgetTabView.description")
   String templateNetvibesWidgetTabViewDescription();
   
   @Key("template.netvibesWidgetSampleBlogPost.name")
   String templateNetvibesWidgetSampleBlogPostName();
   
   @Key("template.netvibesWidgetSampleBlogPost.description")
   String templateNetvibesWidgetSampleBlogPostDescription();
   
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
   
   @Key("saveAsTemplate.templateAlreadyExists")
   String saveAsTemplateTemplateAlreadyExists();
   
   @Key("saveAsTemplate.templateCreated")
   String saveAsTemplateCreated();
   
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
