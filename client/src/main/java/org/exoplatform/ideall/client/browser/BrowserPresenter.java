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

package org.exoplatform.ideall.client.browser;

import java.util.Collections;
import java.util.Comparator;

import org.exoplatform.ideall.client.Handlers;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserHandler;
import org.exoplatform.ideall.client.event.file.ItemSelectedEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.File;
import org.exoplatform.ideall.client.model.Folder;
import org.exoplatform.ideall.client.model.Item;
import org.exoplatform.ideall.client.model.Workspace;
import org.exoplatform.ideall.client.model.data.DataService;
import org.exoplatform.ideall.client.model.data.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.data.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.data.event.FolderContentReceivedEvent;
import org.exoplatform.ideall.client.model.data.event.FolderContentReceivedHandler;
import org.exoplatform.ideall.client.model.data.event.FolderCreatedEvent;
import org.exoplatform.ideall.client.model.data.event.FolderCreatedHandler;
import org.exoplatform.ideall.client.model.data.event.ItemDeletedEvent;
import org.exoplatform.ideall.client.model.data.event.ItemDeletedHandler;
import org.exoplatform.ideall.client.model.data.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.data.event.MoveCompleteHandler;
import org.exoplatform.ideall.client.workspace.event.SwitchWorkspaceEvent;
import org.exoplatform.ideall.client.workspace.event.SwitchWorkspaceHandler;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.dom.client.HasDoubleClickHandlers;
import com.google.gwt.event.logical.shared.HasOpenHandlers;
import com.google.gwt.event.logical.shared.HasSelectionHandlers;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.ui.HasValue;

