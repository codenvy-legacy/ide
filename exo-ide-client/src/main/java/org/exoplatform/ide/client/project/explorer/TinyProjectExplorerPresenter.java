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
package org.exoplatform.ide.client.project.explorer;

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
import com.google.gwt.http.client.RequestException;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.Timer;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.copy.AsyncRequestCallback;
import org.exoplatform.gwtframework.ui.client.api.TreeGridItem;
import org.exoplatform.gwtframework.ui.client.component.TreeIconPosition;
import org.exoplatform.gwtframework.ui.client.dialog.BooleanValueReceivedHandler;
import org.exoplatform.gwtframework.ui.client.dialog.Dialogs;
import org.exoplatform.ide.client.event.EnableStandartErrorsHandlingEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent;
import org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler;
import org.exoplatform.ide.client.framework.event.AllFilesClosedEvent;
import org.exoplatform.ide.client.framework.event.AllFilesClosedHandler;
import org.exoplatform.ide.client.framework.event.CloseAllFilesEvent;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserEvent;
import org.exoplatform.ide.client.framework.event.RefreshBrowserHandler;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectCreatedEvent;
import org.exoplatform.ide.client.framework.project.ProjectCreatedHandler;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.ui.api.IsView;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.navigation.event.CopyItemsEvent;
import org.exoplatform.ide.client.navigation.event.CutItemsEvent;
import org.exoplatform.ide.client.navigation.event.PasteItemsEvent;
import org.exoplatform.ide.client.operation.deleteitem.DeleteItemEvent;
import org.exoplatform.ide.client.project.CloseProjectEvent;
import org.exoplatform.ide.client.project.CloseProjectHandler;
import org.exoplatform.ide.client.project.OpenProjectEvent;
import org.exoplatform.ide.client.project.OpenProjectHandler;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemContext;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.Lock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:gavrikvetal@gmail.com">Vitaliy Gulyy</a>
 * @version $
 * 
 */

