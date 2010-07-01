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
import org.exoplatform.gwtframework.commons.dialogs.Dialogs;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.editor.event.EditorUpdateFileStateEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.vfs.api.File;
import org.exoplatform.ideall.vfs.api.Folder;
import org.exoplatform.ideall.vfs.api.Item;
import org.exoplatform.ideall.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.vfs.api.event.ItemPropertiesReceivedEvent;
import org.exoplatform.ideall.vfs.api.event.ItemPropertiesReceivedHandler;
import org.exoplatform.ideall.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.vfs.api.event.MoveCompleteHandler;

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

public class RenameItemPresenter implements MoveCompleteHandler, FileContentSavedHandler, ItemPropertiesReceivedHandler
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

   private ApplicationContext context;

   private String itemBaseHref;

   public RenameItemPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
   }

   public void bindDisplay(Display d)
   {
      display = d;

      itemBaseHref = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getHref();
      if (context.getSelectedItems(context.getSelectedNavigationPanel()).get(0) instanceof Folder)
      {
         itemBaseHref = itemBaseHref.substring(0, itemBaseHref.lastIndexOf("/"));
      }
      itemBaseHref = itemBaseHref.substring(0, itemBaseHref.lastIndexOf("/") + 1);

      display.getItemNameField().setValue(
         context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getName());

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

      final Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);

      final String href = getPathToRename(item);

      if (href.equals(item.getHref()))
      {
         Dialogs.getInstance().showError("Can't rename resource!");
         return;
      }

      VirtualFileSystem.getInstance().move(item, href);
   }

   private String getPathToRename(Item item)
   {
      String href = display.getItemNameField().getValue();

      href = itemBaseHref + href;

      if (item instanceof Folder)
      {
         href += "/";
      }
      return href;
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
               VirtualFileSystem.getInstance().saveContent(file);
               return true;
            }
         }
      }

      return false;
   }

   private void updateOpenedFiles(String href, String sourceHref)
   {
      List<String> keys = new ArrayList<String>();
      for (String key : context.getOpenedFiles().keySet())
      {
         keys.add(key);
      }

      for (String key : keys)
      {
         if (key.startsWith(sourceHref))
         {
            File file = context.getOpenedFiles().get(key);
            String fileHref = file.getHref().replace(sourceHref, href);
            file.setHref(fileHref);

            context.getOpenedFiles().remove(key);
            context.getOpenedFiles().put(fileHref, file);
            eventBus.fireEvent(new EditorUpdateFileStateEvent(file));
         }
      }
   }

   private Item renamedItem;

   private String sourceHref;

   public void onMoveComplete(MoveCompleteEvent event)
   {
      renamedItem = event.getItem();
      sourceHref = event.getSourceHref();

      if (event.getItem() instanceof File)
      {
         File file = (File)event.getItem();

         if (context.getOpenedFiles().containsKey(event.getSourceHref()))
         {
            File openedFle = context.getOpenedFiles().get(event.getSourceHref());
            openedFle.setHref(file.getHref());
            context.getOpenedFiles().remove(event.getSourceHref());
            context.getOpenedFiles().put(openedFle.getHref(), openedFle);

            eventBus.fireEvent(new EditorUpdateFileStateEvent(file));
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
      if (event.getItem().getHref().equals(renamedItem.getHref()) && context.getOpenedFiles().get(renamedItem.getHref()) != null)
      {
         context.getOpenedFiles().get(renamedItem.getHref()).getProperties().clear();
         context.getOpenedFiles().get(renamedItem.getHref()).getProperties().addAll(event.getItem().getProperties());
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

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (context.getSelectedItems(context.getSelectedNavigationPanel()).size() != 1
         && saveNextOpenedFile(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0).getHref()))
      {
         return;
      }

      String href = getPathToRename(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0));
      VirtualFileSystem.getInstance().move(context.getSelectedItems(context.getSelectedNavigationPanel()).get(0), href);
   }

}
