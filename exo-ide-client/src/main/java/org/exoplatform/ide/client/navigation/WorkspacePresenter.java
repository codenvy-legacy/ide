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
package org.exoplatform.ide.client.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.exoplatform.gwtframework.commons.component.Handlers;
import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.gwtframework.commons.webdav.Property;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.ide.client.browser.BrowserPanel;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.event.perspective.RestorePerspectiveEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedEvent;
import org.exoplatform.ide.client.framework.application.event.EntryPointChangedHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.event.SelectViewEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.gwt.ViewDisplay;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedEvent;
import org.exoplatform.ide.client.framework.ui.gwt.ViewOpenedHandler;
import org.exoplatform.ide.client.framework.vfs.File;
import org.exoplatform.ide.client.framework.vfs.Folder;
import org.exoplatform.ide.client.framework.vfs.Item;
import org.exoplatform.ide.client.framework.vfs.ItemPropertiesCallback;
import org.exoplatform.ide.client.framework.vfs.ItemProperty;
import org.exoplatform.ide.client.framework.vfs.VirtualFileSystem;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent;
import org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler;
import org.exoplatform.ide.client.module.navigation.event.DeleteItemEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.CopyItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.CutItemsEvent;
import org.exoplatform.ide.client.module.navigation.event.edit.PasteItemsEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedEvent;
import org.exoplatform.ide.client.panel.event.PanelSelectedHandler;
import org.exoplatform.ide.client.workspace.event.SwitchEntryPointEvent;
import org.exoplatform.ide.client.workspace.event.SwitchEntryPointHandler;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DoubleClickEvent;
import com.google.gwt.event.dom.client.DoubleClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.event.logical.shared.CloseEvent;
import com.google.gwt.event.logical.shared.CloseHandler;
import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.shared.HandlerManager;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;

