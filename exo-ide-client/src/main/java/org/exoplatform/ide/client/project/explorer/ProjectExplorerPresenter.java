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
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
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
import com.google.gwt.user.client.Window;

import org.exoplatform.gwtframework.commons.exception.ExceptionThrownEvent;
import org.exoplatform.gwtframework.commons.rest.AsyncRequestCallback;
import org.exoplatform.ide.client.framework.application.event.VfsChangedEvent;
import org.exoplatform.ide.client.framework.application.event.VfsChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorChangeActiveFileEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent;
import org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler;
import org.exoplatform.ide.client.framework.event.AllFilesClosedEvent;
import org.exoplatform.ide.client.framework.event.AllFilesClosedHandler;
import org.exoplatform.ide.client.framework.event.CloseAllFilesEvent;
import org.exoplatform.ide.client.framework.event.CursorPosition;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent;
import org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler;
import org.exoplatform.ide.client.framework.event.OpenFileEvent;
import org.exoplatform.ide.client.framework.module.IDE;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.AddItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.FolderRefreshedHandler;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.GoToItemHandler;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedEvent;
import org.exoplatform.ide.client.framework.navigation.event.ItemsSelectedHandler;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconEvent;
import org.exoplatform.ide.client.framework.navigation.event.RemoveItemTreeIconHandler;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemEvent;
import org.exoplatform.ide.client.framework.navigation.event.SelectItemHandler;
import org.exoplatform.ide.client.framework.project.ActiveProjectChangedEvent;
import org.exoplatform.ide.client.framework.project.CloseProjectEvent;
import org.exoplatform.ide.client.framework.project.CloseProjectHandler;
import org.exoplatform.ide.client.framework.project.OpenProjectEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedEvent;
import org.exoplatform.ide.client.framework.project.ProjectClosedHandler;
import org.exoplatform.ide.client.framework.project.ProjectExplorerDisplay;
import org.exoplatform.ide.client.framework.project.ProjectOpenedEvent;
import org.exoplatform.ide.client.framework.project.ProjectOpenedHandler;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedEvent;
import org.exoplatform.ide.client.framework.project.api.TreeRefreshedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings;
import org.exoplatform.ide.client.framework.settings.ApplicationSettings.Store;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedEvent;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsReceivedHandler;
import org.exoplatform.ide.client.framework.settings.ApplicationSettingsSavedEvent;
import org.exoplatform.ide.client.framework.settings.SaveApplicationSettingsEvent.SaveType;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewActivatedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewClosedHandler;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent;
import org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler;
import org.exoplatform.ide.client.model.SettingsService;
import org.exoplatform.ide.client.navigation.event.ShowHideHiddenFilesEvent;
import org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler;
import org.exoplatform.ide.client.operation.cutcopy.CopyItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.CutItemsEvent;
import org.exoplatform.ide.client.operation.cutcopy.PasteItemsEvent;
import org.exoplatform.ide.client.operation.deleteitem.DeleteItemEvent;
import org.exoplatform.ide.vfs.client.VirtualFileSystem;
import org.exoplatform.ide.vfs.client.event.ItemDeletedEvent;
import org.exoplatform.ide.vfs.client.event.ItemDeletedHandler;
import org.exoplatform.ide.vfs.client.event.ItemLockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemLockedHandler;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedEvent;
import org.exoplatform.ide.vfs.client.event.ItemUnlockedHandler;
import org.exoplatform.ide.vfs.client.marshal.ChildrenUnmarshaller;
import org.exoplatform.ide.vfs.client.marshal.ItemUnmarshaller;
import org.exoplatform.ide.vfs.client.model.FileModel;
import org.exoplatform.ide.vfs.client.model.FolderModel;
import org.exoplatform.ide.vfs.client.model.ItemWrapper;
import org.exoplatform.ide.vfs.client.model.ProjectModel;
import org.exoplatform.ide.vfs.shared.File;
import org.exoplatform.ide.vfs.shared.Folder;
import org.exoplatform.ide.vfs.shared.Item;
import org.exoplatform.ide.vfs.shared.ItemList;
import org.exoplatform.ide.vfs.shared.ItemNode;
import org.exoplatform.ide.vfs.shared.ItemType;
import org.exoplatform.ide.vfs.shared.Lock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
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

