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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateEvent;
import org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateHandler;
import org.exoplatform.ide.client.template.CreateProjectTemplateForm;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id: Sep 27, 2010 $
 *
 */
public class ProjectTemplateControlHandler implements CreateProjectTemplateHandler, TemplateListReceivedHandler, 
ExceptionThrownHandler
{
   private HandlerManager eventBus;

   private Handlers handlers;

   public ProjectTemplateControlHandler(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(CreateProjectTemplateEvent.TYPE, this);
   }

   /**
    * @see org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler#onError(org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent)
    */
   public void onError(ExceptionThrownEvent event)
   {
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
   }
   
   /**
    * @see org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateHandler#onCreateProjectTemplate(org.exoplatform.ide.client.module.navigation.event.newitem.CreateProjectTemplateEvent)
    */
   public void onCreateProjectTemplate(CreateProjectTemplateEvent event)
   {
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      TemplateService.getInstance().getTemplates();
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler#onTemplateListReceived(org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent)
    */
   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      handlers.removeHandler(TemplateListReceivedEvent.TYPE);
      TemplateList templateList = event.getTemplateList();
      new CreateProjectTemplateForm(eventBus, templateList.getTemplates());
   }

}