/**
 * Created by The eXo Platform SAS.
 * 
 * Handlers of BrowserPanel events
 * 
 * @author <a href="mailto:dmitry.ndp@exoplatform.com.ua">Dmytro Nochevnov</a>
 * @version $Id: $
*/
public class WorkspacePresenter implements RefreshBrowserHandler, SwitchEntryPointHandler, SelectItemHandler,
   PanelSelectedHandler, EntryPointChangedHandler, ItemUnlockedHandler, ItemLockResultReceivedHandler,
   ApplicationSettingsReceivedHandler, ViewOpenedHandler, ViewClosedHandler
{

   public interface Display extends ViewDisplay
   {

      static final String ID = "ideWorkspaceView";

      /**
       * @return {@link TreeGridItem}
       */
      TreeGridItem<Item> getBrowserTree();

      /**
       * Get selected items in the tree.
       * 
       * @return {@link List} selected items
       */
      List<Item> getSelectedItems();

      /**
       * Select item in browser tree by path.
       * 
       * @param path item's path
       */
      void selectItem(String path);

      /**
       * Deselect item in browser tree by path.
       * 
       * @param path item's path
       */
      void deselectItem(String path);

      /**
       * Update the state of the item in the tree.
       * 
       * @param file
       */
      void updateItemState(File file);

      /**
       * Set lock tokens to the items in the tree.
       * 
       * @param locktokens
       */
      void setLockTokens(Map<String, String> locktokens);

   }

   private Display display;

   private boolean viewOpened = false;

   private HandlerManager eventBus;

   private Handlers handlers;

   private String itemToSelect;

   private List<Folder> foldersToRefresh = new ArrayList<Folder>();

   private List<Item> selectedItems = new ArrayList<Item>();

   private String entryPoint;

   public WorkspacePresenter(HandlerManager eventBus)
   {
      this.eventBus = eventBus;
      handlers = new Handlers(eventBus);
      handlers.addHandler(RefreshBrowserEvent.TYPE, this);
      handlers.addHandler(EntryPointChangedEvent.TYPE, this);
      handlers.addHandler(ItemUnlockedEvent.TYPE, this);
      handlers.addHandler(ItemLockResultReceivedEvent.TYPE, this);
      handlers.addHandler(SwitchEntryPointEvent.TYPE, this);
      handlers.addHandler(SelectItemEvent.TYPE, this);
      handlers.addHandler(PanelSelectedEvent.TYPE, this);
      handlers.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      
      eventBus.addHandler(ViewOpenedEvent.TYPE, this);
      eventBus.addHandler(ViewClosedEvent.TYPE, this);

      Display display = GWT.create(Display.class);
      bindDisplay(display);
   }

   public void destroy()
   {
      handlers.removeHandlers();
   }

   public void bindDisplay(Display display)
   {
      this.display = display;

      display.getBrowserTree().addOpenHandler(new OpenHandler<Item>()
      {
         public void onOpen(OpenEvent<Item> event)
         {
            onFolderOpened((Folder)event.getTarget());
         }
      });

      display.getBrowserTree().addCloseHandler(new CloseHandler<Item>()
      {

         public void onClose(CloseEvent<Item> event)
         {
            onCloseFolder((Folder)event.getTarget());
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

      display.getBrowserTree().addKeyPressHandler(new KeyPressHandler()
      {
         public void onKeyPress(KeyPressEvent event)
         {
            onKeyPressed(event.getCharCode(), event.isControlKeyDown());
         }

      });
   }

   /**
    * Perform actions when folder is closed in browser tree.
    * 
    * @param folder closed folder
    */
   public void onCloseFolder(Folder folder)
   {
      for (Item item : display.getSelectedItems())
      {
         if (item.getHref().startsWith(folder.getHref()) && !item.getHref().equals(folder.getHref()))
         {
            display.deselectItem(item.getHref());
         }
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

         //context.getSelectedItems(context.getSelectedNavigationPanel()).clear();
         //context.getSelectedItems(context.getSelectedNavigationPanel()).addAll(selectedItems);
         eventBus.fireEvent(new ItemsSelectedEvent(selectedItems, BrowserPanel.ID));
      }
   };

   /**
    * Handling of mouse double clicking
    */
   protected void onBrowserDoubleClicked()
   {
      if (selectedItems.size() != 1)
      {
         return;
      }

      Item item = selectedItems.get(0);

      if (item instanceof File)
      {
         eventBus.fireEvent(new OpenFileEvent((File)item));
      }
   }

   /**
    * Handling of folder opened event from browser
    * 
    * @param openedFolder
    */
   protected void onFolderOpened(Folder openedFolder)
   {
      itemToSelect = null;
      foldersToRefresh = new ArrayList<Folder>();
      foldersToRefresh.add(openedFolder);
      refreshNextFolder();
   }

   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      if (event.getItemToSelect() != null)
      {
         itemToSelect = event.getItemToSelect().getHref();
      }
      else
      {
         List<Item> selectedItems = display.getSelectedItems();
         if (selectedItems.size() > 0)
         {
            itemToSelect = selectedItems.get(0).getHref();
         }
         else
         {
            itemToSelect = null;
         }
      }

      if (event.getFolders() != null)
      {
         foldersToRefresh = event.getFolders();
      }
      else
      {
         foldersToRefresh = new ArrayList<Folder>();

         Item item = selectedItems.get(0);
         if (item instanceof File)
         {
            String href = item.getHref();
            href = href.substring(0, href.lastIndexOf("/") + 1);
            Folder folder = new Folder(href);
            foldersToRefresh.add(folder);
         }
         else
         {
            foldersToRefresh.add((Folder)item);
         }
      }

      refreshNextFolder();
   }

   private void refreshNextFolder()
   {
      if (foldersToRefresh.size() == 0)
      {
         return;
      }

      VirtualFileSystem.getInstance().getChildren(foldersToRefresh.get(0), new AsyncRequestCallback<Folder>()
      {

         @Override
         protected void onSuccess(Folder result)
         {
            final Folder folder = result;
            foldersToRefresh.remove(folder);
            //TODO if will be some value - display system items or not, then add check here:
            removeSystemItemsFromView(folder.getChildren());
            Collections.sort(folder.getChildren(), comparator);

            display.getBrowserTree().setValue(folder);

            eventBus.fireEvent(new RestorePerspectiveEvent());
            eventBus.fireEvent(new SelectViewEvent(BrowserPanel.ID));

            if (itemToSelect != null)
            {
               display.selectItem(itemToSelect);
               itemToSelect = null;
            }

            if (foldersToRefresh.size() > 0)
            {
               refreshNextFolder();
            }
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            itemToSelect = null;
            foldersToRefresh.clear();

            eventBus.fireEvent(new ExceptionThrownEvent("Service is not deployed.<br>Parent folder not found."));
            eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
         }
      });
   }

   /**
    * Removes items for not to be displayed, if they are system ones
    * (for example, ".groovyclasspath" file).
    * To known system item or not call {@link Item} method: boolean isSystem().
    * 
    * @param items
    */
   private void removeSystemItemsFromView(List<Item> items)
   {
      List<Item> itemsToRemove = new ArrayList<Item>();
      for (Item item : items)
      {
         if (item.isSystem())
            itemsToRemove.add(item);
      }
      items.removeAll(itemsToRemove);
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
         return item1.getName().compareTo(item2.getName());
      }
   };

   //   /**
   //    * Initializing application
   //    * 
   //    * @see org.exoplatform.ide.client.application.event.InitializeApplicationHandler#onInitializeApplication(org.exoplatform.ide.client.application.event.InitializeApplicationEvent)
   //    */
   //   public void onInitializeApplication(InitializeApplicationEvent event)
   //   {
   //      eventBus.fireEvent(new PanelSelectedEvent(BrowserPanel.ID));
   //
   //      if (entryPoint == null)
   //      {
   //         return;
   //      }
   //
   //      switchWorkspace(entryPoint);
   //
   //      new Timer()
   //      {
   //         @Override
   //         public void run()
   //         {
   //            eventBus.fireEvent(new PanelSelectedEvent(BrowserPanel.ID));
   //         }
   //      }.schedule(500);
   //   }

   /**
    * Select chosen item in browser.
    * 
    * @see org.exoplatform.ide.client.browser.event.SelectItemHandler#onSelectItem(org.exoplatform.ide.client.browser.event.SelectItemEvent)
    */
   public void onSelectItem(SelectItemEvent event)
   {
      display.selectItem(event.getItemHref());
   }

   public void onPanelSelected(PanelSelectedEvent event)
   {
      if (BrowserPanel.ID.equals(event.getPanelId()))
      {
         onItemSelected();
      }
   }

   public void onEntryPointChanged(EntryPointChangedEvent event)
   {
      entryPoint = event.getEntryPoint();
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      Item item = event.getItem();
      if (item instanceof File)
      {
         File file = (File)item;
         Property lockOwnerProperty = file.getProperty(ItemProperty.LOCKDISCOVERY);
         file.getProperties().remove(lockOwnerProperty);
         display.updateItemState(file);
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedHandler#onItemLockResultReceived(org.exoplatform.ide.client.framework.vfs.event.ItemLockResultReceivedEvent)
    */
   public void onItemLockResultReceived(ItemLockResultReceivedEvent event)
   {
      if (event.getException() == null)
      {
         Item item = event.getItem();
         if (item instanceof File)
         {
            File file = (File)item;
            Property lockOwnerProperty = new Property(ItemProperty.LOCKDISCOVERY);
            lockOwnerProperty.setValue("&nbsp;");
            file.getProperties().add(lockOwnerProperty);
            display.updateItemState(file);
         }
      }
   }

   //   /**
   //    * Switching active workspace
   //    */
   //   private void switchWorkspace(String entryPoint)
   //   {
   //
   //      //handlers.addHandler(ChildrenReceivedEvent.TYPE, this);
   //      //handlers.addHandler(type, handler)
   //      handlers.addHandler(ExceptionThrownEvent.TYPE, this);
   //
   //      Folder rootFolder = new Folder(entryPoint);
   //      rootFolder.setIcon(Images.FileTypes.WORKSPACE);
   //      VirtualFileSystem.getInstance().getChildren(rootFolder);
   //   }

   /**
    * Switching active workspace by Switch Workspace Event
    * 
    * @see SwitchEntryPointEvent#onSwitchEntryPoint(org.exoplatform.ide.client.workspace.event.SwitchEntryPointEvent)
    */
   public void onSwitchEntryPoint(SwitchEntryPointEvent event)
   {
      if (!viewOpened) {
         IDE.getInstance().openView(display.getView());         
      }

      entryPoint = null;

      display.getBrowserTree().setValue(null);
      selectedItems.clear();
      selectedItems.clear();
      eventBus.fireEvent(new ItemsSelectedEvent(selectedItems, BrowserPanel.ID));

      eventBus.fireEvent(new EnableStandartErrorsHandlingEvent(false));

      // TODO [IDE-307] check appConfig["entryPoint"] property
      final Folder rootFolder = new Folder(event.getEntryPoint());
      VirtualFileSystem.getInstance().getProperties(rootFolder, new ItemPropertiesCallback()
      {
         @Override
         protected void onSuccess(Item result)
         {
            eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());

            entryPoint = result.getHref();

            eventBus.fireEvent(new EntryPointChangedEvent(result.getHref()));

            eventBus.fireEvent(new SelectViewEvent(Display.ID));
            eventBus.fireEvent(new PanelSelectedEvent(Display.ID));

            display.getBrowserTree().setValue(result);
            display.selectItem(result.getHref());
            selectedItems = display.getSelectedItems();

            try
            {
               onRefreshBrowser(new RefreshBrowserEvent());
            }
            catch (Exception e)
            {
               e.printStackTrace();
            }
         }

         @Override
         protected void onFailure(Throwable exception)
         {
            super.onFailure(exception);

            itemToSelect = null;
            foldersToRefresh.clear();

            eventBus.fireEvent(new EnableStandartErrorsHandlingEvent());
            eventBus.fireEvent(new EntryPointChangedEvent(null));
         }

      });
   }

   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      //      applicationSettings = event.getApplicationSettings();

      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      display.setLockTokens(event.getApplicationSettings().getValueAsMap("lock-tokens"));
   }

   // keyboard keys doesn't work within the TreeGrid in the Internet Explorer 8.0, Safari 5.0.2 and Google Chrome 7.0.5 seems because of SmartGWT issues
   protected void onKeyPressed(char charCode, boolean isControlKeyDown)
   {
      if (isControlKeyDown)
      {
         // "Ctrl+C" hotkey handling
         if (String.valueOf(charCode).toUpperCase().equals("C"))
         {
            eventBus.fireEvent(new CopyItemsEvent());
         }

         // "Ctrl+X" hotkey handling         
         else if (String.valueOf(charCode).toUpperCase().equals("X"))
         {
            eventBus.fireEvent(new CutItemsEvent());
         }

         // "Ctrl+V" hotkey handling
         else if (String.valueOf(charCode).toUpperCase().equals("V"))
         {
            eventBus.fireEvent(new PasteItemsEvent());
         }
      }

      // "Delete" hotkey handling
      else if (charCode == KeyCodes.KEY_DELETE)
      {
         eventBus.fireEvent(new DeleteItemEvent());
      }

      // "Enter" hotkey handling - impossible to handle Enter key pressing event within the TreeGrid and ListGrid in the SmartGWT 2.2 because of bug when Enter keypress is not caugth. http://code.google.com/p/smartgwt/issues/detail?id=430 
      //      else if (charCode == KeyCodes.KEY_ENTER)
      //      {
      //         onBrowserDoubleClicked();
      //      }
   }

   @Override
   public void onViewOpened(ViewOpenedEvent event)
   {
      if (Display.ID.equals(event.getViewId()))
      {
         viewOpened = true;
      }
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (Display.ID.equals(event.getViewId()))
      {
         viewOpened = false;
      }
   }

}