/**
 * Created by The eXo Platform SAS.
 * 
 * Handlers of BrowserPanel events
 * 
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class BrowserPresenter implements FolderCreatedHandler, ItemDeletedHandler, FileContentSavedHandler,
   RefreshBrowserHandler, FolderContentReceivedHandler, MoveCompleteHandler, SwitchWorkspaceHandler,
   RegisterEventHandlersHandler
{

   interface Display
   {

      HasValue<Item> getBrowserTree();

      HasOpenHandlers<Item> getBrowserTreeNavigator();

      HasSelectionHandlers<Item> getBrowserTreeSelectable();

      HasDoubleClickHandlers getBrowserTreeDClickable();

      HasClickHandlers getBrowserClickable();

   }

   private Display display;

   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationContext context;

   private String folderToUpdate;

   public BrowserPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   void bindDisplay(Display display)
   {
      this.display = display;

      display.getBrowserTreeNavigator().addOpenHandler(new OpenHandler<Item>()
      {
         public void onOpen(OpenEvent<Item> event)
         {
            onFolderOpened((Folder)event.getTarget());
         }
      });

      display.getBrowserTreeSelectable().addSelectionHandler(new SelectionHandler<Item>()
      {
         public void onSelection(SelectionEvent<Item> event)
         {
            onItemSelected(event.getSelectedItem());
         }
      });

      display.getBrowserTreeDClickable().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            onBrowserDoubleClicked();
         }
      });

      display.getBrowserClickable().addClickHandler(new ClickHandler()
      {
         public void onClick(ClickEvent event)
         {
            //eventBus.fireEvent(new BrowserFormSelectedEvent());
            System.out.println("navigator panel selected");
         }
      });
   }

   protected void onFolderOpened(Folder openedFolder)
   {
      if (openedFolder.getChildren() == null)
      {
         DataService.getInstance().getFolderContent(openedFolder.getPath());
      }
   }

   protected void onItemSelected(Item item)
   {
      if (item == context.getSelectedItem())
      {
         return;
      }

      context.setSelectedItem(item);
      eventBus.fireEvent(new ItemSelectedEvent(item));
   }

   protected void onBrowserDoubleClicked()
   {
      if (context.getSelectedItem() == null)
      {
         return;
      }

      if (context.getSelectedItem() instanceof File)
      {
         DataService.getInstance().getFileContent((File)context.getSelectedItem());
      }
   }

   public void onRefreshBrowser()
   {
      String selectedItemPath = context.getSelectedItem().getPath();
      if (context.getSelectedItem() instanceof File)
      {
         selectedItemPath = selectedItemPath.substring(0, selectedItemPath.lastIndexOf("/"));
      }

      Folder folder = new Folder(selectedItemPath);
      DataService.getInstance().getFolderContent(folder.getPath());
   }

   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (!event.isNewFile() && !event.isSaveAs())
      {
         return;
      }

      String path = event.getPath();
      path = path.substring(0, path.lastIndexOf("/")) + "/";
      DataService.getInstance().getFolderContent(path);
   }

   private void switchWorkspace()
   {
      String path = "/" + context.getRepository() + "/" + context.getWorkspace();

      Workspace workspace = new Workspace(path);
      workspace.setIcon(Images.FileTypes.WORKSPACE);
      context.setSelectedItem(workspace);
      display.getBrowserTree().setValue(workspace);

      eventBus.fireEvent(new ItemSelectedEvent(workspace));

      DataService.getInstance().getFolderContent(workspace.getPath());
   }

   public void onSwitchWorkspace(SwitchWorkspaceEvent event)
   {
      context.setRepository(event.getRepository());
      context.setWorkspace(event.getWorkspace());
      switchWorkspace();
   }

   private Comparator<Item> comparator = new Comparator<Item>()
   {
      public int compare(Item item1, Item item2)
      {
         if (item1 instanceof Folder && item2 instanceof File)
         {
            return -1;
         }
         else if (item1 instanceof File && item2 instanceof Folder)
         {
            return 1;
         }
         return item1.getPath().compareToIgnoreCase(item2.getPath());
      }
   };

   public void onFolderContentReceivedEx(FolderContentReceivedEvent event)
   {
      Collections.sort(event.getFolder().getChildren(), comparator);

      for (Item i : event.getFolder().getChildren())
      {
         System.out.println("item [" + i.getPath() + "]");
      }

      display.getBrowserTree().setValue(event.getFolder());

      if (folderToUpdate != null)
      {
         String path = folderToUpdate;
         folderToUpdate = null;
         DataService.getInstance().getFolderContent(path);
      }
   }

   public void onFolderCreated(FolderCreatedEvent event)
   {
      String path = event.getPath();
      path = path.substring(0, path.lastIndexOf("/"));
      DataService.getInstance().getFolderContent(context.getSelectedItem().getPath());
   }

   public void onItemDeleted(ItemDeletedEvent event)
   {
      String path = event.getItem().getPath();
      path = path.substring(0, path.lastIndexOf("/"));
      DataService.getInstance().getFolderContent(path);
   }

   public void onMoveComplete(MoveCompleteEvent event)
   {
      String source = event.getItem().getPath();
      String destination = event.getDestination();

      if (isSameFolder(source, destination))
      {
         String path = source.substring(0, source.lastIndexOf("/"));
         DataService.getInstance().getFolderContent(path);
      }
      else
      {
         String path1 = source.substring(0, source.lastIndexOf("/"));
         String path2 = destination.substring(0, destination.lastIndexOf("/"));

         folderToUpdate = path2;
         DataService.getInstance().getFolderContent(path1);
      }
   }

   private boolean isSameFolder(String source, String destination)
   {
      source = source.substring(0, source.lastIndexOf("/"));
      destination = destination.substring(0, destination.lastIndexOf("/"));
      return source.equals(destination);
   }

   private void registerHandlers()
   {
      handlers.addHandler(FolderCreatedEvent.TYPE, this);
      handlers.addHandler(ItemDeletedEvent.TYPE, this);
      handlers.addHandler(FileContentSavedEvent.TYPE, this);

      handlers.addHandler(RefreshBrowserEvent.TYPE, this);

      handlers.addHandler(FolderContentReceivedEvent.TYPE, this);
      handlers.addHandler(MoveCompleteEvent.TYPE, this);
      handlers.addHandler(SwitchWorkspaceEvent.TYPE, this);
   }

   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      registerHandlers();
      switchWorkspace();
   }

}
