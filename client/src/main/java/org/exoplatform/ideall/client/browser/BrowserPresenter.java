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
import org.exoplatform.ideall.client.browser.event.ItemsSelectedEvent;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserEvent;
import org.exoplatform.ideall.client.browser.event.RefreshBrowserHandler;
import org.exoplatform.ideall.client.browser.event.SelectItemEvent;
import org.exoplatform.ideall.client.browser.event.SelectItemHandler;
import org.exoplatform.ideall.client.cookie.CookieManager;
import org.exoplatform.ideall.client.event.file.OpenFileEvent;
import org.exoplatform.ideall.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ideall.client.model.ApplicationContext;
import org.exoplatform.ideall.client.model.vfs.api.File;
import org.exoplatform.ideall.client.model.vfs.api.Folder;
import org.exoplatform.ideall.client.model.vfs.api.Item;
import org.exoplatform.ideall.client.model.vfs.api.VirtualFileSystem;
import org.exoplatform.ideall.client.model.vfs.api.event.ChildrenReceivedEvent;
import org.exoplatform.ideall.client.model.vfs.api.event.ChildrenReceivedHandler;
import org.exoplatform.ideall.client.model.vfs.api.event.MoveCompleteEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ideall.client.panel.event.PanelSelectedHandler;
import org.exoplatform.ideall.client.panel.event.SelectPanelEvent;
import org.exoplatform.ideall.client.workspace.event.SwitchEntryPointEvent;
import org.exoplatform.ideall.client.workspace.event.SwitchEntryPointHandler;

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
public class BrowserPresenter implements  RefreshBrowserHandler,
   ChildrenReceivedHandler, SwitchEntryPointHandler, RegisterEventHandlersHandler,
   InitializeApplicationHandler, SelectItemHandler, ExceptionThrownHandler, PanelSelectedHandler 
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
            onItemSelected();
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
         VirtualFileSystem.getInstance().getChildren(openedFolder);
      }
   }

   /**
    * 
    * Handling item selected event from browser
    * @param item
    */
   protected void onItemSelected()
   {
      updateSelectionTimer.cancel();
      updateSelectionTimer.schedule(10);
   }

   private Timer updateSelectionTimer = new Timer()
   {

      @Override
      public void run()
      {
         selectedItems = display.getSelectedItems();

         System.out.println("selected items: " + selectedItems.size());
         for (Item i : selectedItems)
         {
            System.out.println(">> " + i.getHref());
         }

         context.getSelectedItems(context.getSelectedNavigationPanel()).clear();
         context.getSelectedItems(context.getSelectedNavigationPanel()).addAll(selectedItems);

         eventBus.fireEvent(new ItemsSelectedEvent(selectedItems));
      }

   };

   /**
    * Handling of mouse double clicking
    */
   protected void onBrowserDoubleClicked()
   {
      if (context.getSelectedItems(context.getSelectedNavigationPanel()).size() != 1)
      {
         return;
      }

      Item item = selectedItems.get(0);

      if (item instanceof File)
      {
         context.setSelectedEditorDescription(null);
         //VirtualFileSystem.getInstance().getFileContent((File)item);
         eventBus.fireEvent(new OpenFileEvent((File)item));
      }
   }

   /**
    * Refreshing browser content ( selected item path only )
    * 
    * @see org.exoplatform.ideall.client.browser.event.RefreshBrowserHandler#onRefreshBrowser()
    */
   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      //TODO df adsfadsflksadm l
      if (context.getSelectedItems(context.getSelectedNavigationPanel()).size() != 1)
      {
         return;
      }
      Item item = context.getSelectedItems(context.getSelectedNavigationPanel()).get(0);
      String href = item.getHref();
      if (item instanceof File)
      {
         href = href.substring(0, href.lastIndexOf("/"));
      }

      Folder folder = new Folder(href);
      VirtualFileSystem.getInstance().getChildren(folder);
   }

