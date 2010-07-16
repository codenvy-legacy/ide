/*
 * Copyright (C) 2009 eXo Platform SAS.
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
package org.exoplatform.ideall.client.template;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ideall.client.model.template.Template;
import org.exoplatform.ideall.client.model.template.TemplateServiceImpl;
import org.exoplatform.ideall.client.model.template.event.TemplateCreatedEvent;
import org.exoplatform.ideall.client.model.template.event.TemplateCreatedHandler;
import org.exoplatform.ideall.client.module.vfs.api.File;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SaveAsTemplatePresenter implements TemplateCreatedHandler
{
   public interface Display
   {

      void closeForm();

      HasValue<String> getTypeField();

      HasValue<String> getNameField();

      HasValue<String> getDescriptionField();

      HasClickHandlers getSaveButton();

      HasClickHandlers getCancelButton();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private File file;

   public SaveAsTemplatePresenter(HandlerManager eventBus, File file)
   {
      this.eventBus = eventBus;
      this.file = file;
      handlers = new Handlers(eventBus);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      display = d;

      handlers.addHandler(TemplateCreatedEvent.TYPE, this);

      display.getSaveButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            createTemplate();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getTypeField().setValue(file.getContentType());

   }

   /**
    * @see org.exoplatform.ideall.client.model.template.event.TemplateCreatedHandler#onTemplateCreated(org.exoplatform.ideall.client.model.template.event.TemplateCreatedEvent)
    */
   public void onTemplateCreated(TemplateCreatedEvent event)
   {
      display.closeForm();
      Dialogs.getInstance().showInfo("Template created successfully!");
   }

   void createTemplate()
   {
      String name = display.getNameField().getValue().trim();
      if ("".equals(name))
      {
         Dialogs.getInstance().showError("You should specify the name of template!");
         return;
      }

      String description = "";
      if (display.getDescriptionField().getValue() != null)
      {
         description = display.getDescriptionField().getValue();
      }

      Template template = new Template(file.getContentType(), name, description, file.getContent(),null);
      TemplateServiceImpl.getInstance().createTemplate(template);
   }

}
