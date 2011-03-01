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

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectFromTemplateHandler;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;
import org.exoplatform.ide.client.template.CreateProjectFromTemplateForm;
import org.exoplatform.ide.client.template.CreateProjectFromTemplatePresenter;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class CreateProjectFromTemplateCommandHandler implements CreateProjectFromTemplateHandler, 
ItemsSelectedHandler, ConfigurationReceivedSuccessfullyHandler
{
   
   private HandlerManager eventBus;
   
   private List<Item> selectedItems = new ArrayList<Item>();   
   
   private String restContext;
   
   public CreateProjectFromTemplateCommandHandler(HandlerManager eventBus) {
     System.out.println("CreateProjectFromTemplateCommandHandler.CreateProjectFromTemplateCommandHandler()"+eventBus);
      this.eventBus = eventBus;
      
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
      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
      {
         
         @Override
         protected void onSuccess(TemplateList result)
         {
            CreateProjectFromTemplatePresenter createProjectPresenter =
               new CreateProjectFromTemplatePresenter(eventBus, selectedItems, result.getTemplates(), restContext);
            CreateFromTemplateDisplay<ProjectTemplate> createProjectDisplay =
               new CreateProjectFromTemplateForm(eventBus, result.getTemplates(), createProjectPresenter);
            createProjectPresenter.bindDisplay(createProjectDisplay);
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      System.out.println("CreateProjectFromTemplateCommandHandler.onConfigurationReceivedSuccessfully()");
      restContext = event.getConfiguration().getContext();
   }

}
