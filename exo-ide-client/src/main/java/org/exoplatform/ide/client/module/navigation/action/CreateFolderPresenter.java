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
package org.exoplatform.ideall.client.module.navigation.action;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.ideall.client.module.navigation.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.module.vfs.api.File;
import org.exoplatform.ideall.client.module.vfs.api.Folder;
import org.exoplatform.ideall.client.module.vfs.api.Item;
import org.exoplatform.ideall.client.module.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.module.vfs.api.event.FolderCreatedEvent;
import org.exoplatform.ideall.client.module.vfs.api.event.FolderCreatedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFolderPresenter implements FolderCreatedHandler
{

   public interface Display
   {

      HasValue<String> getFolderNameField();

      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();

      HasKeyPressHandlers getFolderNameFiledKeyPressed();

      void closeForm();

   }

   private Display display;

   private HandlerManager eventBus;

   private String href;

   private Handlers handlers;
   
   private Item selectedItem;

   public CreateFolderPresenter(HandlerManager eventBus, Item selectedItem, String href)
   {
      this.eventBus = eventBus;
      this.selectedItem = selectedItem;
      this.href = href;
      handlers = new Handlers(eventBus);
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
            createFolder();
         }
      });

      display.getFolderNameField().setValue("New Folder");

      display.getFolderNameFiledKeyPressed().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER)
            {
               createFolder();
            }
         }
      });

      handlers.addHandler(FolderCreatedEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   protected void createFolder()
   {
      String newFolderHref = href + display.getFolderNameField().getValue() + "/";
      Folder newFolder = new Folder(newFolderHref);
      VirtualFileSystem.getInstance().createFolder(newFolder);
   }

   public void onFolderCreated(FolderCreatedEvent event)
   {
      //Item item = selectedItem; context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
      String folder = selectedItem.getHref();
      if (selectedItem instanceof File)
      {
         folder = folder.substring(0, folder.lastIndexOf("/")+1);
      }
      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(folder), event.getFolder()));
      display.closeForm();
   }
//   
//   public void onItemsSelected(ItemsSelectedEvent event)
//   {
//      if(event.getSelectedItems().size() != 0)
//      {
//         selectedItem = event.getSelectedItems().get(0);
//      }
//   }   

}
