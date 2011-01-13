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
package org.exoplatform.ide.client.template;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.model.template.event.TemplateCreatedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateCreatedHandler;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;

/**
 * @author <a href="mailto:zhulevaanna@gmail.com">Ann Zhuleva</a>
 * @version $Id:   $
 *
 */
public class SaveAsTemplatePresenter implements TemplateCreatedHandler, TemplateListReceivedHandler
{
   public interface Display
   {

      void closeForm();

      HasValue<String> getTypeField();

      HasValue<String> getNameField();

      HasValue<String> getDescriptionField();

      HasClickHandlers getSaveButton();

      HasClickHandlers getCancelButton();
      
      void disableSaveButton();
      
      void enableSaveButton();

   }

   private Display display;

   private Handlers handlers;

   private File file;
   
   private Template templateToCreate;

   public SaveAsTemplatePresenter(HandlerManager eventBus, File file)
   {
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
      
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String value = event.getValue();
            
            if (value == null || value.length() == 0)
            {
               display.disableSaveButton();
            }
            else
            {
               display.enableSaveButton();
            }
         }
      });

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
      display.disableSaveButton();

   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateCreatedHandler#onTemplateCreated(org.exoplatform.ide.client.model.template.event.TemplateCreatedEvent)
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
      
      templateToCreate = new FileTemplate(file.getContentType(), name, description, file.getContent(), null);
      
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      TemplateServiceImpl.getInstance().getTemplates();
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler#onTemplateListReceived(org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent)
    */
   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      handlers.removeHandler(TemplateListReceivedEvent.TYPE);
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      
      for (Template template : event.getTemplateList().getTemplates())
      {
         if (template instanceof FileTemplate && templateToCreate.getName().equals(template.getName()))
         {
            Dialogs.getInstance().showError("Template with such name already exists!");
            return;
         }
      }
      TemplateServiceImpl.getInstance().createTemplate(templateToCreate);
   }

}
