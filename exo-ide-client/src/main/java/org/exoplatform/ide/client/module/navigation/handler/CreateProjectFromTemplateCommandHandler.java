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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateHandler;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;
import org.exoplatform.ide.client.template.CreateProjectFromTemplateForm;
import org.exoplatform.ide.client.template.CreateProjectFromTemplatePresenter;

import com.google.gwt.event.shared.HandlerManager;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateProjectFromTemplateCommandHandler implements CreateProjectFromTemplateHandler, TemplateListReceivedHandler, ExceptionThrownHandler, ItemsSelectedHandler, ConfigurationReceivedSuccessfullyHandler
{
   
   private HandlerManager eventBus;
   
   private Handlers handlers;
   
   private List<Item> selectedItems = new ArrayList<Item>();   
   
   private String restContext;
   
   public CreateProjectFromTemplateCommandHandler(HandlerManager eventBus) {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      eventBus.addHandler(CreateProjectFromTemplateEvent.TYPE, this);
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
   }
   
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }   
   
   /**
    * @see org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateHandler#onCreateProjectFromTemplate(org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateEvent)
    */
   public void onCreateProjectFromTemplate(CreateProjectFromTemplateEvent event)
   {
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
      
      TemplateService.getInstance().getTemplates();
   }

   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      handlers.removeHandlers();
      
      TemplateList templateList = event.getTemplateList();

      CreateProjectFromTemplatePresenter createProjectPresenter =
         new CreateProjectFromTemplatePresenter(eventBus, selectedItems, templateList.getTemplates(), restContext);
      CreateFromTemplateDisplay<ProjectTemplate> createProjectDisplay =
         new CreateProjectFromTemplateForm(eventBus, templateList.getTemplates(), createProjectPresenter);
      createProjectPresenter.bindDisplay(createProjectDisplay);
      
   }

   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      restContext = event.getConfiguration().getContext();
   }

}
