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
package org.exoplatform.ide.client.search.file;

import java.util.List;

import org.exoplatform.gwtframework.commons.rest.MimeType;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

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
public class SearchPresenter
{

   public interface Display
   {
      HasClickHandlers getSearchButton();

      HasClickHandlers getCancelButton();

      HasValue<String> getSearchContentItem();

      HasValue<String> getPathItem();

      HasValue<String> getMimeTypeItem();

      void setMimeTypeValues(String[] mimeTypes);

      void closeForm();

   }

   private Display display;

   private List<Item> selectedItems;
   
   private String entryPoint;

   public SearchPresenter(HandlerManager eventBus, List<Item> selectedItems, String entryPoint)
   {
      this.selectedItems = selectedItems;
      this.entryPoint = entryPoint;
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getSearchButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            doSearch();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {

         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      String path;
      if (selectedItems.size() == 0)
      {
         path = "/";
      }
      else
      {
         Item selectedItem = selectedItems.get(0);

         String href = selectedItem.getHref();
         if (selectedItem instanceof File)
         {
            href = href.substring(0, href.lastIndexOf("/") + 1);
         }

         path = href.substring(entryPoint.length() - 1);
      }

      display.getPathItem().setValue(path);

      fillMimeTypes();
   }

   private void doSearch()
   {
      String content = display.getSearchContentItem().getValue();
      String contentType = display.getMimeTypeItem().getValue();

//      if (content != null)
//      {
//         context.setSearchContent(content);
//      }
//
//      if (contentType != null)
//      {
//         context.setSearchContentType(contentType);
//      }

      Item item = selectedItems.get(0);

      String path = item.getHref();
      path = path.substring(entryPoint.length());
      if (item instanceof File)
      {
         path = path.substring(0, path.lastIndexOf("/") + 1);
      }

      if (!"".equals(path) && !path.startsWith("/"))
      {
         path = "/" + path;
      }

      Folder folder = new Folder(entryPoint);
      VirtualFileSystem.getInstance().search(folder, content, contentType, path);
      display.closeForm();
   }

   private void fillMimeTypes()
   {
      String[] mimeTypes = new String[10];
      mimeTypes[0] = MimeType.TEXT_HTML;
      mimeTypes[1] = MimeType.TEXT_CSS;
      mimeTypes[2] = MimeType.TEXT_PLAIN;
      mimeTypes[3] = MimeType.APPLICATION_X_JAVASCRIPT;
      mimeTypes[4] = MimeType.APPLICATION_JAVASCRIPT;
      mimeTypes[5] = MimeType.TEXT_JAVASCRIPT;
      mimeTypes[6] = MimeType.TEXT_XML;
      mimeTypes[7] = MimeType.GROOVY_SERVICE;
      mimeTypes[8] = MimeType.APPLICATION_GROOVY;
      mimeTypes[9] = MimeType.GOOGLE_GADGET;
      display.setMimeTypeValues(mimeTypes);
   }

}
