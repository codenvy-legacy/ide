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
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateHandler;
import org.exoplatform.ide.client.template.CreateProjectTemplateForm;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class CreateProjectTemplateCommandHandler implements CreateProjectTemplateHandler
{
   private HandlerManager eventBus;

   public CreateProjectTemplateCommandHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      
      eventBus.addHandler(CreateProjectTemplateEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateHandler#onCreateProjectTemplate(org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateEvent)
    */
   public void onCreateProjectTemplate(CreateProjectTemplateEvent event)
   {
      TemplateList defaultTemplates = TemplateServiceImpl.getDefaultTemplates();
      new CreateProjectTemplateForm(eventBus, defaultTemplates.getTemplates());
      /*TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
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
      });*/
   }
   
}
