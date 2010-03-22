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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownHandler;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ideall.client.Images;
import org.exoplatform.ideall.client.application.event.InitializeApplicationEvent;
import org.exoplatform.ideall.client.application.event.InitializeApplicationHandler;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent;
import org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler;
import org.exoplatform.ideall.client.browser.event.BrowserPanelSelectedEvent;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserHandler;
import org.exoplatform.ideall.client.browser.event.SelectBrowserPanelEvent;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.event.browse.SetFocusOnItemEvent;
import org.exoplatform.ideall.client.event.browse.SetFocusOnItemHandler;
import org.exoplatform.ideall.client.event.file.OpenFileEvent;
import org.exoplatform.ideall.client.event.file.SelectedItemsEvent;
import org.exoplatform.ideall.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.Workspace;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.FolderContentReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FolderContentReceivedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.FolderCreatedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.FolderCreatedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteHandler;
import org.exoplatform.ideall.client.workspace.event.SwitchWorkspaceEvent;
import org.exoplatform.ideall.client.workspace.event.SwitchWorkspaceHandler;

import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;

/**
 * Created by The eXo Platform SAS.
 * 
 * Handlers of BrowserPanel events
 * 
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class BrowserPresenter implements FolderCreatedHandler, FileContentSavedHandler,
   RefreshBrowserHandler, FolderContentReceivedHandler, MoveCompleteHandler, SwitchWorkspaceHandler,
   RegisterEventHandlersHandler, InitializeApplicationHandler, SetFocusOnItemHandler, ExceptionThrownHandler
{

   interface Display
   {

      TreeGridItem<Item> getBrowserTree();
      
      List<Item> getSelectedItems();

      void selectItem(String path);

   }

   private Display display;

   private HandlerManager eventBus;

   private Handlers handlers;

   private ApplicationContext context;

   private String folderToUpdate;

   private String forlderToSelect;

   private List<Item> selectedItems = new ArrayList<Item>();

   public BrowserPresenter(HandlerManager eventBus, ApplicationContext context)
   {
      this.eventBus = eventBus;
      this.context = context;
      handlers = new Handlers(eventBus);
      handlers.addHandler(RegisterEventHandlersEvent.TYPE, this);
      handlers.addHandler(InitializeApplicationEvent.TYPE, this);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   void bindDisplay(Display display)
   {
      this.display = display;

      display.getBrowserTree().addOpenHandler(new OpenHandler<Item>()
      {
         public void onOpen(OpenEvent<Item> event)
         {
            onFolderOpened((Folder)event.getTarget());
         }
      });

      display.getBrowserTree().addSelectionHandler(new SelectionHandler<Item>()
      {
         public void onSelection(SelectionEvent<Item> event)
         {
            onItemSelected(event.getSelectedItem());
         }
      });

      display.getBrowserTree().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            onBrowserDoubleClicked();
         }
      });
   }

   /**
    * Handling of folder opened event from browser
    * 
    * @param openedFolder
    */
   protected void onFolderOpened(Folder openedFolder)
   {
      if (openedFolder.getChildren() == null)
      {
         VirtualFileSystem.getInstance().getFolderContent(openedFolder.getPath());
      }
   }
   
   /**
    * 
    * Handling item selected event from browser
    * @param item
    */
   protected void onItemSelected(Item item)
   {
      updateSelectionTimer.cancel();
      updateSelectionTimer.schedule(10);
      
//      updateSelectionTimer.cancel();
//      
//      if (selectedItems.contains(item)) {
//         selectedItems.remove(item);
//      } else {
//         selectedItems.add(item);
//      }
//      
//      updateSelectionTimer.schedule(10);
   }
   
   private Timer updateSelectionTimer = new Timer() {

      @Override
      public void run()
      {
         selectedItems = display.getSelectedItems();
         
         context.getSelectedItems().clear();
         context.getSelectedItems().addAll(selectedItems);
         
         eventBus.fireEvent(new SelectedItemsEvent(selectedItems));
      }
      
   };

   /**
    * Handling of mouse double clicking
    */
   protected void onBrowserDoubleClicked()
   {
      if (context.getSelectedItems().size() != 1)
      {
         return;
      }

      Item item = selectedItems.get(0);
      
      if (item instanceof File)
      {
         context.setSelectedEditorDescriptor(null);
         //VirtualFileSystem.getInstance().getFileContent((File)item);
         eventBus.fireEvent(new OpenFileEvent((File)item));
      }
   }

   /**
    * Refreshing browser content ( selected item path only )
    * 
    * @see org.exoplatform.ideall.client.browser.event.RefreshBrowserHandler#onRefreshBrowser()
    */
   public void onRefreshBrowser()
   {
      if (context.getSelectedItems().size() != 1)
      {
         return;
      }
      Item item = context.getSelectedItems().get(0);
      String selectedItemPath = item.getPath();
      if (item instanceof File)
      {
         selectedItemPath = selectedItemPath.substring(0, selectedItemPath.lastIndexOf("/"));
      }

      Folder folder = new Folder(selectedItemPath);
      VirtualFileSystem.getInstance().getFolderContent(folder.getPath());
   }

   /**
    * Handling of file content saved event.
    * After saving folder which contains saved file will be refreshed. 
    * 
    * @see org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler#onFileContentSaved(org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent)
    */
   public void onFileContentSaved(FileContentSavedEvent event)
   {
      if (!event.isNewFile() && !event.isSaveAs())
      {
         return;
      }

      String path = event.getPath();
      path = path.substring(0, path.lastIndexOf("/")) + "/";
      VirtualFileSystem.getInstance().getFolderContent(path);
   }

   /**
    * Switching active workspace
    */
   private void switchWorkspace()
   {
      String path = "/" + context.getRepository() + "/" + context.getWorkspace();

      Workspace workspace = new Workspace(path);
      workspace.setIcon(Images.FileTypes.WORKSPACE);
      
      selectedItems.clear();
      selectedItems.add(workspace);
      context.getSelectedItems().clear();
      context.getSelectedItems().add(workspace);
      
      display.getBrowserTree().setValue(workspace);

      eventBus.fireEvent(new SelectedItemsEvent(selectedItems));

      VirtualFileSystem.getInstance().getFolderContent(workspace.getPath());
   }

   /**
    * Switching active workspace by Switch Workspace Event
    * 
    * @see org.exoplatform.ideall.client.workspace.event.SwitchWorkspaceHandler#onSwitchWorkspace(org.exoplatform.ideall.client.workspace.event.SwitchWorkspaceEvent)
    */
   public void onSwitchWorkspace(SwitchWorkspaceEvent event)
   {
      context.setRepository(event.getRepository());
      context.setWorkspace(event.getWorkspace());

      CookieManager.storeRepository(event.getRepository());
      CookieManager.storeWorkspace(event.getWorkspace());

      switchWorkspace();
   }

   /**
    * Comparator for comparing items in received directory.
    */
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

   /**
    * Handling folder content receiving.
    * Browser subtree should be refreshed and browser panel should be selected.
    * 
    * @see org.exoplatform.ideall.client.model.vfs.api.event.FolderContentReceivedHandler#onFolderContentReceived(org.exoplatform.ideall.client.model.vfs.api.event.FolderContentReceivedEvent)
    */
   public void onFolderContentReceived(FolderContentReceivedEvent event)
   {
      Collections.sort(event.getFolder().getChildren(), comparator);

      display.getBrowserTree().setValue(event.getFolder());
      eventBus.fireEvent(new RestorePerspectiveEvent());
      eventBus.fireEvent(new SelectBrowserPanelEvent());

      if (forlderToSelect != null)
      {
         display.selectItem(forlderToSelect);
         forlderToSelect = null;
      }

      if (folderToUpdate != null)
      {
         String path = folderToUpdate;
         folderToUpdate = null;
         VirtualFileSystem.getInstance().getFolderContent(path);
      }
   }

   /**
    * Handling folder created event.
    * Browser should be refreshed.
    * 
    * @see org.exoplatform.ideall.client.model.vfs.api.event.FolderCreatedHandler#onFolderCreated(org.exoplatform.ideall.client.model.vfs.api.event.FolderCreatedEvent)
    */
   public void onFolderCreated(FolderCreatedEvent event)
   {
      if(context.getSelectedItems().size() != 1)
      {
         return;
      }
   
      Item item = context.getSelectedItems().get(0);
      String path = event.getPath();
      forlderToSelect = path;
      path = path.substring(0, path.lastIndexOf("/"));
      VirtualFileSystem.getInstance().getFolderContent(item.getPath());
   }

