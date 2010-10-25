/*
 * Copyright (C) 2003-2009 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.ide.client.upload;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.upload.event.FilePathSelectedEvent;
import org.exoplatform.ide.client.upload.event.FilePathSelectedHandler;

import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.HasValue;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HasClickHandlers;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathPresenter implements FilePathSelectedHandler, ItemPropertiesReceivedHandler,
ExceptionThrownHandler
{

   interface Display
   {
      HasClickHandlers getOpenFileButton();

      HasClickHandlers getCloseButton();

      void closeDisplay();

      HasValue<String> getFilePathField();
   }

   private HandlerManager eventBus;

   private Display display;

   private HandlerRegistration fileSelectedHandler;

   private Handlers handlers;

   public OpenFileByPathPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      fileSelectedHandler = eventBus.addHandler(FilePathSelectedEvent.TYPE, this);
   }

   void bindDisplay(Display d)
   {
      display = d;

      display.getOpenFileButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            openFile();
         }
      });

      display.getCloseButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeDisplay();
         }
      });

//      display.disableOpenFileButton();
   }

   void destroy()
   {
      if (fileSelectedHandler != null)
      {
         fileSelectedHandler.removeHandler();
      }
   }

   public void onFilePathSelected(FilePathSelectedEvent event)
   {
      openFile();
   }

   private void openFile()
   {
      String filePath = display.getFilePathField().getValue();

      if (filePath == null || filePath.trim().length() == 0)
      {
         return;
      }
      
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

      File file = new File(filePath);
      VirtualFileSystem.getInstance().getProperties(file); 
   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      stopHandling();
     
      VirtualFileSystem.getInstance().getContent((File) event.getItem());
      eventBus.fireEvent(new OpenFileEvent((File) event.getItem()));  
    
      display.closeDisplay();
   }

   public void onError(ExceptionThrownEvent event)
   {
      stopHandling();
   }

   private void stopHandling()
   {
      handlers.removeHandlers();
   }   
   
}