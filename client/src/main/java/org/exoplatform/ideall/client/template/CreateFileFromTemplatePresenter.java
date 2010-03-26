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
package org.exoplatform.ideall.client.template;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.api.ListGridItem;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.ideall.client.event.file.OpenFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.template.Template;
import org.exoplatform.ideall.client.model.util.ImageUtil;
import org.exoplatform.ideall.client.model.util.MimeTypeResolver;
import org.exoplatform.ideall.client.model.util.NodeTypeUtil;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;

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

public class CreateFileFromTemplatePresenter
{

   public interface Display
   {

      ListGridItem<Template> getTemplateListGrid();

      HasValue<String> getFileNameField();

      HasClickHandlers getCancelButton();

      HasClickHandlers getCreateButton();

      void closeForm();

      void enableCreateButton();

      void disableCreateButton();

   }

   private HandlerManager eventBus;

   private Handlers handlers;

   private Display display;

   private ApplicationContext context;

   private Template selectedTemplate;

   private String previousExtension;

   public CreateFileFromTemplatePresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display d)
   {
      display = d;

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
            createFile();
         }
      });

      display.getTemplateListGrid().addSelectionHandler(new SelectionHandler<Template>()
      {
         public void onSelection(SelectionEvent<Template> event)
         {
            templateSelected(event.getSelectedItem());
         }
      });

      display.getTemplateListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            createFile();
         }
      });

      display.getFileNameField().setValue("Untitled file");

      display.getTemplateListGrid().setValue(context.getTemplateList().getTemplates());

      display.disableCreateButton();
   }

   protected void templateSelected(Template template)
   {
      if (selectedTemplate == template)
      {
         return;
      }
      selectedTemplate = template;
      display.enableCreateButton();

      String extension = MimeTypeResolver.getExtensionsMap().get(template.getMimeType());
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
//      TODO
      String fileName = display.getFileNameField().getValue();
      if ("".equals(fileName.trim()))
      {
         Dialogs.getInstance().showError("You must enter file name the first!");
         return;
      }

      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);

      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/")+1);
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

}