//   /**
//    * Handling of file content saved event.
//    * After saving folder which contains saved file will be refreshed. 
//    * 
//    * @see org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedHandler#onFileContentSaved(org.exoplatform.ideall.client.model.vfs.api.event.FileContentSavedEvent)
//    */
//   public void onFileContentSaved(FileContentSavedEvent event)
//   {
//      //      TODO
//      //      if (!event.isNewFile() && !event.isSaveAs())
//      //      {
//      //         return;
//      //      }
//      //
//      //      String path = event.getPath();
//      //      path = path.substring(0, path.lastIndexOf("/")) + "/";
//      //      Folder folder = new Folder(path);
//      //      VirtualFileSystem.getInstance().getChildren(folder);
//   }

   /**
    * Switching active workspace
    */
   private void switchWorkspace()
   {
      Folder rootFolder = new Folder(context.getEntryPoint());
      rootFolder.setIcon(Images.FileTypes.WORKSPACE);

      selectedItems.clear();
      selectedItems.add(rootFolder);
      context.getSelectedItems(context.getSelectedNavigationPanel()).clear();
      context.getSelectedItems(context.getSelectedNavigationPanel()).add(rootFolder);

      display.getBrowserTree().setValue(rootFolder);

      eventBus.fireEvent(new ItemsSelectedEvent(selectedItems));

      VirtualFileSystem.getInstance().getChildren(rootFolder);
   }

   /**
    * Switching active workspace by Switch Workspace Event
    * 
    * @see SwitchEntryPointEvent#onSwitchEntryPoint(org.exoplatform.ideall.client.workspace.event.SwitchEntryPointEvent)
    */
   public void onSwitchEntryPoint(SwitchEntryPointEvent event)
   {
      CookieManager.storeEntryPoint(context.getEntryPoint());
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
         return item1.getHref().compareToIgnoreCase(item2.getHref());
      }
   };

   /**
    * Handling folder content receiving.
    * Browser subtree should be refreshed and browser panel should be selected.
    * 
    */
   public void onChildrenReceived(ChildrenReceivedEvent event)
   {
      Collections.sort(event.getFolder().getChildren(), comparator);

      System.out.println("children received for: " + event.getFolder().getHref());

      display.getBrowserTree().setValue(event.getFolder());
      eventBus.fireEvent(new RestorePerspectiveEvent());

      eventBus.fireEvent(new SelectPanelEvent(BrowserPanel.ID));

      if (forlderToSelect != null)
      {
         display.selectItem(forlderToSelect);
         forlderToSelect = null;
      }

      if (folderToUpdate != null)
      {
         String path = folderToUpdate;
         folderToUpdate = null;
         Folder folder = new Folder(path);
         VirtualFileSystem.getInstance().getChildren(folder);
      }
   }
   
    /**
    * Registering handlers
    * 
    * @see org.exoplatform.ideall.client.application.event.RegisterEventHandlersHandler#onRegisterEventHandlers(org.exoplatform.ideall.client.application.event.RegisterEventHandlersEvent)
    */
   public void onRegisterEventHandlers(RegisterEventHandlersEvent event)
   {
      handlers.addHandler(RefreshBrowserEvent.TYPE, this);

      handlers.addHandler(ChildrenReceivedEvent.TYPE, this);
      handlers.addHandler(SwitchEntryPointEvent.TYPE, this);

      handlers.addHandler(SelectItemEvent.TYPE, this);

      handlers.addHandler(ExceptionThrownEvent.TYPE, this);

      handlers.addHandler(PanelSelectedEvent.TYPE, this);
   }

   /**
    * Initializing application
    * 
    * @see org.exoplatform.ideall.client.application.event.InitializeApplicationHandler#onInitializeApplication(org.exoplatform.ideall.client.application.event.InitializeApplicationEvent)
    */
   public void onInitializeApplication(InitializeApplicationEvent event)
   {
      switchWorkspace();
      //TODO fire new event
      //eventBus.fireEvent(new PanelSelectedEvent(BrowserPanel.ID));
   }

   /**
    * Select chosen item in browser.
    * 
    * @see org.exoplatform.ideall.client.browser.event.SelectItemHandler#onSelectItem(org.exoplatform.ideall.client.browser.event.SelectItemEvent)
    */
   public void onSelectItem(SelectItemEvent event)
   {
      display.selectItem(event.getPath());
   }

   public void onError(ExceptionThrownEvent event)
   {
      forlderToSelect = null;
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (BrowserPanel.ID.equals(event.getPanelId()))
      {
         onItemSelected();
      }
   }

}
