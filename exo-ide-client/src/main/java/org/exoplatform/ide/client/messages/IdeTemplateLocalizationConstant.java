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
   
}
