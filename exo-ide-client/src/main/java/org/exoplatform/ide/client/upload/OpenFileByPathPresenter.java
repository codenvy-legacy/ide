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
import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedHandler;

import com.google.gwt.event.dom.client.HasKeyPressHandlers;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.events.HasClickHandlers;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathPresenter implements ItemPropertiesReceivedHandler,
ExceptionThrownHandler
{

   interface Display
   {
      HasClickHandlers getOpenButton();

      HasClickHandlers getCancelButton();
      
      void enableOpenButton();
      
      void disableOpenButton();

      void closeDisplay();

      HasKeyPressHandlers getFilePathField();
      
      // HasValue<String> getFilePathFieldValue(); // getFilePathFieldValue().addValueChangeHandler(new ValueChangeHandler<String>() isn't called by org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField.addValueChangeHandler()

      TextField getFilePathFieldOrigin(); 
   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   public OpenFileByPathPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
   }

   void bindDisplay(Display d)
   {
      display = d;

      display.getOpenButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            openFile();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeDisplay();
         }
      });
      
      display.getFilePathField().addKeyPressHandler(new KeyPressHandler()
      {

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER)
            {
               openFile();
            }
         }
         
      });
      
      display.getFilePathFieldOrigin().addChangedHandler(new ChangedHandler()
      {

         public void onChanged(ChangedEvent event)
         {
            updateOpenButtonState(event.getValue());
         }
         
      });
      
   } 

   private void updateOpenButtonState(Object filePath)
   {
      if (filePath == null || filePath.toString().trim().length() == 0)
      {
         display.disableOpenButton();
      }
      else
      {
         display.enableOpenButton();
      }
   }
   
   private void openFile()
   {
      String filePath = display.getFilePathFieldOrigin().getValue();

      if (filePath == null || filePath.trim().length() == 0)
      {
         display.disableOpenButton();
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
      display.getFilePathFieldOrigin().focusInItem();
   }

   private void stopHandling()
   {
      handlers.removeHandlers();
   }   
   
}