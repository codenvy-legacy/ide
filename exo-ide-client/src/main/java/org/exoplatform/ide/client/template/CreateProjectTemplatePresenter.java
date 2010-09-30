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
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.ProjectTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateServiceImpl;
import org.exoplatform.ide.client.model.template.event.TemplateCreatedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateCreatedHandler;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * @author <a href="mailto:oksana.vereshchaka@gmail.com">Oksana Vereshchaka</a>
 * @version $Id:
 *
 */
public class CreateProjectTemplatePresenter implements TemplateCreatedHandler, TemplateListReceivedHandler
{
   private HandlerManager eventBus;
   
   private Handlers handlers;
   
   private Display display;
   
   private List<Template> templateList = new ArrayList<Template>();
   
   private Template templateToCreate;
   
   public interface Display
   {
      ListGridItem<Template> getTemplateListGrid();

      HasValue<String> getNameField();
      
      HasValue<String> getDescriptionField();

      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();
      
      List<Template> getFileTemplatesSelected();

      void closeForm();

      void enableCreateButton();

      void disableCreateButton();
      

   }
   
   public CreateProjectTemplatePresenter(HandlerManager eventBus, List<Template> templateList)
   {
      this.eventBus = eventBus;
      for (Template template : templateList)
      {
         if (template instanceof FileTemplate)
         {
            this.templateList.add(template);
         }
      }
      
      handlers = new Handlers(eventBus);
   }
   
   public void bindDisplay(Display d)
   {
      display = d;
      
      handlers.addHandler(TemplateCreatedEvent.TYPE, this);
      
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            createTemplate();
         }
      });

      display.getTemplateListGrid().setValue(templateList);
   }
   
   private void createTemplate()
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
      
      List<String> fileTemplates = new ArrayList<String>();
      
      for (Template template : display.getFileTemplatesSelected())
      {
         if (template instanceof FileTemplate)
         {
            fileTemplates.add(((FileTemplate)template).getName());
         }
      }
      
      templateToCreate = new ProjectTemplate(name, description, null, fileTemplates);
//      TemplateServiceImpl.getInstance().createTemplate(templateToCreate);
      
      handlers.addHandler(TemplateListReceivedEvent.TYPE, this);
      TemplateServiceImpl.getInstance().getTemplates();
   }
   
   public void destroy()
   {
      handlers.removeHandlers();
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler#onTemplateListReceived(org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent)
    */
   public void onTemplateListReceived(TemplateListReceivedEvent event)
   {
      System.out.println("CreateProjectTemplatePresenter.onTemplateListReceived()");
      handlers.removeHandler(TemplateListReceivedEvent.TYPE);
      handlers.removeHandler(ExceptionThrownEvent.TYPE);
      
      System.out.println("CreateProjectTemplatePresenter.onTemplateListReceived() " + event.getTemplateList().getTemplates().size());
      
      for (Template template : event.getTemplateList().getTemplates())
      {
         if (template instanceof ProjectTemplate && templateToCreate.getName().equals(template.getName()))
         {
            System.out.println("CreateProjectTemplatePresenter.onTemplateListReceived() " + template.getName());
            Dialogs.getInstance().showError("Project template with such name already exists!");
            return;
         }
      }
      TemplateServiceImpl.getInstance().createTemplate(templateToCreate);
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateCreatedHandler#onTemplateCreated(org.exoplatform.ide.client.model.template.event.TemplateCreatedEvent)
    */
   public void onTemplateCreated(TemplateCreatedEvent event)
   {
      display.closeForm();
      Dialogs.getInstance().showInfo("Template created successfully!");
   }

}