//   /**
//    * Handling item deleted event.
//    * Browser should be refreshed.
//    * 
//    * @see org.exoplatform.ideall.client.model.data.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ideall.client.model.data.event.ItemDeletedEvent)
//    */
//   public void onItemDeleted(ItemDeletedEvent event)
//   {
//      String path = event.getItem().getPath();
//      path = path.substring(0, path.lastIndexOf("/"));
//      DataService.getInstance().getFolderContent(path);
//   }

   /**
    * Handling item moved event.
    * Refreshing source and destination folders.
    * 
    * @see org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteHandler#onMoveComplete(org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent)
    */
   public void onMoveComplete(MoveCompleteEvent event)
   {
      String source = event.getItem().getPath();
      String destination = event.getDestination();

      if (isSameFolder(source, destination))
      {
         String path = source.substring(0, source.lastIndexOf("/"));
         VirtualFileSystem.getInstance().getFolderContent(path);
      }
      else
      {
         String path1 = source.substring(0, source.lastIndexOf("/"));
         String path2 = destination.substring(0, destination.lastIndexOf("/"));

         folderToUpdate = path2;
         VirtualFileSystem.getInstance().getFolderContent(path1);
      }
   }

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

   /**
    * Registering handlers
    * 
    * @see org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler#onRegisterEventHandlers(org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent)
    */
   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.addHandler(FolderCreatedEvent.TYPE, this);
      //handlers.addHandler(ItemDeletedEvent.TYPE, this);
      handlers.addHandler(FileContentSavedEvent.TYPE, this);

      handlers.addHandler(RefreshBrowserEvent.TYPE, this);

      handlers.addHandler(FolderContentReceivedEvent.TYPE, this);
      handlers.addHandler(MoveCompleteEvent.TYPE, this);
      handlers.addHandler(SwitchWorkspaceEvent.TYPE, this);

      handlers.addHandler(SetFocusOnItemEvent.TYPE, this);

      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
   }

   /**
    * Initializing application
    * 
    * @see org.exoplatform.ideall.client.application.event.InitializeApplicationHandler#onInitializeApplication(org.exoplatform.ideall.client.application.event.InitializeApplicationEvent)
    */
   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      switchWorkspace();
      eventBus.fireEvent(new BrowserPanelSelectedEvent());
   }

   /**
    * Select chosen item in browser.
    * 
    * @see org.exoplatform.ideall.client.event.browse.SetFocusOnItemHandler#onSetFocusOnItem(org.exoplatform.ideall.client.event.browse.SetFocusOnItemEvent)
    */
   public void onSetFocusOnItem(SetFocusOnItemEvent event)
   {
      display.selectItem(event.getPath());
   }

   public void onError(ExceptionThrownEvent event)
   {
      forlderToSelect = null;
   }

}