public class TinyProjectExplorerPresenter implements RefreshBrowserHandler, SelectItemHandler,
   ViewVisibilityChangedHandler, ItemUnlockedHandler, ItemLockedHandler, ApplicationSettingsReceivedHandler,
   ViewClosedHandler, AddItemTreeIconHandler, RemoveItemTreeIconHandler,
   ConfigurationReceivedSuccessfullyHandler, ShowProjectExplorerHandler, ItemsSelectedHandler, ViewActivatedHandler,
   OpenProjectHandler, VfsChangedHandler, ProjectCreatedHandler, CloseProjectHandler, AllFilesClosedHandler
{
   
   public interface Display extends IsView
   {
      
      void setProjectExplorerTreeVisible(boolean visible);

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
       * @param itemId item's path
       */
      void selectItem(String itemId);

      /**
       * Deselect item in browser tree by path.
       * 
       * @param itemId item's path
       */
      void deselectItem(String itemId);

      /**
       * Update the state of the item in the tree.
       * 
       * @param file
       */
      void updateItemState(FileModel file);

      /**
       * Set lock tokens to the items in the tree.
       * 
       * @param locktokens
       */
      void setLockTokens(Map<String, String> locktokens);

      /**
       * Add info icons to main item icon
       */
      void addItemsIcons(Map<Item, Map<TreeIconPosition, ImageResource>> itemsIcons);

      /**
       * Remove info icon from item
       */
      void removeItemIcons(Map<Item, TreeIconPosition> itemsIcons);

   }
   
   private static final String DEFAULT_TITLE = "&nbsp;Project Explorer&nbsp;";

   private static final String RECEIVE_CHILDREN_ERROR_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .workspaceReceiveChildrenError();

   private Display display;

   //private boolean viewOpened = false;

   private String itemToSelect;

   private List<Folder> foldersToRefresh = new ArrayList<Folder>();

   private List<Item> selectedItems = new ArrayList<Item>();

   private List<Item> navigatorSelectedItems = new ArrayList<Item>();

   private String vfsBaseUrl;
   
   private ProjectModel openedProject;

   public TinyProjectExplorerPresenter()
   {
      IDE.getInstance().addControl(new ShowProjectExplorerControl());
      IDE.getInstance().addControl(new CloseProjectControl());

      IDE.addHandler(ShowProjectExplorerEvent.TYPE, this);

      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      IDE.addHandler(RefreshBrowserEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ItemLockedEvent.TYPE, this);
      IDE.addHandler(ItemUnlockedEvent.TYPE, this);
      IDE.addHandler(ConfigurationReceivedSuccessfullyEvent.TYPE, this);
      IDE.addHandler(AddItemTreeIconEvent.TYPE, this);
      IDE.addHandler(RemoveItemTreeIconEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
      IDE.addHandler(OpenProjectEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      IDE.addHandler(ProjectCreatedEvent.TYPE, this);
      IDE.addHandler(CloseProjectEvent.TYPE, this);
      
      IDE.addHandler(AllFilesClosedEvent.TYPE, this);
      IDE.addHandler(SelectItemEvent.TYPE, this);
   }
   
   @Override
   public void onShowProjectExplorer(ShowProjectExplorerEvent event)
   {
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }

      showProjectExplorer();

      if (navigatorSelectedItems.size() == 0)
      {
         display.setProjectExplorerTreeVisible(false);
         return;
      }
      
      display.setProjectExplorerTreeVisible(true);

      display.getBrowserTree().setValue(navigatorSelectedItems.get(0));
      display.asView().setTitle("&nbsp;" + navigatorSelectedItems.get(0).getName() + "&nbsp;");
      display.selectItem(navigatorSelectedItems.get(0).getId());
      selectedItems = display.getSelectedItems();

      if (!(navigatorSelectedItems.get(0) instanceof Folder))
      {
         return;
      }

      Folder folder = (Folder)navigatorSelectedItems.get(0);
      foldersToRefresh.clear();
      foldersToRefresh.add(folder);
      refreshNextFolder();
   }
   
   private void showProjectExplorer() {
      if (display != null) {
         return;
      }
      
      display = GWT.create(Display.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }
   
   public void bindDisplay()
   {
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
            onKeyPressed(event.getNativeEvent().getKeyCode(), event.isControlKeyDown());
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
         if (item.getPath().startsWith(folder.getPath()) && !item.getId().equals(folder.getId()))
         {
            display.deselectItem(item.getId());
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
      updateSelectionTimer.schedule(1);
   }

   private Timer updateSelectionTimer = new Timer()
   {
      @Override
      public void run()
      {
         if (display == null)
         {
            return;
         }

         selectedItems = display.getSelectedItems();
         IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView().getId()));
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
         IDE.fireEvent(new OpenFileEvent((FileModel)item));
      }
   }

   /**
    * Handling of folder opened event from browser
    * 
    * @param openedFolder
    */
   protected void onFolderOpened(Folder openedFolder)
   {
      //Commented to fix bug with selection of new folder
      //      itemToSelect = null;
      ItemList<Item> children =
         (openedFolder instanceof ProjectModel) ? ((ProjectModel)openedFolder).getChildren()
            : ((FolderModel)openedFolder).getChildren();
      if (!children.getItems().isEmpty())
      {
         return;
      }

      foldersToRefresh.clear();
      foldersToRefresh.add(openedFolder);
      refreshNextFolder();
   }

   public void onRefreshBrowser(RefreshBrowserEvent event)
   {
      if (display == null)
      {
         return;
      }

      if (!display.asView().isActive() && !"ideTinyProjectExplorerView".equals(lastNavigatorId))
      {
         return;
      }

      if (event.getItemToSelect() != null)
      {
         itemToSelect = event.getItemToSelect().getId();
      }
      else
      {
         List<Item> selectedItems = display.getSelectedItems();
         if (selectedItems.size() > 0)
         {
            itemToSelect = selectedItems.get(0).getId();
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

         if (selectedItems.size() > 0)
         {
            Item item = selectedItems.get(0);
            if (item instanceof FileModel)
            {
               foldersToRefresh.add(((FileModel)item).getParent());
            }
            else if (item instanceof Folder)
            {
               foldersToRefresh.add((Folder)item);
            }
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
      
      final Folder folder = foldersToRefresh.get(0);
      try
      {
         VirtualFileSystem.getInstance().getChildren(folder,
            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
            {
               @Override
               protected void onFailure(Throwable exception)
               {
                  itemToSelect = null;
                  foldersToRefresh.clear();
                  exception.printStackTrace();
                  IDE.fireEvent(new ExceptionThrownEvent(exception, RECEIVE_CHILDREN_ERROR_MSG));
                  IDE.fireEvent(new EnableStandartErrorsHandlingEvent());
               }

               @Override
               protected void onSuccess(List<Item> result)
               {
                  if (folder instanceof FolderModel)
                  {
                     ((FolderModel)folder).getChildren().getItems().clear();
                     ((FolderModel)folder).getChildren().getItems().addAll(result);
                  }
                  else if (folder instanceof ProjectModel)
                  {
                     ((ProjectModel)folder).getChildren().getItems().clear();
                     ((ProjectModel)folder).getChildren().getItems().addAll(result);
                  }

                  for (Item i : result)
                  {
                     if (i instanceof ItemContext)
                     {
                        ((ItemContext)i).setParent(new FolderModel(folder));
                     }

                     if (folder instanceof ProjectModel)
                     {
                        ((ItemContext)i).setProject((ProjectModel)folder);
                     }
                     else if (folder instanceof ItemContext && ((ItemContext)folder).getProject() != null)
                     {
                        ((ItemContext)i).setProject(((ItemContext)folder).getProject());
                     }
                  }
                  folderContentReceived(folder);
               }
            });
      }
      catch (RequestException e)
      {
         e.printStackTrace();
      }
   }

   private void folderContentReceived(Folder folder)
   {
      IDE.fireEvent(new FolderRefreshedEvent(folder));
      foldersToRefresh.remove(folder);
      //TODO if will be some value - display system items or not, then add check here:
      List<Item> children =
         (folder instanceof ProjectModel) ? ((ProjectModel)folder).getChildren().getItems() : ((FolderModel)folder)
            .getChildren().getItems();
      removeSystemItemsFromView(children);
      Collections.sort(children, comparator);

      display.getBrowserTree().setValue(folder);

      display.asView().setViewVisible();

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
         if (item.getName().startsWith(".")) {
            itemsToRemove.add(item);
         }
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
         if (item1 instanceof Folder && item2 instanceof FileModel)
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

   /**
    * Select chosen item in browser.
    * 
    * @see org.exoplatform.ide.client.browser.event.SelectItemHandler#onSelectItem(org.exoplatform.ide.client.browser.event.SelectItemEvent)
    */
   public void onSelectItem(SelectItemEvent event)
   {
      if (display == null) {
         return;
      }
      
      display.selectItem(event.getItemHref());
   }

   /**
    * @see org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedHandler#onItemUnlocked(org.exoplatform.ide.client.framework.vfs.event.ItemUnlockedEvent)
    */
   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      Item item = event.getItem();
      if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         file.setLocked(false);
         file.setLock(null);
         display.updateItemState(file);
      }
   }

   public void onItemLocked(ItemLockedEvent event)
   {
      Item item = event.getItem();
      onRefreshBrowser(new RefreshBrowserEvent());
      if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         file.setLocked(true);
         file.setLock(new Lock("", event.getLockToken().getLockToken(), 0));
         display.updateItemState(file);
      }
   }


   /**
    * @see org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedHandler#onApplicationSettingsReceived(org.exoplatform.ide.client.framework.settings.event.ApplicationSettingsReceivedEvent)
    */
   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      if (event.getApplicationSettings().getValueAsMap("lock-tokens") == null)
      {
         event.getApplicationSettings().setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      if (display == null)
      {
         return;
      }

      display.setLockTokens(event.getApplicationSettings().getValueAsMap("lock-tokens"));
   }

   // keyboard keys doesn't work within the TreeGrid in the Internet Explorer 8.0, Safari 5.0.2 and Google Chrome 7.0.5 seems because of SmartGWT issues
   protected void onKeyPressed(int keyCode, boolean isControlKeyDown)
   {
      if (isControlKeyDown)
      {
         // "Ctrl+C" hotkey handling
         if (String.valueOf(keyCode).toUpperCase().equals("C"))
         {
            IDE.fireEvent(new CopyItemsEvent());
         }

         // "Ctrl+X" hotkey handling         
         else if (String.valueOf(keyCode).toUpperCase().equals("X"))
         {
            IDE.fireEvent(new CutItemsEvent());
         }

         // "Ctrl+V" hotkey handling
         else if (String.valueOf(keyCode).toUpperCase().equals("V"))
         {
            IDE.fireEvent(new PasteItemsEvent());
         }
      }

      // "Delete" hotkey handling
      else if (keyCode == KeyCodes.KEY_DELETE)
      {
         IDE.fireEvent(new DeleteItemEvent());
      }

      // "Enter" hotkey handling - impossible to handle Enter key pressing event within the TreeGrid and ListGrid in the SmartGWT 2.2 because of bug when Enter keypress is not caugth. http://code.google.com/p/smartgwt/issues/detail?id=430 
      //      else if (charCode == KeyCodes.KEY_ENTER)
      //      {
      //         onBrowserDoubleClicked();
      //      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof Display && event.getView().isViewVisible())
      {
         onItemSelected();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler#onRemoveItemTreeIcon(org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent)
    */
   @Override
   public void onRemoveItemTreeIcon(RemoveItemTreeIconEvent event)
   {
      display.removeItemIcons(event.getIconsToRemove());
   }

   /**
    * @see org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler#onAddItemTreeIcon(org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent)
    */
   @Override
   public void onAddItemTreeIcon(AddItemTreeIconEvent event)
   {
      display.addItemsIcons(event.getTreeItemIcons());
   }

   @Override
   public void onViewClosed(ViewClosedEvent event)
   {
      if (event.getView() instanceof Display)
      {
         display = null;
         openedProject = null;
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyHandler#onConfigurationReceivedSuccessfully(org.exoplatform.ide.client.framework.configuration.ConfigurationReceivedSuccessfullyEvent)
    */
   @Override
   public void onConfigurationReceivedSuccessfully(ConfigurationReceivedSuccessfullyEvent event)
   {
      this.vfsBaseUrl = event.getConfiguration().getVfsBaseUrl();      
      showProjectExplorer();
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      navigatorSelectedItems.clear();
      navigatorSelectedItems.addAll(event.getSelectedItems());

      if (display == null)
      {
         return;
      }
   }

   private String lastNavigatorId = null;

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      if ("ideWorkspaceView".equals(event.getView().getId())  &&!event.getView().getId().equals(lastNavigatorId))
      {
         lastNavigatorId = "ideWorkspaceView";
      }
      else if ("ideTinyProjectExplorerView".equals(event.getView().getId()) && !event.getView().getId().equals(lastNavigatorId))
      {
         lastNavigatorId = "ideTinyProjectExplorerView";
      }
      
      if (event.getView() instanceof Display) {
         onItemSelected();
      }
   }

   @Override
   public void onOpenProject(OpenProjectEvent event)
   {
      if (display == null) {
         display = GWT.create(Display.class);
         IDE.getInstance().openView(display.asView());
         bindDisplay();         
      }
      
      display.setProjectExplorerTreeVisible(true);
      
      if (openedProject != null) {
         if (openedProject.getId().equals(event.getProject().getId())) {
            return;
         }
      }
      
      doOpenProject(event.getProject());
   }

   private void doOpenProject(ProjectModel project) {
      needCloseProject = false;
      
      display.setProjectExplorerTreeVisible(true);
      
      openedProject = new ProjectModel(project);
      display.getBrowserTree().setValue(null);
      display.getBrowserTree().setValue(openedProject);
      display.asView().setTitle("&nbsp;" + openedProject.getName() + "&nbsp;");
      display.selectItem(openedProject.getId());
      selectedItems = display.getSelectedItems();
      
      IDE.fireEvent(new ProjectOpenedEvent(openedProject));
      
      navigatorSelectedItems.clear();
      navigatorSelectedItems.add(openedProject);
      
//      Folder folder = (Folder)navigatorSelectedItems.get(0);
      foldersToRefresh.clear();
      foldersToRefresh.add(openedProject);
      refreshNextFolder();
   }

   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      if (display == null) {
         return;
      }
      
      display.setProjectExplorerTreeVisible(false);
      display.asView().setTitle(DEFAULT_TITLE);
   }

   @Override
   public void onProjectCreated(final ProjectCreatedEvent event)
   {
      if (display == null) {
         return;
      }
      
      if (openedProject == null) {
         doOpenProject(event.getProject());
         return;
      }

      Dialogs.getInstance().ask("IDE", "Open project " + event.getProject().getName() + " ?", new BooleanValueReceivedHandler()
      {
         @Override
         public void booleanValueReceived(Boolean value)
         {
            if (true == value) {
               doOpenProject(event.getProject());
            }
         }
      });
   }
   
   @Override
   public void onCloseProject(CloseProjectEvent event)
   {
      if (openedProject == null || display == null) {
         return;
      }
      
      needCloseProject = true;      
      IDE.fireEvent(new CloseAllFilesEvent());
   }
   
   private boolean needCloseProject = false;

   @Override
   public void onAllFilesClosed(AllFilesClosedEvent event)
   {
      if (openedProject == null || display == null || !needCloseProject) {
         return;
      }      
      needCloseProject = false;
      
      ProjectClosedEvent projectClosedEvent = new ProjectClosedEvent(openedProject);
      
      openedProject = null;
      
      display.getBrowserTree().setValue(null);
      display.asView().setTitle(DEFAULT_TITLE);
      display.setProjectExplorerTreeVisible(false);
      
      selectedItems.clear();
      IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView().getId()));
      
      IDE.fireEvent(projectClosedEvent);
   }

}
