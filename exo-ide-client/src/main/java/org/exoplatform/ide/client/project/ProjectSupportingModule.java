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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.control.event.RegisterControlEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.project.control.CreateProjectFromTemplateControl;
import org.exoplatform.ide.client.project.control.CreateProjectTemplateControl;
import org.exoplatform.ide.client.project.event.CreateProjectFromTemplateEvent;
import org.exoplatform.ide.client.project.event.CreateProjectFromTemplateHandler;
import org.exoplatform.ide.client.project.event.CreateProjectTemplateEvent;
import org.exoplatform.ide.client.project.event.CreateProjectTemplateHandler;
import org.exoplatform.ide.client.template.CreateFromTemplateDisplay;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 */

public class ProjectSupportingModule implements ItemsSelectedHandler, ConfigurationReceivedSuccessfullyHandler, CreateProjectTemplateHandler
{
   
   private HandlerManager eventBus;
   
   private String restServiceContext;
   
   private List<Item> selectedItems = new ArrayList<Item>();

   public ProjectSupportingModule(HandlerManager eventBus) {
      this.eventBus = eventBus;
      
      eventBus.fireEvent(new RegisterControlEvent(new CreateProjectFromTemplateControl()));
      eventBus.fireEvent(new RegisterControlEvent(new CreateProjectTemplateControl()));      
      
      eventBus.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      eventBus.addHandler(ItemsSelectedEvent.TYPE, this);
      
      eventBus.addHandler(CreateProjectTemplateEvent.TYPE, this);
      
      new CreateProjectFromTemplatePresenter(eventBus);
   }
   
   /**
    * @see org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.event.ConfigurationReceivedSuccessfullyEvent)
    */
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      restServiceContext = event.getConfiguration().getContext();
   }
   
   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      selectedItems = event.getSelectedItems();
   }   

   @Override
   public void onCreateProjectTemplate(CreateProjectTemplateEvent event)
   {
      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
         {
            @Override
            protected void onSuccess(TemplateList result)
            {
               new CreateProjectTemplateForm(eventBus, result.getTemplates());
            }
            
            @Override
            protected void onFailure(Throwable exception)
            {
               eventBus.fireEvent(new ExceptionThrownEvent(exception));
            }
         });
      
   }
   
}
