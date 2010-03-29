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

import java.util.ArrayList;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.ui.client.dialogs.Dialogs;
import org.exoplatform.gwtframework.ui.client.dialogs.callback.BooleanValueReceivedCallback;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
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

      HasValue<String> getItemHrefField();

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
      display.getItemHrefField().setValue(
         context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getHref());

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

      display.getItemPathFieldKeyPressHandler().addKeyPressHandler(new KeyPressHandler()
      {

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

   //   TODO
   protected void move()
   {
      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);

      String href = display.getItemHrefField().getValue();

      if (href.equals(item.getHref()))
      {
         Dialogs.getInstance().showError("Can't move / rename resource!");
         return;
      }

      String selectedItemPath = item.getHref();
      if (hasOpenedFiles(selectedItemPath))
      {
         Dialogs.getInstance().ask("Move", "Save opened files?", new BooleanValueReceivedCallback()
         {
            public void execute(Boolean value)
            {
               if (value != null && value == true)
               {
                  saveNextOpenedFile(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getHref());
               }
            }

         });

         return;
      }
      handlers.addHandler(FileContentSavedEvent.TYPE, this);
      VirtualFileSystem.getInstance().move(item, href);
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
               VirtualFileSystem.getInstance().saveFileContent(file);
               return true;
            }
         }
      }

      return false;
   }

   //
   /**
    * @param source
    * @param destination
    * @return
    */
   private boolean isSameFolder(String source, String destination)
   {
      source = source.substring(0, source.lastIndexOf("/"));
      destination = destination.substring(0, destination.lastIndexOf("/"));
      return source.equals(destination);
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
      //      String dest = event.getDestination();
      //      ArrayList<String> keys = new ArrayList<String>();
      //      for (String key : context.getOpenedFiles().keySet())
      //      {
      //         keys.add(key);
      //      }
      //
      //      for (String key : keys)
      //      {
      //         if (key.startsWith(event.getItem().getHref()))
      //         {
      //            File file = context.getOpenedFiles().get(key);
      //            String sourcePath = file.getHref();
      //            String destinationHref = file.getHref();
      //
      //            destinationHref = destinationHref.substring(event.getItem().getHref().length());
      //            destinationHref = dest + destinationHref;
      //            System.out.println("move open file to: " + destinationHref);
      //            file.setHref(destinationHref);
      //            eventBus.fireEvent(new OpenFileEvent(file));
      //         }
      //      }

      //            TODO
      String source = event.getItem().getHref();
      String destination = event.getDestination();

      if (isSameFolder(source, destination))
      {
         String href = source.substring(0, source.lastIndexOf("/"));
         eventBus.fireEvent(new RefreshBrowserEvent(new Folder(href)));         
      }
      else
      {
         String href1 = source.substring(0, source.lastIndexOf("/"));
         String href2 = destination.substring(0, destination.lastIndexOf("/"));

         List<Folder> folders = new ArrayList<Folder>();
         folders.add(new Folder(href1));
         folders.add(new Folder(href2));
         eventBus.fireEvent(new RefreshBrowserEvent(folders, new Folder(destination)));
         //VirtualFileSystem.getInstance().getChildren();
      }
      display.closeForm();
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      //      TODO
      if (context.getSelectedItems(context.getSelectedNavigationPanel()).size() != 1
         && saveNextOpenedFile(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getHref()))
      {
         return;
      }

      String path = display.getItemHrefField().getValue();
      VirtualFileSystem.getInstance().move(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0), path);
   }

}
