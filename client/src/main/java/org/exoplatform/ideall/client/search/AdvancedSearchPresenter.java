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
package org.exoplatform.ideall.client.search;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.gwtframework.ui.dialogs.Dialogs;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.data.DataService;

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
public class AdvancedSearchPresenter
{

   public interface Display
   {
      HasClickHandlers getSearchButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getSearchContentItem();

      HasValue<String> getPathItem();

      HasValue<String> getFileNameItem();

      HasValue<String> getMimeTypeItem();

      void setMimeTypeValues(String[] mimeTypes);

      void disablePathItem();

      void closeForm();

   }

   private Display display;

   private HandlerManager eventBus;

   private ApplicationContext context;

   private String path;

   public AdvancedSearchPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      getPathValue();
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getSearchButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doAdvancedSearch();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getPathItem().setValue(path);
      display.disablePathItem();

      fillMimeTypes();

      if (context.getSearchContent() != null)
      {
         display.getSearchContentItem().setValue(context.getSearchContent());
      }

      if (context.getSearchFileName() != null)
      {
         display.getFileNameItem().setValue(context.getSearchFileName());
      }

      if (context.getSearchContentType() != null)
      {
         display.getMimeTypeItem().setValue(context.getSearchContentType());
      }
   }

   private void doAdvancedSearch()
   {
      String content = display.getSearchContentItem().getValue();
      String mainPath = display.getPathItem().getValue();
      String searchPath = display.getPathItem().getValue();
      String fileName = display.getFileNameItem().getValue();
      String contentType = display.getMimeTypeItem().getValue();

      if (mainPath == null || mainPath.length() == 0)
      {
         Dialogs.showError("Path must not be empty!");
         return;
      }

      String[] parts = searchPath.split("/");
      int i = 3;
      if (parts[0].length() != 0)
      {
         i = 2;
      }

      // Get path of the folder where to search
      searchPath = "";
      while (i <= parts.length - 1)
      {
         searchPath += "/" + parts[i];
         i++;
      }

      if (content != null && content.length() > 0)
      {
         context.setSearchContent(content);
      }

      if (fileName != null && fileName.length() > 0)
      {
         context.setSearchFileName(fileName);
      }

      if (contentType != null && contentType.length() > 0)
      {
         context.setSearchContentType(contentType);
      }

      DataService.getInstance().search(mainPath, content, fileName, contentType, searchPath);
      display.closeForm();
   }

   private void fillMimeTypes()
   {
      String[] mimeTypes = new String[9];
      mimeTypes[0] = MimeType.TEXT_HTML;
      mimeTypes[1] = MimeType.TEXT_CSS;
      mimeTypes[2] = MimeType.TEXT_PLAIN;
      mimeTypes[3] = MimeType.APPLICATION_X_JAVASCRIPT;
      mimeTypes[4] = MimeType.APPLICATION_JAVASCRIPT;
      mimeTypes[5] = MimeType.TEXT_JAVASCRIPT;
      mimeTypes[6] = MimeType.TEXT_XML;
      mimeTypes[7] = MimeType.SCRIPT_GROOVY;
      mimeTypes[8] = MimeType.GOOGLE_GADGET;
      display.setMimeTypeValues(mimeTypes);
   }

   private void getPathValue()
   {
      //if file was selected then delete it's name from path
      if (context.getSelectedItem() instanceof File)
      {
         String filePath = context.getSelectedItem().getPath();
         String fileName = ((File)context.getSelectedItem()).getName();
         int index = filePath.lastIndexOf(fileName);
         if (index > 0)
         {
            // remove file's name plus dilimeter before it
            path = filePath.substring(0, index - 1);
         }
      }
      else
      {
         path = context.getSelectedItem().getPath();
      }
   }

}
