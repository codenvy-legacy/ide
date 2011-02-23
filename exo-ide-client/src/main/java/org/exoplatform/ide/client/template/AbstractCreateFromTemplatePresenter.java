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
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateDeletedCallback;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.event.TemplateDeletedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateDeletedHandler;

import java.util.List;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public abstract class AbstractCreateFromTemplatePresenter<T extends Template> implements TemplateDeletedHandler 
{

   protected HandlerManager eventBus;

   protected Handlers handlers;

   protected CreateFromTemplateDisplay<T> display;

   protected List<T> selectedTemplates;

   protected List<T> templateList;
   
   public AbstractCreateFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems)
   {
      this.eventBus = eventBus;
      
      handlers = new Handlers(eventBus);
      handlers.addHandler(TemplateDeletedEvent.TYPE, this);
   }
   
   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(CreateFromTemplateDisplay<T> d)
   {
      display = d;
      
      display.getNameField().addValueChangeHandler(new ValueChangeHandler<String>()
      {
         public void onValueChange(ValueChangeEvent<String> event)
         {
            String value = event.getValue();
            
            if (value == null || value.length() == 0)
            {
               display.disableCreateButton();
            }
            else
            {
               display.enableCreateButton();
            }
         }
      });

      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            submitTemplate();
         }
      });

      display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            submitTemplate();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getTemplateListGrid().addSelectionHandler(new SelectionHandler<T>()
      {
         public void onSelection(SelectionEvent<T> event)
         {
            selectedTemplates = display.getTemplatesSelected();
            templatesSelected();
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            deleteTemplate();
         }
      });

      display.getTemplateListGrid().setValue(templateList);
      display.disableCreateButton();
      display.disableDeleteButton();
      display.disableNameField();
   }

   /**
    * Delete selected template
    */
   protected void deleteTemplate()
   {
      if (selectedTemplates.size() == 0)
      {
         return;
      }
      
      String message = "Do you want to delete template";
      if (selectedTemplates.size() == 1)
      {
         message += " <b>" + selectedTemplates.get(0).getName() + "</b>?";
      }
      else if (selectedTemplates.size() > 1)
      {
         message += "s?";
      }
      
      Dialogs.getInstance().ask("IDE", message, new BooleanValueReceivedHandler()
      {
         public void booleanValueReceived(Boolean value)
         {
            if (value == null)
            {
               return;
            }
            if (value)
            {
               deleteNextTemplate();
            }
         }
      });
   }
   
   protected void deleteNextTemplate()
   {
      if (selectedTemplates.size() == 0)
      {
         refreshTemplateList();
         return;
      }
      deleteOneTemplate(selectedTemplates.get(0));
   }
   
   protected void deleteOneTemplate(T template)
   {
      deleteTemplate(template);
   }
   
   protected void deleteTemplate(T template)
   {
      TemplateService.getInstance().deleteTemplate(template, new TemplateDeletedCallback()
      {
         @Override
         protected void onSuccess(Template result)
         {
            selectedTemplates.remove(result);
            deleteNextTemplate();
         }
      });
   }
   
   protected void templatesSelected()
   {
      if (selectedTemplates.size() == 0)
      {
         display.disableCreateButton();
         display.disableDeleteButton();
         display.disableNameField();
         return;
      }
      
      if (selectedTemplates.size() > 1)
      {
         display.disableNameField();
         display.disableCreateButton();
         //check is one of selected templates is default
         for (Template template : selectedTemplates)
         {
            if (template.getNodeName() == null)
            {
               display.disableDeleteButton();
               return;
            }
         }
         
         display.enableDeleteButton();
         return;
      }
      
      display.enableNameField();
      display.enableCreateButton();
      if (selectedTemplates.get(0).getNodeName() == null)
      {
         display.disableDeleteButton();
      }
      else
      {
         display.enableDeleteButton();
      }
      
      setNewInstanceName();
   }
   
   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateDeletedHandler#onTemplateDeleted(org.exoplatform.ide.client.model.template.event.TemplateDeletedEvent)
    */
   public void onTemplateDeleted(TemplateDeletedEvent event)
   {
      selectedTemplates.remove(event.getTemplate());
      deleteNextTemplate();
   }

   /**
    * Refresh List of the templates, after deleting
    */
   private void refreshTemplateList()
   {
      TemplateService.getInstance().getTemplates(new AsyncRequestCallback<TemplateList>()
      {
         
         @Override
         protected void onSuccess(TemplateList result)
         {
            updateTemplateList(result.getTemplates());
            
            display.getTemplateListGrid().setValue(templateList);
            if (templateList.size() > 0)
            {
               display.selectLastTemplate();
            }
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            eventBus.fireEvent(new ExceptionThrownEvent(exception));
         }
      });
   }
   
   abstract void setNewInstanceName();
   
   abstract void updateTemplateList(List<Template> templates);
   
   abstract void submitTemplate();

}
