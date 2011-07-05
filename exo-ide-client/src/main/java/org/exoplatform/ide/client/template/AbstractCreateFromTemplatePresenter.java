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

import java.util.List;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateDeletedCallback;
import org.exoplatform.ide.client.model.template.TemplateList;
import org.exoplatform.ide.client.model.template.TemplateService;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Abstract presenter for template form.
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $Id: $
 */

public abstract class AbstractCreateFromTemplatePresenter<T extends Template> 
{

   protected HandlerManager eventBus;

   protected CreateFromTemplateDisplay<T> display;

   /**
    * The list of templates, that selected.
    */
   protected List<T> selectedTemplates;

   /**
    * The list of templates to display.
    * This list must be initialized by subclasses,
    * because it depends on type of template (file of project).
    */
   protected List<T> templateList;
   
   public AbstractCreateFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems)
   {
      this.eventBus = eventBus;
   }
   
   public void destroy()
   {
   }

   /**
    * @param d
    */
   public void bindDisplay(CreateFromTemplateDisplay<T> d)
   {
      display = d;
      
      /*
       * If name field is empty - disable create button
       */
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

      /*
       * Add click handler for create button
       */
      display.getCreateButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            submitTemplate();
         }
      });

      /*
       * If double click on template - than new template will be created.
       */
      display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            submitTemplate();
         }
      });

      /*
       * Close action on cancel button
       */
      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      /*
       * If template selected - than copy template name to name field and enable create button
       */
      display.getTemplateListGrid().addSelectionHandler(new SelectionHandler<T>()
      {
         public void onSelection(SelectionEvent<T> event)
         {
            selectedTemplates = display.getTemplatesSelected();
            templatesSelected();
         }
      });

      /*
       * Delete action on delete button
       */
      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            deleteTemplate();
         }
      });

      /*
       * Initialize template list grid with template list
       */
      display.getTemplateListGrid().setValue(templateList);
      /*
       * Disable buttons and name field, because no template is selected
       */
      display.disableCreateButton();
      display.disableDeleteButton();
      display.disableNameField();
   }

   /**
    * Executes, when delete button pressed.
    * Show ask dialog.
    */
   protected void deleteTemplate()
   {
      if (selectedTemplates.size() == 0)
      {
         return;
      }
      
      String message = "";
      if (selectedTemplates.size() == 1)
      {
         final String templateName = selectedTemplates.get(0).getName();
         message =
            org.exoplatform.ide.client.IDE.IDE_LOCALIZATION_MESSAGES
               .createFromTemplateAskDeleteOneTemplate(templateName);
      }
      else if (selectedTemplates.size() > 1)
      {
         message =
            org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.createFromTemplateAskDeleteSeveralTemplates();
      }
      
      Dialogs.getInstance().ask(org.exoplatform.ide.client.IDE.TEMPLATE_CONSTANT.askDeleteTemplateDialogTitle(),
         message, new BooleanValueReceivedHandler()
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
   
   /**
    * Delete next template from selected list.
    */
   protected void deleteNextTemplate()
   {
      if (selectedTemplates.size() == 0)
      {
         refreshTemplateList();
         return;
      }
      deleteOneTemplate(selectedTemplates.get(0));
   }
   
   /**
    * Call template service to delete template.
    * If success, call method, that will delete next template from selected list.
    * @param template
    */
   protected void deleteOneTemplate(T template)
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
   
   /**
    * Calls, when template selected in list grid.
    */
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
   
   /**
    * Set the value to name field, based on selected template.
    */
   protected abstract void setNewInstanceName();
   
   /**
    * Updates template list with new values.
    * Pass the list of all templates (projects and files),
    * subclasses have to filter this list and save only thos templates,
    * that they are interested in.
    * @param templates - the list of all templates.
    */
   protected abstract void updateTemplateList(List<Template> templates);
   
   /**
    * Call, when create button pressed (or when double clicked on template).
    * Create new instance of selected template.
    */
   public abstract void submitTemplate();

}
