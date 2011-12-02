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

import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.model.configuration.IDEConfigurationLoader;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.FolderTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateCreatedCallback;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.project.create.CreateProjectPresenter;
import org.exoplatform.ide.client.project.explorer.TinyProjectExplorerPresenter;
import org.exoplatform.ide.client.project.list.ShowProjectsPresenter;
import org.exoplatform.ide.client.project.properties.ProjectPropertiesPresenter;
import org.exoplatform.ide.client.template.MigrateTemplatesEvent;
import org.exoplatform.ide.client.template.MigrateTemplatesHandler;
import org.exoplatform.ide.client.template.TemplatesMigratedCallback;
import org.exoplatform.ide.client.template.TemplatesMigratedEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectSupportingModule implements ConfigurationReceivedSuccessfullyHandler, MigrateTemplatesHandler
{

   private TemplatesMigratedCallback callback;

   public ProjectSupportingModule()
   {

      //      new CreateProjectFromTemplatePresenter();

      //      new CreateProjectTemplatePresenter();

      new ShowProjectsPresenter();

      new TinyProjectExplorerPresenter();

      new CreateProjectPresenter();
      
      new ProjectPropertiesPresenter();
      
      IDE.getInstance().addControlsFormatter(new ProjectMenuItemFormatter());

      IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      IDE.addHandler(MigrateTemplatesEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      if (TemplateService.getInstance() == null)
      {
         new TemplateServiceImpl(IDE.eventBus(), IDELoader.getInstance(), event.getConfiguration().getRegistryURL()
            + "/" + "exo:applications" + "/" + IDEConfigurationLoader.APPLICATION_NAME, event.getConfiguration()
            .getContext());
      }
      //only for test, will be removed
      //      saveSomeTemplatesToRegistry();
   }

   private void moveTemplatesFromRegistryToPlainText()
   {
      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>(IDE.eventBus())
      {
         @Override
         protected void onSuccess(TemplateList result)
         {
            if (result.getTemplates().size() == 0)
            {
               IDE.fireEvent(new TemplatesMigratedEvent());
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

      TemplateService.getInstance().addFileTemplateList(fileTemplates, new AsyncRequestCallback<String>(IDE.eventBus())
      {
         @Override
         protected void onSuccess(String result)
         {
            TemplateService.getInstance().addProjectTemplateList(projectTemplates,
               new AsyncRequestCallback<String>(IDE.eventBus())
               {
                  @Override
                  protected void onSuccess(String result)
                  {
                     TemplateService.getInstance().deleteTemplatesFromRegistry(
                        new AsyncRequestCallback<String>(IDE.eventBus())
                        {
                           @Override
                           protected void onSuccess(String result)
                           {
                              IDE.fireEvent(new TemplatesMigratedEvent());
                              if (callback != null)
                              {
                                 callback.onTemplatesMigrated();
                              }
                           }
                        });
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
   @SuppressWarnings("unused")
   private void saveSomeTemplatesToRegistry()
   {
      FileTemplate ft1 = new FileTemplate(MimeType.APPLICATION_XML, "abc", "hello", "some content", null);
      final FileTemplate ft2 = new FileTemplate(MimeType.APPLICATION_XML, "bcd", "second", "lalala", null);
      final ProjectTemplate pt1 = new ProjectTemplate("hh1");
      pt1.setDescription("sample project");
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

}
