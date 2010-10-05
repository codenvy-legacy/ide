/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.template;

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.gwtframework.commons.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.model.template.FileTemplate;
import org.exoplatform.ide.client.model.template.Template;
import org.exoplatform.ide.client.model.template.TemplateService;
import org.exoplatform.ide.client.model.template.event.TemplateDeletedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateDeletedHandler;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedEvent;
import org.exoplatform.ide.client.model.template.event.TemplateListReceivedHandler;
import org.exoplatform.ide.client.model.util.IDEMimeTypes;
import org.exoplatform.ide.client.model.util.ImageUtil;
import org.exoplatform.ide.client.module.vfs.api.File;
import org.exoplatform.ide.client.module.vfs.api.Item;
import org.exoplatform.ide.client.module.vfs.webdav.NodeTypeUtil;
import org.exoplatform.ide.client.template.event.AddFileTemplateToProjectTemplateEvent;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFileFromTemplatePresenter implements TemplateDeletedHandler, TemplateListReceivedHandler
{

   public interface Display
   {

      ListGridItem<Template> getTemplateListGrid();

      HasValue<String> getFileNameField();

      HasClickHandlers getCancelButton();

      HasClickHandlers getCreateButton();

      HasClickHandlers getDeleteButton();

      void closeForm();

      void enableCreateButton();

      void disableCreateButton();
      
      void setDeleteButtonDisabled(boolean value);
      
      void selectLastTemplate();

   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   private FileTemplate selectedTemplate;

   private String previousExtension;
   
   private List<Item> selectedItems;
   
   private List<Template> templateList = new ArrayList<Template>();
   
   private boolean createFile;

   public CreateFileFromTemplatePresenter(HandlerManager eventBus, List<Item> selectedItems, List<Template> templateList, boolean createFile)
   {
      this.eventBus = eventBus;
      this.createFile = createFile;
      
      this.selectedItems = selectedItems;
      for (Template template : templateList)
      {
         if (template instanceof FileTemplate)
         {
            this.templateList.add(template);
         }
      }

      handlers = new Handlers(eventBus);
      handlers.addHandler(TemplateDeletedEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      display = d;

      if (createFile)
      {
         display.getCreateButton().addClickHandler(new ClickHandler()
         {
            public void onClick(ClickEvent event)
            {
               createFile();
            }
         });

         display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
         {
            public void onDoubleClick(DoubleClickEvent event)
            {
               createFile();
            }
         });
      }
      else
      {
         display.getCreateButton().addClickHandler(new ClickHandler()
         {
            public void onClick(ClickEvent event)
            {
               addFileToProjectTemplate();
            }
         });

         display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
         {
            public void onDoubleClick(DoubleClickEvent event)
            {
               addFileToProjectTemplate();
            }
         });
      }

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getTemplateListGrid().addSelectionHandler(new SelectionHandler<Template>()
      {
         public void onSelection(SelectionEvent<Template> event)
         {
            templateSelected(event.getSelectedItem());
         }
      });

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            deleteTemplate();
         }
      });

      display.getFileNameField().setValue("Untitled file");

      display.getTemplateListGrid().setValue(templateList);

      display.disableCreateButton();
      
      display.setDeleteButtonDisabled(true);
   }

   /**
    * Delete selected template
    */
   private void deleteTemplate()
   {

      String message = "Do you want to delete template <b>" + selectedTemplate.getName() + "</b>?";
      Dialogs.getInstance().ask("IDEall", message, new BooleanValueReceivedCallback()
      {

         public void execute(Boolean value)
         {
            if (value == null)
            {
               return;
            }
            if (value)
            {
               TemplateService.getInstance().deleteTemplate(selectedTemplate);
            }
         }

      });

   }

   protected void templateSelected(Template template)
   {
      if (selectedTemplate == template)
      {
         return;
      }
      selectedTemplate = (FileTemplate)template;
      display.enableCreateButton();
      
      if (template.getNodeName() == null)
      {
         display.setDeleteButtonDisabled(true);
      }
      else
      {
         display.setDeleteButtonDisabled(false);
      }

      String extension = IDEMimeTypes.getExtensionsMap().get(((FileTemplate)template).getMimeType());
      if (previousExtension != null)
      {
         String fName = display.getFileNameField().getValue();
         if (fName.endsWith("." + previousExtension))
         {
            fName = fName.substring(0, fName.length() - previousExtension.length() - 1);
         }
         fName += "." + extension;
         display.getFileNameField().setValue(fName);
      }
      else
      {
         display.getFileNameField().setValue(display.getFileNameField().getValue() + "." + extension);
      }
      previousExtension = extension;
   }

   protected void createFile()
   {
      String fileName = display.getFileNameField().getValue();
      if ("".equals(fileName.trim()))
      {
         Dialogs.getInstance().showError("You must enter file name the first!");
         return;
      }

      String href;
           
      if (selectedItems != null && selectedItems.size() != 0)
      {
         Item item = selectedItems.get(0);

         href = item.getHref();
         if (item instanceof File)
         {
            href = href.substring(0, href.lastIndexOf("/") + 1);
         }
      }
      else
      {
         href = "";
      }

      String contentType = selectedTemplate.getMimeType();

      File newFile = new File(href + fileName);
      newFile.setContentType(contentType);
      newFile.setJcrContentNodeType(NodeTypeUtil.getContentNodeType(contentType));
      newFile.setIcon(ImageUtil.getIcon(contentType));
      newFile.setNewFile(true);
      newFile.setContent(selectedTemplate.getContent());
      newFile.setContentChanged(true);

      eventBus.fireEvent(new OpenFileEvent(newFile));

      display.closeForm();
   }
   
   private void addFileToProjectTemplate()
   {
      final String fileName = display.getFileNameField().getValue().trim();
      if ("".equals(fileName))
      {
         Dialogs.getInstance().showError("You must enter file name the first!");
         return;
      }

      eventBus.fireEvent(new AddFileTemplateToProjectTemplateEvent(
         new FileTemplate(selectedTemplate.getName(), fileName, selectedTemplate.getMimeType())));
      display.closeForm();
   }

   /**
    * @see org.exoplatform.ide.client.model.template.event.TemplateDeletedHandler#onTemplateDeleted(org.exoplatform.ide.client.model.template.event.TemplateDeletedEvent)
    */
   public void onTemplateDeleted(TemplateDeletedEvent event)
   {
      refreshTemplateList();
      String message = "Template <b>" + event.getTemplateName() + "</b> deleted.";
      Dialogs.getInstance().showInfo("IDEall", message);
   }

   /**
    * Refresh List of the templates, after deleting
    */
   private void refreshTemplateList()
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
      templateList.clear();
      templateList.addAll(event.getTemplateList().getTemplates());
      display.getTemplateListGrid().setValue(templateList);
      display.selectLastTemplate();
   }

}
