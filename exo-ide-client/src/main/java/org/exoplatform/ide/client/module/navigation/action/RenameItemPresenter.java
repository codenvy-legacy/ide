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
package org.exoplatform.ide.client.module.navigation.action;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ide.client.editor.event.EditorReplaceFileEvent;
import org.exoplatform.ide.client.module.navigation.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.MoveCompleteEvent;
import org.exoplatform.ide.client.framework.vfs.event.MoveCompleteHandler;

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

public class RenameItemPresenter implements MoveCompleteHandler, ItemPropertiesReceivedHandler
{

   public interface Display
   {

      HasValue<String> getItemNameField();

      HasClickHandlers getRenameButton();

      HasClickHandlers getCancelButton();

      HasKeyPressHandlers getItemNameFieldKeyPressHandler();

      void closeForm();

   }

   private HandlerManager eventBus;

   private Display display;

   private Handlers handlers;

   //private ApplicationContext context;

   private String itemBaseHref;

   private List<Item> selectedItems;

   private Map<String, File> openedFiles;

   private Map<String, String> lockTokens;

   public RenameItemPresenter(HandlerManager eventBus, List<Item> selectedItems, Map<String, File> openedFiles,
      Map<String, String> lockTokens)
   {
      this.eventBus = eventBus;
      this.selectedItems = selectedItems;
      this.openedFiles = openedFiles;
      this.lockTokens = lockTokens;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      itemBaseHref = selectedItems.get(0).getHref();
      if (selectedItems.get(0) instanceof Folder)
      {
         itemBaseHref = itemBaseHref.substring(0, itemBaseHref.lastIndexOf("/"));
      }
      itemBaseHref = itemBaseHref.substring(0, itemBaseHref.lastIndexOf("/") + 1);

      display.getItemNameField().setValue(selectedItems.get(0).getName());

      display.getRenameButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            rename();
         }
      });

      display.getCancelButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.closeForm();
         }
      });

      display.getItemNameFieldKeyPressHandler().addKeyPressHandler(new KeyPressHandler()
      {

         public void onKeyPress(KeyPressEvent event)
         {
            if (event.getCharCode() == KeyCodes.KEY_ENTER)
            {
               rename();
            }
         }

      });
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   protected void rename()
   {
      handlers.addHandler(MoveCompleteEvent.TYPE, this);
      handlers.addHandler(ItemPropertiesReceivedEvent.TYPE, this);

      final Item item = selectedItems.get(0);

      final String destination = getDestination(item);

      if (destination.equals(item.getHref()))
      {
         Dialogs.getInstance().showError("Can't rename resource!");
         return;
      }

      VirtualFileSystem.getInstance().move(item, destination, lockTokens.get(item.getHref()));
   }

   private String getDestination(Item item)
   {
      String href = display.getItemNameField().getValue();

      href = itemBaseHref + href;

      if (item instanceof Folder)
      {
         href += "/";
      }
      return href;
   }

   //
   //   private boolean saveNextOpenedFile(String path)
   //   {
   //      for (String key : openedFiles.keySet())
   //      {
   //         if (key.startsWith(path))
   //         {
   //            File file = openedFiles.get(key);
   //            if (file.isContentChanged())
   //            {
   //               VirtualFileSystem.getInstance().saveContent(file, lockTokens.get(file.getHref()));
   //               return true;
   //            }
   //         }
   //      }
   //
   //      return false;
   //   }
   //
   private void updateOpenedFiles(String href, String sourceHref)
   {
      List<String> keys = new ArrayList<String>();
      for (String key : openedFiles.keySet())
      {
         keys.add(key);
      }

      for (String key : keys)
      {
         if (key.startsWith(sourceHref))
         {
            File file = openedFiles.get(key);
            String fileHref = file.getHref().replace(sourceHref, href);
            file.setHref(fileHref);

            openedFiles.remove(key);
            openedFiles.put(fileHref, file);
            eventBus.fireEvent(new EditorReplaceFileEvent(file, null));
         }
      }
   }

   private Item renamedItem;

   private String sourceHref;

   //
   public void onMoveComplete(MoveCompleteEvent event)
   {
      renamedItem = event.getItem();
      sourceHref = event.getSourceHref();

      if (event.getItem() instanceof File)
      {
         File file = (File)event.getItem();

         if (openedFiles.containsKey(event.getSourceHref()))
         {
            File openedFile = openedFiles.get(event.getSourceHref());
            openedFile.setHref(file.getHref());
            openedFiles.remove(event.getSourceHref());
            openedFiles.put(openedFile.getHref(), openedFile);

            eventBus.fireEvent(new EditorReplaceFileEvent(file, null));
         }

         VirtualFileSystem.getInstance().getProperties(event.getItem());
      }
      else
      {
         updateOpenedFiles(event.getItem().getHref(), event.getSourceHref());
         completeMove();
      }

   }

   public void onItemPropertiesReceived(ItemPropertiesReceivedEvent event)
   {
      if (event.getItem().getHref().equals(renamedItem.getHref()) && openedFiles.get(renamedItem.getHref()) != null)
      {
         openedFiles.get(renamedItem.getHref()).getProperties().clear();
         openedFiles.get(renamedItem.getHref()).getProperties().addAll(event.getItem().getProperties());
      }
      completeMove();
   }

   private void completeMove()
   {
      String href = sourceHref;
      if (href.endsWith("/"))
      {
         href = href.substring(0, href.length() - 1);
      }

      href = href.substring(0, href.lastIndexOf("/") + 1);
      eventBus.fireEvent(new RefreshBrowserEvent(new Folder(href), renamedItem));

      handlers.removeHandlers();
      display.closeForm();
   }
   //
   //   public void onFileContentSaved(FileContentSavedEvent event)
   //   {
   //      if (selectedItems.size() != 1 && saveNextOpenedFile(selectedItems.get(0).getHref()))
   //      {
   //         return;
   //      }
   //
   //      String href = getDestination(selectedItems.get(0));
   //      VirtualFileSystem.getInstance().move(selectedItems.get(0), href);
   //   }

}
