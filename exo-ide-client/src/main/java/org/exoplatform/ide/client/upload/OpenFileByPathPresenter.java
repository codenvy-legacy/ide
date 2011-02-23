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
package org.exoplatform.ide.client.upload;

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

import org.exoplatform.gwtframework.ui.client.smartgwt.component.TextField;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.FileCallback;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.FileContentReceivedEvent;

/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:dmitry.ndp@gmail.com">Dmytro Nochevnov</a>
 * @version $Id: $
 */
public class OpenFileByPathPresenter
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

   public OpenFileByPathPresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
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
      
      File file = new File(filePath);
      VirtualFileSystem.getInstance().getPropertiesCallback(file, new ItemPropertiesCallback()
      {
         
         @Override
         protected void onSuccess(Item result)
         {
            eventBus.fireEvent(new OpenFileEvent((File)result));
            getFileContent((File)result);

            display.closeDisplay();
         }
         
         @Override
         protected void onFailure(Throwable exception)
         {
            super.onFailure(exception);
            display.getFilePathFieldOrigin().focusInItem();
         }
      });
   }
   
   private void getFileContent(File file)
   {
      VirtualFileSystem.getInstance().getContent(file, new FileCallback()
      {
         @Override
         protected void onSuccess(File result)
         {
            eventBus.fireEvent(new FileContentReceivedEvent(result));
         }
      });
   }
   
}