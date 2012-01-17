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

import com.google.gwt.http.client.RequestException;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.ide.client.IDE;
import org.exoplatform.ide.client.IDELoader;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.model.configuration.IDEConfigurationLoader;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.model.template.marshal.TemplateListUnmarshaller;
import org.exoplatform.ide.client.project.create.CreateProjectFromTemplatePresenter;
import org.exoplatform.ide.client.project.create.empty.CreateEmptyProjectPresenter;
import org.exoplatform.ide.client.project.deploy.DeployProjectToPaasPresenter;
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
      IDE.getInstance().addControl(new ProjectPaaSControl());

      new CreateProjectFromTemplatePresenter();

      new DeployProjectToPaasPresenter();

      new ShowProjectsPresenter();

      new TinyProjectExplorerPresenter();

      new CreateEmptyProjectPresenter();

      new ProjectPropertiesPresenter();

      new ProjectCreatedEventHandler();

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
         new TemplateServiceImpl(IDELoader.getInstance(), event.getConfiguration().getRegistryURL() + "/"
            + "exo:applications" + "/" + IDEConfigurationLoader.APPLICATION_NAME, event.getConfiguration().getContext());
      }
   }

   private void moveTemplatesFromRegistryToPlainText()
   {
      try
      {
         TemplateService.getInstance().getTemplates(
            new AsyncRequestCallback<List<Template>>(new TemplateListUnmarshaller(new ArrayList<Template>()))
            {
               @Override
               protected void onSuccess(List<Template> result)
               {
                  if (result.size() == 0)
                  {
                     IDE.fireEvent(new TemplatesMigratedEvent());
                     callback.onTemplatesMigrated();
                  }
                  else
                  {
                     saveTemplatesOnServer(result);
                  }
               }

               @Override
               protected void onFailure(Throwable exception)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(exception));
               }
            });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
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

      try
      {
         TemplateService.getInstance().addFileTemplateList(fileTemplates, new AsyncRequestCallback<String>()
         {
            @Override
            protected void onSuccess(String result)
            {
               try
               {
                  TemplateService.getInstance().addProjectTemplateList(projectTemplates,
                     new AsyncRequestCallback<String>()
                     {
                        @Override
                        protected void onSuccess(String result)
                        {
                           deleteTemplatesFromRegistry();
                        }

                        @Override
                        protected void onFailure(Throwable exception)
                        {
                           IDE.fireEvent(new ExceptionThrownEvent(exception));
                        }
                     });
               }
               catch (RequestException e)
               {
                  IDE.fireEvent(new ExceptionThrownEvent(e));
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
   }

   protected void deleteTemplatesFromRegistry()
   {
      try
      {
         TemplateService.getInstance().deleteTemplatesFromRegistry(new AsyncRequestCallback<String>()
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

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e));
      }
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
}
