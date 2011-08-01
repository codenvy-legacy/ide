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
package org.exoplatform.ide.client.samples.ide;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateList;

import java.util.ArrayList;
import java.util.List;

/**
 * Create default ide project template.
 * 
 * @author <a href="oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id: DefaultIdeProject.java May 12, 2011 3:23:35 PM vereshchaka $
 *
 */
public class DefaultIdeProject
{

   private TemplateList templateList = new TemplateList();

   private ProjectTemplate defaultSamplePrj = new ProjectTemplate("ide-project");

   private FolderTemplate uiFolder = new FolderTemplate("UI");

   private FolderTemplate dataFolder = new FolderTemplate("data");

   private FolderTemplate businessLogicFolder = new FolderTemplate("logic");

   public DefaultIdeProject()
   {
      businessLogicFolder.setChildren(new ArrayList<Template>());
      uiFolder.setChildren(new ArrayList<Template>());
      dataFolder.setChildren(new ArrayList<Template>());
      defaultSamplePrj.getChildren().add(dataFolder);
      defaultSamplePrj.getChildren().add(businessLogicFolder);
      defaultSamplePrj.getChildren().add(uiFolder);
      createDataObjectFile();
      createPojoFile();
      createProjectGadget();
      createProjectRestService();
      defaultSamplePrj.setDescription("Sample Demo Project");
      defaultSamplePrj.setDefault(true);
      templateList.getTemplates().add(defaultSamplePrj);

   }
   
   private void createProjectGadget() 
   {
      String content = DefaultIdeSample.INSTANCE.getGreetingGoogleGadgetSource().getText();
      FileTemplate gadgetFileTemplate =
         new FileTemplate(MimeType.GOOGLE_GADGET, "GreetingGoogleGadget.xml", "Google Gadget with request to service",
            content, true);
      gadgetFileTemplate.setFileName("GreetingGoogleGadget.xml");
      templateList.getTemplates().add(gadgetFileTemplate);
      uiFolder.getChildren().add(gadgetFileTemplate);
   }

   private void createProjectRestService() 
   {
      String content = DefaultIdeSample.INSTANCE.getGreetingRESTServiceSource().getText();
      FileTemplate restServiceTemplate =
         new FileTemplate(MimeType.GROOVY_SERVICE, "GreetingRESTService.grs", "Template for greeting REST service",
            content, true);
      restServiceTemplate.setFileName("GreetingRESTService.grs");
      templateList.getTemplates().add(restServiceTemplate);
      businessLogicFolder.getChildren().add(restServiceTemplate);
   }

   private void createDataObjectFile() 
   {
      String content = DefaultIdeSample.INSTANCE.getDataObjectSource().getText();
      FileTemplate template =
         new FileTemplate(MimeType.CHROMATTIC_DATA_OBJECT, "DataObject.groovy", "Data Object", content,
            true);
      template.setFileName("DataObject.groovy");
      templateList.getTemplates().add(template);
      dataFolder.getChildren().add(template);
   }
   
   private void createPojoFile() 
   {
      String content = DefaultIdeSample.INSTANCE.getPojoSource().getText();
      FileTemplate template =
         new FileTemplate(MimeType.APPLICATION_GROOVY, "Pogo.groovy", "POGO file template", content,
            true);
      template.setFileName("Pogo.groovy");
      templateList.getTemplates().add(template);
      dataFolder.getChildren().add(template);
   }

   public List<Template> getTemplateList()
   {
      return templateList.getTemplates();
   }
   
   public ProjectTemplate getProjectTemplate()
   {
      return defaultSamplePrj;
   }

}
