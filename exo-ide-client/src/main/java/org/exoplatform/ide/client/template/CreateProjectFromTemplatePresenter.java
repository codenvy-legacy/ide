/*
 * Copyright (C) 2010 eXo Platform SAS.
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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileContentSaveCallback;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.FolderCreateCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.NodeTypeUtil;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.extension.groovy.client.classpath.EnumSourceType;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathEntry;
import org.exoplatform.ide.extension.groovy.client.classpath.GroovyClassPathUtil;
import org.exoplatform.ide.extension.groovy.client.event.ConfigureBuildPathEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateProjectFromTemplatePresenter extends AbstractCreateFromTemplatePresenter<ProjectTemplate>
{
   private List<FileTemplate> fileTemplateList = new ArrayList<FileTemplate>();
   
   private List<Folder> folderList = new ArrayList<Folder>();
   
   private List<File> fileList = new ArrayList<File>();
   
   private int itemsCreated = 0;
   
   private String baseHref;
   
   private Folder projectFolder;
   
   private String restContext;
   
   public CreateProjectFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems, List<Template> templateList, String restContext)
   {
      super(eventBus, selectedItems);
      this.restContext = restContext;
      this.templateList = new ArrayList<ProjectTemplate>();
      
      if (selectedItems != null && selectedItems.size() != 0)
      {
         Item item = selectedItems.get(0);

         baseHref = item.getHref();
         if (item instanceof File)
         {
            baseHref = baseHref.substring(0, baseHref.lastIndexOf("/") + 1);
         }
      }
      
      initFileAndProjectTemplates(templateList);
   }
   
   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#updateTemplateList(java.util.List)
    */
   @Override
   void updateTemplateList(List<Template> templates)
   {
      templateList.clear();
      fileTemplateList.clear();
      
      initFileAndProjectTemplates(templates);
      
      display.getTemplateListGrid().setValue(templateList);
      if (templateList != null && templateList.size() > 0)
      {
         display.selectLastTemplate();
      }
   }
   
   /**
    * Split list of templates on two lists: 
    * list of project templates and file templates.
    * 
    * @param templates
    */
   private void initFileAndProjectTemplates(List<Template> templates)
   {
      for (Template template : templates)
      {
         if (template instanceof ProjectTemplate)
         {
            templateList.add((ProjectTemplate)template);
         }
         else if (template instanceof FileTemplate)
         {
            fileTemplateList.add((FileTemplate)template);
         }
      }
   }
   
   private File createFileFromTemplate(FileTemplate fileTemplate, String href)
   {
      for (FileTemplate fTemplate : fileTemplateList)
      {
         if (fTemplate.getName().equals(fileTemplate.getName()))
         {
            String contentType = fTemplate.getMimeType();

            File newFile = new File(href + fileTemplate.getFileName());
            newFile.setContentType(contentType);
            newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
            newFile.setIcon(ImageUtil.getIcon(contentType));
            newFile.setNewFile(true);
            newFile.setContent(fTemplate.getContent());
            
            return newFile;
         }
      }
      
      return null;
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#setNewInstanceName()
    */
   @Override
   void setNewInstanceName()
   {
      display.getNameField().setValue(selectedTemplates.get(0).getName());
   }

   /**
    * @see org.exoplatform.ide.client.template.AbstractCreateFromTemplatePresenter#submitTemplate()
    */
   @Override
   void submitTemplate()
   {
      String projectName = display.getNameField().getValue();

      ProjectTemplate selectedTemplate = selectedTemplates.get(0);
      
      FileTemplate classPathTemplate = new FileTemplate(MimeType.APPLICATION_JSON, ".groovyclasspath", "", "", null);
      selectedTemplate.getChildren().add(classPathTemplate);
      
      build(selectedTemplate.getChildren(), baseHref + projectName + "/");
      projectFolder = new Folder(baseHref + projectName + "/");
      fileList.add(getClasspathFile(baseHref + projectName + "/"));
      
      createFolder(projectFolder);
   }
   
   private void build(List<Template>templates, String href)
   {
      if (templates == null || templates.size() == 0)
      {
         return;
      }
      
      for (Template template : templates)
      {
         if (template instanceof FolderTemplate)
         {
            FolderTemplate projectTemplate = (FolderTemplate)template;
            
            folderList.add(new Folder(href + projectTemplate.getName() + "/"));
            build(projectTemplate.getChildren(), href + projectTemplate.getName() + "/");
         }
         else if (template instanceof FileTemplate)
         {
            FileTemplate fileTemplate = (FileTemplate)template;
            File file = createFileFromTemplate(fileTemplate, href);
            if (file != null)
            {
               fileList.add(file);
            }
         }
      }
   }
   
   /**
    * Get classpath file.
    * 
    * @param href href
    * @return {@link File} classpath file
    */
   private File getClasspathFile(String href)
   {
      href = (href.endsWith("/")) ? href : href + "/";
      String contentType = MimeType.APPLICATION_JSON;
      File newFile = new File(href + ".groovyclasspath");
      newFile.setContentType(contentType);
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
      newFile.setIcon(ImageUtil.getIcon(contentType));
      newFile.setNewFile(true);
     
      String path = GroovyClassPathUtil.formPathFromHref(href, restContext);
      GroovyClassPathEntry projectClassPathEntry = GroovyClassPathEntry.build(EnumSourceType.DIR.getValue(), path);
      List<GroovyClassPathEntry> groovyClassPathEntries = new ArrayList<GroovyClassPathEntry>();
      groovyClassPathEntries.add(projectClassPathEntry);
      
      String content = GroovyClassPathUtil.getClassPathJSON(groovyClassPathEntries);
      newFile.setContent(content);
      return newFile;
   }
   
   
   private void createFolder(Folder folder)
   {
      VirtualFileSystem.getInstance().createFolder(folder, new FolderCreateCallback(eventBus)
      {
         public void onResponseReceived(Request request, Response response)
         {
            onFolderCreated(this.getFolder());
         }
      });
   }
   
   private void onFolderCreated(Folder folder)
   {
      if (itemsCreated < folderList.size())
      {
         createFolder(folderList.get(itemsCreated));
         itemsCreated++;
         return;
      }
      if (fileList.size() == 0)
      {
        finishProjectCreation();
         return;
      }
      
      saveFileContent(fileList.get(0));
      itemsCreated = 1;
   }
   
   private void saveFileContent(File file)
   {
      VirtualFileSystem.getInstance().saveContent(file, null, new FileContentSaveCallback(eventBus)
      {
         public void onResponseReceived(Request request, Response response)
         {
            if (itemsCreated < fileList.size())
            {
               saveFileContent(fileList.get(itemsCreated));
               itemsCreated++;
               return;
            }
            finishProjectCreation();
         }
      });
   }

   /**
    * Do actions when project is created.
    */
   private void finishProjectCreation()
   {
      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(baseHref), projectFolder));
      display.closeForm();
      eventBus.fireEvent(new ConfigureBuildPathEvent(projectFolder.getHref()));
   }
}
