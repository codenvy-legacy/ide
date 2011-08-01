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
package org.exoplatform.ide.client.samples.linkedin;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateList;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
*/
public class LinkedinContactsProject
{

   private TemplateList templateList = new TemplateList();

   private ProjectTemplate linkedinContactsProject = new ProjectTemplate("linkedin-contacts-project");

   private FolderTemplate skinFolder = new FolderTemplate("skin");

   private FolderTemplate libFolder = new FolderTemplate("lib");

   private FolderTemplate logicFolder = new FolderTemplate("logic");

   public LinkedinContactsProject()
   {
      logicFolder.setChildren(new ArrayList<Template>());
      skinFolder.setChildren(new ArrayList<Template>());
      libFolder.setChildren(new ArrayList<Template>());
      linkedinContactsProject.getChildren().add(libFolder);
      linkedinContactsProject.getChildren().add(logicFolder);
      linkedinContactsProject.getChildren().add(skinFolder);
      createReadme();
      createMobileHtml();
      createCachManifest();
      createLibFolder();
      createContactLogic();
      createCss();
      linkedinContactsProject.setDescription("Linkedin Contacts Demo Project");
      linkedinContactsProject.setDefault(true);
      templateList.getTemplates().add(linkedinContactsProject);

   }

   
   private void createCss() 
   {
      String content = LinkedinContactsSample.INSTANCE.getExomobileCss().getText();
      FileTemplate cssTemplate =
         new FileTemplate(MimeType.TEXT_CSS, "exomobile.css", "Exomobile CSS", content, true);
      cssTemplate.setFileName("exomobile.css");
      templateList.getTemplates().add(cssTemplate);
      skinFolder.getChildren().add(cssTemplate);
   }

   private void createLibFolder() 
   {
      libJtouchMinCss();
      libJqtouchMinJs();
      libJqueryMinJs();
      libLinkedinJs();
      libTemplateJs();
   }


   private void libTemplateJs()
   {
      String content = LinkedinContactsSample.INSTANCE.getLibTemplateJs().getText();
      FileTemplate template =
         new FileTemplate(MimeType.APPLICATION_JAVASCRIPT, "template.js", "template js", content, true);
      template.setFileName("template.js");
      templateList.getTemplates().add(template);
      libFolder.getChildren().add(template);
   }


   private void libJqueryMinJs()
   {
      String content = LinkedinContactsSample.INSTANCE.getLibJqueryMinJs().getText();
      FileTemplate template =
         new FileTemplate(MimeType.APPLICATION_JAVASCRIPT, "jquery.1.4.2.min.js", "jquery 1.4.2 min ", content, true);
      template.setFileName("jquery.1.4.2.min.js");
      templateList.getTemplates().add(template);
      libFolder.getChildren().add(template);
   }
   
   private void libLinkedinJs()
   {
      String content = LinkedinContactsSample.INSTANCE.getLibLinkedinJs().getText();
      FileTemplate template =
         new FileTemplate(MimeType.APPLICATION_JAVASCRIPT, "linkedin.js", "linkedin ", content, true);
      template.setFileName("linkedin.js");
      templateList.getTemplates().add(template);
      libFolder.getChildren().add(template);
   }


   private void libJqtouchMinJs()
   {
      String content = LinkedinContactsSample.INSTANCE.getLibJqtouchMinJs().getText();
      FileTemplate template =
         new FileTemplate(MimeType.APPLICATION_JAVASCRIPT, "jqtouch.min.js", "jqtouch min js", content, true);
      template.setFileName("jqtouch.min.js");
      templateList.getTemplates().add(template);
      libFolder.getChildren().add(template);
   }


   private void libJtouchMinCss()
   {
      String content = LinkedinContactsSample.INSTANCE.getLibJqtouchMinCss().getText();
      FileTemplate template =
         new FileTemplate(MimeType.TEXT_CSS, "jqtouch.min.css", "jqtouch css", content, true);
      template.setFileName("jqtouch.min.css");
      templateList.getTemplates().add(template);
      libFolder.getChildren().add(template);
   }

   private void createMobileHtml() 
   {
      String content = LinkedinContactsSample.INSTANCE.getMobileSource().getText();
      FileTemplate template =
         new FileTemplate(MimeType.TEXT_HTML, "mobile.html", "linkedin mobile html",
            content, true);
      template.setFileName("mobile.html");
      templateList.getTemplates().add(template);
      linkedinContactsProject.getChildren().add(template);
   }

   private void createReadme() 
   {
      String content = LinkedinContactsSample.INSTANCE.getReadme().getText();
      FileTemplate template =
         new FileTemplate(MimeType.TEXT_PLAIN, "readme-linkedin-contacts.txt", "linkedin readme file",
            content, true);
      template.setFileName("readme-linkedin-contacts.txt");
      templateList.getTemplates().add(template);
      linkedinContactsProject.getChildren().add(template);
   }
   
   private void createCachManifest() 
   {
      String content = LinkedinContactsSample.INSTANCE.getCachManifest().getText();
      FileTemplate template =
         new FileTemplate(MimeType.TEXT_PLAIN, "cache.manifest", "linkedin cache manifest file",
            content, true);
      template.setFileName("cache.manifest");
      templateList.getTemplates().add(template);
      linkedinContactsProject.getChildren().add(template);
   }

  
   private void createContactLogic() 
   {
      String content = LinkedinContactsSample.INSTANCE.getLogicContactsJs().getText();
      FileTemplate template =
         new FileTemplate(MimeType.APPLICATION_JAVASCRIPT, "contacts.js", "linkedin contacts", content, true);
      template.setFileName("contacts.js");
      templateList.getTemplates().add(template);
      logicFolder.getChildren().add(template);
      logicOfflineJs();
   }
   
   private void logicOfflineJs()
   {
      String content = LinkedinContactsSample.INSTANCE.getLogicOfflineJs().getText();
      FileTemplate template =
         new FileTemplate(MimeType.APPLICATION_JAVASCRIPT, "offline.js", "offline js", content, true);
      template.setFileName("offline.js");
      templateList.getTemplates().add(template);
      logicFolder.getChildren().add(template);
   }

   public List<Template> getTemplateList()
   {
      return templateList.getTemplates();
   }
   
   public ProjectTemplate getProjectTemplate()
   {
      return linkedinContactsProject;
   }

}