public class ProjectExplorerPresenter implements SelectItemHandler,
   ViewVisibilityChangedHandler, ItemUnlockedHandler, ItemLockedHandler, ApplicationSettingsReceivedHandler,
   ViewClosedHandler, AddItemTreeIconHandler, RemoveItemTreeIconHandler, ShowProjectExplorerHandler,
   ItemsSelectedHandler, ViewActivatedHandler, VfsChangedHandler,
//   AllFilesClosedHandler, 
   GoToItemHandler, EditorActiveFileChangedHandler, IDELoadCompleteHandler,
   EditorFileOpenedHandler, EditorFileClosedHandler, ShowHideHiddenFilesHandler, ItemDeletedHandler,
   
   ProjectOpenedHandler,
   ProjectClosedHandler,
   TreeRefreshedHandler
   
//   , OpenProjectHandler
{

   private static final String DEFAULT_TITLE = "Project Explorer";

   private static final String RECEIVE_CHILDREN_ERROR_MSG = org.exoplatform.ide.client.IDE.ERRORS_CONSTANT
      .workspaceReceiveChildrenError();

   /**
    * Comparator for ordering projects by name.
    */
   private static final Comparator<ProjectModel> PROJECT_COMPARATOR = new ProjectComparator();

   private ProjectExplorerDisplay display;

   private String itemToSelect;

   private HashMap<String, ProjectModel> map = new HashMap<String, ProjectModel>();

   //private List<Folder> foldersToRefresh = new ArrayList<Folder>();

   private List<Item> selectedItems = new ArrayList<Item>();

   private ProjectModel openedProject;

   private ProjectModel currentProject;

   private boolean openFileAfterGotoItem = false;

   private CursorPosition openFileCursorPosition = null;

   private FileModel editorActiveFile;

   private ApplicationSettings applicationSettings;

   private boolean ideLoadComplete = false;

   private List<String> itemsToBeOpened = new ArrayList<String>();

   public ProjectExplorerPresenter()
   {
      IDE.getInstance().addControl(new OpenProjectControl());

      IDE.addHandler(ShowProjectExplorerEvent.TYPE, this);

      IDE.addHandler(ViewClosedEvent.TYPE, this);
      IDE.addHandler(ViewVisibilityChangedEvent.TYPE, this);
      IDE.addHandler(ItemsSelectedEvent.TYPE, this);
      //IDE.addHandler(RefreshBrowserEvent.TYPE, this);
      IDE.addHandler(ApplicationSettingsReceivedEvent.TYPE, this);
      IDE.addHandler(ItemLockedEvent.TYPE, this);
      IDE.addHandler(ItemUnlockedEvent.TYPE, this);
      IDE.addHandler(AddItemTreeIconEvent.TYPE, this);
      IDE.addHandler(RemoveItemTreeIconEvent.TYPE, this);
      IDE.addHandler(ViewActivatedEvent.TYPE, this);
      IDE.addHandler(VfsChangedEvent.TYPE, this);
      //IDE.addHandler(CloseProjectEvent.TYPE, this);

      IDE.addHandler(SelectItemEvent.TYPE, this);
      IDE.addHandler(GoToItemEvent.TYPE, this);
      IDE.addHandler(EditorActiveFileChangedEvent.TYPE, this);
      IDE.addHandler(IDELoadCompleteEvent.TYPE, this);
      IDE.addHandler(EditorFileOpenedEvent.TYPE, this);
      IDE.addHandler(EditorFileClosedEvent.TYPE, this);
      IDE.addHandler(ShowHideHiddenFilesEvent.TYPE, this);
      IDE.addHandler(ItemDeletedEvent.TYPE, this);

      //IDE.addHandler(OpenProjectEvent.TYPE, this);
      IDE.addHandler(ProjectOpenedEvent.TYPE, this);
      IDE.addHandler(ProjectClosedEvent.TYPE, this);
      IDE.addHandler(TreeRefreshedEvent.TYPE, this);
   }

   private void ensureProjectExplorerDisplayCreated()
   {
      if (display != null)
      {
         return;
      }

      display = GWT.create(ProjectExplorerDisplay.class);
      IDE.getInstance().openView(display.asView());
      bindDisplay();
   }

   public void bindDisplay()
   {
      display.getBrowserTree().addOpenHandler(new OpenHandler<Item>()
      {
         public void onOpen(OpenEvent<Item> event)
         {
            Folder folder = (Folder)event.getTarget();
            
            ItemList<Item> children = null;
            if (folder instanceof ProjectModel)
            {
               children = ((ProjectModel)folder).getChildren();
            }
            else if (folder instanceof FolderModel)
            {
               children = ((FolderModel)folder).getChildren();
            }
            
            if (children != null && !children.getItems().isEmpty())
            {
               display.getBrowserTree().setValue(folder);
            }
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
            treeItemSelected();
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

      display.getLinkWithEditorButton().addClickHandler(linkWithEditorButtonClickHandler);
      display.setLinkWithEditorButtonSelected(linkingWithEditor);

      display.getProjectsListGrid().addSelectionHandler(new SelectionHandler<ProjectModel>()
      {
         @Override
         public void onSelection(SelectionEvent<ProjectModel> event)
         {
            IDE.fireEvent(new ProjectSelectedEvent(event.getSelectedItem()));
         }
      });

      display.getProjectsListGrid().addDoubleClickHandler(new DoubleClickHandler()
      {
         public void onDoubleClick(DoubleClickEvent event)
         {
            List<ProjectModel> selectedProjects = display.getSelectedProjects();
            if (selectedProjects.size() == 1)
            {
               IDE.fireEvent(new OpenProjectEvent(selectedProjects.get(0)));
            }
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
    *
    */
   protected void treeItemSelected()
   {
      Scheduler.get().scheduleDeferred(new ScheduledCommand()
      {
         @Override
         public void execute()
         {
            if (display == null)
            {
               return;
            }

            selectedItems = display.getSelectedItems();
            IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
            changeActiveFileOnSelection();
         }
      });
   }

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

//   /**
//    * Handling of folder opened event from browser
//    *
//    * @param openedFolder
//    */
//   protected void onFolderOpened(Folder openedFolder)
//   {
//      // Commented to fix bug with selection of new folder
//      // itemToSelect = null;
//      ItemList<Item> children =
//         (openedFolder instanceof ProjectModel) ? ((ProjectModel)openedFolder).getChildren()
//            : ((FolderModel)openedFolder).getChildren();
//      if (!children.getItems().isEmpty())
//      {
//         return;
//      }
//
//      foldersToRefresh.clear();
//      foldersToRefresh.add(openedFolder);
//      display.setUpdateTreeValue(false);
//      refreshNextFolder();
//   }

//   public void onRefreshBrowser(RefreshBrowserEvent event)
//   {
//      if (display == null)
//      {
//         return;
//      }
//
//      if (!display.asView().isActive() && !display.asView().getId().equals(lastNavigatorId))
//      {
//         return;
//      }
//
//      Window.alert("REFRESH BROWSER");
//      
//      if (event.getItemToSelect() != null)
//      {
//         itemToSelect = event.getItemToSelect().getId();
//      }
//      else
//      {
//         List<Item> selectedItems = display.getSelectedItems();
//         if (selectedItems.size() > 0)
//         {
//            itemToSelect = selectedItems.get(0).getId();
//         }
//         else
//         {
//            itemToSelect = null;
//         }
//      }
//
//      foldersToRefresh = event.getFolders();
//
//      if (foldersToRefresh == null || foldersToRefresh.size() == 0)
//      {
//         foldersToRefresh = new ArrayList<Folder>();
//
//         if (selectedItems.size() > 0)
//         {
//            Item item = selectedItems.get(0);
//            if (item instanceof FileModel)
//            {
//               foldersToRefresh.add(((FileModel)item).getParent());
//            }
//            else if (item instanceof Folder)
//            {
//               foldersToRefresh.add((Folder)item);
//            }
//         }
//      }
//
//      display.setUpdateTreeValue(false);
//      refreshNextFolder();
//   }

//   /**
//    * Refresh folder's properties.
//    *
//    * @param folder
//    */
//   private void refreshFolderProperties(final Folder folder)
//   {
//      try
//      {
//         VirtualFileSystem.getInstance().getItemById(folder.getId(),
//            new AsyncRequestCallback<ItemWrapper>(new ItemUnmarshaller(new ItemWrapper()))
//            {
//
//               @Override
//               protected void onSuccess(ItemWrapper result)
//               {
//                  folder.getProperties().clear();
//                  folder.getProperties().addAll(result.getItem().getProperties());
//               }
//
//               protected void onFailure(Throwable exception)
//               {
//               }
//            });
//      }
//      catch (RequestException e)
//      {
//      }
//   }

//   private void refreshNextFolder()
//   {
//      if (foldersToRefresh.size() == 0)
//      {
//         if (itemToSelect != null)
//         {
//            display.selectItem(itemToSelect);
//            itemToSelect = null;
//         }
//
//         return;
//      }
//
//      final Folder folder = foldersToRefresh.get(0);
//      // remove folder hear to open sever folder simultaneously
//      foldersToRefresh.remove(folder);
//      refreshFolderProperties(folder);
//      try
//      {
//         display.changeFolderIcon(folder, true);
//         VirtualFileSystem.getInstance().getChildren(folder,
//            new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
//            {
//               @Override
//               protected void onFailure(Throwable exception)
//               {
//                  itemToSelect = null;
//                  foldersToRefresh.clear();
//                  IDE.fireEvent(new ExceptionThrownEvent(exception, RECEIVE_CHILDREN_ERROR_MSG));
//               }
//
//               @Override
//               protected void onSuccess(List<Item> result)
//               {
//                  folderContentReceived(folder, result);
//               }
//            });
//      }
//      catch (RequestException e)
//      {
//         IDE.fireEvent(new ExceptionThrownEvent(e));
//      }
//   }

//   private void folderContentReceived(Folder folder, List<Item> result)
//   {
//      // loader.hide();
//      for (Item i : result)
//      {
//         if (i instanceof ItemContext)
//         {
//            ItemContext context = (ItemContext)i;
//            context.setParent(new FolderModel(folder));
//            context.setProject(map.get(i.getId()) != null ? map.get(i.getId()) : openedProject);
//         }
//      }
//
//      if (folder instanceof FolderModel)
//      {
//         ((FolderModel)folder).getChildren().getItems().clear();
//         ((FolderModel)folder).getChildren().getItems().addAll(result);
//      }
//      else if (folder instanceof ProjectModel)
//      {
//         ((ProjectModel)folder).getChildren().getItems().clear();
//         ((ProjectModel)folder).getChildren().getItems().addAll(result);
//      }
//
//      // TODO if will be some value - display system items or not, then add check here:
//      List<Item> children =
//         (folder instanceof ProjectModel) ? ((ProjectModel)folder).getChildren().getItems() : ((FolderModel)folder)
//            .getChildren().getItems();
//      // removeSystemItems(children);
//      Collections.sort(children, comparator);
//
//      display.getBrowserTree().setValue(folder);
//      IDE.fireEvent(new FolderRefreshedEvent(folder));
//      display.changeFolderIcon(folder, false);
//      // display.asView().setViewVisible();
//
//      refreshNextFolder();
//   }

//   /**
//    * Comparator for comparing items in received directory.
//    */
//   private Comparator<Item> comparator = new Comparator<Item>()
//   {
//      public int compare(Item item1, Item item2)
//      {
//         if (item1 instanceof Folder && item2 instanceof FileModel)
//         {
//            return -1;
//         }
//         else if (item1 instanceof File && item2 instanceof Folder)
//         {
//            return 1;
//         }
//         return item1.getName().compareTo(item2.getName());
//      }
//   };

   /**
    * Select chosen item in browser.
    *
    */
   public void onSelectItem(SelectItemEvent event)
   {
      if (display == null)
      {
         return;
      }
      display.selectItem(event.getItemId());
   }

   public void onItemUnlocked(ItemUnlockedEvent event)
   {
      Item item = event.getItem();
//      onRefreshBrowser(new RefreshBrowserEvent());
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
      //onRefreshBrowser(new RefreshBrowserEvent());
      if (item instanceof FileModel)
      {
         FileModel file = (FileModel)item;
         file.setLocked(true);
         file.setLock(new Lock("", event.getLockToken().getLockToken(), 0));
         display.updateItemState(file);
      }
   }

   public void onApplicationSettingsReceived(ApplicationSettingsReceivedEvent event)
   {
      applicationSettings = event.getApplicationSettings();

      if (applicationSettings.getValueAsMap("lock-tokens") == null)
      {
         applicationSettings.setValue("lock-tokens", new LinkedHashMap<String, String>(), Store.COOKIES);
      }

      if (applicationSettings.getValueAsBoolean("project-explorer-linked-with-editor") == null)
      {
         applicationSettings.setValue("project-explorer-linked-with-editor", Boolean.FALSE, Store.COOKIES);
      }
      linkingWithEditor = applicationSettings.getValueAsBoolean("project-explorer-linked-with-editor");

      ensureProjectExplorerDisplayCreated();

      display.setLockTokens(applicationSettings.getValueAsMap("lock-tokens"));
      display.setLinkWithEditorButtonSelected(linkingWithEditor);

      if (openedProject == null)
      {
         display.setLinkWithEditorButtonEnabled(false);
      }
   }

   // keyboard keys doesn't work within the TreeGrid in the Internet Explorer 8.0, Safari 5.0.2 and Google Chrome 7.0.5
   // seems because of SmartGWT issues
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

      // "Enter" hotkey handling - impossible to handle Enter key pressing event within the TreeGrid and ListGrid in the
      // SmartGWT 2.2 because of bug when Enter keypress is not caugth.
      // http://code.google.com/p/smartgwt/issues/detail?id=430
      // else if (charCode == KeyCodes.KEY_ENTER)
      // {
      // onBrowserDoubleClicked();
      // }
   }

   /**
    * @see org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedHandler#onViewVisibilityChanged(org.exoplatform.ide.client.framework.ui.api.event.ViewVisibilityChangedEvent)
    */
   @Override
   public void onViewVisibilityChanged(ViewVisibilityChangedEvent event)
   {
      if (event.getView() instanceof ProjectExplorerDisplay && event.getView().isViewVisible())
      {
         treeItemSelected();
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
      if (event.getView() instanceof ProjectExplorerDisplay)
      {
         display = null;
      }
   }

   @Override
   public void onItemsSelected(ItemsSelectedEvent event)
   {
      if (event.getSelectedItems().size() == 1)
      {
         Item item = event.getSelectedItems().get(0);
         if (item.getItemType().equals(ItemType.PROJECT))
         {
            currentProject = (ProjectModel)item;
         }
         else
         {
            currentProject = map.get(item.getId());
         }
         if (currentProject != null)
         {
            IDE.fireEvent(new ActiveProjectChangedEvent(currentProject));
         }
         else
         {
            IDE.fireEvent(new ActiveProjectChangedEvent(openedProject));
         }
      }
   }

   private String lastNavigatorId = null;

   protected ProjectModel projectTree;

   protected ItemNode tree;

   @Override
   public void onViewActivated(ViewActivatedEvent event)
   {
      if ("ideWorkspaceView".equals(event.getView().getId()) && !event.getView().getId().equals(lastNavigatorId))
      {
         lastNavigatorId = "ideWorkspaceView";
      }
      else if ("ideTinyProjectExplorerView".equals(event.getView().getId())
         && !event.getView().getId().equals(lastNavigatorId))
      {
         lastNavigatorId = "ideTinyProjectExplorerView";
      }

      if (event.getView() instanceof ProjectExplorerDisplay)
      {
         treeItemSelected();
      }
   }

   
   @Override
   public void onVfsChanged(VfsChangedEvent event)
   {
      if (display == null)
      {
         return;
      }

      display.setProjectExplorerTreeVisible(false);
      if (ideLoadComplete)
      {
         refreshProjectsList();
      }

      display.asView().setTitle(DEFAULT_TITLE);
   }
   
   
   
   
   
   
   
   
   @Override
   public void onGoToFolder(GoToItemEvent event)
   {
      goToFolder();
   }
   
   private void goToFolder()
   {
      if (editorActiveFile == null)
      {
         return;
      }
      
       if (display.selectItem(editorActiveFile.getId()))
       {
          return;
       }
            
      display.navigateToItem(editorActiveFile);
   }

   /*
   * Linking With Editor functionality
   */

   /**
    * Enabled or disabled Linking with Editor.
    */
   private boolean linkingWithEditor = false;

   /**
    * Opened files.
    */
   private Map<String, FileModel> openedFiles = new HashMap<String, FileModel>();

   /**
    * Link with Editor button click handler.
    */
   private ClickHandler linkWithEditorButtonClickHandler = new ClickHandler()
   {
      @Override
      public void onClick(ClickEvent event)
      {
         linkingWithEditor = !linkingWithEditor;
         display.setLinkWithEditorButtonSelected(linkingWithEditor);

         applicationSettings.setValue("project-explorer-linked-with-editor", new Boolean(linkingWithEditor),
            Store.COOKIES);

         SettingsService.getInstance().saveSettingsToCookies(applicationSettings);
         /*
          * fire event for show-hide line numbers command be able to update state.
          */
         IDE.fireEvent(new ApplicationSettingsSavedEvent(applicationSettings, SaveType.COOKIES));

         if (linkingWithEditor)
         {
            goToFolder();
         }
      }
   };

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedHandler#onEditorActiveFileChanged(org.exoplatform.ide.client.framework.editor.event.EditorActiveFileChangedEvent)
    */
   @Override
   public void onEditorActiveFileChanged(EditorActiveFileChangedEvent event)
   {
      editorActiveFile = event.getFile();

      if (ideLoadComplete && linkingWithEditor)
      {
         if (selectedItems.size() == 1 && editorActiveFile != null
            && editorActiveFile.getId().equals(selectedItems.get(0).getId()))
         {
            return;
         }

         goToFolder();
      }
   }

   /**
    * Changes the active file in Editor just after item selected in the Tree.
    */
   private void changeActiveFileOnSelection()
   {
      if (!ideLoadComplete || !linkingWithEditor || selectedItems.size() != 1)
      {
         return;
      }

      Item selectedItem = selectedItems.get(0);
      FileModel file = openedFiles.get(selectedItem.getId());
      if (file != null && editorActiveFile != null)
      {
         if (!file.getId().equals(editorActiveFile.getId()))
         {
            IDE.fireEvent(new EditorChangeActiveFileEvent(file));
            display.asView().activate();

         }
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.event.IDELoadCompleteHandler#onIDELoadComplete(org.exoplatform.ide.client.framework.event.IDELoadCompleteEvent)
    */
   @Override
   public void onIDELoadComplete(IDELoadCompleteEvent event)
   {
      ideLoadComplete = true;

      if (linkingWithEditor)
      {
         goToFolder();
      }

      if (openedProject == null)
      {
         refreshProjectsList();
      }
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileClosedHandler#onEditorFileClosed(org.exoplatform.ide.client.framework.editor.event.EditorFileClosedEvent)
    */
   @Override
   public void onEditorFileClosed(EditorFileClosedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedHandler#onEditorFileOpened(org.exoplatform.ide.client.framework.editor.event.EditorFileOpenedEvent)
    */
   @Override
   public void onEditorFileOpened(EditorFileOpenedEvent event)
   {
      openedFiles = event.getOpenedFiles();
   }

   /**
    * @see org.exoplatform.ide.client.navigation.handler.ShowHideHiddenFilesHandler#onShowHideHiddenFiles(org.exoplatform.ide.client.navigation.event.ShowHideHiddenFilesEvent)
    */
   @Override
   public void onShowHideHiddenFiles(ShowHideHiddenFilesEvent event)
   {
      if (display != null && openedProject != null)
      {
         Window.alert("ON SHOW HIDDEN FILES");
      }
   }

   /**
    * Refreshes the list of existing projects in project explorer and changes visibility of projects list.
    */
   private void refreshProjectsList()
   {
      try
      {
         VirtualFileSystem.getInstance().getChildren(VirtualFileSystem.getInstance().getInfo().getRoot(),
            ItemType.PROJECT, new AsyncRequestCallback<List<Item>>(new ChildrenUnmarshaller(new ArrayList<Item>()))
         {
            @Override
            protected void onSuccess(List<Item> result)
            {
               if (openedProject != null)
               {
                  display.setProjectsListGridVisible(false);
                  IDE.fireEvent(new ProjectSelectedEvent(null));
                  display.setProjectNotOpenedPanelVisible(false);
                  return;
               }

               List<ProjectModel> projects = new ArrayList<ProjectModel>();
               for (Item item : result)
               {
                  if (item instanceof ProjectModel)
                  {
                     projects.add((ProjectModel)item);
                  }
               }

               Collections.sort(projects, PROJECT_COMPARATOR);
               display.getProjectsListGrid().setValue(projects);

               if (projects.size() == 0)
               {
                  display.setProjectsListGridVisible(false);
                  IDE.fireEvent(new ProjectSelectedEvent(null));
                  display.setProjectNotOpenedPanelVisible(true);
               }
               else
               {
                  display.setProjectsListGridVisible(true);
                  display.setProjectNotOpenedPanelVisible(false);
               }
            }

            @Override
            protected void onFailure(Throwable exception)
            {
               IDE.fireEvent(new ExceptionThrownEvent(exception, "Searching of projects failed."));
            }
         });
      }
      catch (RequestException e)
      {
         IDE.fireEvent(new ExceptionThrownEvent(e, "Searching of projects failed."));
      }
   }

   /**
    * @see org.exoplatform.ide.vfs.client.event.ItemDeletedHandler#onItemDeleted(org.exoplatform.ide.vfs.client.event.ItemDeletedEvent)
    */
   @Override
   public void onItemDeleted(ItemDeletedEvent event)
   {
      if (event.getItem() instanceof ProjectModel)
      {
         if (openedProject == null)
         {
            refreshProjectsList();
         }
      }
   }


   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectOpenedHandler#onProjectOpened(org.exoplatform.ide.client.framework.project.ProjectOpenedEvent)
    */
   @Override
   public void onProjectOpened(final ProjectOpenedEvent projectOpenedEvent)
   {
      openedProject = projectOpenedEvent.getProject();
      ensureProjectExplorerDisplayCreated();
      
      if (ProjectUpdater.isNeedUpdateProject(openedProject))
      {
         ProjectUpdater.updateProject(projectOpenedEvent.getProject(), new ProjectUpdater.ProjectUpdatedHandler()
         {
            @Override
            public void onProjectUpdated()
            {
               showProjectTree();
            }
         });
      }
      else
      {
         showProjectTree();
      }
   }
   
   
   /**
    * @see org.exoplatform.ide.client.project.explorer.ShowProjectExplorerHandler#onShowProjectExplorer(org.exoplatform.ide.client.project.explorer.ShowProjectExplorerEvent)
    */
   @Override
   public void onShowProjectExplorer(ShowProjectExplorerEvent event)
   {
      if (display != null)
      {
         IDE.getInstance().closeView(display.asView().getId());
         return;
      }

      ensureProjectExplorerDisplayCreated();

      if (openedProject == null)
      {
         display.setProjectExplorerTreeVisible(false);
         refreshProjectsList();
         return;
      }
      
      if (ProjectUpdater.isNeedUpdateProject(openedProject))
      {
         ProjectUpdater.updateProject(openedProject, new ProjectUpdater.ProjectUpdatedHandler()
         {
            @Override
            public void onProjectUpdated()
            {
               showProjectTree();
            }
         });
      }
      else
      {
         showProjectTree();
      }
   }
   
   
   
   private void showProjectTree()
   {
      display.asView().setTitle(openedProject.getName());
      
      display.setProjectExplorerTreeVisible(true);
      display.getBrowserTree().setValue(null);
      display.getBrowserTree().setValue(openedProject);
      
      display.selectItem(openedProject.getId());
      selectedItems = display.getSelectedItems();      
      
      display.setLinkWithEditorButtonEnabled(true);
      display.setLinkWithEditorButtonSelected(linkingWithEditor);
   }


   @Override
   public void onTreeRefreshed(TreeRefreshedEvent event)
   {
      if (display == null)
      {
         return;
      }
      
      display.getBrowserTree().setValue(event.getFolder());
   }

   /**
    * @see org.exoplatform.ide.client.framework.project.ProjectClosedHandler#onProjectClosed(org.exoplatform.ide.client.framework.project.ProjectClosedEvent)
    */
   @Override
   public void onProjectClosed(ProjectClosedEvent event)
   {
      openedProject = null;

      if (display == null)
      {
         return;
      }

      display.getBrowserTree().setValue(null);
      display.asView().setTitle(DEFAULT_TITLE);
      display.setProjectExplorerTreeVisible(false);
      refreshProjectsList();
      display.setLinkWithEditorButtonEnabled(false);

      selectedItems.clear();
      IDE.fireEvent(new ItemsSelectedEvent(selectedItems, display.asView()));
   }
   
}
