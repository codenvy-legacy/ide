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
package org.exoplatform.ide.client.navigation;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.FolderCreateCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class CreateFolderPresenter 
{
   
   public interface Display
   {
      HasValue<String> getFolderNameField();

      HasClickHandlers getCreateButton();

      HasClickHandlers getCancelButton();

      HasKeyPressHandlers getFolderNameFiledKeyPressed();

      void closeForm();
   }

   private CreateFolderDisplay display;

   private HandlerManager eventBus;

   private String href;

   private Item selectedItem;

   public CreateFolderPresenter(HandlerManager eventBus, Item selectedItem, String href)
   {
      this.eventBus = eventBus;
      this.selectedItem = selectedItem;
      this.href = href;
   }

   public void bindDisplay(CreateFolderDisplay d)
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

   }

   public void destroy()
   {
   }

   protected void createFolder()
   {
      String newFolderHref = href + display.getFolderNameField().getValue() + "/";
      Folder newFolder = new Folder(newFolderHref);
      VirtualFileSystem.getInstance().createFolder(newFolder, new FolderCreateCallback()
      {
         @Override
         protected void onSuccess(Folder result)
         {
            String folder = selectedItem.getHref();
            if (selectedItem instanceof File)
            {
               folder = folder.substring(0, folder.lastIndexOf("/") + 1);
            }
            eventBus.fireEvent(new RefreshBrowserEvent(new Folder(folder), result));
            display.closeForm();
         }
      });
   }

}
