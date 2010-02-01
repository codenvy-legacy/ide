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
package org.exoplatform.ideall.client.action;

import org.exoplatform.gwt.commons.client.Handlers;
import org.exoplatform.gwt.commons.smartgwt.dialogs.BooleanReceivedCallback;
import org.exoplatform.gwt.commons.smartgwt.dialogs.Dialogs;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.data.DataService;
import org.exoplatform.ideall.client.model.data.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.data.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.data.event.MoveCompleteHandler;

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

public class MoveItemPresenter implements MoveCompleteHandler, FileContentSavedHandler
{

   public interface Display
   {

      HasValue<String> getItemPathField();

      HasClickHandlers getMoveButton();

      HasClickHandlers getCancelButton();
      
      HasKeyPressHandlers getItemPathFieldKeyPressHandler();

      void closeForm();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   private ApplicationContext context;

   public MoveItemPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
      handlers.addHandler(MoveCompleteEvent.TYPE, this);
      handlers.addHandler(FileContentSavedEvent.TYPE, this);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      display.getItemPathField().setValue(context.getSelectedItem().getPath());

      display.getMoveButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            move();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });
      
      display.getItemPathFieldKeyPressHandler().addKeyPressHandler(new KeyPressHandler() {

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER) 
            {
               move();            
            }             
         }
         
      });
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   protected void move()
   {
      String path = display.getItemPathField().getValue();

      if (path.equals(context.getSelectedItem().getPath()))
      {
         Dialogs.showError("Can't move / rename resource!");
         return;
      }

      String selectedItemPath = context.getSelectedItem().getPath();
      if (hasOpenedFiles(selectedItemPath))
      {
         Dialogs.ask("Move", "Save opened files?", new BooleanReceivedCallback()
         {
            public void execute(Boolean value)
            {
               if (value != null && value == true)
               {
                  saveNextOpenedFile(context.getSelectedItem().getPath());
               }
            }

         });

         return;
      }

      DataService.getInstance().move(context.getSelectedItem(), path);
   }

   private boolean hasOpenedFiles(String path)
   {
      for (String key : context.getOpenedFiles().keySet())
      {
         if (key.startsWith(path))
         {
            File file = context.getOpenedFiles().get(key);
            if (file.isContentChanged() || file.isPropertiesChanged())
            {
               return true;
            }
         }
      }

      return false;
   }

   private boolean saveNextOpenedFile(String path)
   {
      for (String key : context.getOpenedFiles().keySet())
      {
         if (key.startsWith(path))
         {
            File file = context.getOpenedFiles().get(key);
            if (file.isContentChanged())
            {
               DataService.getInstance().saveFileContent(file, file.getPath());
               return true;
            }
         }
      }

      return false;
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
      display.closeForm();
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (saveNextOpenedFile(context.getSelectedItem().getPath()))
      {
         return;
      }

      String path = display.getItemPathField().getValue();
      DataService.getInstance().move(context.getSelectedItem(), path);
   }

}
