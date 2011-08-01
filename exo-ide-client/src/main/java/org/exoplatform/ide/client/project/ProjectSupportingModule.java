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
package org.exoplatform.ide.client.project;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.initializer.RegistryConstants;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.configuration.IDEConfigurationLoader;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FileTemplateList;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplateList;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateCreatedCallback;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.project.control.CreateProjectFromTemplateControl;
import org.exoplatform.ide.client.project.control.CreateProjectTemplateControl;
import org.exoplatform.ide.client.project.event.CreateProjectTemplateEvent;
import org.exoplatform.ide.client.project.event.CreateProjectTemplateHandler;
import org.exoplatform.ide.client.template.MigrateTemplatesEvent;
import org.exoplatform.ide.client.template.MigrateTemplatesHandler;
import org.exoplatform.ide.client.template.TemplatesMigratedCallback;
import org.exoplatform.ide.client.template.TemplatesMigratedEvent;
import org.exoplatform.ide.client.template.TemplatesMigratedHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectSupportingModule implements ItemsSelectedHandler, ConfigurationReceivedSuccessfullyHandler,
   CreateProjectTemplateHandler, MigrateTemplatesHandler, TemplatesMigratedHandler
{
   
   private HandlerManager eventBus;
   
   private String restServiceContext;
   
   private List<Item> selectedItems = new ArrayList<Item>();
   
   private TemplatesMigratedCallback callback;
   
   private boolean isTemplatesMigrated = false;

   public ProjectSupportingModule(HandlerManager eventBus) {
      this.eventBus = eventBus;
      
      eventBus.fireEvent(new RegisterControlEvent(new CreateProjectFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateProjectTemplateControl()));      
      
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      
      eventBus.addHandler(CreateProjectTemplateEvent.TYPE, this);
      eventBus.addHandler(MigrateTemplatesEvent.TYPE, this);
      eventBus.addHandler(TemplatesMigratedEvent.TYPE, this);
      
      new CreateProjectFromTemplatePresenter(eventBus);
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      restServiceContext = event.getConfiguration().getContext();
      if (TemplateService.getInstance() == null)
      {
         new TemplateServiceImpl(eventBus, IDELoader.getInstance(), event.getConfiguration().getRegistryURL() + "/"
            + RegistryConstants.EXO_APPLICATIONS + "/" + IDEConfigurationLoader.APPLICATION_NAME, event.getConfiguration().getContext());
      }
      //only for test, will be removed
//      saveSomeTemplatesToRegistry();
   }
   
   private void moveTemplatesFromRegistryToPlainText()
   {
      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>(eventBus)
      {
         @Override
         protected void onSuccess(TemplateList result)
         {
            if (result.getTemplates().size() == 0)
            {
               eventBus.fireEvent(new TemplatesMigratedEvent());
               callback.onTemplatesMigrated();
            }
            else
            {
               saveTemplatesOnServer(result.getTemplates());
            }
         }
      });
   }
   
   private void saveTemplatesOnServer(List<Template> templates)
   {
      if (templates.isEmpty())
         return;
      
      List<FileTemplate> fileTemplates = new ArrayList<FileTemplate>();
      final List<ProjectTemplate> projectTemplates = new ArrayList<ProjectTemplate>();
      for (Template template : templates)
      {
         template.setDefault(false);
         if (template instanceof FileTemplate)
         {
            fileTemplates.add((FileTemplate)template);
         }
         else if (template instanceof ProjectTemplate)
         {
            projectTemplates.add((ProjectTemplate)template);
         }
      }
      
      TemplateService.getInstance().addFileTemplateList(fileTemplates, new AsyncRequestCallback<String>(eventBus)
      {
         @Override
         protected void onSuccess(String result)
         {
            TemplateService.getInstance().addProjectTemplateList(projectTemplates, new AsyncRequestCallback<String>(eventBus)
            {
               @Override
               protected void onSuccess(String result)
               {
                  TemplateService.getInstance().deleteTemplatesFromRegistry(new AsyncRequestCallback<String>(eventBus)
                  {
                     @Override
                     protected void onSuccess(String result)
                     {
                        eventBus.fireEvent(new TemplatesMigratedEvent());
                        callback.onTemplatesMigrated();
                     }
                  });
               }
            });
         }
      });
   }
   
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }   

   @Override
   public void onCreateProjectTemplate(CreateProjectTemplateEvent event)
   {
      if (isTemplatesMigrated)
      {
         createProjectTemplate();
      }
      else
      {
         eventBus.fireEvent(new MigrateTemplatesEvent(new TemplatesMigratedCallback()
         {
            @Override
            public void onTemplatesMigrated()
            {
               createProjectTemplate();
            }
         }));
      }
   }
   
   private void createProjectTemplate()
   {
      final List<Template> templates = new ArrayList<Template>();
      TemplateService.getInstance().getProjectTemplateList(new AsyncRequestCallback<ProjectTemplateList>(eventBus)
      {
         @Override
         protected void onSuccess(ProjectTemplateList result)
         {
            templates.addAll(result.getProjectTemplates());
            TemplateService.getInstance().getFileTemplateList(new AsyncRequestCallback<FileTemplateList>()
            {

               @Override
               protected void onSuccess(FileTemplateList result)
               {
                  templates.addAll(result.getFileTemplates());
                  new CreateProjectTemplateForm(eventBus, templates);
               }
            });
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.template.MigrateTemplatesHandler#onMigrateTemplates(org.exoplatform.ide.client.template.MigrateTemplatesEvent)
    */
   @Override
   public void onMigrateTemplates(MigrateTemplatesEvent event)
   {
      this.callback = event.getTemplatesMigratedCallback();
      moveTemplatesFromRegistryToPlainText();
   }
   
   //----only for test, will be removed.
   /**
    * Method, for testing migrating templates from registry to plain text file.
    */
   private void saveSomeTemplatesToRegistry()
   {
      FileTemplate ft1 = new FileTemplate(MimeType.APPLICATION_XML, "abc", "hello", "some content", null);
      final FileTemplate ft2 = new FileTemplate(MimeType.APPLICATION_XML, "bcd", "second", "lalala", null);
      final ProjectTemplate pt1 = new ProjectTemplate("hh1");
      pt1.getChildren().add(new FileTemplate("abc", "hello"));
      FolderTemplate fot1 = new FolderTemplate("ddd");
      pt1.getChildren().add(fot1);
      TemplateService.getInstance().createTemplate(ft1, new TemplateCreatedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            TemplateService.getInstance().createTemplate(ft2, new TemplateCreatedCallback()
            {
               @Override
               protected void onSuccess(Template result)
               {
                  TemplateService.getInstance().createTemplate(pt1, new TemplateCreatedCallback()
                  {
                     @Override
                     protected void onSuccess(Template result)
                     {
                        moveTemplatesFromRegistryToPlainText();
                     }
                  });
               }
            });
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.template.TemplatesMigratedHandler#onTemplatesMigrated(org.exoplatform.ide.client.template.TemplatesMigratedEvent)
    */
   @Override
   public void onTemplatesMigrated(TemplatesMigratedEvent event)
   {
      isTemplatesMigrated = true;
   }
   
}
