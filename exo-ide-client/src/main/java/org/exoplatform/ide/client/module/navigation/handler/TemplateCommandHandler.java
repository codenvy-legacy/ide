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
package org.exoplatform.ide.client.module.navigation.handler;

import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.TemplateListReceivedCallback;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateHandler;
import org.exoplatform.ide.client.template.CreateFileFromTemplateForm;
import org.exoplatform.ide.client.template.CreateFileFromTemplatePresenter;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;
import org.exoplatform.ide.client.template.CreateProjectFromTemplateForm;
import org.exoplatform.ide.client.template.CreateProjectFromTemplatePresenter;
import org.exoplatform.ide.client.template.CreateProjectTemplateForm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 * Handler for "Create file from template" command.
 * 
 * Handler "Create file from template" event and open form.
 * 
 * Also handlers events to store selected items and opened files.
 * This data is needed for "Create file from template" presenter.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class TemplateCommandHandler implements CreateFileFromTemplateHandler, ItemsSelectedHandler, 
EditorFileOpenedHandler, EditorFileClosedHandler, CreateProjectFromTemplateHandler, ConfigurationReceivedSuccessfullyHandler,
CreateProjectTemplateHandler
{

   private HandlerManager eventBus;

   private List<Item> selectedItems = new ArrayList<Item>();
   
   private Map<String, File> openedFiles = new HashMap<String, File>();
   
   private String restContext;

   public TemplateCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;

      eventBus.addHandler(CreateFileFromTemplateEvent.TYPE, this);
      eventBus.addHandler(CreateProjectFromTemplateEvent.TYPE, this);
      eventBus.addHandler(CreateProjectTemplateEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(EditorFileOpenedEvent.TYPE, this);
      eventBus.addHandler(EditorFileClosedEvent.TYPE, this);
   }

   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      restContext = event.getConfiguration().getContext();
   }
   
   /**
    * @see org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateHandler#onCreateFileFromTemplate(org.exoplatform.ide.client.module.navigation.event.newitem.CreateFileFromTemplateEvent)
    */
   public void onCreateFileFromTemplate(CreateFileFromTemplateEvent event)
   {
      TemplateService.getInstance().getTemplates(new TemplateListReceivedCallback(eventBus)
      {
         @Override
         public void onTemplateListReceived()
         {
            CreateFileFromTemplatePresenter createFilePresenter =
               new CreateFileFromTemplatePresenter(eventBus, selectedItems, this.getTemplateList().getTemplates(), openedFiles);
            CreateFromTemplateDisplay<FileTemplate> createFileDisplay =
               new CreateFileFromTemplateForm(eventBus, this.getTemplateList().getTemplates(), createFilePresenter);
            createFilePresenter.bindDisplay(createFileDisplay);
         }
      });
   }


   /**
    * @see org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateHandler#onCreateProjectFromTemplate(org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateEvent)
    */
   public void onCreateProjectFromTemplate(CreateProjectFromTemplateEvent event)
   {
      TemplateService.getInstance().getTemplates(new TemplateListReceivedCallback(eventBus)
      {
         @Override
         public void onTemplateListReceived()
         {
            CreateProjectFromTemplatePresenter createProjectPresenter =
               new CreateProjectFromTemplatePresenter(eventBus, selectedItems, this.getTemplateList().getTemplates(), restContext);
            CreateFromTemplateDisplay<ProjectTemplate> createProjectDisplay =
               new CreateProjectFromTemplateForm(eventBus, this.getTemplateList().getTemplates(), createProjectPresenter);
            createProjectPresenter.bindDisplay(createProjectDisplay);
         }
      });
   }
   
   /**
    * @see org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateHandler#onCreateProjectTemplate(org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateEvent)
    */
   public void onCreateProjectTemplate(CreateProjectTemplateEvent event)
   {
      TemplateService.getInstance().getTemplates(new TemplateListReceivedCallback(eventBus)
      {
         @Override
         public void onTemplateListReceived()
         {
            new CreateProjectTemplateForm(eventBus, this.getTemplateList().getTemplates());
         }
      });
   }
   
}
