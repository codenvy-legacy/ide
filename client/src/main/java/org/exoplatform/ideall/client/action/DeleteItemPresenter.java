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
import java.util.HashMap;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.ideall.client.Utils;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.browser.event.SelectItemEvent;
import org.exoplatform.ideall.client.editor.event.EditorCloseFileEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemDeletedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ItemDeletedHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerManager;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version @version $Id: $
 */

public class DeleteItemPresenter implements ItemDeletedHandler, ExceptionThrownHandler
{

   public interface Display
   {

      HasClickHandlers getDeleteButton();

      HasClickHandlers getCancelButton();

      void closeForm();

      void hideForm();

   }

   private Display display;

   private List<Item> items;

   private ApplicationContext context;

   private Handlers handlers;

   private HandlerManager eventBus;

   private Item lastDeletedItem;

   public DeleteItemPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.context = context;
      this.eventBus = eventBus;
      items = new ArrayList<Item>();
      items.addAll(context.getSelectedItems(context.getSelectedNavigationPanel()));
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

      display.getDeleteButton().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            display.hideForm();
            deleteNextItem();
         }
      });

      handlers.addHandler(ItemDeletedEvent.TYPE, this);
      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   private void deleteNextItem()
   {
      if (items.size() == 0)
      {
         display.closeForm();
         deleteItemsComplete();
         return;
      }

      Item item = items.get(0);
      items.remove(0);
      VirtualFileSystem.getInstance().deleteItem(item);
   }

   public void onItemDeleted(ItemDeletedEvent event)
   {
      Item item = event.getItem();

      context.getItemsToCopy().remove(item);
      context.getItemsToCut().remove(item);
      if (item instanceof File)
      {
         if (context.getOpenedFiles().get(item.getHref()) != null)
         {
            eventBus.fireEvent(new EditorCloseFileEvent((File)item));
         }
      }
      else
      {
         //find out the files are been in the removed folder

         String href = event.getItem().getHref();
         HashMap<String, File> openedFiles = context.getOpenedFiles();

         HashMap<String, File> copy = new HashMap<String, File>();
         for (String key : openedFiles.keySet())
         {
            File file = openedFiles.get(key);
            copy.put(key, file);
         }

         for (File file : copy.values())
         {
            if (Utils.match(file.getHref(), "^" + href + ".*", ""))
            {
               eventBus.fireEvent(new EditorCloseFileEvent(file, true));
            }
         }
      }
      lastDeletedItem = item;
      deleteNextItem();
   }

   public void onError(ExceptionThrownEvent event)
   {
      display.closeForm();
   }

   private void deleteItemsComplete()
   {
      if (lastDeletedItem == null)
      {
         return;
      }

      String selectedItemHref = lastDeletedItem.getHref();
      
      if(lastDeletedItem instanceof Folder)
      {
         selectedItemHref = selectedItemHref.substring(0, selectedItemHref.lastIndexOf("/"));
      }
      selectedItemHref = selectedItemHref.substring(0, selectedItemHref.lastIndexOf("/") + 1);

      Folder folder = new Folder(selectedItemHref);

      context.getSelectedItems(context.getSelectedNavigationPanel()).clear();
      context.getSelectedItems(context.getSelectedNavigationPanel()).add(folder);

      eventBus.fireEvent(new RefreshBrowserEvent(folder));
      eventBus.fireEvent(new SelectItemEvent(folder.getHref()));
   }

}
