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

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteHandler;

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

   }

   public void bindDisplay(Display d)
   {
      display = d;

//      TODO
//      display.getItemPathField().setValue(
//         context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getPath());
//
//      display.getMoveButton().addClickHandler(new ClickHandler()
//      {
//         public void onClick(ClickEvent event)
//         {
//            move();
//         }
//      });
//
//      display.getCancelButton().addClickHandler(new ClickHandler()
//      {
//         public void onClick(ClickEvent event)
//         {
//            display.closeForm();
//         }
//      });
//
//      display.getItemPathFieldKeyPressHandler().addKeyPressHandler(new KeyPressHandler()
//      {
//
//         public void onKeyPress(KeyPressEvent event)
//         {
//            if (event.getCharCode() == KeyCodes.KEY_ENTER)
//            {
//               move();
//            }
//         }
//
//      });
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

//   TODO
//   protected void move()
//   {
//      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
//
//      String path = display.getItemPathField().getValue();
//
//      if (path.equals(item.getPath()))
//      {
//         Dialogs.getInstance().showError("Can't move / rename resource!");
//         return;
//      }
//
//      String selectedItemPath = item.getPath();
//      if (hasOpenedFiles(selectedItemPath))
//      {
//         Dialogs.getInstance().ask("Move", "Save opened files?", new BooleanValueReceivedCallback()
//         {
//            public void execute(Boolean value)
//            {
//               if (value != null && value == true)
//               {
//                  saveNextOpenedFile(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getPath());
//               }
//            }
//
//         });
//
//         return;
//      }
//      handlers.addHandler(FileContentSavedEvent.TYPE, this);
//      VirtualFileSystem.getInstance().move(item, path);
//   }
//
//   private boolean hasOpenedFiles(String path)
//   {
//      for (String key : context.getOpenedFiles().keySet())
//      {
//         if (key.startsWith(path))
//         {
//            File file = context.getOpenedFiles().get(key);
//            if (file.isContentChanged() || file.isPropertiesChanged())
//            {
//               return true;
//            }
//         }
//      }
//
//      return false;
//   }
//
//   private boolean saveNextOpenedFile(String path)
//   {
//      for (String key : context.getOpenedFiles().keySet())
//      {
//         if (key.startsWith(path))
//         {
//            File file = context.getOpenedFiles().get(key);
//            if (file.isContentChanged())
//            {
//               VirtualFileSystem.getInstance().saveFileContent(file, file.getPath());
//               return true;
//            }
//         }
//      }
//
//      return false;
//   }
//
   public void onMoveComplete(MoveCompleteEvent event)
   {
//      display.closeForm();
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
//      TODO
//      if (context.getSelectedItems(context.getSelectedNavigationPanel()).size() != 1
//         && saveNextOpenedFile(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getPath()))
//      {
//         return;
//      }
//
//      String path = display.getItemPathField().getValue();
//      VirtualFileSystem.getInstance().move(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0), path);
   }

}
